package er.indexing.storage;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.eof.ERXEOControlUtilities;

public class ERIDirectory extends _ERIDirectory {
    /**
     * Do I need to update serialVersionUID?
     * See section 5.6 <cite>Type Changes Affecting Serialization</cite> on page 51 of the 
     * <a href="http://java.sun.com/j2se/1.4/pdf/serial-spec.pdf">Java Object Serialization Spec</a>
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("hiding")
    private static final Logger log = LoggerFactory.getLogger(ERIDirectory.class);

    public static final ERIDirectoryClazz clazz = new ERIDirectoryClazz();

    private class LockingDirectory extends Directory {

        private Directory _wrapped;

        LockingDirectory(Directory wrapped) {
            _wrapped = wrapped;
        }

        @Override
        public void close() throws IOException {
            editingContext().lock();
            try {
                _wrapped.close();
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public IndexOutput createOutput(String name, IOContext context) throws IOException {
            editingContext().lock();
            try {
                return _wrapped.createOutput(name, context);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public void deleteFile(String name) throws IOException {
            editingContext().lock();
            try {
                _wrapped.deleteFile(name);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public long fileLength(String name) throws IOException {
            editingContext().lock();
            try {
                return _wrapped.fileLength(name);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public String[] listAll() throws IOException
        {
            editingContext().lock();
            try {
                return _wrapped.listAll();
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public IndexOutput createTempOutput(String prefix, String suffix, IOContext context) throws IOException
        {
            editingContext().lock();
            try {
                return _wrapped.createTempOutput(prefix, suffix, context);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public void sync(Collection<String> names) throws IOException
        {
            editingContext().lock();
            try {
                _wrapped.sync(names);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public void syncMetaData() throws IOException
        {
            editingContext().lock();
            try {
                _wrapped.syncMetaData();
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public void rename(String source, String dest) throws IOException
        {
            editingContext().lock();
            try {
                _wrapped.rename(source, dest);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public IndexInput openInput(String name, IOContext context) throws IOException
        {
            editingContext().lock();
            try {
                return _wrapped.openInput(name, context);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public Lock obtainLock(String name) throws IOException
        {
            editingContext().lock();
            try {
                return _wrapped.obtainLock(name);
            } finally {
                editingContext().unlock();
            }
        }

        @Override
        public Set<String> getPendingDeletions() throws IOException
        {
            editingContext().lock();
            try {
                return _wrapped.getPendingDeletions();
            } finally {
                editingContext().unlock();
            }
        }

    }
    
    private class EOFLockFactory extends LockFactory {
        private class EOFLock extends Lock {
            
            private String _name;
            
            private EOFLock(String name) {
                _name = name;
                editingContext().lock();
                try {
                    dir.createFile(_name);
                } finally {
                    editingContext().unlock();
                }
            }
            
            public String name() {
                return _name;
            }
            
            @Override
            public void close() throws IOException
            {
                editingContext().lock();
                try {
                    dir.deleteFile(name());
                    synchronized (locks) {
                        locks.remove(_name);
                    }
                } finally {
                    editingContext().unlock();
                }
            }
            
            @Override
            public void ensureValid() throws IOException
            {
                synchronized (locks) {
                    if (!locks.containsKey(_name)) {
                        throw new IOException("lock "+_name+" no longer valid");
                    }
                }
            }
        }
        
        private Map<String, EOFLock> locks = new HashMap<>();
        private EOFDirectory dir;
        
        @Override
        public Lock obtainLock(Directory dir, String lockName) throws IOException
        {
            synchronized (locks) {
                EOFLock lock = new EOFLock(lockName);
                locks.put(lockName, lock);
                return lock;
            }
        }

        public void init(EOFDirectory eofDirectory)
        {
            dir = eofDirectory;
        }
    }

    private class EOFDirectory extends BaseDirectory {
        
        public EOFDirectory(ERIDirectory eridir) {
            super(new EOFLockFactory());
            ((EOFLockFactory)lockFactory).init(this);
        }

        @Override
        public void close() throws IOException {
            editingContext().saveChanges();
        }

        public ERIFile createFile(String s) {
            ERIFile file;
            file = ERIFile.clazz.createAndInsertObject(editingContext());
            file.setName(s);
            file.setDirectory(ERIDirectory.this);
            addToFiles(file);
            editingContext().saveChanges();
            return file;
        }

        @Override
        public void deleteFile(String s) throws IOException {
            log.debug("deleteFile: {}", s);
            ERIFile file = fileForName(s);
            if (file != null) {
                file.delete();
                editingContext().saveChanges();
            }
        }

        @Override
        public long fileLength(String s) throws IOException {
            return fileForName(s).length();
        }

        @Override
        public void rename(String source, String destination) throws IOException {
            fileForName(source).setName(destination);
            editingContext().saveChanges();
        }

        @Override
        public String[] listAll() throws IOException
        {
            return files().stream().map(ERIFile::name).toArray(String[]::new);
        }

        @Override
        public IndexOutput createOutput(String name, IOContext context) throws IOException
        {
            log.debug("createOutput: {}", name);
            ERIFile file = fileForName(name);
            if (file == null) {
                file = createFile(name);
                return file.createOutput();
            }
            
            throw new FileAlreadyExistsException(name);
        }

        @Override
        public IndexOutput createTempOutput(String prefix, String suffix, IOContext context) throws IOException
        {
            log.debug("createTempOutput: {}-???-{}.tmp", prefix, suffix);
            List<String> currentList = files().stream().map(ERIFile::name).toList();
            
            ThreadLocalRandom r = ThreadLocalRandom.current();
            
            String fileName;
            do {
                int i = r.nextInt();
                fileName = prefix + i + suffix + ".tmp";
            } while (currentList.contains(fileName));
            
            ERIFile file = createFile(fileName);
            return file.createOutput();
        }

        @Override
        public void sync(Collection<String> names) throws IOException
        {
            editingContext().saveChanges();
        }

        @Override
        public void syncMetaData() throws IOException
        {
            editingContext().saveChanges();
        }

         @Override
         public IndexInput openInput(String name, IOContext context) throws IOException
         {
            ERIFile file = fileForName(name);
            if (file == null) {
                throw new IOException("File not found: " + name);
            }
            try {
                return file.openInput();
            } catch (Exception ex) {
                ERXEOControlUtilities.refaultObject(ERIDirectory.this);
                ERXEOControlUtilities.clearSnapshotForRelationshipNamed(ERIDirectory.this, Key.FILES);
                throw new IOException("File not found: " + name);
            }
        }

        @Override
        public Set<String> getPendingDeletions() throws IOException
        {
            return Set.of();
        }
    }

    public static class ERIDirectoryClazz extends _ERIDirectory._ERIDirectoryClazz {

        public Directory directoryForName(EOEditingContext ec, String store) {
            ERIDirectory directory = objectMatchingKeyAndValue(ec, Key.NAME, store);
            if (directory == null) {
                directory = createAndInsertObject(ec);
                directory.setName(store);
                ec.saveChanges();
            }
            return directory.directory();
        }

    }

    public interface Key extends _ERIDirectory.Key {
    }

    @Override
    public void init(EOEditingContext ec) {
        super.init(ec);
    }

    public ERIFile fileForName(String name) {
        return files().stream().filter(f -> f.name().equals(name)).findFirst().orElse(null);
    }

    private Directory _directory;

    public Directory directory() {

        if (_directory == null) {
            _directory = new LockingDirectory(new EOFDirectory(this));
        }
        return _directory;
    }
}
