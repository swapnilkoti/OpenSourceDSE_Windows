package dsePackage;

import java.io.*;
import java.awt.Desktop;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;

public class SearchGUI extends javax.swing.JFrame {
    
    public SearchGUI() {
        initComponents();
    }
    
    private void initComponents() {
    	renderer=new MyListRenderer();
    	result=new DefaultListModel();
        jLabel1 = new javax.swing.JLabel();
        button=new javax.swing.JButton();
        textFieldPage=new javax.swing.JTextField();
        textFieldSearch = new javax.swing.JTextField();
        buttonExit = new javax.swing.JButton();
        buttonSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaResult = new javax.swing.JTextArea();
        buttonPrevious = new javax.swing.JButton();
        buttonNext = new javax.swing.JButton();
        listResult=new javax.swing.JList(result);
        listResult.setCellRenderer(renderer);
        S1=new SearchFiles();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Search");

        jLabel1.setText("Search What:");

        textFieldSearch.setEnabled(true);

        buttonExit.setText("Exit");
        buttonExit.setActionCommand("Exit");
        buttonExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	try{buttonExitMouseClicked(evt);}
                catch(Exception e){}
            }
        });

        buttonSearch.setText("Search");
        buttonSearch.setActionCommand("Search");
        buttonSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try{buttonSearchMouseClicked(evt);}
                catch(Exception e){System.out.println(e.getMessage());}
            }
        });
        
        buttonPrevious.setText("Previous");
        buttonPrevious.setActionCommand("Previous");
        buttonPrevious.setEnabled(false);
        buttonPrevious.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try{buttonPreviousMouseClicked(evt);}
                catch(Exception e){};
            }
        });

        buttonNext.setText("Next");
        buttonNext.setActionCommand("Next");
        buttonNext.setEnabled(false);
        buttonNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try{buttonNextMouseClicked(evt);}
                catch(Exception e){};
                }
            });

        textFieldPage.setEditable(false);
        textAreaResult.setColumns(20);
        textAreaResult.setRows(5);
        jScrollPane2.setViewportView(textAreaResult);
        
        listResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try{listResultMouseClicked(evt);}
                catch(Exception e){}
            }
        });
        listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(listResult);
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(textFieldSearch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, buttonSearch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, buttonPrevious, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(buttonNext, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                    .add(buttonExit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)))))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .add(textFieldPage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(textFieldSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(buttonSearch)
                    .add(buttonExit))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(buttonPrevious)
                    .add(buttonNext))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, Short.MAX_VALUE)
                .add(textFieldPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }

    private void buttonExitMouseClicked(java.awt.event.MouseEvent evt)throws Exception {                                      
        try{S1.closeReader();}
        catch(Exception e){}
    	System.exit(0);
    }              
    
    private void buttonSearchMouseClicked(java.awt.event.MouseEvent evt)throws Exception {                                      
    	String key=new String();
    	buttonPrevious.setEnabled(false);
    	buttonNext.setEnabled(false);
    	textAreaResult.setText("");
    	textAreaResult.setCaretPosition(0);
        key=textFieldSearch.getText();
        result.removeAllElements();
        if(key.length()!=0){
        	S1.search_files(key,textAreaResult,textFieldPage,result);
        }
    }                                     

    private void buttonPreviousMouseClicked(java.awt.event.MouseEvent evt)throws Exception {
    	buttonPrevious.setEnabled(false);
    	buttonNext.setEnabled(false);
    	result.removeAllElements();
    	S1.newPage(textAreaResult,-1,textFieldPage,result);
    }

   private void listResultMouseClicked(java.awt.event.MouseEvent evt)throws Exception {
    	try{
    		if(evt.getClickCount()==2){
    			JPanel panelFileName;
    			panelFileName=(JPanel)result.elementAt(listResult.getSelectedIndex());
    			JLabel labelFileName;
    			labelFileName=(JLabel)panelFileName.getComponent(0);
    			String stringFileName;
    			stringFileName=labelFileName.getText();
    			File fileName=new File(stringFileName);
    			Desktop appDesktop=null;
    			appDesktop=Desktop.getDesktop();
    			appDesktop.open(fileName);
    		}
    	}
    	catch(Exception e){System.out.println(e.getMessage());}
    }
    
    private void buttonNextMouseClicked(java.awt.event.MouseEvent evt)throws Exception {
    	try{
    		buttonPrevious.setEnabled(false);
        	buttonNext.setEnabled(false);
        	result.removeAllElements();
        	S1.newPage(textAreaResult,1,textFieldPage,result);
    	}
    	catch(Exception e){textAreaResult.append("\nError");}
    }
    private JButton buttonExit;
    private JButton buttonSearch;
    public static JButton buttonPrevious;
    public static JButton buttonNext;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextArea textAreaResult;
    private JTextField textFieldSearch,textFieldPage;
    private JList listResult;
    private SearchFiles S1;
    private DefaultListModel result;
    private MyListRenderer renderer;
    public static javax.swing.JButton button; 
}
