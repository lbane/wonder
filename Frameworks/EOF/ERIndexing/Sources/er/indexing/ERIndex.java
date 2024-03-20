package er.indexing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.Format;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixTermEnum;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Parameter;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSTimestampFormatter;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXGenericRecord;
import er.extensions.eof.ERXKeyGlobalID;
import er.extensions.foundation.ERXKeyValueCodingUtilities;
import er.extensions.foundation.ERXMutableDictionary;
import er.extensions.foundation.ERXPatcher;
import er.extensions.foundation.ERXSelectorUtilities;
import er.indexing.storage.ERIDirectory;

public class ERIndex {

    protected Logger log;

    public static final String IndexingStartedNotification = "ERIndexingStartedNotification";

    public static final String IndexingEndedNotification = "ERIndexingEndedNotification";

    public static final String IndexingFailedNotification = "ERIndexingFailedNotification";

    private static final String GID = "EOGlobalID";

    protected static final String KEY = "ERIndexing";

    private static NSMutableDictionary<String, ERIndex> indices = ERXMutableDictionary.synchronizedDictionary();

    public class IndexDocument implements NSKeyValueCoding {

        private final Document _document;

        public IndexDocument(Document document) {
            _document = document;
        }

        @Override
        public void takeValueForKey(Object value, String key) {
            _document.getField(key).setValue(key);
        }

        @Override
        public Object valueForKey(String key) {
            return _document.get(key);
        }

        public Document document() {
            return _document;
        }

        public void save() {
            // TODO Auto-generated method stub

        }

        public void revert() {
            // TODO Auto-generated method stub

        }

        public void delete() {
            // TODO Auto-generated method stub

        }

    }

    protected class IndexAttribute {
        String _name;

        TermVector _termVector;

        Store _store;

        Index _index;

        Analyzer _analyzer;

        Format _format;

        ERIndex _model;

        /**
         * This class corresponds to one property. indexModel --> properties --> a property
         * 
         * @param index the index
         * @param name the property name (a key or keypath)
         * @param dict the property definition form indexModel
         */
        IndexAttribute(ERIndex index, String name, NSDictionary<String, String> dict) {
            _name = name;
            _termVector = (TermVector) classValue(dict, "termVector", TermVector.class, "YES");
            _store = (Store) classValue(dict, "store", Store.class, "NO");
            _index = (Index) classValue(dict, "index", Index.class, "ANALYZED");
            String analyzerClass = dict.objectForKey("analyzer");
            if (analyzerClass == null) {
                analyzerClass = StandardAnalyzer.class.getName();
            }
            _analyzer = (Analyzer) create(analyzerClass);
            if (_analyzer == null && name.matches("\\w+_(\\w+)")) {
                // String locale = name.substring(name.lastIndexOf('_') + 1);
            }
            _format = (Format) create(dict.objectForKey("format"));
            String numberFormat = dict.objectForKey("numberformat");
            if (numberFormat != null) {
                _format = new NSNumberFormatter(numberFormat);
            }
            String dateformat = dict.objectForKey("dateformat");
            if (dateformat != null) {
                _format = new NSTimestampFormatter(dateformat);
            }
        }

        private Object create(String className) {
            if (className != null) {
                try {
                    Class<?> c = ERXPatcher.classForName(className);
                    return c.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    throw NSForwardException._runtimeExceptionForThrowable(e);
                } 
            }
            return null;
        }

        private Object classValue(NSDictionary<String, String> dict, String key, Class<?> c, String defaultValue) {
            String code = dict.objectForKey(key);
            if (code == null) {
                code = defaultValue;
            }
            return ERXKeyValueCodingUtilities.classValueForKey(c, code);
        }

        public TermVector termVector() {
            return _termVector;
        }

        public Index index() {
            return _index;
        }

        public Store store() {
            return _store;
        }

        public String name() {
            return _name;
        }

        public Analyzer analyzer() {
            return _analyzer;
        }

        public String formatValue(Object value) {
            if (_format != null) {
                return _format.format(value);
            }
            if (value instanceof Number numberValue) {
                return NumberTools.longToString(numberValue.longValue());
            }
            if (value instanceof Date dateValue) {
                return DateTools.dateToString(dateValue, Resolution.MILLISECOND);
            }
            if (value instanceof NSArray<?> arrayValue) {
                return arrayValue.componentsJoinedByString(" ");
            }
            return (value != null ? value.toString() : null);
        }
    }

    protected static class Command extends Parameter {
    	/**
    	 * Do I need to update serialVersionUID?
    	 * See section 5.6 <cite>Type Changes Affecting Serialization</cite> on page 51 of the 
    	 * <a href="http://java.sun.com/j2se/1.4/pdf/serial-spec.pdf">Java Object Serialization Spec</a>
    	 */
    	private static final long serialVersionUID = 1L;

        protected Command(String name) {
            super(name);
        }

        protected static Command ADD = new Command("ADD");

        protected static Command DELETE = new Command("DELETE");
    }

    protected class Job {

        private final Command _command;

        private final NSArray<? extends Object> _objects;

        public Job(Command command, NSArray<? extends Object> objects) {
            _command = command;
            _objects = objects;
        }

        public NSArray<? extends Object> objects() {
            return _objects;
        }

        public Command command() {
            return _command;
        }
    }

    protected class Transaction {

        private final NSMutableArray<Job> _jobs = new NSMutableArray<>();

        private final EOEditingContext _editingContext;

        private boolean _clear = false;

        private int _objectCount = 0;

        Transaction(EOEditingContext ec) {
            _editingContext = ec;
        }

        public void clear() {
            _clear = true;
        }

        public void addJob(Command command, NSArray<? extends Object> objects) {
            if (objects.count() > 0) {
                _objectCount += objects.count();
                _jobs.addObject(new Job(command, objects));
            }
        }

        public EOEditingContext editingContext() {
            return _editingContext;
        }

        public NSArray<Job> jobs() {
            return _jobs;
        }

        @Override
        public String toString() {
            if (hasClear()) {
                return "Transaction@" + hashCode() + " clear";
            }
            return "Transaction@" + (editingContext() != null ? editingContext().hashCode() : null) + "@" + hashCode() + 
                    " jobs: " + jobs().count() + 
                    " objects: " + _objectCount;
        }

        public boolean hasClear() {
            return _clear;
        }

        TransactionHandler handler() {
            return _handler;
        }
    }
   
    protected abstract class TransactionHandler {

        protected Map<EOEditingContext, Transaction> activeChanges = new WeakHashMap<>();

        TransactionHandler() {
            registerNotifications();
        }

        public void clear() {
            Transaction transaction = new Transaction(null);
            transaction.clear();
            submit(transaction);
        }

        private void registerNotification(String notificationName, String selectorName) {
            NSNotificationCenter.defaultCenter().addObserver(this, ERXSelectorUtilities.notificationSelector(selectorName), notificationName, null);
        }

        protected void registerNotifications() {
            registerNotification(ERXEC.EditingContextWillSaveChangesNotification, "_handleChanges");
            registerNotification(EOEditingContext.EditingContextDidSaveChangesNotification, "_handleChanges");
            registerNotification(ERXEC.EditingContextDidRevertChanges, "_handleChanges");
            registerNotification(ERXEC.EditingContextFailedToSaveChanges, "_handleChanges");
        }

        protected void addObjectsToIndex(Transaction transaction, NSArray<EOEnterpriseObject> objects) {
            NSArray<Document> added = addedDocumentsForObjects(objects);
            transaction.addJob(Command.ADD, added);
        }

        protected void deleteObjectsFromIndex(Transaction transaction, NSArray<? extends EOEnterpriseObject> objects) {
            NSArray<Term> deleted = deletedTermsForObjects(objects);
            transaction.addJob(Command.DELETE, deleted);
        }

        public void submit(Transaction transaction) {
            index(transaction);
        }

        synchronized void index(Transaction transaction) {
            try {
                NSNotificationCenter.defaultCenter().postNotification(IndexingStartedNotification, transaction);
                boolean create = transaction.hasClear();
                if (create) {
                    log.warn("Clearing index");
                } else if (transaction.jobs().count() == 0) {
                    NSNotificationCenter.defaultCenter().postNotification(IndexingEndedNotification, transaction);
                    return;
                }
                long start = System.currentTimeMillis();
                log.info("Running {}", transaction);
                if (!create && !indexDirectory().fileExists("segments.gen")) {
                    log.error("segments did not exist but create wasn't called");
                    create = true;
                }
                IndexWriter writer = new IndexWriter(indexDirectory(), analyzer(), create, IndexWriter.MaxFieldLength.UNLIMITED);
                for (Job job : transaction.jobs()) {
                    log.info("Indexing: {} {}", job.command(), job.objects().count());
                    if (job.command() == Command.DELETE) {
                        for (Enumeration<?> iter = job.objects().objectEnumerator(); iter.hasMoreElements();) {
                            Term term = (Term)iter.nextElement();
                            writer.deleteDocuments(term);
                        }
                    } else if (job.command() == Command.ADD) {
                        for (Enumeration<?> iter = job.objects().objectEnumerator(); iter.hasMoreElements();) {
                            Document document = (Document)iter.nextElement();
                            writer.addDocument(document, analyzer());
                        }
                    }
                    log.info("Done: {} {}", job.command(), job.objects().count());
                }
                writer.flush();
                writer.close();
                NSNotificationCenter.defaultCenter().postNotification(IndexingEndedNotification, transaction);
                log.info("Finished in {}s: {}", (System.currentTimeMillis() - start) / 1000, transaction);
            } catch (IOException e) {
                NSNotificationCenter.defaultCenter().postNotification(IndexingFailedNotification, transaction);
                throw NSForwardException._runtimeExceptionForThrowable(e);
            }
        }
      
        public abstract void _handleChanges(NSNotification n);
    }
 
    private TransactionHandler _handler;

    private Directory _indexDirectory;

    private NSDictionary<String, IndexAttribute> _attributes = NSDictionary.EmptyDictionary;

    private final String _name;

    private String _store;
    
    protected ERIndex(String name) {
        log = LoggerFactory.getLogger(ERIndex.class.getName() + "." + name);
       _name = name;
        indices.setObjectForKey(this, name);
    }
   
    public void addObjectsToIndex(EOEditingContext ec, NSArray<EOEnterpriseObject> objects) {
        Transaction transaction = new Transaction(ec);
        _handler.addObjectsToIndex(transaction, objects);
        _handler.submit(transaction);
    }

    public void deleteObjectsFromIndex(EOEditingContext ec, NSArray<? extends EOEnterpriseObject> objects) {
        Transaction transaction = new Transaction(ec);
        _handler.deleteObjectsFromIndex(transaction, objects);
        _handler.submit(transaction);
    }
    
    protected TransactionHandler handler() {
        return _handler;
    }
    
    protected void setTransactionHandler(TransactionHandler handler) {
        _handler = handler;
    }
    
    protected void setStore(String store) {
        _store = store;
    }
    
    protected Analyzer analyzer() {
        PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_24));
        for (IndexAttribute attribute : attributes()) {
            wrapper.addAnalyzer(attribute.name(), attribute.analyzer());
        }
        return wrapper;
    }
    
    public void addAttribute(String propertyName, NSDictionary<String, String> propertyDefinition) {
        createAttribute(propertyName, propertyDefinition);
    }

    /**
     * Creates a new {@link IndexAttribute} and adds it to the attributes dictionary of this Index
     * 
     * @param propertyName
     * @param propertyDefinition
     * @return the new {@link IndexAttribute}
     */
    protected IndexAttribute createAttribute(String propertyName, NSDictionary<String, String> propertyDefinition) {
        IndexAttribute attribute = new IndexAttribute(this, propertyName, propertyDefinition);
        NSMutableDictionary<String, IndexAttribute> attributes = _attributes.mutableClone();
        attributes.setObjectForKey(attribute, propertyName);
        _attributes = attributes.immutableClone();
        return attribute;
    }

    private Directory indexDirectory() {
        if(_indexDirectory == null) {
            try {
                if (_store.startsWith("file://")) {
                    File indexDirectory = new File(URI.create(_store));
                    _indexDirectory = FSDirectory.open(indexDirectory);
                } else {
                    EOEditingContext ec = ERXEC.newEditingContext();

                    ec.lock();
                    try {
                        _indexDirectory = ERIDirectory.clazz.directoryForName(ec, _store);
                    } finally {
                        ec.unlock();
                    }
                }
            } catch (IOException e) {
                throw NSForwardException._runtimeExceptionForThrowable(e);
            }

        }
        return _indexDirectory;
    }

    private IndexReader _reader;
    private IndexSearcher _searcher;
    
    private IndexReader indexReader() throws IOException {
        if (_reader == null) {
            _reader = IndexReader.open(indexDirectory(), true);
            //											  ^^^ readOnly
            _searcher = new IndexSearcher(_reader);
        }
        if (!_reader.isCurrent()) {
            _reader = _reader.reopen();
            _searcher = new IndexSearcher(_reader);
        }
        return _reader;
    }
    
    public IndexSearcher indexSearcher() throws IOException {
        IndexReader indexReader = indexReader();
        return _searcher;
    }

    private NSArray<IndexAttribute> attributes() {
        return _attributes.allValues();
    }

    public String name() {
        return _name;
    }

    public NSArray<String> attributeNames() {
        return _attributes.allKeys();
    }

    public void clear() {
        _handler.clear();
    }

    protected IndexAttribute attributeNamed(String fieldName) {
        return _attributes.objectForKey(fieldName);
    }

    protected boolean handlesObject(EOEnterpriseObject eo) {
        return true;
    }
    
    protected NSArray<Document> addedDocumentsForObjects(NSArray<EOEnterpriseObject> objects) {
        NSMutableArray<Document> documents = new NSMutableArray<>();

        for (Enumeration<EOEnterpriseObject> e = objects.objectEnumerator(); e.hasMoreElements();) {
            EOEnterpriseObject eo = e.nextElement();
            if (handlesObject(eo)) {
                Document doc = createDocumentForObject(eo);
                if (doc != null) {
                    documents.addObject(doc);
                }
            }
        }
        return documents;
    }

    protected Document createDocumentForObject(EOEnterpriseObject eo) {
        EOKeyGlobalID gid = ((ERXGenericRecord) eo).permanentGlobalID();
        IndexDocument document = createDocumentForGlobalID(gid);
        Document doc = document.document();
        for (IndexAttribute info : attributes()) {
            String key = info.name();
            Object value = eo.valueForKeyPath(key);

            if (log.isDebugEnabled()) {
                log.info("{}->{}", key, value);
            }

            String stringValue = info.formatValue(value);
            if (stringValue != null) {
                Field field = new Field(key, stringValue, info.store(), info.index(), info.termVector());
                document.document().add(field);
            }
        }
        return doc;
    }

    protected NSArray<Term> deletedTermsForObjects(NSArray<? extends EOEnterpriseObject> objects) {
        NSMutableArray<Term> terms = new NSMutableArray<>();
        Term term;
        for (Enumeration<? extends EOEnterpriseObject> e = objects.objectEnumerator(); e.hasMoreElements();) {
            EOEnterpriseObject eo = e.nextElement();
            if (handlesObject(eo)) {
                term = createTerm(eo);
                if (term != null) {
                    terms.addObject(term);
                }
            }
        }
        return terms;
    }

    protected Term createTerm(EOEnterpriseObject eo) {
        Term term = null;
        String pk = gidStringForObject(eo);
        term = new Term(GID, pk);
        return term;
    }

    private static String gidStringForObject(EOEnterpriseObject eo) {
        EOKeyGlobalID gid = ((ERXGenericRecord) eo).permanentGlobalID();
        return ERXKeyGlobalID.globalIDForGID(gid).asString();
    }

    private Query queryForQualifier(EOQualifier qualifier) throws ParseException {
        EOKeyValueQualifier q = (EOKeyValueQualifier) qualifier;
        return queryForString(q.key(), (String) q.value());
    }

    private Query queryForString(String fieldName, String queryString) throws ParseException {
        Analyzer analyzer = attributeNamed(fieldName).analyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, fieldName, analyzer);
        return parser.parse(queryString);
    }

    private Query queryForString(String queryString) {
        //TODO
        return null;
    }

	public NSArray<? extends EOEnterpriseObject> findObjects(EOEditingContext ec, Query query, Filter filter, Sort sort, int start, int end) {
		 return ERXEOControlUtilities.faultsForGlobalIDs(ec, findGlobalIDs(query, filter, sort, start, end));
	}

	private NSArray<EOKeyGlobalID> findGlobalIDs(Query query, Filter filter, Sort sort, int start, int end) {
		NSMutableArray<EOKeyGlobalID> result = new NSMutableArray<>();
		try {
			Searcher searcher = indexSearcher();
			long startTime = System.currentTimeMillis();
			sort = sort == null ? new Sort() : sort;
			TopFieldDocs topFielsDocs = searcher.search(query, filter, end, sort);
			log.info("Searched for: {} in  {} ms", query, (System.currentTimeMillis() - startTime));
			for (int i = start; i < topFielsDocs.scoreDocs.length; i++) {
				String gidString = searcher.doc(topFielsDocs.scoreDocs[i].doc).getField(GID).stringValue();
				EOKeyGlobalID gid = ERXKeyGlobalID.fromString(gidString).globalID();
				result.addObject(gid);
			}
			log.info("Returning {} after {} ms", result.count(), System.currentTimeMillis() - startTime);
			return result;
		} catch (IOException e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
	}

    private NSArray<EOKeyGlobalID> findGlobalIDs(Query query) {
        NSMutableArray<EOKeyGlobalID> result = new NSMutableArray<>();
        long start = System.currentTimeMillis();
        try {
            Searcher searcher = indexSearcher();
            Hits hits = searcher.search(query);
            log.info("Searched for: {} in  {} ms", query, (System.currentTimeMillis() - start));
            for (Iterator<Hit> iter = hits.iterator(); iter.hasNext();) {
                Hit hit = iter.next();
                String gidString = hit.getDocument().getField(GID).stringValue();
                EOKeyGlobalID gid = ERXKeyGlobalID.fromString(gidString).globalID();
                result.addObject(gid);
            }
            log.info("Returning {} after {} ms", result.count(), System.currentTimeMillis() - start);
            return result;
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }

    public NSArray<? extends EOEnterpriseObject> findObjects(EOEditingContext ec, Query query) {
        return ERXEOControlUtilities.faultsForGlobalIDs(ec, findGlobalIDs(query));
    }

    public NSArray<EOKeyGlobalID> findGlobalIDs(String queryString) {
        Query query = queryForString(queryString);
        return findGlobalIDs(query);
    }

    public NSArray<EOKeyGlobalID> findGlobalIDs(EOQualifier qualifier) {
        try {
            Query query = queryForQualifier(qualifier);
            return findGlobalIDs(query);
        } catch (ParseException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }
    
    public ScoreDoc[] findScoreDocs(Query query, int hitsPerPage) {
        ScoreDoc[] hits = null;
        long start = System.currentTimeMillis();
        try {

            Searcher searcher = indexSearcher();
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
            searcher.search(query, collector);
            hits = collector.topDocs().scoreDocs;

            log.debug("Returning {} after {} ms", hits.length, System.currentTimeMillis() - start);
            return hits;
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }
    
    public NSArray<String> findTermStringsForPrefix(String field, String prefix) {
    	NSMutableArray<String> terms = new NSMutableArray<>();
    	try {
    		IndexReader reader = indexReader(); 
    		TermEnum tenum = new PrefixTermEnum(reader, new Term(field, prefix));
    		do {
    			if (tenum.term() == null) break;
    			final String termText = tenum.term().text();
    			terms.addObject(termText);
    		} while (tenum.next());

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return terms;
    }

    public IndexDocument findDocument(EOKeyGlobalID globalID) {
        NSMutableArray<Document> result = new NSMutableArray<>();
        long start = System.currentTimeMillis();
        try {
            Searcher searcher = indexSearcher();
            String pk = ERXKeyGlobalID.globalIDForGID(globalID).asString();
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term(GID, pk)), Occur.MUST);
            Hits hits = searcher.search(query);

            if (log.isInfoEnabled()) {
                log.info("Searched for: {} in  {} ms", query.toString(GID), System.currentTimeMillis() - start);
            }
            for (Iterator<Hit> iter = hits.iterator(); iter.hasNext();) {
                Hit hit = iter.next();
                result.addObject(hit.getDocument());
            }
            log.info("Returning {} after {} ms", result.count(), System.currentTimeMillis() - start);
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
        return new IndexDocument(result.lastObject());
    }
    
    public ERDocument documentForId(int docId, float score) {
    	ERDocument doc = null;
    	try {
    		Document _doc = indexSearcher().doc(docId);
    		doc = new ERDocument(_doc, score);
    	} catch (IOException e) {
    		throw NSForwardException._runtimeExceptionForThrowable(e);
    	}
    	return doc;
    }

    public NSArray<? extends EOEnterpriseObject> findObjects(EOEditingContext ec, EOQualifier qualifier) {
        try {
            Query query = queryForQualifier(qualifier);
            return findObjects(ec, query);
        } catch (ParseException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }

    public NSArray<EOEnterpriseObject> findObjects(EOEditingContext ec, String queryString) {
        Query query = queryForString(queryString);
        NSArray<EOKeyGlobalID> gids = findGlobalIDs(query);
        return ERXEOControlUtilities.faultsForGlobalIDs(ec, gids);
    }

    public NSArray<String> terms(String fieldName) {
        NSMutableSet<String> result = new NSMutableSet<>();
        TermEnum terms = null;
        try {
            IndexReader reader = indexReader();
            terms = reader.terms(new Term(fieldName, ""));
            while (fieldName.equals(terms.term().field())) {
                result.addObject(terms.term().text());
                if (!terms.next()) {
                    break;
                }
            }
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        } finally {
            if (terms != null) {
                try {
                    terms.close();
                } catch (IOException e) {
                    log.error("Could not close terms", e);
                }
            }
        }

        return result.allObjects();
    }

    public IndexDocument documentForGlobalID(EOKeyGlobalID globalID) {
        return findDocument(globalID);
    }

    public IndexDocument createDocumentForGlobalID(EOKeyGlobalID globalID) {
        Document doc = new Document();
        String pk = ERXKeyGlobalID.globalIDForGID(globalID).asString();
        doc.add(new Field(GID, pk, Field.Store.YES, Field.Index.NOT_ANALYZED));
        return new IndexDocument(doc);
    }

    public static ERIndex indexNamed(String key) {
        return indices.objectForKey(key);
    }

    protected boolean handlesEntity(String entityName)
    {
        return true;
    }
}
