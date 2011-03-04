package dsePackage;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyTermEnum;
import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.filechooser.FileSystemView;
//import org.apache.pdfbox.pdfviewer.PageDrawer;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import sun.awt.shell.*;

public class SearchFiles {
    private int hitsPerPage = 10;
	private Query query;
	private int start,end,numTotalHits;
	private ScoreDoc[] hits;
	private TopScoreDocCollector collector;
	private Searcher searcher;
	private IndexReader reader;
	
    private static class OneNormsReader extends FilterIndexReader {
    private String field;

    public OneNormsReader(IndexReader in, String field) {
      super(in);
      this.field = field;
    }

    @Override
    public byte[] norms(String field) throws IOException {
      return in.norms(this.field);
    }
  }

  SearchFiles() {}
  
  public void closeReader()throws Exception
  {
	  reader.close();
  }

  public void search_files(String key,JTextArea textAreaResult,JTextField textFieldPage,DefaultListModel result) throws Exception {
    String index = "index";
    File file=new File(index);
    file.setReadOnly();
    String field = "contents";
    
    try{
    reader = IndexReader.open(FSDirectory.open(file), true);
    }catch(Exception e){textAreaResult.append(e.getMessage());return;}
    searcher = new IndexSearcher(reader);
    
    if (key == null || key.length() == -1)
        return;

     key = key.trim();
       if (key.length() == 0)
         return;
       
    if(key.contains(":")){
    	int position;
    	position=key.indexOf(":");
    	field=key.substring(0,position);
    	key=key.substring(position+1);
    }
    
    int flag=0;
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
    QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, field, analyzer);
    textAreaResult.append("Searching for: " + key);
    
    if(key.contains(" ")){
    	PhraseQuery phraseQuery=new PhraseQuery();
    	int start=0,end;
    	String key1=key;
    	end=key1.indexOf(" ");
    	do{
    		phraseQuery.add(new Term(field,key1.substring(start, end).toLowerCase()));
    		start=end+1;
    		key1=key1.substring(start);
    		end=key1.indexOf(" ");
    		start=0;
    	}while(end!=-1);
    	phraseQuery.add(new Term(field,key1.toLowerCase()));
    	query=phraseQuery;
    	flag=doPagingSearch(textAreaResult,textFieldPage,result);
    }
   
    if(flag==0){
        query = parser.parse(key);
        flag=doPagingSearch(textAreaResult,textFieldPage,result);
    }
    
    if(flag==0){
    	String key1=key;
    	key1=key1.concat("*");
    	key1="*".concat(key1);
    	query = new WildcardQuery(new Term(field,key1));
    	flag=doPagingSearch(textAreaResult,textFieldPage,result);
    }
    
    if(flag==0){
    	Term term=new Term(field,key);
    	FuzzyTermEnum fuzzyTermEnum=new FuzzyTermEnum(reader,term);
    	
    	if(fuzzyTermEnum.term()!=null){
    		String newKey=fuzzyTermEnum.term().text();
    		query=parser.parse(newKey);
        	flag=doPagingSearch(textAreaResult,textFieldPage,result);
        	if(flag!=0){
        		textAreaResult.append("\nNo matching documents found for query : \""+key+"\"");
            	textAreaResult.append("\nDid you mean : \""+newKey+"\"");
            	textAreaResult.append("\n" + numTotalHits + " total matching documents");
        	}
        	else{
        		textAreaResult.append("\nNo matching documents found for query : \""+key+"\"");
        	}
    	}
    	else{
    		textAreaResult.append("\nNo matching documents found for query : \""+key+"\"");
    	}
    	fuzzyTermEnum.close();
    }
    else{
    	textAreaResult.append("\n" + numTotalHits + " total matching documents");
    }
  }

 public void newPage(JTextArea textAreaResult,int opt,JTextField textFieldPage,DefaultListModel result) throws IOException {
	 if (opt == -1) {
         start = Math.max(0, start - hitsPerPage);
       } else if (opt == 1) {
         if (start + hitsPerPage < numTotalHits) {
           start+=hitsPerPage;
         }
       }
	 end = Math.min(numTotalHits, start + hitsPerPage);
	 if (end > hits.length) {
       	collector = TopScoreDocCollector.create(numTotalHits, false);
       	searcher.search(query, collector);
     	hits = collector.topDocs().scoreDocs;
     }
	 end = Math.min(hits.length, start + hitsPerPage);
	 textFieldPage.setText("Showing Page : "+(start/10+1));
	 for (int i = start; i < end; i++) {
	        Document doc = searcher.doc(hits[i].doc);
	        String path = doc.get("path");
	        if (path != null) {
	        	String fileType=doc.get("Content-Type");
	        	ImageIcon icon=new ImageIcon();
	        	Image img;
	        	if(fileType.contains("image")){
	        		icon=new ImageIcon(path);
		        	img=icon.getImage();
		        	img = img.getScaledInstance(30,30,java.awt.Image.SCALE_SMOOTH);  
		        	icon=new ImageIcon(img);
	        	}
	        	/*else if(fileType.equalsIgnoreCase("pdf")){
	        		PageDrawer pageDrawer = new PageDrawer();
	        		PDDocument document=PDDocument.load(path);
	        		PDPage page=pageDrawer.getPage();
	        		img=page.convertToImage();
	        	}*/
	        	else{
	        		fileType=path.substring(path.lastIndexOf("."));
	        		File file=File.createTempFile("tempFile",fileType);
	        		FileSystemView view = FileSystemView.getFileSystemView();      
	        		icon = (ImageIcon)view.getSystemIcon(file);
	        		img=icon.getImage();
		        	img = img.getScaledInstance(30,30,java.awt.Image.SCALE_SMOOTH);  
		        	icon=new ImageIcon(img);
	        		file.delete();
	        	}
	        	JLabel label=new JLabel(path,icon,JLabel.LEFT);
	        	JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        	panel.add(label);
	        	result.addElement(panel);
	        } else {
	          textAreaResult.append("\n" + (i+1) + ". " + "No path for this document");
	        }           
	      if (numTotalHits >= end) {
	          if (start - hitsPerPage >= 0) {
	              SearchGUI.buttonPrevious.setEnabled(true);
	          }
	          if (start + hitsPerPage < numTotalHits) {
	        	  SearchGUI.buttonNext.setEnabled(true);
	          }
	      }     
	    }
 }

  public int doPagingSearch(JTextArea textAreaResult,JTextField textFieldPage,DefaultListModel result) throws IOException {
    collector = TopScoreDocCollector.create(5 * hitsPerPage, false);
    searcher.search(query, collector);
    
    numTotalHits = collector.getTotalHits();
    if(numTotalHits>0){
    	hits = collector.topDocs().scoreDocs;
    	start = 0;
        end = Math.min(numTotalHits, hitsPerPage);
        newPage(textAreaResult,0,textFieldPage,result);
    }
    return(numTotalHits);
  }
}