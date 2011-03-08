package dsePackage;
import java.io.File;

public class IndexGUI extends javax.swing.JFrame {

    public IndexGUI() {
        initComponents();
    }
    
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();
        textFieldIndex = new javax.swing.JTextField();
        buttonBrowse = new javax.swing.JButton();
        buttonExit = new javax.swing.JButton();
        buttonIndex = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaResult = new javax.swing.JTextArea();

        fileChooser.setDialogTitle("Select Directory");
        fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Index");

        jLabel1.setText("Index What");

        textFieldIndex.setEnabled(false);

        buttonBrowse.setText("Browse");
        buttonBrowse.setActionCommand("Browse");
        buttonBrowse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonBrowseMouseClicked(evt);
            }
        });

        buttonExit.setText("Exit");
        buttonExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonExitMouseClicked(evt);
            }
        });

        buttonIndex.setText("Index");
        buttonIndex.setActionCommand("Index");
        buttonIndex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonIndexMouseClicked(evt);
            }
        });

        textAreaResult.setColumns(20);
        textAreaResult.setEditable(false);
        textAreaResult.setLineWrap(true);
        textAreaResult.setRows(5);
        jScrollPane1.setViewportView(textAreaResult);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(154, 154, 154)
                .add(buttonExit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .add(94, 94, 94))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(textFieldIndex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(buttonBrowse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(buttonIndex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(textFieldIndex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(buttonBrowse)
                    .add(buttonIndex))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonExit)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
    }

    private void buttonBrowseMouseClicked(java.awt.event.MouseEvent evt) {                                      
        int chk=fileChooser.showOpenDialog(this);  
        if(chk==javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File f;
            f=fileChooser.getSelectedFile();
            textFieldIndex.setText(f.getPath());
        }
    }                                     

    private void buttonExitMouseClicked(java.awt.event.MouseEvent evt) {                                      
        System.exit(0);        
    }                                     

    private void buttonIndexMouseClicked(java.awt.event.MouseEvent evt) {
    	textAreaResult.setText("");
    	if(textFieldIndex.getText().length()!=0)
    		new IndexFiles().index_files(textFieldIndex.getText(),textAreaResult);
    	else
    		textAreaResult.insert("\nPlease select the directory/file to be indexed", textAreaResult.getCaretPosition());
    }
    
    private javax.swing.JButton buttonBrowse;
    private javax.swing.JButton buttonExit;
    private javax.swing.JButton buttonIndex;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textAreaResult;
    private javax.swing.JTextField textFieldIndex;
}
