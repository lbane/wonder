package er.indexing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Path;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.TermsEnum.SeekStatus;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorable;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollectorManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
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
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSTimestampFormatter;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXGenericRecord;
import er.extensions.eof.ERXKeyGlobalID;
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
            IndexableField ifield = _document.getField(key); 
            switch (value) {
                case Double dv -> ifield.storedValue().setDoubleValue(dv);
                case Float v -> ifield.storedValue().setFloatValue(v);
                case Long lv -> ifield.storedValue().setLongValue(lv);
                case Integer v -> ifield.storedValue().setIntValue(v);
                case String v -> ifield.storedValue().setStringValue(v);
                default -> ifield.storedValue().setStringValue(String.valueOf(value)); 
            }
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
        IndexableFieldType _fieldType;
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
        IndexAttribute(ERIndex index, String name, NSDictionary<String, Object> dict) {
            _name = name;
            
            // FIXME: die Definitionen werden aus einem Index-Model gewonnen. Das Modell enthält die Infos in einem
            // Textformat, ggf. in Dictionaries.
            // wir müssen hier die richtige "Übersetzung" von Text auf FieldType-Aufrufe machen
            // in der alten Doku (2.9.3) sind die Werte für Field.Index, Field.Store und Field.TermVector definiert.
            _fieldType = (IndexableFieldType)dict.objectForKey("fieldType");
            String analyzerClass = (String)dict.objectForKey("analyzer");
            if (analyzerClass == null) {
                analyzerClass = StandardAnalyzer.class.getName();
            }
            _analyzer = (Analyzer) create(analyzerClass);
            _format = (Format) create((String)dict.objectForKey("format"));
            String numberFormat = (String)dict.objectForKey("numberformat");
            if (numberFormat != null) {
                _format = new NSNumberFormatter(numberFormat);
            }
            String dateformat = (String)dict.objectForKey("dateformat");
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

        public IndexableFieldType fieldType()
        {
            return _fieldType;
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
                return String.valueOf(numberValue.longValue());
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

    protected static enum Command {
        ADD,
        DELETE;
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
                if (!create && !Arrays.asList(indexDirectory().listAll()).contains("segments.gen")) {
                    log.error("segments did not exist but create wasn't called");
                    create = true;
                }
                
                try (
                    final IndexWriter writer = new IndexWriter(indexDirectory(), 
                                                     new IndexWriterConfig(analyzer())
                                                         .setOpenMode(create ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND)
                                                         .setCommitOnClose(true))
                ) {
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
                                writer.addDocument(document);
                            }
                        }
                        log.info("Done: {} {}", job.command(), job.objects().count());
                    }
                }
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
        Map<String, Analyzer> fieldAnalyzer = HashMap.newHashMap(attributes().size());
        for (IndexAttribute attribute : attributes()) {
            fieldAnalyzer.put(attribute.name(), attribute.analyzer());
        }
        
        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), fieldAnalyzer);
    }
    
    public void addAttribute(String propertyName, NSDictionary<String, Object> propertyDefinition) {
        createAttribute(propertyName, propertyDefinition);
    }

    /**
     * Creates a new {@link IndexAttribute} and adds it to the attributes dictionary of this Index
     * 
     * @param propertyName
     * @param propertyDefinition
     * @return the new {@link IndexAttribute}
     */
    protected IndexAttribute createAttribute(String propertyName, NSDictionary<String, Object> propertyDefinition) {
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
                    Path indexDirectory = Path.of(URI.create(_store)); 
                    _indexDirectory = FSDirectory.open(indexDirectory);
                } else {
                    log.warn("Creating an EOF based Lucene storage is a bad idea.");
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

    private DirectoryReader _reader;
    private IndexSearcher _searcher;
    
    private IndexReader indexReader() throws IOException {
        if (_reader == null) {
            _reader = DirectoryReader.open(indexDirectory());
            //											  ^^^ readOnly
            _searcher = new IndexSearcher(_reader);
        }
        if (!_reader.isCurrent()) {
            DirectoryReader newReader = DirectoryReader.openIfChanged(_reader);
            _reader.close();
            _reader = newReader;
            _searcher = new IndexSearcher(_reader);
        }
        return _reader;
    }
    
    public IndexSearcher indexSearcher() throws IOException {
        indexReader();
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
                Field field = new StringField(key, stringValue, info.fieldType().stored() ? Store.YES : Store.NO); 
                //new Field(key, stringValue, info.store(), info.index(), info.termVector());
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
        QueryParser parser = new QueryParser(fieldName, analyzer);
        return parser.parse(queryString);
    }

    private Query queryForString(String queryString) {
        //TODO
        return null;
    }

    // Filter parameter removed, not available anymore in newer Lucene versions.
	public NSArray<? extends EOEnterpriseObject> findObjects(EOEditingContext ec, Query query, Sort sort, int start, int end) {
		 return ERXEOControlUtilities.faultsForGlobalIDs(ec, findGlobalIDs(query, sort, start, end));
	}

	private NSArray<EOKeyGlobalID> findGlobalIDs(Query query, Sort sort, int start, int end) {
		NSMutableArray<EOKeyGlobalID> result = new NSMutableArray<>();
		try {
			IndexSearcher searcher = indexSearcher();
			long startTime = System.currentTimeMillis();
			sort = sort == null ? new Sort() : sort;
			TopFieldDocs topFielsDocs = searcher.search(query, end, sort);
			log.info("Searched for: {} in  {} ms", query, (System.currentTimeMillis() - startTime));
			
			StoredFields storedFields = searcher.storedFields();
			final Set<String> gidFieldSet = Set.of(GID);
			
			for (int i = start; i < topFielsDocs.scoreDocs.length; i++) {
			    Document doc = storedFields.document(topFielsDocs.scoreDocs[i].doc, gidFieldSet);
			    String gidString = doc.getField(GID).stringValue();
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
            IndexSearcher searcher = indexSearcher();
            TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
            log.info("Searched for: {} in  {} ms", query, (System.currentTimeMillis() - start));
            StoredFields storedFields = searcher.storedFields();
            final Set<String> gidFieldSet = Set.of(GID);
            for (ScoreDoc hit: hits.scoreDocs) {
                Document doc = storedFields.document(hit.doc, gidFieldSet);
                String gidString = doc.getField(GID).stringValue();
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
            final IndexSearcher searcher = indexSearcher();
            TopScoreDocCollectorManager collectorManager = new TopScoreDocCollectorManager(hitsPerPage, hitsPerPage);
            TopDocs topDocs = searcher.search(query, collectorManager);
            hits = topDocs.scoreDocs;
            log.debug("Returning {} after {} ms", hits.length, System.currentTimeMillis() - start);
            return hits;
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }
    
    public List<String> findTermStringsForPrefix(String field, String prefix) {
    	final List<String> terms = new ArrayList<>();
    	try {
    	    IndexReader reader = indexReader();

    	    // get TermsEnum
    	    Terms mterms = MultiTerms.getTerms(reader, field);
    	    TermsEnum tenum = mterms.iterator();

    	    // seek the first instance of prefix. If not found, maybe we get a term containing the prefix 
    	    if (tenum.seekCeil(new BytesRef(prefix)) != SeekStatus.END) {

    	        BytesRef termBytes = tenum.term();
    	        while (termBytes != null) {
    	            String ts = termBytes.utf8ToString();
    	            // check if the term starts with the prefix and add it to the result
    	            if (ts.startsWith(prefix)) {
    	                terms.add(ts);
    	                // got to the next term (or finish search if no more terms exists
    	                termBytes = tenum.next();
    	            } else {
    	                // the current term does not start with the prefix, so no other term will, end search
    	                termBytes = null;
    	            }
    	        }
    	    }

    	} catch (Exception e) {
    	    log.error("Could not find prefix terms", e);
    	}
    	return terms;
    }
    
    public IndexDocument findDocument(EOKeyGlobalID globalID) {
        final long start = System.currentTimeMillis();
        try {
            final IndexSearcher searcher = indexSearcher();
            final String pk = ERXKeyGlobalID.globalIDForGID(globalID).asString();
            
            // build a query. we use a boolean query to wrap the term query to specify a MUST clause
            final BooleanQuery query = new BooleanQuery.Builder()
                    .add(new TermQuery(new Term(GID, pk)), Occur.MUST)
                    .build();
            
            // a collector collects found documentIds in a list
            class AllDocCollector implements Collector {
                
                final List<Integer> docSet = new ArrayList<>();

                @Override
                public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException
                {
                    final int docBase = context.docBase;
                    
                    return new LeafCollector() {
                        
                        @Override
                        public void setScorer(Scorable scorer) throws IOException
                        {
                            // ignore
                        }
                        
                        @Override
                        public void collect(int doc) throws IOException
                        {
                            docSet.add(docBase + doc);
                        }
                    };
                }

                @Override
                public ScoreMode scoreMode()
                {
                    return ScoreMode.COMPLETE_NO_SCORES;
                }
                
                public Stream<Integer> docStream()
                {
                    return docSet.stream();
                }
                
            };
            
            // a collection manager is used to reduce the output of multiple collectors to a single result
            CollectorManager<AllDocCollector, IndexDocument> colman = new CollectorManager<AllDocCollector, IndexDocument>() {

                @Override
                public AllDocCollector newCollector() throws IOException
                {
                    return new AllDocCollector();
                }

                @Override
                public IndexDocument reduce(Collection<AllDocCollector> collectors) throws IOException
                {
                    List<Integer> allDocSet = collectors.stream()
                                                        .parallel()
                                                        .flatMap(AllDocCollector::docStream)
                                                        .toList();
                    if (allDocSet.isEmpty()) {
                        return null;
                    }
                    if (allDocSet.size() != 1) {
                        log.warn("Found more than 1 document containing GID {}, using first one", globalID);
                    }
                    return new IndexDocument(searcher.getIndexReader().storedFields().document(allDocSet.getFirst()));
                } 
            };
            
            final IndexDocument result = searcher.search(query, colman);

            if (log.isInfoEnabled()) {
                log.info("Searched for: {} in  {} ms", query.toString(GID), System.currentTimeMillis() - start);
            }
            return result;
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }
    
    public ERDocument documentForId(int docId, float score) {
    	ERDocument doc = null;
    	try {
    	    Document _doc = indexReader().storedFields().document(docId);
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

    public Collection<String> terms(String fieldName) {
        try {
            IndexReader reader = indexReader();
            Terms terms = MultiTerms.getTerms(reader, fieldName);
            TermsEnum tnum = terms.iterator();
            
            Set<String> result = new TreeSet<String>();
            BytesRef term;
            while ((term = tnum.next()) != null) {
                result.add(term.utf8ToString());
            }
            
            return result;
        } catch (IOException e) {
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
    }

    public IndexDocument documentForGlobalID(EOKeyGlobalID globalID) {
        return findDocument(globalID);
    }
    
    private static FieldType gidFieldsType;
    static {
        gidFieldsType = new FieldType();
        gidFieldsType.setStored(true);
        gidFieldsType.setTokenized(false);
        gidFieldsType.setIndexOptions(IndexOptions.DOCS);
        gidFieldsType.setOmitNorms(true);
        gidFieldsType.freeze();
    }

    public IndexDocument createDocumentForGlobalID(EOKeyGlobalID globalID) {
        Document doc = new Document();
        String pk = ERXKeyGlobalID.globalIDForGID(globalID).asString();
        doc.add(new StoredField(GID, pk, gidFieldsType));
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
