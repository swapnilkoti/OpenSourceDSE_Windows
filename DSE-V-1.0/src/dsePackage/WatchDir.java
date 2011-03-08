package dsePackage;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKind.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;

public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    
    private String fileName;
	private static FSDirectory fsdDirIndex;
	static final File INDEX_DIR = new File("index");
	private File file;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
    	WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) {
                try {
                	if(dir.toString().contains(INDEX_DIR.getCanonicalPath())==false){
                		register(dir);
                	}
                } catch (IOException x) {
                    throw new IOError(x);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;
        
        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
            System.out.println("Done.");
        }
        this.trace = true;
        // enable trace after initial registration
        
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
            	/*key=watcher.poll();
            	if(key==null){
            		if(IndexFiles.flag==true){
            			IndexFiles.flag=false;
            			IndexFiles.writerLock.notifyAll();
            		}*/
            		key = watcher.take();
            		/*IndexFiles.flag=true;
            		IndexFiles.writerLock.wait();
            	}*/
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                    	BasicFileAttributes b=Files.readAttributes(child, BasicFileAttributes.class,NOFOLLOW_LINKS);
                        if (b.isDirectory()) {
                            registerAll(child);
                        }
                        else{
                        	register(child);
                        	file=new File(child.toString());
                    		updateIndex(1);
                        }
                    } 
                    catch (IOException x) {System.out.println(x.getMessage());
                        // ignore to keep sample readbale
                    }
                }
                else if(recursive && (kind==ENTRY_MODIFY)){
                	try{
                		System.out.println(child);
                		BasicFileAttributes b=Files.readAttributes(child, BasicFileAttributes.class,NOFOLLOW_LINKS);
                		if(!b.isDirectory()){
                			file=new File(child.toString());
                    		updateIndex(1);
                		}
                	}catch(Exception e){}
                }
                else if(recursive && (kind==ENTRY_DELETE)){
                	try{
                		System.out.println(child);
                		fileName=child.toString();
                    	updateIndex(2);
                	}catch(Exception e){}
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
    
    private void updateIndex(int flg){
    	synchronized(IndexFiles.writer){
    		if(flg==1){
        		int numHits=0;
            	try{
            		fsdDirIndex=FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory());
                	IndexReader reader = IndexReader.open(fsdDirIndex, true);
            		IndexSearcher searcher = new IndexSearcher(reader);
            		TopScoreDocCollector collector = TopScoreDocCollector.create(1, false);
              	  	try{
              	  		BooleanQuery query=new BooleanQuery();
              	  		query.add(new TermQuery(new Term("path",file.getCanonicalPath())),BooleanClause.Occur.MUST);
              	  		searcher.search(query, collector);
              	  		numHits = collector.getTotalHits();
              	  	}catch(Exception e){}
              	  	if(numHits>0){
              	  		long time=file.lastModified();
              	  		String newTime=Long.toString(time);
              	  		Document doc=searcher.doc(0);
              	  		String oldTime=doc.get("lastModified");
              	  		if(newTime.compareTo(oldTime)!=0){
              	  			try{
              	  				fileName=file.getCanonicalPath();
              	  				IndexFiles.writer.updateDocument(new Term("path",fileName),IndexFiles.getDocument(file));
              	  			}
              	  			catch(Exception e){}
              	  		}
              	  	}
              	  	else{
              	  		try {
              	  			IndexFiles.writer.addDocument(IndexFiles.getDocument(file));
              	  		}
              	  		catch(Exception e){}
              	  	}
              	  	IndexFiles.writer.optimize();
              	  	fsdDirIndex.close();
            	}
            	catch(Exception e){}
        	}
        	else if(flg==2){
        		try{
                	Term term=new Term("path",fileName);
                	IndexFiles.writer.deleteDocuments(term);
                	IndexFiles.writer.optimize();
        		}catch(Exception e){}
        	}
    	}
    }
}