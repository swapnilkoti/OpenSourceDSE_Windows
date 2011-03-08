package dsePackage;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import java.io.*;
import java.util.*;
import org.apache.tika.metadata.*;
import org.apache.tika.parser.*;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.*;
import org.apache.lucene.document.*;
import org.apache.tika.config.TikaConfig;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;

//import org.apache.tika.utils.ParseUtils;
//import org.apache.tika.parser.pdf.PDFParser;
//import org.apache.tika.parser.jpeg.JpegParser;

public class IndexFiles {
	
	static Set<String> textualMetadataFields=new HashSet<String>();
	static {
		textualMetadataFields.add(Metadata.TITLE);
		textualMetadataFields.add(Metadata.AUTHOR);
		textualMetadataFields.add(Metadata.COMMENTS);
		textualMetadataFields.add(Metadata.KEYWORDS);
		textualMetadataFields.add(Metadata.DESCRIPTION);
		textualMetadataFields.add(Metadata.SUBJECT);
		textualMetadataFields.add("Windows XP Title");
		textualMetadataFields.add(Metadata.RESOURCE_NAME_KEY);
	}
	
  public IndexFiles() {}
  
  static final File INDEX_DIR = new File("index");
  public void index_files(String dir,javax.swing.JTextArea TA) {
    
    final File docDir = new File(dir);
    
    Date start = new Date();
    try {
    	analyzer=new StandardAnalyzer(Version.LUCENE_CURRENT);
    	fsdDirIndex=FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory());
    	if(INDEX_DIR.exists())
    		writer = new IndexWriter(fsdDirIndex, analyzer, false, new KeepOnlyLastCommitDeletionPolicy(),
    				IndexWriter.MaxFieldLength.LIMITED);
    	else
    		writer = new IndexWriter(fsdDirIndex, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
    	TA.insert("\nIndexing to directory "+INDEX_DIR.getPath()+"...",TA.getCaretPosition());
    	directoryToBeIndexedSize=docDir.getTotalSpace();
    	indexDocs(docDir,TA);
    	writer.optimize();
    	writer.close();
    	Date end = new Date();
    	TA.append("\n"+Long.toString(end.getTime() - start.getTime()));
    	TA.insert(" total milliseconds",TA.getCaretPosition());
    } catch (IOException e) {
    	TA.append("\nCaught a " + e.getClass() + "\n with message: " + e.getMessage());
    }
  }

  void indexDocs(File file,javax.swing.JTextArea TA)
    throws IOException {
	if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            indexDocs(new File(file, files[i]),TA);
          }
        }
      }
      else {
    	  int numHits=0;
    	  IndexReader reader = IndexReader.open(fsdDirIndex, true);
		  IndexSearcher searcher = new IndexSearcher(reader);
		  TopScoreDocCollector collector = TopScoreDocCollector.create(1, false);
    	  try{
    		  BooleanQuery query=new BooleanQuery();
    		  query.add(new TermQuery(new Term("path",file.getCanonicalPath())),BooleanClause.Occur.MUST);
    		  searcher.search(query, collector);
    		  numHits = collector.getTotalHits();
    	  }catch(Exception e){}
    	  synchronized(writer){
    		  if(numHits>0){
        		  long time=file.lastModified();
        		  String newTime=Long.toString(time);
        		  Document doc=searcher.doc(0);
        		  String oldTime=doc.get("lastModified");
        		  if(newTime.compareTo(oldTime)!=0){
        			  TA.insert("\nUpdating file "+file.getPath(),TA.getCaretPosition());
            		  try{
            			  writer.updateDocument(new Term("path",file.getCanonicalPath()),getDocument(file));
            		  }
            		  catch(Exception e){}
        		  }
        	  }
        	  else{
        		  TA.insert("\nAdding file "+file.getPath(),TA.getCaretPosition());
            	  try {
            		  writer.addDocument(getDocument(file));
            	  }
            	  catch(Exception e){}
        	  }
    	  }
    	  directoryIndexedSize+=file.getTotalSpace();
		  if((directoryIndexedSize/directoryToBeIndexedSize)*100>=10*percentage){
			  writer.commit();
			  percentage++;
		  }
      }
    }
  }
	
	static protected Document getDocument(File f) throws Exception {
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
		InputStream is = new FileInputStream(f);
		Parser parser;
		TikaConfig config = TikaConfig.getDefaultConfig();
		//String txt=ParseUtils.getStringContent(f, config);
		String fileName=f.getName();
		System.out.println(fileName);
		parser = new AutoDetectParser(config);
		ContentHandler handler = new BodyContentHandler();
		ParseContext context = new ParseContext();
		context.set(Parser.class, parser);
		try {
			parser.parse(is, handler, metadata,context);
		}
		catch(Exception e){System.out.println(e.getMessage());}
		finally {
			is.close();
		}
		Document doc = new Document();
		doc.add(new Field("contents", handler.toString().toLowerCase(),Field.Store.NO, Field.Index.ANALYZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
		String fileType=new String();
		for(String name : metadata.names()) {
			String value = metadata.get(name);
			value=value.toLowerCase();
			if (textualMetadataFields.contains(name)) {
				if(name.equalsIgnoreCase("resourceName")==true){
					int pos=value.lastIndexOf(".");
					fileType=value.substring(pos+1);
					value=value.substring(0, pos);
					doc.add(new Field("fileType", fileType,Field.Store.YES, Field.Index.ANALYZED));
				}
				doc.add(new Field("contents", value,Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field(name, value,Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
			else{
				doc.add(new Field(name, value,Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		}
		String path=f.getCanonicalPath();
		if(fileType.equalsIgnoreCase("srt")){
			int pos1=path.lastIndexOf("\\");
			int pos2=path.lastIndexOf(".");
			File file=new File(path.substring(0,pos1));
			String curFileName=path.substring(pos1+1,pos2);
			String[] list=file.list();
			for(int i=0;i<list.length;i++){
				if(list[i].contains(curFileName)){
					path=file.getCanonicalPath()+"\\"+list[i];
					break;
				}
			}
		}
		doc.add(new Field("path",path ,Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("lastModified",Long.toString(f.lastModified()),Field.Store.YES,Field.Index.NOT_ANALYZED));
		return doc;
	}
  
  public static boolean flag=false;
  public static IndexWriter writer;
  static Analyzer analyzer;
  static long percentage=1;
  static long directoryToBeIndexedSize;
  static long directoryIndexedSize=0;
  private static FSDirectory fsdDirIndex;
}