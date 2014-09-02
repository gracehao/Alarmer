package org.cas.database.UI;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.cas.database.Utils;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class Dialog extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JSplitPane panel;
	private JTree tree;
	private JScrollPane treePane;
	private JLabel sql_label;
	private JTextArea sqlstatement;
	private JPanel operationPane;
	private JButton button;
	private JPanel rightPane = new JPanel();
	private JTable table;
	private JScrollPane scrollPane;
	private String schema = "";
	private String tableN = "";
	private JCheckBox checkBox;
	private JButton buttonLeft;
	private JButton buttonRight;
	
	public Dialog(JFrame aFrame) 
	{
		super();
		//super(aFrame, true);
	    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
	    init();
	}
	
	public void setSchema(String schema)
	{
		this.schema = schema;
	}
	
	public void setTable(String table)
	{
		this.tableN = table;
	}
	
	public String getSchema()
	{
		return this.schema;
	}
	
	public String getTableName()
	{
		return this.tableN;
	}
	
	public void setTitle(String title)
	{
		super.setTitle("Querying " + title);
	}
	
	public void setSqlStatement(String sqlstatement)
	{
		this.sqlstatement.setText(sqlstatement);
	}
	
	public String getSqlStatement()
	{
		if(sqlstatement.getSelectedText() != null && sqlstatement.getSelectedText().length() > 0)
			return sqlstatement.getSelectedText();
		return sqlstatement.getText();
	}
	
	private void init()
	{
		panel = new JSplitPane();
		
		rightPane.setLayout(new BorderLayout());
		
		treePane = new JScrollPane();
		
		tree = new JTree();
		initOperationPane();
		initTable();

		scrollPane = new JScrollPane(table);
		
		treePane.setPreferredSize(new Dimension(250, 500));
		treePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		treePane.setViewportView(tree);
		
		operationPane.setPreferredSize(new Dimension(200, 120));
		rightPane.add(operationPane, BorderLayout.NORTH);
		rightPane.add(scrollPane, BorderLayout.CENTER);
		
		panel.add(treePane, JSplitPane.LEFT);
		panel.add(rightPane, JSplitPane.RIGHT);
		
		panel.validate();
		panel.invalidate();
		panel.repaint();
		setContentPane(panel);
	}
	
	private void initOperationPane()
	{
		sql_label = new JLabel("Sql Statement");
		checkBox = new JCheckBox("Filting Result data by Click Vaule of Column");
		sqlstatement = new JTextArea();
		button = new JButton("Query");
		buttonLeft = new JButton("<<");
		buttonRight = new JButton(">>");
		operationPane = new JPanel();
		
		
		JScrollPane sqlPane = new JScrollPane(sqlstatement);

		operationPane.setLayout(null);
		sql_label.setBounds(10, 10, 100, 20);
		checkBox.setBounds(220, 10, 300, 20);
		sqlPane.setBounds(10, 30, 800, 80);
		button.setBounds(820, 30, 100, 30);
		buttonLeft.setBounds(820, 65, 50, 30);
		buttonRight.setBounds(870, 65, 50, 30);
		
		sqlstatement.setLineWrap(true);

		operationPane.add(sql_label);
		operationPane.add(checkBox);
		operationPane.add(sqlPane);
		operationPane.add(button);	
		operationPane.add(buttonLeft);	
		operationPane.add(buttonRight);	
	}
	
	public void setButtonLeftEnable(boolean b)
	{
		this.buttonLeft.setEnabled(b);
	}
	
	public void setButtonRightEnable(boolean b)
	{
		this.buttonRight.setEnabled(b);
	}
	
	public boolean isCheckBoxSelected()
	{
		return checkBox.isSelected();
	}
	
	public void addQueryButtonAction(ActionListener action)
	{
		button.addActionListener(action);
	}
	
	public void addLeftButtonAction(ActionListener action)
	{
		buttonLeft.addActionListener(action);
	}
	
	public void addRightButtonAction(ActionListener action)
	{
		buttonRight.addActionListener(action);
	}
	
	public void setTreeMouseAction(MouseAdapter adapter)
	{
		tree.addMouseListener(adapter);
	}
	
	public void setTableMouseAction(MouseAdapter adapter)
	{
		table.addMouseListener(adapter);
	}
	
	public void initTree(TreeNode node)
	{
		DefaultTreeModel model = new DefaultTreeModel(node);
		tree.setModel(model);
	}	

	public JTree getTree()
	{
		return this.tree;
	}
	
	public void insertIntoTableData(Object[] objs)
	{
		if(objs == null || objs.length == 0)
		{
			return;
		}	
		
		if(objs.length == 1)
		{
			Exception e = (Exception)objs[0];
			JOptionPane.showMessageDialog(this, "Sql Statement: " + this.sqlstatement.getText() + "\n" + e.getMessage(), "Sql Query Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
		{
			String[] columns = (String[])objs[0];
			Object[][] data = (Object[][])objs[2];
			
			DefaultTableModel model = new DefaultTableModel(data, columns);
			table.setModel(model);
			Utils.setTableSortable(table);
			Utils.fitTableColumns(table);
		}	
	}
	
	public JTable getTable()
	{
		return table;
	}
	
	private void initTable()
	{
		table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
}
