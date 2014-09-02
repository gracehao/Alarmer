package org.cas.database.UI;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.cas.database.Utils;

import org.cas.database.data.Column;

import java.awt.event.MouseListener;
import java.util.List;

public class Dialog2 extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JSplitPane panel;
	private JScrollPane topPane;
	private JScrollPane bottomPane;
	private JTable table_primary;
	private JTable table_foreign;
	private DefaultTableModel table_primary_module;
	private DefaultTableModel table_foreign_module;
	
	public Dialog2() 
	{
		super();
	    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
	    init();
		setSize(600, 300);
	}
	
	public void setTitle(String title)
	{
		super.setTitle("Querying Relationship " + title);
	}
	
    private void initTable()
    {
        String[] columnNames = new String[]{"Schema", "Table Name", "Column Name", "Freq", "Sql", "Table", "PrimaryKey", "ForeignKey"};
        String[][] rows = new String[][]{{"", "", "", "", "", "", "", ""}};

        table_primary_module = new DefaultTableModel(rows, columnNames)
        {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row,int col)
        	{
        		return false;
        	}         	
        }; 

        table_foreign_module = new DefaultTableModel(rows, columnNames)
        {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row,int col)
        	{
        		return false;
        	}         	
        }; 

        table_primary = new JTable(table_primary_module);
        table_foreign = new JTable(table_foreign_module);

        table_primary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_primary.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		table_foreign.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_foreign.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        Utils.setTableSortable(table_primary);
        Utils.hideColumn(table_primary, 4);
        Utils.hideColumn(table_primary, 5);
        Utils.setColumnWidth(table_primary, 0, 70);
        Utils.setColumnWidth(table_primary, 1, 160);
        Utils.setColumnWidth(table_primary, 2, 160);
        Utils.setColumnWidth(table_primary, 3, 50);
        Utils.setColumnWidth(table_primary, 6, 70);
        Utils.setColumnWidth(table_primary, 7, 70);
        Utils.clearTableData(table_primary_module);
        Utils.setHeaderHeight(table_primary, 25);

        Utils.setTableSortable(table_foreign);
        Utils.hideColumn(table_foreign, 4);
        Utils.hideColumn(table_foreign, 5);
        Utils.setColumnWidth(table_foreign, 0, 70);
        Utils.setColumnWidth(table_foreign, 1, 160);
        Utils.setColumnWidth(table_foreign, 2, 160);
        Utils.setColumnWidth(table_foreign, 3, 50);
        Utils.setColumnWidth(table_foreign, 6, 70);
        Utils.setColumnWidth(table_foreign, 7, 70);
        Utils.clearTableData(table_foreign_module);
        Utils.setHeaderHeight(table_foreign, 25);

    }

    public void insertDataToTable(List<Column> primay, List<Column> foreign)
    {
    	if(primay != null)
    	{
    		for(Column c : primay)
    		{
        		if(c.getCount() == 0)
        			continue;
    			Object[] objs = new Object[]{c.getSchemaName(), c.getTableName(), c.getColumnName(), c.getCount(), c.getSql(), c, c.isPrimaryKey(), c.isForeignKey()};
        		table_primary_module.addRow(objs);
    		}	
    	}
    	
    	if(foreign != null)
    	{
    		for(Column c : foreign)
    		{
	    		Object[] objs = new Object[]{c.getSchemaName(), c.getTableName(), c.getColumnName(), c.getCount(), c.getSql(), c, c.isPrimaryKey(), c.isForeignKey()};
	    		table_foreign_module.addRow(objs);
    		}	
    	}	
    }
    
	
	private void init()
	{
		panel = new JSplitPane();
		panel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		initTable();

		topPane = new JScrollPane(table_primary);
		
		bottomPane = new JScrollPane(table_foreign);
				
		panel.setDividerLocation(80);
		panel.add(topPane, JSplitPane.TOP);
		panel.add(bottomPane, JSplitPane.BOTTOM);
		
		panel.validate();
		panel.invalidate();
		panel.repaint();
		setContentPane(panel);
	}
	
	public void addTablePrimaryListener(MouseListener listener)
	{
		table_primary.addMouseListener(listener);
	}
	
	public void addTableForeignListener(MouseListener listener)
	{
		table_foreign.addMouseListener(listener);
	}

	public Column getPrimaryColumnFromTable()
    {
    	return (Column)table_primary.getValueAt(table_primary.getSelectedRow(), 5);
    }
	
    public Column getForeignColumnFromTable()
    {
    	return (Column)table_foreign.getValueAt(table_foreign.getSelectedRow(), 5);
    }
	
	public int getPrimaryCountFromTable()
    {
    	return Integer.parseInt(table_primary.getValueAt(table_primary.getSelectedRow(), 3).toString());
    }
	
    public int getForeignCountFromTable()
    {
    	return Integer.parseInt(table_foreign.getValueAt(table_foreign.getSelectedRow(), 3).toString());
    }
	
	public void insertIntoTableData(Object[] objs)
	{
		if(objs == null || objs.length == 0)
		{
			return;
		}	
		
		String[] columns = (String[])objs[0];
		Object[][] data = (Object[][])objs[2];
			
		DefaultTableModel model = new DefaultTableModel(data, columns);
		table_primary.setModel(model);
		Utils.setTableSortable(table_primary);
		Utils.fitTableColumns(table_primary);

		table_foreign.setModel(model);
		Utils.setTableSortable(table_foreign);
		Utils.fitTableColumns(table_foreign);
	}
	
	public JTable getTablePrimary()
	{
		return table_primary;
	}

	public JTable getTableForeign()
	{
		return table_foreign;
	}
			

	public static void main(String[] args) 
    {
        Dialog2 d2 = new Dialog2();
        Utils.setWindowAtCentre(d2);
        d2.setVisible(true);   
        
    }
	
}
