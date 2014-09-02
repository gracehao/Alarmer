package org.cas.database.UI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.cas.database.Utils;

import org.cas.database.business.Application;
import org.cas.database.data.Column;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JLabel label_databaseUrl;
	private JLabel label_databaseUser;
	private JLabel label_databasePassword;
	private JLabel label_value;
	private JLabel label_connectionStatus;
	private JLabel label_TablesInfo;
	private JLabel label_scanInfo;
	private JLabel label_schema;

	private JTextField field_databaseUrl;
	private JTextField field_databaseUser;
	private JPasswordField field_databasePassword;
	private JComboBox field_value;
	private JPanel panel;
	private JButton connectButton;
	private JButton scanButton;
	private JButton quitButton;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JProgressBar progressbar;
	private JComboBox comboSchema;
	private JComboBox comboCondition;
	private JCheckBox caseSensitiveCheckBox;
	private JCheckBox isOnlyScanTables;
	private JCheckBox isScanSystemTables;
	private JTable table;
	private DefaultTableModel tableModel;
	private String frameTitle = "Database Scan Tool";

	public Frame() {
		initGUI();
		initActions();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Frame(String title) {
		this();
		frameTitle = title;
		setTitle(frameTitle);
	}

	private void initGUI() {
		setSize(600, 620);

		panel = new JPanel();

		label_databaseUrl = new JLabel("Database URL");
		label_databaseUser = new JLabel("Database User");
		label_databasePassword = new JLabel("Database Password");
		label_value = new JLabel("Scan Data");
		label_connectionStatus = new JLabel("");
		label_TablesInfo = new JLabel("");
		label_scanInfo = new JLabel("");
		label_schema = new JLabel("Schema");

		field_databaseUrl = new JTextField();
		field_databaseUser = new JTextField();
		field_databasePassword = new JPasswordField();
		field_value = new JComboBox();
		field_value.setEditable(true);

		connectButton = new JButton("Connect");
		scanButton = new JButton("Scan");
		quitButton = new JButton("Stop");

		comboSchema = new JComboBox();
		comboCondition = new JComboBox(new String[] { " = ", "Like" });
		caseSensitiveCheckBox = new JCheckBox("Case sensitive");
		isOnlyScanTables = new JCheckBox("Only scan tables");
		isScanSystemTables = new JCheckBox("Scan system tables");
		textArea = new JTextArea();
		
		isOnlyScanTables.setSelected(true);
		quitButton.setEnabled(false);
		textArea.setLineWrap(true);
		textArea.setEditable(false);

		initTable();
		scrollPane = new JScrollPane(table);

		int x = 20;
		int y = 20;
		int w = 150;
		int h = 20;
		label_databaseUrl.setBounds(x, y, w, h);
		field_databaseUrl.setBounds(x + 120, y, w + 150, h);
		y = y + 20;
		label_databaseUser.setBounds(x, y, w, h);
		field_databaseUser.setBounds(x + 120, y, w, h);
		y = y + 20;
		label_databasePassword.setBounds(x, y, w, h);
		field_databasePassword.setBounds(x + 120, y, w, h);
		y = y + 20;
		label_schema.setBounds(x, y, w, h);
		comboSchema.setBounds(x + 120, y, w, h);
		y = y + 20;
		label_value.setBounds(x, y, w, h);
		comboCondition.setBounds(x + 70, y, 50, h);
		field_value.setBounds(x + 120, y, w + 100, h);
		caseSensitiveCheckBox.setBounds(x + 65, y + 20, w - 50, h);
		isOnlyScanTables.setBounds(x + 65 + w - 50, y + 20, w - 38, h);
		isScanSystemTables.setBounds(x + 65 + w + 60, y + 20, w - 30, h);
		y = y + 45;
		label_connectionStatus.setBounds(x, y, w + 120, h);
		y = y + 20;
		label_TablesInfo.setBounds(x, y, w + 320, h);
		y = y + 20;
		label_scanInfo.setBounds(x, y, w + 240, h);
		y = y + 20;

		progressbar = new JProgressBar();
		progressbar.setOrientation(JProgressBar.HORIZONTAL);
		progressbar.setMinimum(0);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);
		progressbar.setBorderPainted(true);
		progressbar.setBackground(Color.green);
		progressbar.setBounds(20, y, 540, 20);
		y = y + 30;

		scrollPane.setBounds(20, y, 540, 280);
		textArea.setBounds(20, y + 290, 540, 40);

		connectButton.setBounds(450, 20, 120, 28);
		scanButton.setBounds(450, 50, 120, 28);
		quitButton.setBounds(450, 80, 120, 28);

		panel.add(label_databaseUrl);
		panel.add(label_databaseUser);
		panel.add(label_databasePassword);
		panel.add(label_value);
		panel.add(label_connectionStatus);
		panel.add(label_TablesInfo);
		panel.add(field_databaseUrl);
		panel.add(field_databaseUser);
		panel.add(field_databasePassword);
		panel.add(field_value);
		panel.add(connectButton);
		panel.add(scanButton);
		panel.add(quitButton);
		panel.add(scrollPane);
		panel.add(progressbar);
		panel.add(label_scanInfo);
		panel.add(label_schema);
		panel.add(comboSchema);
		panel.add(comboCondition);
		panel.add(caseSensitiveCheckBox);
		panel.add(textArea);
		panel.add(isOnlyScanTables);
		panel.add(isScanSystemTables);

		// panel.add
		panel.setLayout(null);
		setContentPane(panel);
	}

	private void initTable() {
		String[] columnNames = new String[] { "Schema", "Table Name",
				"Column Name", "Cons Type", "Freq", "Sql", "Table" };
		String[][] rows = new String[][] { { "", "", "", "", "", "", "" } };

		tableModel = new DefaultTableModel(rows, columnNames) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		Utils.setTableSortable(table);
		Utils.hideColumn(table, 5);
		Utils.hideColumn(table, 6);
		Utils.setColumnWidth(table, 0, 70);
		Utils.setColumnWidth(table, 3, 70);
		Utils.setColumnWidth(table, 4, 50);
		Utils.clearTableData(tableModel);
		Utils.setHeaderHeight(table, 25);
	}

	private void initActions() {
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Application.stopFlag_Scan = true;
			}
		});
		
		field_value.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10 && field_value.getEditor().getItem().toString() != null
						&& field_value.getEditor().getItem().toString().length() > 0)
					scanButton.doClick();
			}
		});
		field_value.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				field_value.getEditor().selectAll();				
			}
			public void focusLost(FocusEvent arg0) {}
		});
	}

	public boolean isScanSystemTables() {
		return isScanSystemTables.isSelected();
	}

	public boolean isOnlyScanTables() {
		return isOnlyScanTables.isSelected();
	}

	public String getScanButtonText() {
		return scanButton.getText();
	}

	public void setScanButtonText(String text) {
		scanButton.setText(text);
	}

	public Column getColumnFromTable() {
		return (Column) table.getValueAt(table.getSelectedRow(), 6);
	}

	public boolean isForeignKey() {
		if (table.getValueAt(table.getSelectedRow(), 3) == null
				|| table.getValueAt(table.getSelectedRow(), 3).toString()
						.length() == 0) {
			return false;
		}

		if (table.getValueAt(table.getSelectedRow(), 3).toString().indexOf("F") > -1) {
			return true;
		} else {
			return false;
		}
	}

	public void clearTableData() {
		Utils.clearTableData(tableModel);
	}

	public void setProgressBarMaximum(int max) {
		progressbar.setMaximum(max);
	}

	public void setProgressBarValue(int value) {
		progressbar.setValue(value);
	}

	public void setScanInfo(String text) {
		label_scanInfo.setText(text);
	}

	public void setConnectionStatusColor(Color color) {
		label_connectionStatus.setForeground(color);
	}

	public void addConnectButtonListener(ActionListener listener) {
		connectButton.addActionListener(listener);
	}

	public void addConBOxAction(ActionListener listener) {
		field_value.addActionListener(listener);
	}

	public void addScanButtonListener(ActionListener listener) {
		scanButton.addActionListener(listener);
	}

	public void addTableListener(MouseListener listener) {
		table.addMouseListener(listener);
	}

	public void setUrl(String url) {
		field_databaseUrl.setText(url);
	}

	public String getUrl() {
		return field_databaseUrl.getText();
	}

	public void setUser(String user) {
		field_databaseUser.setText(user);
	}

	public String getUser() {
		return field_databaseUser.getText();
	}

	public void setPassword(String password) {
		field_databasePassword.setText(password);
	}

	public String getPassword() {
		return String.valueOf(field_databasePassword.getPassword());
	}

	public String getScanData() {
		String tScanContent = field_value.getEditor().getItem().toString();
		if(tScanContent.startsWith("\""))
			
		if(tScanContent.endsWith("\""))
			tScanContent = tScanContent.substring(0, tScanContent.length() - 1);
		
		return tScanContent;
	}

	public void addScanItem(String item) {
		boolean unique = true;
		for (int i = 0; i < field_value.getItemCount(); i++) {
			Object o = field_value.getItemAt(i);
			if (o != null && o.toString().equals(item)) {
				unique = false;
			}
		}

		if (unique) {
			field_value.addItem(item);
		}
	}

	public void removeScanItems() {
		field_value.removeAllItems();
	}

	public void setScanData(String data) {
		field_value.getEditor().setItem(data);
	}

	public void setConnectionStatus(String status) {
		label_connectionStatus.setText(status);
	}

	public void setTableInfo(String info) {
		label_TablesInfo.setText(info);
	}

	public void setConnectButtonEnable(Boolean b) {
		connectButton.setEnabled(b);
	}

	public void setConnectButtonText(String text) {
		connectButton.setText(text);
	}

	public void setScanButtonEnable(Boolean b) {
		scanButton.setEnabled(b);
	}

	public String getTextAreaText() {
		return this.textArea.getText();
	}

	public void setTextAreaText(String text) {
		textArea.setText(text);
	}

	public void setTextAreaColor(Color color) {
		textArea.setForeground(color);
	}

	public String getSelectedSchema() {
		return this.comboSchema.getSelectedItem().toString();
	}

	public void clearSchema() {
		comboSchema.removeAllItems();
	}

	public void setSchemaEnable(boolean b) {
		comboSchema.setEnabled(b);
	}

	public void setConditionEnable(boolean b) {
		comboCondition.setEnabled(b);
	}

	public void setCaseSensitiveCheckBoxEnable(boolean b) {
		caseSensitiveCheckBox.setEnabled(b);
	}

	public boolean isCaseSensitiveCheckBoxSelected() {
		return caseSensitiveCheckBox.isSelected();
	}

	public String getSelectedCondition() {
		return this.comboCondition.getSelectedItem().toString();
	}

	public void setComboSchemaItems(List<String> l) {
		comboSchema.removeAllItems();

		comboSchema.addItem("All");
		for (int i = 0; i < l.size(); i++) {
			this.comboSchema.addItem(l.get(i).toString());
		}
	}

	public void insertDataToTable(Column col, String constraintType, int count) {
		if (col != null) {
			Object[] objs = new Object[] { col.getSchemaName(),
					col.getTableName(), col.getColumnName(), constraintType,
					count, col.getSql(), col };
			this.tableModel.addRow(objs);
		}
	}

	public static void main(String[] args) {
		Frame frame = new Frame();
		Utils.setWindowAtCentre(frame);
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	public void setStartScanState(boolean pSate){
		scanButton.setEnabled(!pSate);
		quitButton.setEnabled(pSate);
	}
}
