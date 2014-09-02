package org.cas.database;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class Utils 
{
	public static void setTableSortable(JTable table)
	{
        RowSorter sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
	}
	
	public static void hideColumn(JTable table, int columnIndex)
	{
    	TableColumn tc = table.getColumnModel().getColumn(columnIndex);
    	tc.setMaxWidth(0);
    	tc.setPreferredWidth(0);
    	tc.setWidth(0);
    	tc.setMinWidth(0);

    	table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
    	table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);	
	}
	
	public static void setColumnWidth(JTable table, int columnIndex, int width)
	{
        TableColumn firsetColumn = table.getColumnModel().getColumn(columnIndex);
        firsetColumn.setPreferredWidth(width);
        firsetColumn.setMaxWidth(width);
        firsetColumn.setMinWidth(width);
	}
	
	public static void setHeaderHeight(JTable table, int height)
	{
		JTableHeader header = table.getTableHeader();
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, height));
		
		DefaultTableCellRenderer hr = (DefaultTableCellRenderer) header.getDefaultRenderer();
		hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
	}

	public static void clearTableData(DefaultTableModel moduel)
    {
    	int count = moduel.getRowCount();
    	for(int i = 0; i < count; i ++)
    	{
    		moduel.removeRow(0);
    	}	
    }
	
	public static void fitTableColumns(JTable table) 
	{  
		JTableHeader header = table.getTableHeader();  
		int rowCount = table.getRowCount();  
		Enumeration columns = table.getColumnModel().getColumns();  
		while (columns.hasMoreElements()) 
		{  
			TableColumn column = (TableColumn) columns.nextElement();  
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());  
			int width = (int) table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table,column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();  
			
			for (int row = 0; row < rowCount; row++) 
			{  
				int preferedWidth = (int) table.getCellRenderer(row, col).getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row,col).getPreferredSize().getWidth();  
				width = Math.max(width, preferedWidth);
	        }  
			header.setResizingColumn(column); 
			column.setWidth(width + table.getIntercellSpacing().width);  
		}
	} 
	
		
	public static void setWindowAtCentre(Window window)
	{
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();
        
        if (frameSize.height > screenSize.height)
        	frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
        	frameSize.width = screenSize.width;

        window.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}

	public static DefaultMutableTreeNode searchNode(JTree tree, DefaultMutableTreeNode root, Object node)
	{
	    DefaultMutableTreeNode node1 = null;
	    Enumeration e = root.breadthFirstEnumeration();
	    while (e.hasMoreElements()) 
	    {
	      node1 = (DefaultMutableTreeNode) e.nextElement();
	      if (node.equals(node1.getUserObject())) 
	      {
	        return node1;
	      }
	    }
	    return null;	
	}
	
	public static void openTreeByNode(JTree tree, DefaultMutableTreeNode root, Object node)
	{
	    DefaultMutableTreeNode node1 = searchNode(tree, root, node);
	    if (node != null) 
	    {
	    	TreeNode[] nodes = new DefaultTreeModel(root).getPathToRoot(node1);
	    	TreePath path = new TreePath(nodes);
	    	tree.scrollPathToVisible(path);
	    	tree.setSelectionPath(path);
	    } 
	    else 
	    {
	    	System.out.println("Node with string " + node.toString() + " not found");
	    }
	}	
}
