package de.hhu.cs.dbs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.util.FileManager;

import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.interfaces.constants.ButtonConstants;
import de.hhu.cs.dbs.interfaces.constants.CommandConstants;
import de.hhu.cs.dbs.tasks.ExportDataSelectedTask;
import de.hhu.cs.dbs.tasks.ExportDataTask;
import de.hhu.cs.dbs.tasks.ExportSQLTask;
import de.hhu.cs.dbs.tasks.ExportSchemaTask;
import de.hhu.cs.dbs.tasks.ImportDataSelectedTask;
import de.hhu.cs.dbs.tasks.ImportDataTask;
import de.hhu.cs.dbs.tasks.ImportSchemaTask;

public class RelationalOWL extends JPanel 
							implements ActionListener, TreeSelectionListener, CommandConstants, ButtonConstants  {
	
	private static final String CONNECTION_PROPERTIES_PATH = "connections.properties";
	private static final int PROGRESS_BAR_SIZE = 10000;
	private static final int ONE_SECOND = 1000;
	//text fields
	private JTextField textJDBCDriver;
	private JTextField textDBUrl;
	private JTextField textDBUser;
	private JPasswordField textDBPassword;
	private JTextField textDB;
	private JTextField textExportDataPath;
	private JTextField textExportSchemaPath;
	private JTextField textExportSchemaName;
	private JTextField textImportDataPath;
	private JTextField textImportSchemaPath;
	private JTextField textConnectionName;
	//labels
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JLabel label11;
	private JLabel label12;
	private JLabel label13;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
	private JLabel label17;
	private JLabel label18;
	private JLabel lblExportSQL;
	
	//buttons
	private JButton btSchemaImportPath;
	private JButton btDataImportPath;
	private JButton btSchemaExportPath;
	private JButton btDataExportPath;
	private JButton btImportSchema;
	private JButton btImportDataAll;
	private JButton btImportDataSelected;
	private JButton btLoadSchema;
	private JButton btExportSchema;
	private JButton btExportDataAll;
	private JButton btExportDataSelected;
	private JButton btSaveSettings;
	private JButton btReloadConnection;
	private JButton btExportSQL;
	private JButton btAddConnection;
	private JButton btDeleteConnection;
	
	//panels
	private JPanel panelImport;
	private JPanel panelExport;
	private JPanel panelConfig;
	private JPanel panelConsole;
	
	private JFileChooser fc;
	private JTree treeExport;
	private JTree treeImport;
	private DefaultTreeModel exportTreeModel;
	private DefaultTreeModel importTreeModel;
	private JTextArea textArea;
	private JComboBox connectionList;
	private DefaultMutableTreeNode topImport;
	private DefaultMutableTreeNode topExport;
	private CheckTreeManager exportTreeManager;
	private CheckTreeManager importTreeManager;
	private PersistentOntology persistentOntology;
	private RelationalOWLProperties props;

	// progress bar
	private JProgressBar pbImport;
    private JProgressBar pbExport;
	private Timer timerES;
	private Timer timerED;
	private Timer timerEDS;
	private Timer timerESQL;
	private Timer timerIS;
	private Timer timerID;
	private Timer timerIDS;
    private ExportSchemaTask taskES;
    private ExportDataTask taskED;
    private ExportDataSelectedTask taskEDS;
    private ExportSQLTask taskESQL;
    private ImportSchemaTask taskIS;
    private ImportDataTask taskID;
    private ImportDataSelectedTask taskIDS;
    
    DbManager dbManager;
    String exportSchemaPath;
    String exportDataPath;
    String importSchemaPath;
    String importDataPath;
    String exportSQL;
    String schema;
    
	public RelationalOWL(){
		super (new BorderLayout());
		fc = new JFileChooser();
		props = new RelationalOWLProperties(CONNECTION_PROPERTIES_PATH);
		try {
			persistentOntology = new PersistentOntology(props);
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}

		// config panel text fields
		label8 = new JLabel("JDBC-Connections");
		label9 = new JLabel("JDBC Driver");
		textJDBCDriver = new JTextField(40);
		textJDBCDriver.addActionListener(this);
		label10 = new JLabel("Database URL");
		textDBUrl = new JTextField(40);
		textDBUrl.addActionListener(this);
		label11 = new JLabel("Userid");
		textDBUser = new JTextField(40);
		textDBUser.addActionListener(this);
		textDBUser.setText("root");
		label12 = new JLabel("Passwort");
		textDBPassword = new JPasswordField(40);
		textDBPassword.addActionListener(this);
		label13 = new JLabel("Database");
		textDB = new JTextField(40);
		textDB.addActionListener(this);
		// import panel text fields
		label14 = new JLabel("Import path schema:");
		textImportSchemaPath = new JTextField(30);
		textImportSchemaPath.addActionListener(this);
		textImportSchemaPath.setText("");
		label15 = new JLabel("Import path data:");
		textImportDataPath = new JTextField(30);
		textImportDataPath.addActionListener(this);
		textImportDataPath.setText("");
		// export panel text fields
		label16 = new JLabel("Schema to export:");
		textExportSchemaName = new JTextField(20);
		textExportSchemaName.addActionListener(this);
		textExportSchemaName.setText("");
		label17 = new JLabel("Export path schema:");
		textExportSchemaPath = new JTextField(30);
		textExportSchemaPath.addActionListener(this);
		textExportSchemaPath.setText("");
		label18 = new JLabel("Export path data:");
		textExportDataPath = new JTextField(30);
		textExportDataPath.addActionListener(this);
		textExportDataPath.setText("");
		textArea = new JTextArea(5, 45);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		lblExportSQL = new JLabel("Export SQL:");
		
		// progress bar export tab
		pbExport = new JProgressBar(0, PROGRESS_BAR_SIZE);
        pbExport.setValue(0);
        pbExport.setStringPainted(true);
        pbExport.setPreferredSize(new Dimension(500, 20));
        pbExport.setVisible(false);
        // progress bar import tab
        pbImport = new JProgressBar(0, PROGRESS_BAR_SIZE);
        pbImport.setValue(0);
        pbImport.setStringPainted(true);
        pbImport.setPreferredSize(new Dimension(500, 20));
        pbImport.setVisible(false);
        
        timers();
		
        // panels
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = makeImportPanel();
		JComponent panel2 = makeExportPanel();
		JComponent panel3 = makeConfigPanel();
		JComponent panel4 = makeConsolePanel();
        tabbedPane.addTab("Import", panel1);
		tabbedPane.addTab("Export", panel2);
		tabbedPane.addTab("Config", panel3);
		//tabbedPane.addTab("Console", panel4);

		// Add the tabbed pane to this panel.
        add(tabbedPane);
		// set connections data
		setConnectionsList();
		selectActiveConnection();
		
	}
	
	private void timers(){
		
		// export schema
        timerES = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pbExport.setValue(taskES.getCurrent());
                if (taskES.isDone()) {
                    timerES.stop();
                    btExportSchema.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    
                    OntModel schemaOntology = taskES.getSchemaOntology();
                    OutputStream output;
					try {
						output = new FileOutputStream(exportSchemaPath);
						RDFWriter utf8Writer = schemaOntology.getWriter("RDF/XML");
	        			utf8Writer.setProperty("allowBadURIs","true");
	        			utf8Writer.setProperty("relativeURIs","same-document,relative");
	        			utf8Writer.write(schemaOntology, output, "");
	        			output.close();
					} catch (FileNotFoundException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} 
					
					// progress bar minimum
					pbExport.setVisible(false);
					pbExport.setValue(pbExport.getMinimum());
                }
            }
        });
        
        // export data
        timerED = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pbExport.setValue(taskED.getCurrent());
                if (taskED.isDone()) {
                	timerED.stop();
                	btExportDataAll.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    
                    OntModel dataOntology = taskED.getDataOntology();
                    OutputStream output;
					try {
						output = new FileOutputStream(exportDataPath); 
						Writer isoWriter =  new BufferedWriter( new OutputStreamWriter( output, "ISO-8859-1" ));
						dataOntology.write(isoWriter, "RDF/XML", "");
						output.close();
					} catch (FileNotFoundException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					}
					// progress bar minimum
					pbExport.setVisible(false);
					pbExport.setValue(pbExport.getMinimum());
                    
                }
            }
        });
        
        // export data selected
        timerEDS = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pbExport.setValue(new Double(taskEDS.getCurrent()).intValue());
                if (taskEDS.isDone()) {
                	timerEDS.stop();
                	btExportDataSelected.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    
                    try {
						OntModel dataOntology = taskEDS.getDataOntology();
						OutputStream output = new FileOutputStream(exportDataPath); 
						Writer isoWriter =  new BufferedWriter( new OutputStreamWriter( output, "ISO-8859-1" ));
						dataOntology.write(isoWriter, "RDF/XML", "");
						output.close();
					} catch (FileNotFoundException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					}
					// progress bar minimum
					pbExport.setVisible(false);
					pbExport.setValue(pbExport.getMinimum());
                    
                }
            }
        });
		
        // export sql
        timerESQL = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pbExport.setValue(new Double(taskESQL.getCurrent()).intValue());
                if (taskESQL.isDone()) {
                	timerESQL.stop();
                	btExportSQL.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    
                    try {
                    	OntModel schemaOntology = taskESQL.getSchemaOntology();
						OutputStream output = new FileOutputStream(exportSchemaPath); 
            		    RDFWriter utf8Writer = schemaOntology.getWriter("RDF/XML");
            			utf8Writer.setProperty("allowBadURIs","true");
            			utf8Writer.setProperty("relativeURIs","same-document,relative");
            			utf8Writer.write(schemaOntology, output, "");
            			output.close();
            			OntModel dataOntology = taskESQL.getDataOntology();
						output = new FileOutputStream(exportDataPath); 
            		    Writer isoWriter = new BufferedWriter( new OutputStreamWriter( output, "ISO-8859-1" ));
            		    dataOntology.write(isoWriter, "RDF/XML", "");
            		    output.close();
            		} catch (FileNotFoundException e) {
            			showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						showExceptionDialog(e.getMessage());
						e.printStackTrace();
					}
					// progress bar minimum
					pbExport.setVisible(false);
					pbExport.setValue(pbExport.getMinimum());
                    
                }
            }
        });
        
        // import schema
        timerIS = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pbImport.setValue(taskIS.getCurrent());
                if (taskIS.isDone()) {
                	timerIS.stop();
                	btImportSchema.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    // progress bar minimum
                    pbImport.setVisible(false);
                    pbImport.setValue(pbImport.getMinimum());
                }
            }
        });
        
        // import data
        timerID = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	pbImport.setValue(taskID.getCurrent());
                if (taskID.isDone()) {
                	timerID.stop();
                	btImportDataAll.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    // progress bar minimum
                    pbImport.setVisible(false);
                    pbImport.setValue(pbImport.getMinimum());
                }
            }
        });
        
        // import data selected
        timerIDS = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	pbImport.setValue(new Double(taskIDS.getCurrent()).intValue());
                if (taskIDS.isDone()) {
                	timerIDS.stop();
                	btImportDataSelected.setEnabled(true);
                    setCursor(null); //turn off the wait cursor
                    // progress bar minimum
                    pbImport.setVisible(false);
                    pbImport.setValue(pbImport.getMinimum());
                }
            }
        });
	}
	
	protected JComponent makeImportPanel() {
        JPanel panelInputData = new JPanel();
		JPanel panelButtons = new JPanel();
		JPanel panelRight = new JPanel();
		panelButtons.setLayout(new FlowLayout());
		panelRight.setLayout(new BorderLayout());
		panelInputData.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// buttons and fields
		btImportSchema = new JButton(TXT_IMPORT_SCHEMA);
		btImportSchema.setActionCommand(CMD_IMPORT_SCHEMA);
		btImportSchema.addActionListener(this);
		btImportDataSelected = new JButton(TXT_IMPORT_DATA_SELECTED);
		btImportDataSelected.setActionCommand(CMD_IMPORT_DATA_SELECTED);
		btImportDataSelected.addActionListener(this);
		btImportDataAll = new JButton(TXT_IMPORT_DATA_ALL);
		btImportDataAll.setActionCommand(CMD_IMPORT_DATA_ALL);
		btImportDataAll.addActionListener(this);
		btLoadSchema = new JButton(TXT_LOAD_SCHEMA);
		btLoadSchema.setActionCommand(CMD_LOAD_SCHEMA);
		btLoadSchema.addActionListener(this);
		btSchemaImportPath = new JButton(TXT_IMPORT_SCHEMA_PATH);
		btSchemaImportPath.setActionCommand(CMD_GET_IMPORT_SCHEMA_PATH);
		btSchemaImportPath.addActionListener(this);
		btDataImportPath = new JButton(TXT_IMPORT_DATA_PATH);
		btDataImportPath.setActionCommand(CMD_GET_IMPORT_DATA_PATH);
		btDataImportPath.addActionListener(this);
		
		// Spacing between label and field
		EmptyBorder border = new EmptyBorder( new Insets( 0, 0, 0, 10 ) );
		
		// add space around all components to avoid clutter
		c.insets = new Insets( 2, 2, 2, 2 );
		
		// anchor all components WEST
		c.anchor = GridBagConstraints.WEST;
		
		// import schema path
		c.gridx = 0;
		c.gridy = 0;
		panelInputData.add(new JLabel(" "), c); // this was a need to get button pannel center
		c.gridx = 1;
		c.gridy = 1;
		label14.setBorder(border);
		panelInputData.add(label14, c); // add some space to the right
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.0; // use all available horizontal space
		c.gridwidth = 2; // spans across 3 columns
		c.fill = GridBagConstraints.HORIZONTAL; // fills the 3 columns
		panelInputData.add( textImportSchemaPath, c );
		c.gridx = 4;
		c.gridy = 1;
		c.weightx = 0.0; // do not use any extra horizontal space
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		panelInputData.add( btSchemaImportPath, c );
		
		// import data path
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.0; // do not use any extra horizontal space
		panelInputData.add(label15, c);
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.0; // use all available horizontal space
		c.gridwidth = 2; // spans across 2 columns
		c.fill = GridBagConstraints.HORIZONTAL; // fills the 3 columns
		panelInputData.add( textImportDataPath, c );
		c.gridx = 4;
		c.gridy = 2;
		c.weightx = 0.0; // do not use any extra horizontal space
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		panelInputData.add( btDataImportPath, c );
		
		// button panel
        panelButtons.setLayout(new FlowLayout());
		panelButtons.add(btLoadSchema);
		panelButtons.add(btImportSchema);
		panelButtons.add(btImportDataAll);
		panelButtons.add(btImportDataSelected);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 5;
		c.weightx = 0.0; // do not use all available horizontal space
		panelInputData.add( panelButtons, c);
		// progress bar  
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 4;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.BOTH;
		JPanel panelProgress = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelProgress.add(pbImport, c);
		// main panel
		panelRight.add(panelInputData, BorderLayout.NORTH);
		panelRight.add(panelProgress, BorderLayout.SOUTH);
		
		//Create the nodes.
		topImport = new DefaultMutableTreeNode(getProperties().getConnectionName());
		importTreeModel = new DefaultTreeModel(topImport);
		//tree view
		treeImport = new JTree(importTreeModel);
		treeImport.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		//makes the tree as CheckTree
		importTreeManager = new CheckTreeManager(treeImport); 
		
		//Listen for when the selection changes.
		treeImport.addTreeSelectionListener(this);
		//Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(treeImport);
        
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(250);
        splitPane.setLeftComponent(treeView);
        splitPane.setRightComponent(panelRight);
		
        return splitPane;
    }
	
	protected JComponent makeExportPanel() {
		JPanel panelInputData = new JPanel();
		JPanel panelButtons = new JPanel();
		JPanel panelRight = new JPanel();
		panelRight.setLayout(new BorderLayout());
		panelInputData.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		btExportSchema = new JButton(TXT_EXPORT_SCHEMA);
		btExportSchema.setActionCommand(CMD_EXPORT_SCHEMA);
		btExportSchema.addActionListener(this);
		btExportDataAll = new JButton(TXT_EXPORT_DATA_ALL);
		btExportDataAll.setActionCommand(CMD_EXPORT_DATA_ALL);
		btExportDataAll.addActionListener(this);
		btExportDataSelected = new JButton(TXT_EXPORT_DATA_SELECTED);
		btExportDataSelected.setActionCommand(CMD_EXPORT_DATA_SELECTED);
		btExportDataSelected.addActionListener(this);
		btSchemaExportPath = new JButton(TXT_EXPORT_SCHEMA_PATH);
		btSchemaExportPath.setActionCommand(CMD_GET_EXPORT_SCHEMA_PATH);
		btSchemaExportPath.addActionListener(this);
		btDataExportPath = new JButton(TXT_EXPORT_DATA_PATH);
		btDataExportPath.setActionCommand(CMD_GET_EXPORT_DATA_PATH);
		btDataExportPath.addActionListener(this);
		btExportSQL = new JButton(TXT_EXPORT_SQL);
		btExportSQL.setActionCommand(CMD_EXPORT_SQL);
		btExportSQL.addActionListener(this);
		
		// Spacing between label and field
		EmptyBorder border = new EmptyBorder( new Insets( 0, 0, 0, 10 ) );
		
		// add space around all components to avoid clutter
		c.insets = new Insets( 2, 2, 2, 2 );
		
		// anchor all components WEST
		c.anchor = GridBagConstraints.WEST;
		
		// export schema name
		c.gridx = 1;
		c.gridy = 0;
		panelInputData.add(new JLabel("         "), c); // this was a need to get button pannel center
		c.gridx = 1;
		c.gridy = 1;
		label14.setBorder(border);
		panelInputData.add(label16, c); // add some space to the right
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.0; // use all available horizontal space
		c.gridwidth = 1; // spans across 2 columns
		c.fill = GridBagConstraints.NONE;
		panelInputData.add( textExportSchemaName, c );
		
		// export schema path
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.0; // do not use any extra horizontal space
		panelInputData.add(label17, c);
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.0; // use all available horizontal space
		c.gridwidth = 2; // spans across 3 columns
		c.fill = GridBagConstraints.HORIZONTAL; // fills the 3 columns
		panelInputData.add( textExportSchemaPath, c );
		c.gridx = 4;
		c.gridy = 2;
		c.weightx = 0.0; // do not use any extra horizontal space
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		panelInputData.add( btSchemaExportPath, c );
		
		// export data path
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0.0; // do not use any extra horizontal space
		panelInputData.add(label18, c);
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 0.0; // use all available horizontal space
		c.gridwidth = 2; // spans across 3 columns
		c.fill = GridBagConstraints.HORIZONTAL; // fills the 3 columns
		panelInputData.add( textExportDataPath, c );
		c.gridx = 4;
		c.gridy = 3;
		c.weightx = 0.0; // do not use any extra horizontal space
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		panelInputData.add( btDataExportPath, c );
		
		// button panel
        panelButtons.setLayout(new FlowLayout());
        panelButtons.add(btExportSchema);
		panelButtons.add(btExportDataAll);
		panelButtons.add(btExportDataSelected);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 4;
		c.weightx = 0.0; // do not use all available horizontal space
		c.fill = GridBagConstraints.WEST;
		panelInputData.add( panelButtons, c);
		
		// export-sql text area
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		c.weightx = 0.0; // do not use all available horizontal space
		panelInputData.add(lblExportSQL, c);
		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 4;
		c.weightx = 0.0; // do not use all available horizontal space
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scroll = new JScrollPane( textArea );
		panelInputData.add( scroll, c);
		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 4;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		JPanel panelButtons2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelButtons2.add(btExportSQL, c);
		panelInputData.add(panelButtons2, c);
		// progress bar  
		c.gridx = 1;
		c.gridy = 8;
		c.gridwidth = 4;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.BOTH;
		JPanel panelProgress = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelProgress.add(pbExport, c);
		// main panel
		panelRight.add(panelInputData, BorderLayout.NORTH);
		panelRight.add(panelProgress, BorderLayout.SOUTH);
		
		//Create the nodes.
        topExport = new DefaultMutableTreeNode(getProperties().getConnectionName());
		exportTreeModel = new DefaultTreeModel(topExport);

		//tree view
		treeExport = new JTree(exportTreeModel);
		treeExport.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		//makes your tree as CheckTree
		exportTreeManager = new CheckTreeManager(treeExport); 
		
		//Listen for when the selection changes.
		treeExport.addTreeSelectionListener(this);
		//Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(treeExport);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(250);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(panelRight);

		return splitPane;
	}
	
	protected JComponent makeConfigPanel() {
		panelConfig = new JPanel();
		panelConfig.setLayout(new BorderLayout());
		JPanel panelInputData = new JPanel();
		JPanel panelButtons = new JPanel();
		panelInputData.setLayout(new GridBagLayout());
		panelButtons.setLayout(new FlowLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// connection name
		label7 = new JLabel("Connection name:");
		textConnectionName = new JTextField(31); 
		textConnectionName.addActionListener(this);
		// jdbc connections
		label8 = new JLabel("JDBC-Connections:");
		connectionList = new JComboBox();
		connectionList.addActionListener(this);
		// jdbc driver
		label9 = new JLabel("JDBC Driver: ");
		textJDBCDriver = new JTextField(31);
		textJDBCDriver.addActionListener(this);
		textJDBCDriver.setText(props.getJDBCDriver());
		// database url
		label10 = new JLabel("Database URL:");
		textDBUrl = new JTextField(31);
		textDBUrl.addActionListener(this);
		textDBUrl.setText(props.getDbUrl());
		// database user
		label11 = new JLabel("Userid:");
		textDBUser = new JTextField(31);
		textDBUser.addActionListener(this);
		textDBUser.setText(props.getDbUser());
		// password
		label12 = new JLabel("Password:");
		textDBPassword = new JPasswordField(31);
		textDBPassword.addActionListener(this);
		textDBPassword.setText(props.getDbPassword());
		// buttons
		btSaveSettings = new JButton(TXT_SAVE_SETTINGS);
		btSaveSettings.setActionCommand(CMD_SAVE_SETTINGS);
		btSaveSettings.addActionListener(this);
		btReloadConnection = new JButton(TXT_RELOAD_CONNECTION);
		btReloadConnection.setActionCommand(CMD_RELOAD_CONNECTION);
		btReloadConnection.addActionListener(this);
		btAddConnection = new JButton(TXT_ADD_CONNECTION);
		btAddConnection.setActionCommand(CMD_ADD_CONNECTION);
		btAddConnection.addActionListener(this);
		btDeleteConnection = new JButton(TXT_DELETE_CONNECTION);
		btDeleteConnection.setActionCommand(CMD_DELETE_CONNECTION);
		btDeleteConnection.addActionListener(this);
		
		// Spacing between label and field
		EmptyBorder border = new EmptyBorder( new Insets( 0, 0, 0, 10 ) );
		
		// add space around all components to avoid clutter
		c.insets = new Insets( 3, 3, 3, 3 );
		
		// anchor all components WEST
		c.anchor = GridBagConstraints.WEST;
		
		// connections combo
		c.gridx = 1;
		c.gridy = 0;
		panelInputData.add(new JLabel("         "), c); // spacer
		c.gridx = 1;
		c.gridy = 1;
		label8.setBorder(border);
		panelInputData.add(label8, c); // add some space to the right
		c.gridx = 2; 
		c.gridy = 1; 
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		panelInputData.add(connectionList, c); 
		// set at (1,2) 
		c.gridx = 1; 
		c.gridy = 2; 
		panelInputData.add(new JLabel("         "), c); // spacer); 
		// set at (1,3) 
		c.gridx = 1; 
		c.gridy = 3; 
		panelInputData.add(label7, c); 
		// set at (2,3) 
		c.gridx = 2; 
		c.gridy = 3; 
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.WEST;
		panelInputData.add(textConnectionName, c); 
		// set at (1,4) 
		c.gridx = 1; 
		c.gridy = 4; 
		panelInputData.add(label9, c); 
		// set at (2,4) 
		c.gridx = 2; 
		c.gridy = 4; 
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.WEST;
		panelInputData.add(textJDBCDriver, c); 
		// set at (1,5) 
		c.gridx = 1; 
		c.gridy = 5; 
		panelInputData.add(label10, c); 
		// set at (2,5) 
		c.gridx = 2; 
		c.gridy = 5; 
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.WEST;
		panelInputData.add(textDBUrl, c); 
		// set at (1,6) 
		c.gridx = 1; 
		c.gridy = 6; 
		panelInputData.add(label11, c); 
		// set at (2,6) 
		c.gridx = 2; 
		c.gridy = 6; 
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.WEST;
		panelInputData.add(textDBUser, c); 
		// set at (1,7) 
		c.gridx = 1; 
		c.gridy = 7; 
		panelInputData.add(label12, c); 
		// set at (2,7) 
		c.gridx = 2; 
		c.gridy = 7; 
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.WEST;
		panelInputData.add(textDBPassword, c); 
	    
		// panel buttons
		panelButtons.add(btReloadConnection);
		panelButtons.add(btSaveSettings);
		panelButtons.add(btAddConnection);
		panelButtons.add(btDeleteConnection);
		c.gridx = 1; 
		c.gridy = 8; 
		c.gridwidth = 7;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.WEST;
		panelInputData.add(panelButtons, c); 
	    
		panelConfig.add(panelInputData, BorderLayout.NORTH);
		return panelConfig;
	}
	
	protected JComponent makeConsolePanel(){
		panelConsole = new JPanel();
		panelConsole.setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(40,40);
		JScrollPane scrollPane = 
		    new JScrollPane(textArea,
		                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		textArea.setEditable(true);
		panelConsole.add(scrollPane, BorderLayout.NORTH);
		return panelConsole;
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("RelationalOWL");
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        GraphicsEnvironment env =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e){
        		new RelationalOWLProperties(CONNECTION_PROPERTIES_PATH).removeConnection("");
        		System.exit(0);
        	}
        });
        
        // application on full screen
        // do not cover the taskbar
        Dimension maxSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        Dimension maxSizeFrame = new Dimension(new Double(maxSize.getWidth()).intValue() - 1, new Double(maxSize.getHeight()).intValue() - 1);
        Dimension maxContentPane = new Dimension(new Double(maxSize.getWidth()).intValue() - 100, new Double(maxSize.getHeight()).intValue() - 100);
        frame.setMaximizedBounds(new Rectangle(maxSizeFrame));
        frame.getContentPane().setPreferredSize(maxContentPane);
        frame.setSize(maxSizeFrame);
        //frame.getContentPane().setSize(maxSize);
        
        // center the frame by normal state
        Dimension frameSize = frame.getSize ();
        frame.setLocation (Math.abs((maxContentPane .width - frameSize .width) / 2),
        		Math.abs((maxContentPane .height - frameSize .height ) / 2));
        
        //Create and set up the content pane.
        JComponent newContentPane = new RelationalOWL();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.getContentPane().add(new RelationalOWL(),
                                 BorderLayout.CENTER);
		
        //Display the window.
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

	private RelationalOWLProperties getProperties(){
		return new RelationalOWLProperties(CONNECTION_PROPERTIES_PATH);
	}
	
	private void checkExportSchema(String schema, String path) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Select schema name from which data should be exported.");
		if (path == null || path.length() == 0)
			throw new Exception("Schema path should be given.");	
	}
	
	private void checkExportDataAll(String schema, String schemaPath, String dataPath) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");	
		if (dataPath == null || dataPath.length() == 0)
			throw new Exception("Data path should be given.");	
	}
	
	private void checkExportDataSelected(String schema, String schemaPath, String name) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (name == null || name.length() == 0)
			throw new Exception("Table name have to be passed as well.");
	}
	
	private void checkExportSQL(String schema, String schemaPath, String sql, String path) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (sql == null || sql.length() == 0)
			throw new Exception("You have to pass SQL-Statement as well.");	
		if (path == null || path.length() == 0)
			throw new Exception("Data path should be given.");	
	}
	
	private void checkExportTable(String schema, String schemaPath, String name) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (name == null || name.length() == 0)
			throw new Exception("Table name have to be passed as well.");
	}
	
	private void checkExportSchemaSQL(String schemaPath, String sql) throws Exception{
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (sql == null || sql.length() == 0)
			throw new Exception("You have to pass SQL-Statement as well.");	
	}
	
	private void checkImportSchema(String path) throws Exception{
		if (path == null || path.length() == 0)
			throw new Exception("Schema path has to be given.");
	}
	
	private void checkImportData(String schemaPath, String dataPath) throws Exception{
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Schema path has to be given.");
		if (dataPath == null || dataPath.length() == 0)
			throw new Exception("Data path has to be given.");	
	}

	private void checkImportDataSelected(String schemaPath, String dataPath) throws Exception{
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Schema path has to be given.");
		if (dataPath == null || dataPath.length() == 0)
			throw new Exception("Data path has to be given.");	
	}
	
	private void saveProperties(String path, String connectionName){
		props.setJDBCDriver(connectionName, textJDBCDriver.getText());
		props.setDbUrl(connectionName, textDBUrl.getText());
		props.setDbUser(connectionName, textDBUser.getText());
		props.setDbPassword(connectionName, textDBPassword.getText());
		props.setConnectionName(connectionName, textConnectionName.getText());
		props.store();
	}
	
	private void saveProperties(String path, String connectionName, boolean saveConnectionName){
		props.setJDBCDriver(connectionName, textJDBCDriver.getText());
		props.setDbUrl(connectionName, textDBUrl.getText());
		props.setDbUser(connectionName, textDBUser.getText());
		props.setDbPassword(connectionName, textDBPassword.getText());
		if (saveConnectionName){
			props.setConnectionName(connectionName, textConnectionName.getText());
		}
		props.store();
	}
	
	private void addProperties(String path, String connectionName){
		props.addConnection(connectionName);
		props.setJDBCDriver(textJDBCDriver.getText());
		props.setDbUrl(textDBUrl.getText());
		props.setDbUser(textDBUser.getText());
		props.setDbPassword(textDBPassword.getText());
		props.store();
	}
	
	private void setConnectionsList(){
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(CONNECTION_PROPERTIES_PATH));
			Element root = doc.getRootElement();
			
			// sort jdbc-connections by name 
			Iterator iter = root.getChildren().iterator();
			ArrayList items = new ArrayList();
			while (iter.hasNext()){
				items.add(((Element)iter.next()).getAttribute("name").getValue().toString());
			}
			java.util.Collections.sort(items);
			
			iter = items.iterator();
			while (iter.hasNext()){
				connectionList.addItem(iter.next());
			}
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}	
	}
	
	private void selectActiveConnection(){
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(CONNECTION_PROPERTIES_PATH));
			Element root = doc.getRootElement();
			
			// set connections combobox to value acording property file
			Iterator iter = root.getChildren().iterator();
			while (iter.hasNext()){
				Element jdbcConnection = (Element)iter.next();
				if (jdbcConnection.getAttributeValue("checked").equals("true")){
					connectionList.setSelectedItem(jdbcConnection.getAttributeValue("name"));
					break;
				}
			}
		} catch (JDOMException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void setActiveConnection(String name){
		props.setActiveConnection(name);
		props.store();
	}
	
	private void setConnectionData(String connectionName){
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(CONNECTION_PROPERTIES_PATH));
			Element root = doc.getRootElement();
			Iterator iter = root.getChildren().iterator();
			while (iter.hasNext()){
				Element jdbcConnection = (Element)iter.next();
				if (jdbcConnection.getAttributeValue("name").equals(connectionName)){
					this.textConnectionName.setText(jdbcConnection.getAttributeValue("name"));
					this.textJDBCDriver.setText(jdbcConnection.getChild("jdbc-driver").getText());
					this.textDBUrl.setText(jdbcConnection.getChild("dbUrl").getText());
					this.textDBUser.setText(jdbcConnection.getChild("dbUser").getText());
					this.textDBPassword.setText(jdbcConnection.getChild("dbPassword").getText());
					break;
				}
			}
		} catch (JDOMException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private boolean dataComplete(){
		if ((textJDBCDriver.getText().length() > 0)
			&& (textDBUrl.getText().length() > 0)
			&& (textDBUser.getText().length() > 0)
			&& (textDBPassword.getText().length() > 0)
			&& (textConnectionName.getText().length() > 0)){
				return true;
		}
       	return false;	
	}
	
	protected void showExceptionDialog(String message){
		JOptionPane.showMessageDialog(
				this, message, "Exception message", JOptionPane.ERROR_MESSAGE);
	}
	
	private void exportSchema(String schema, String path){
		try {
			exportSchemaPath = path;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskES = new ExportSchemaTask(dbManager.getConnection(), props.getJDBCDriver(), schema);
			pbExport.setVisible(true);
			pbExport.setMaximum(taskES.getLengthOfTask());
			btExportSchema.setEnabled(false);
	        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	        taskES.go();
	        timerES.start();
	        
	        
			/*LastOK OntModel schemaOntology = persistentOntology.exportSchema(schema);
			java.io.OutputStream output = new java.io.FileOutputStream(path); 
			//java.io.Writer isoWriter =  new java.io.BufferedWriter( new java.io.OutputStreamWriter( output, "ISO-8859-1" ));
		    //schemaOntology.write(isoWriter, "RDF/XML", "");
			
			RDFWriter utf8Writer = schemaOntology.getWriter("RDF/XML");
			utf8Writer.setProperty("allowBadURIs","true");
			utf8Writer.setProperty("relativeURIs","same-document,relative");
			utf8Writer.write(schemaOntology, output, "");
			output.close();
			/*utf8Writer.setProperty("prettyTypes", new Resource[] {
			OWL.Ontology,
			OWL.imports,
			OWL.Class,
			OWL.ObjectProperty,
			OWL.DatatypeProperty,
			OWL.OntologyProperty,
			OWL.FunctionalProperty,
			OWL.TransitiveProperty,
			OWL.SymmetricProperty,
			OWL.InverseFunctionalProperty,
			OWL.DataRange,
			OWL.AnnotationProperty
			/* RDF.type,
			RDFS.domain,
			RDFS.range 
			});*/
		//} catch (FileNotFoundException e) {
		//	showExceptionDialog(e.getMessage());
		//	e.printStackTrace();
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void exportDataAll(String schema, String schemaPath, String path){
		try {
			exportDataPath = path;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskED = new ExportDataTask(dbManager.getConnection(), props.getJDBCDriver(), schema, schemaPath);
				   
			pbExport.setVisible(true);
			pbExport.setMaximum(taskED.getLengthOfTask());
			btExportDataAll.setEnabled(false);
	        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	        taskED.go();
	        timerED.start();
	        
			
			/*LastOK OntModel dataOntology = persistentOntology.exportDataAll(schema, schemaPath);
			java.io.OutputStream output = new java.io.FileOutputStream(path); 
		    java.io.Writer isoWriter =  new java.io.BufferedWriter( new java.io.OutputStreamWriter( output, "ISO-8859-1" ));
			dataOntology.write(isoWriter, "RDF/XML-ABBREV", "");
			output.close();
			
			/*RDFWriter utf8Writer = dataModel.getWriter("RDF/XML-ABBREV");
			utf8Writer.setProperty("allowBadURIs","true");
			utf8Writer.setProperty("relativeURIs","");
			utf8Writer.setProperty("showXmlDeclaration", "true");
			utf8Writer.write(dataModel, output, "");*/
		} catch (FileNotFoundException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void exportDataSelected(String schema, String schemaPath, String path) throws Exception{
		try {
			checkExportDataSelected(schema, schemaPath, path);
			ArrayList tablesSelected = new ArrayList();
			
			// go through selection model to get tables the data to export from 
			for (int i = 0; i < treeExport.getSelectionModel().getSelectionPaths().length; i++){
				String selected = ((TreePath)treeExport.getSelectionModel().getSelectionPaths()[i]).getPathComponent(1).toString().trim();
				if (selected.equals(schema)){
					if (((TreePath)treeExport.getSelectionModel().getSelectionPaths()[i]).getPathCount() > 2){
						String table = ((TreePath)treeExport.getSelectionModel().getSelectionPaths()[i]).getPathComponent(2).toString().trim();
						tablesSelected.add((String)table);
					}
				}
			}
			
			String[] tables = new String[tablesSelected.size()];
			for (int i = 0; i < tablesSelected.size(); i++) {
				tables[i] = (String)tablesSelected.get(i);
			}
			exportDataPath = path;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskEDS = new ExportDataSelectedTask(dbManager.getConnection(), props.getJDBCDriver(), schema, schemaPath, tables);
			pbExport.setVisible(true);
			pbExport.setMaximum(new Double(taskEDS.getLengthOfTask()).intValue());
			btExportDataSelected.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			taskEDS.go();
			timerEDS.start();
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void exportSQL(String schema, String schemaPath, String SQL, String path) throws Exception{
		try {
			checkExportSQL(schema, schemaPath, SQL, path);
			
			exportSchemaPath = schemaPath;
			exportDataPath = path;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskESQL = new ExportSQLTask(dbManager.getConnection(), props.getJDBCDriver(), schema, schemaPath, SQL);
			pbExport.setVisible(true);
			pbExport.setMaximum(new Double(taskESQL.getLengthOfTask()).intValue());
			btExportSQL.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			taskESQL.go();
			timerESQL.start();
   
			/*OntModel schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
			schemaOntology = persistentOntology.exportSchemaSQL(schemaPath, sql);
			OntModel dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
			dataOntology = persistentOntology.exportSQL(schema, schemaPath, sql);
			try {
				OutputStream output = new FileOutputStream(schemaPath); 
			    RDFWriter utf8Writer = schemaOntology.getWriter("RDF/XML");
				utf8Writer.setProperty("allowBadURIs","true");
				utf8Writer.setProperty("relativeURIs","same-document,relative");
				utf8Writer.write(schemaOntology, output, "");
				output.close();
				output = new FileOutputStream(path); 
			    Writer isoWriter = new BufferedWriter( new OutputStreamWriter( output, "ISO-8859-1" ));
			    dataOntology.write(isoWriter, "RDF/XML-ABBREV", "");
			    output.close();
			} catch (FileNotFoundException e) {
				showExceptionDialog(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				showExceptionDialog(e.getMessage());
				e.printStackTrace();
			}*/
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void importSchema(String path) throws Exception{
		try {
			importSchemaPath = path;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskIS = new ImportSchemaTask(dbManager.getConnection(), props.getJDBCDriver(), path);
			pbImport.setMaximum(taskIS.getLengthOfTask());
			pbImport.setVisible(true);
			btImportSchema.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			taskIS.go();
			timerIS.start();
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
    }
	
	private void importDataAll(String schemaPath, String dataPath) throws Exception{
		try {
			importSchemaPath = dataPath;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskID = new ImportDataTask(dbManager.getConnection(), props.getJDBCDriver(), schemaPath, dataPath);
			pbImport.setVisible(true);
			pbImport.setMaximum(taskID.getLengthOfTask());
			btImportDataAll.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			taskID.go();
			timerID.start();
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
    }
	
	private void importDataSelected(String schemaPath, String dataPath) throws Exception{
		try {
			ArrayList tablesSelected = new ArrayList();
		
			for (int i=0; i < treeImport.getSelectionModel().getSelectionPaths().length; i++){
				if (((TreePath)treeImport.getSelectionModel().getSelectionPaths()[i]).getPathCount() > 2){
					String table = ((TreePath)treeImport.getSelectionModel().getSelectionPaths()[i]).getPathComponent(2).toString();
					tablesSelected.add(table);
				}
			}
			
			String[] tables = new String[tablesSelected.size()];
			for (int i = 0; i < tablesSelected.size(); i++) {
				tables[i] = (String)tablesSelected.get(i);
			}
			
			exportSchemaPath = schemaPath;
			exportDataPath = dataPath;
			dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			taskIDS = new ImportDataSelectedTask(dbManager.getConnection(), props.getJDBCDriver(), schemaPath, dataPath, tables);
			pbImport.setVisible(true);
			pbImport.setMaximum(new Double(taskIDS.getLengthOfTask()).intValue());
			btImportDataSelected.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			taskIDS.go();
			timerIDS.start();
		} catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void saveSettings(){
		try {
			if (dataComplete()){
				String connectionName = null;
				if (props.getConnectionNames().contains(textConnectionName.getText())){
					saveProperties(CONNECTION_PROPERTIES_PATH, connectionList.getSelectedItem().toString(), false);
					connectionName = connectionList.getSelectedItem().toString();
					//showExceptionDialog("Connection with this name exists yet.");
				}
				else{
					saveProperties(CONNECTION_PROPERTIES_PATH, connectionList.getSelectedItem().toString(), true);	
					connectionName = textConnectionName.getText();
				}
				// actualize combo
				connectionList.setActionCommand("actualizeComboBox");
				connectionList.removeAllItems();
				setConnectionsList();
				// set combo at actualized connection name 
				connectionList.setActionCommand("comboBoxChanged");	
				connectionList.setSelectedItem(connectionName);
			}
			else {
				showExceptionDialog("Connection data not complete.");
			}
		} catch (RuntimeException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void valueChanged(TreeSelectionEvent e){
		try {
			//first check whether it is export pane
			if (treeExport.getSelectionPath() != null){
				if (treeExport.getSelectionPath().getPath().length > 1){
					textExportSchemaName.setText(treeExport.getSelectionPath().getPath()[1].toString());
				}
			}
		} catch (RuntimeException e1) {
			showExceptionDialog(e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	//	Handle action events from all the buttons.
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		//	import schema path
		if (e.getSource() == btSchemaImportPath) {
            int returnVal = fc.showOpenDialog(RelationalOWL.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				textImportSchemaPath.setText(fc.getSelectedFile().getPath());
			}
		}
		//	import data path
		if (e.getSource() == btDataImportPath) {
            int returnVal = fc.showOpenDialog(RelationalOWL.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				textImportDataPath.setText(fc.getSelectedFile().getPath());
			}
		}
		//	export schema path
		if (e.getSource() == btSchemaExportPath) {
            int returnVal = fc.showOpenDialog(RelationalOWL.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				textExportSchemaPath.setText(fc.getSelectedFile().getPath());
			}
		}
		//	export data path
		if (e.getSource() == btDataExportPath) {
            int returnVal = fc.showOpenDialog(RelationalOWL.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				textExportDataPath.setText(fc.getSelectedFile().getPath());
			}
		}
		
        //  schema operations
		if (CMD_IMPORT_SCHEMA.equals(command)) {
			try {
				checkImportSchema(textImportSchemaPath.getText());
				importSchema(textImportSchemaPath.getText());
			} catch (Exception e1) {
				e1.printStackTrace();
				showExceptionDialog(e1.getMessage());
			}
		}
		
		if (CMD_IMPORT_DATA_ALL.equals(command)) {
			try {
				checkImportData(textImportSchemaPath.getText(), textImportDataPath.getText());
				importDataAll(textImportSchemaPath.getText(), textImportDataPath.getText());
			} catch (Exception e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		
		if (CMD_IMPORT_DATA_SELECTED.equals(command)) {
			try {
				checkImportDataSelected(textImportSchemaPath.getText(), textImportDataPath.getText());
				importDataSelected(textImportSchemaPath.getText(), textImportDataPath.getText());
			} catch (Exception e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		
		if (CMD_LOAD_SCHEMA.equals(command)) {
			try {
				checkImportSchema(textImportSchemaPath.getText());
				createNodesOWL(topImport);
			} catch (Exception e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
			importTreeModel = new DefaultTreeModel(topImport);
			treeImport.setModel(importTreeModel);
			treeImport.repaint();
		}
		if (CMD_EXPORT_SCHEMA.equals(command)) {
			try {
				checkExportSchema(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim());
				exportSchema(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim());
			} catch (RuntimeException e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		if (CMD_EXPORT_DATA_ALL.equals(command)) {
            try {
				checkExportDataAll(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim(), this.textExportDataPath.getText().trim());
            	exportDataAll(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim(), this.textExportDataPath.getText().trim());
			} catch (RuntimeException e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		if (CMD_EXPORT_DATA_SELECTED.equals(command)) {
            try {
				exportDataSelected(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim(), this.textExportDataPath.getText().trim());
            	exportDataSelected(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim(), this.textExportDataPath.getText().trim());
			} catch (Exception e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			} 
        }
		if (CMD_EXPORT_SQL.equals(command)) {
			try {
				checkExportSQL(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim(), textArea.getText().trim(), this.textExportDataPath.getText().trim());
				exportSQL(textExportSchemaName.getText().trim(), this.textExportSchemaPath.getText().trim(), textArea.getText().trim(), this.textExportDataPath.getText().trim());
			} catch (Exception e1) {
				this.showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		if (CMD_RELOAD_CONNECTION.equals(command)) {
			topExport = new DefaultMutableTreeNode(textConnectionName.getText().trim());
			if (dataComplete()){
				saveSettings();
				try {
					persistentOntology = new PersistentOntology(props);
					createNodesDB(topExport, false);
				} catch (Exception e1) {
					this.showExceptionDialog(e1.getMessage());
					e1.printStackTrace();
					selectActiveConnection();
					return;
				}
				setActiveConnection(connectionList.getSelectedItem().toString());
				// actualize export tree
				exportTreeModel = new DefaultTreeModel(topExport);
				treeExport.setModel(new DefaultTreeModel(topExport));
				treeExport.repaint();
				// set empty schema to export in export panel
				textExportSchemaName.setText("");
				// actualize connection driver in import panel with no change in tree view
				DefaultMutableTreeNode newTopImport = new DefaultMutableTreeNode(textConnectionName.getText().trim());
				Enumeration treeIterator = ((DefaultMutableTreeNode)treeImport.getModel().getRoot()).children();
				while (treeIterator.hasMoreElements()){
					newTopImport.add((MutableTreeNode)treeIterator.nextElement());
				}
				treeImport.setModel(new DefaultTreeModel(newTopImport));
			}
		}
		if (CMD_SAVE_SETTINGS.equals(command)) {
			try {
				saveSettings();
			} catch (RuntimeException e1) {
				showExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		if (CMD_ADD_CONNECTION.equals(command)){
			String item = connectionList.getItemAt(connectionList.getItemCount() - 1).toString();
			// don't let to add two empty connections
			if (item.toString().length() > 0){
				connectionList.addItem("");
				connectionList.setActionCommand("actualizeComboBox");
				connectionList.setSelectedIndex(connectionList.getItemCount() - 1);
				connectionList.setActionCommand("comboBoxChanged");
				textConnectionName.setText("");
				textJDBCDriver.setText("");
				textDBUrl.setText("");
				textDBUser.setText("");
				textDBPassword.setText("");
				addProperties(CONNECTION_PROPERTIES_PATH, connectionList.getSelectedItem().toString());
			}
			else{
			}
		}
		if (CMD_DELETE_CONNECTION.equals(command)){
			if (!connectionList.getSelectedItem().toString().equals(props.getActiveConnection())){
				props.removeConnection(connectionList.getSelectedItem().toString());
				connectionList.setActionCommand("actualizeComboBox");
				connectionList.removeAllItems();
				setConnectionsList();
				// set combo at actualized connection name 
				connectionList.setActionCommand("comboBoxChanged");	
				connectionList.setSelectedItem(props.getActiveConnection());
				setConnectionData(connectionList.getSelectedItem().toString());
			}
			else{
				showExceptionDialog("Connection already used, cannot delete.");
			}
		}
		
		// combobox connectionList
		if (command.equals("comboBoxChanged")){
			String item = connectionList.getSelectedItem().toString();
			// when new connection choosed and the new one added has no
			// connection name defined
			// TODO extend configuration template with Model that will avoid this situation
			if (props.getConnectionNames().contains("")){
				props.removeConnection("");
			}
			
			// actualize combo
			connectionList.setActionCommand("actualizeComboBox");
			connectionList.removeAllItems();
			setConnectionsList();
			// set combo at actualized connection name 
			connectionList.setSelectedItem(item);
			connectionList.setActionCommand("comboBoxChanged");	
			setConnectionData(item);
		}
    }
	
	private void createNodesDB(DefaultMutableTreeNode top, boolean connectionFromConfig) throws Exception {
        DefaultMutableTreeNode databaseNode = null;
        DefaultMutableTreeNode tableNode = null;
		//DbManager dbManager = null;
		try {
			if (connectionFromConfig){
				dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
			} else{
				dbManager = DatabaseManagerFactory.getDbManagerInstance(textJDBCDriver.getText().trim(), textDBUrl.getText().trim(), textDBUser.getText().trim(), textDBPassword.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		top.removeAllChildren();
		Iterator iter = dbManager.getDatabaseList().iterator();
		while(iter.hasNext()){
			String databaseName = (String)iter.next();
			databaseNode = new DefaultMutableTreeNode(databaseName);
			
			Iterator iter2 = dbManager.getTableList(databaseName).iterator();
			while (iter2.hasNext()){
				String tableName = (String)iter2.next();
				tableNode = new DefaultMutableTreeNode(tableName);
				databaseNode.add(tableNode);
			}
			top.add(databaseNode);
		}
	}
	
	private void createNodesOWL(DefaultMutableTreeNode top) throws Exception{
		DefaultMutableTreeNode databaseNode = null;
        DefaultMutableTreeNode tableNode = null;
		OntModel m = DatabaseModelFactory.createModelSchema(textImportSchemaPath.getText().trim());
		OntModelManager ontModelManager = new OntModelManager(m);
		
		top.removeAllChildren();
		Database database = ontModelManager.getDatabase();
		//add database
		databaseNode = new DefaultMutableTreeNode(database.name());
		//add tables
		Iterator iterTables = database.getTables().iterator();
		while (iterTables.hasNext()){
			tableNode = new DefaultMutableTreeNode(((Table)iterTables.next()).name());
			databaseNode.add(tableNode);
		}
		top.add(databaseNode);
	}
			
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
	//intern classes to create path of selected nodes
	private class CheckTreeSelectionModel extends DefaultTreeSelectionModel{ 
	    private TreeModel model; 
	 
		private CheckTreeSelectionModel(TreeModel model){ 
	        this.model = model; 
	        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); 
	    } 
	 
	    // tests whether there is any unselected node in the subtree of given path 
	    public boolean isPartiallySelected(TreePath path){ 
	        if(isPathSelected(path, true)) 
	            return false; 
	        TreePath[] selectionPaths = getSelectionPaths(); 
	        if(selectionPaths==null) 
	            return false; 
	        for(int j = 0; j<selectionPaths.length; j++){ 
	            if(isDescendant(selectionPaths[j], path)) 
	                return true; 
	        } 
	        return false; 
	    } 
	 
	    // tells whether given path is selected. 
	    // if dig is true, then a path is assumed to be selected, if 
	    // one of its ancestor is selected. 
	    public boolean isPathSelected(TreePath path, boolean dig){ 
	        if(!dig) 
	            return super.isPathSelected(path); 
	        while(path!=null && !super.isPathSelected(path)) 
	            path = path.getParentPath(); 
	        return path!=null; 
	    } 
	 
	    // is path1 descendant of path2 
	    private boolean isDescendant(TreePath path1, TreePath path2){ 
	        Object obj1[] = path1.getPath(); 
	        Object obj2[] = path2.getPath(); 
	        for(int i = 0; i<obj2.length; i++){ 
	            if(obj1[i]!=obj2[i]) 
	                return false; 
	        } 
	        return true; 
	    } 
	 
	    public void setSelectionPaths(TreePath[] pPaths){ 
	        throw new UnsupportedOperationException("not implemented yet!!!"); 
	    } 
	 
	    public void addSelectionPaths(TreePath[] paths){ 
	        // unselect all descendants of paths[] 
	        for(int i = 0; i<paths.length; i++){ 
	            TreePath path = paths[i]; 
	            TreePath[] selectionPaths = getSelectionPaths(); 
	            if(selectionPaths==null) 
	                break; 
	            ArrayList toBeRemoved = new ArrayList(); 
	            for(int j = 0; j<selectionPaths.length; j++){ 
	                if(isDescendant(selectionPaths[j], path)) 
	                    toBeRemoved.add(selectionPaths[j]); 
	            } 
	            super.removeSelectionPaths((TreePath[])toBeRemoved.toArray(new TreePath[0])); 
	        } 
	 
	        // if all siblings are selected then unselect them and select parent recursively 
	        // otherwize just select that path. 
	        for(int i = 0; i<paths.length; i++){ 
	            TreePath path = paths[i]; 
	            TreePath temp = null; 
	            while(areSiblingsSelected(path)){ 
	                temp = path; 
	                if(path.getParentPath()==null) 
	                    break; 
	                path = path.getParentPath(); 
	            } 
	            if(temp!=null){ 
	                if(temp.getParentPath()!=null) 
	                    addSelectionPath(temp.getParentPath()); 
	                else{ 
	                    if(!isSelectionEmpty()) 
	                        removeSelectionPaths(getSelectionPaths()); 
	                    super.addSelectionPaths(new TreePath[]{temp}); 
	                } 
	            }else 
	                super.addSelectionPaths(new TreePath[]{ path}); 
	        } 
	    } 
	 
	    // tells whether all siblings of given path are selected. 
	    private boolean areSiblingsSelected(TreePath path){ 
	        TreePath parent = path.getParentPath(); 
	        if(parent==null) 
	            return true; 
	        Object node = path.getLastPathComponent(); 
	        Object parentNode = parent.getLastPathComponent(); 
	 
	        int childCount = model.getChildCount(parentNode); 
	        for(int i = 0; i<childCount; i++){ 
	            Object childNode = model.getChild(parentNode, i); 
	            if(childNode==node) 
	                continue; 
	            if(!isPathSelected(parent.pathByAddingChild(childNode))) 
	                return false; 
	        } 
	        return true; 
	    } 
	 
	    public void removeSelectionPaths(TreePath[] paths){ 
	        for(int i = 0; i<paths.length; i++){ 
	            TreePath path = paths[i]; 
	            if(path.getPathCount()==1) 
	                super.removeSelectionPaths(new TreePath[]{ path}); 
	            else 
	                toggleRemoveSelection(path); 
	        } 
	    } 
	 
	    // if any ancestor node of given path is selected then unselect it 
	    //  and selection all its descendants except given path and descendants. 
	    // otherwise just unselect the given path 
	    private void toggleRemoveSelection(TreePath path){ 
	        Stack stack = new Stack(); 
	        TreePath parent = path.getParentPath(); 
	        while(parent!=null && !isPathSelected(parent)){ 
	            stack.push(parent); 
	            parent = parent.getParentPath(); 
	        } 
	        if(parent!=null) 
	            stack.push(parent); 
	        else{ 
	            super.removeSelectionPaths(new TreePath[]{path}); 
	            return; 
	        } 
	 
	        while(!stack.isEmpty()){ 
	            TreePath temp = (TreePath)stack.pop(); 
	            TreePath peekPath = stack.isEmpty() ? path : (TreePath)stack.peek(); 
	            Object node = temp.getLastPathComponent(); 
	            Object peekNode = peekPath.getLastPathComponent(); 
	            int childCount = model.getChildCount(node); 
	            for(int i = 0; i<childCount; i++){ 
	                Object childNode = model.getChild(node, i); 
	                if(childNode!=peekNode) 
	                    super.addSelectionPaths(new TreePath[]{temp.pathByAddingChild(childNode)}); 
	            } 
	        } 
	        super.removeSelectionPaths(new TreePath[]{parent}); 
	    } 
	}
	
	private class CheckTreeManager extends MouseAdapter implements TreeSelectionListener{ 
	    private CheckTreeSelectionModel selectionModel; 
	    private JTree tree = new JTree(); 
	    int hotspot = new JCheckBox().getPreferredSize().width; 
	 
	    public CheckTreeManager(JTree tree){ 
	        //this.treeExport = tree;
	        selectionModel = new CheckTreeSelectionModel(tree.getModel()); 
	        //treeExport.setCellRenderer(new CheckTreeCellRenderer(treeExport.getCellRenderer(), selectionModel)); 
	        tree.addMouseListener(this); 
	        selectionModel.addTreeSelectionListener(this); 
	    } 
	 
	    public void mouseClicked(MouseEvent me){ 
	        TreePath path = tree.getPathForLocation(me.getX(), me.getY()); 
	        if(path==null) 
	            return; 
	        if(me.getX()>tree.getPathBounds(path).x+hotspot) 
	            return; 
	 
	        boolean selected = selectionModel.isPathSelected(path, true); 
	        selectionModel.removeTreeSelectionListener(this); 
	 
	        try{ 
	            if(selected) 
	                selectionModel.removeSelectionPath(path); 
	            else 
	                selectionModel.addSelectionPath(path); 
	        } catch (Exception e){
	        	showExceptionDialog(e.getMessage());
	        	e.printStackTrace();
	        } finally{ 
	            selectionModel.addTreeSelectionListener(this); 
	            treeExport.treeDidChange(); 
	        } 
	    } 
	 
	    public CheckTreeSelectionModel getSelectionModel(){ 
	        return selectionModel; 
	    } 
	 
	    public void valueChanged(TreeSelectionEvent e){ 
	        tree.treeDidChange(); 
	    } 
	}
	
}