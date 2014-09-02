package org.cas.database.business;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.cas.database.Utils;

import org.cas.database.UI.Dialog;
import org.cas.database.UI.Dialog2;
import org.cas.database.UI.Frame;
import org.cas.database.data.Column;
import org.cas.database.data.ConfigData;
import org.cas.database.data.Constraint;
import org.cas.database.data.DatabaseInfo;
import org.cas.database.data.Table;

public class Application {

	public static boolean stopFlag_Scan = false;
	
	private Frame frame = null;
	private DatabaseInfo dbInfo = null;
	private Dialog dialog = null;
	private Dialog2 dialog2 = null;
	DefaultMutableTreeNode root = null;
	private ArrayList<String> sqls = new ArrayList<String>();
	private int sqlNum = -1;
	
	public Application() {
		initUI();
		addButtonAction();
		addTableAction();
		// Connect automatically
		new connectionThread().start();
	}

	private void initUI() {
		frame = new Frame("Postgres Database Analysis Tool ("
				+ ConfigData.verion + ")");
		Utils.setWindowAtCentre(frame);
		frame.setVisible(true);
		frame.setResizable(false);

		frame.setUrl(ConfigData.databaseUrl);
		frame.setUser(ConfigData.databaseUser);
		frame.setPassword(ConfigData.databasePassword);
		frame.setConnectionStatus("Database Disconnect");
		frame.setConnectionStatusColor(Color.red);
		frame.setScanButtonEnable(false);
	}

	private void connection() {
		initGuiBeforeConnection();

		// DataBase connection
		String result = Conn.getInstance().connect(frame.getUrl(),
				frame.getUser(), frame.getPassword());
		if (result != null && result.length() == 0) {
			frame.setConnectionStatus("Database Connected");
			frame.setConnectionStatusColor(Color.blue);
		} else {
			frame.setConnectionStatus("Database Connected Failed");
			frame.setConnectionStatusColor(Color.red);
			frame.setConnectButtonEnable(true);
			frame.setTextAreaText(result);
			frame.setTextAreaColor(Color.red);
			frame.setScanButtonEnable(false);
			return;
		}

		// Loading information
		dbInfo = new DatabaseInfo(false);

		frame.setScanInfo("Loading Schema Information...");
		dbInfo.loadSchemaInfo();
		frame.setProgressBarValue(1);
		frame.setTableInfo("Loaded " + dbInfo.getTotalRecords()
				+ " Records From Database " + dbInfo.getCatalogName());

		frame.setComboSchemaItems(dbInfo.getSchemaNames());

		frame.setScanInfo("Loading Table Information...");
		dbInfo.loadTableInfo();
		frame.setProgressBarValue(2);
		frame.setTableInfo("Loaded " + dbInfo.getTotalRecords()
				+ " Records From Database " + dbInfo.getCatalogName());

		frame.setScanInfo("Loading Column Information...");
		dbInfo.loadColumnInfo();
		frame.setProgressBarValue(3);
		frame.setTableInfo("Loaded " + dbInfo.getTotalRecords()
				+ " Records From Database " + dbInfo.getCatalogName());

		frame.setScanInfo("Loading Constraint Information...");
		dbInfo.loadConstraintInfo();
		frame.setProgressBarValue(4);
		frame.setTableInfo("Loaded " + dbInfo.getTotalRecords()
				+ " Records From Database " + dbInfo.getCatalogName());

		frame.setScanInfo("Loading Constraint key column usage Information...");
		dbInfo.loadReferentialConstraintsInfo();
		frame.setProgressBarValue(5);
		frame.setTableInfo("Loaded " + dbInfo.getTotalRecords()
				+ " Records From Database " + dbInfo.getCatalogName());

		initGuiAfterConnection();
	}

	private void initGuiBeforeConnection() {
		frame.setConnectButtonEnable(false);
		frame.setScanButtonEnable(false);

		frame.setConnectionStatus("Database Connectting...");
		frame.setConnectionStatusColor(Color.blue);
		frame.setTableInfo("");
		frame.setScanInfo("");
		frame.clearSchema();
		frame.setProgressBarMaximum(5);
		frame.setProgressBarValue(0);
		frame.setTextAreaText("");
	}

	private void initGuiAfterConnection() {
		frame.setScanButtonEnable(true);
		frame.setConnectButtonEnable(true);
		frame.setConnectButtonText("Reconnect");
		frame.setScanInfo("");
	}

	private void analysis() {
		initGuiBeforeAnalysis();
		String scanData = frame.getScanData().trim();

		if (scanData == null || scanData.length() == 0) {
			frame.setTextAreaText("Please input scan data!");
			frame.setTextAreaColor(Color.red);
			return;
		} else
			frame.setTextAreaText("");
		
		if(scanData.startsWith("\""))
			scanData = scanData.substring(1);
		
		if(scanData.endsWith("\""))
			scanData = scanData.substring(0, scanData.length());
		
		scanTables(scanData);

		initGuiAfterAnalysis();
	}

	private void scanTables(String scanData) {
		String selectedCondition = frame.getSelectedCondition();
		boolean isCaseSensitive = frame.isCaseSensitiveCheckBoxSelected();
		int i = 0;

		List<Table> tables = dbInfo.getScanTables(frame.getSelectedSchema(),
				frame.isOnlyScanTables());

		if (!frame.isScanSystemTables()) {
			List<Table> tabs = new ArrayList<Table>();

			for (Table table : tables) {
				if (!table.getSchemaName().startsWith(ConfigData.prefix_system_table))
					tabs.add(table);
			}
			tables = tabs;
		}

		frame.setProgressBarMaximum(tables.size());

		for (Table t : tables) {
			if(!Application.stopFlag_Scan){
				List<Column> columns = dbInfo.getColumn(t.getSchemaName(), t.getTableName(), t.isTable());
	
				for (Column col : columns)
					scanColumn(col, selectedCondition, isCaseSensitive, scanData);
				
				i++;
				frame.setProgressBarValue(i);
				try{
					Thread.sleep(1);
				}catch(Exception e){
					//condinue;
				}
			}else{
				frame.setStartScanState(false);
				frame.setProgressBarValue(0);
			}
		}
	}

	private void scanColumn(Column col, String selectedCondition,
			boolean isCaseSensitive, String scanData) {
		if (col.getTableName().equals("MTOSO_View")) {
			int i = 0;
			System.out.print(i);
		}

		frame.setScanInfo("Scaning " + col.getSchemaName() + "."
				+ col.getTableName() + "." + col.getColumnName() + "...");
		String sql = buildSql(col, selectedCondition, isCaseSensitive, scanData);
		int frequency = 0;
		try {
			frequency = Integer.parseInt(Conn.getSingleResult(sql).toString());
		} catch (Exception e) {
		}

		if (frequency > 0) {
			col.setSql(sql.replace("count(*)", "*"));
			col.setScanValue(frame.getScanData());
			String s = dbInfo.getConstraintTypeByColumn(col.getSchemaName(),
					col.getTableName(), col.getColumnName());
			if (s != null) {
				if (s.indexOf("F") >= 0)
					col.setIsForeignKey(true);
				if (s.indexOf("P") >= 0)
					col.setIsPrimaryKey(true);
			} else {
				col.setIsForeignKey(false);
				col.setIsPrimaryKey(false);
			}
			col.setCount(frequency);
			frame.insertDataToTable(col, s, frequency);
		}
	}

	private String buildSql(Column col, String selectedCondition, boolean isCaseSensitive, String scanData) {
		String sql = "Select count(*) From " + "\"" + col.getSchemaName() + "\"" + "." + col.getTableName() + " ";
		boolean tLike = selectedCondition.toUpperCase().indexOf("LIKE") >= 0;
		StringBuffer where = new StringBuffer("Where ");
		if(isCaseSensitive)//TODO: tao, for a number, I doubt that it may lead to wrong result by "Upper"
			where.append(col.getColumnName()).append(" ").append(selectedCondition)
			.append(tLike ? " '%" : "'").append(scanData).append(tLike ? " %'" : "'");
		else
			where.append("Upper(").append(col.getColumnName()).append(") ").append(selectedCondition)
			.append(tLike ? " Upper('%" : "Upper('").append(scanData).append(tLike ? "%')" : "')");
		
		return sql.concat(where.toString());
	}
	
	private void initGuiBeforeAnalysis() {
		String scanData = frame.getScanData();

		frame.addScanItem(scanData);

		frame.setTextAreaText("");
		frame.setScanInfo("");
		frame.setScanButtonEnable(false);
		frame.setSchemaEnable(false);
		frame.setConditionEnable(false);
		frame.setCaseSensitiveCheckBoxEnable(false);
		frame.clearTableData();
	}

	private void initGuiAfterAnalysis() {
		// frame.setTextAreaText("");
		// frame.setScanInfo("");
		frame.setStartScanState(false);
		frame.setSchemaEnable(true);
		frame.setConditionEnable(true);
		frame.setCaseSensitiveCheckBoxEnable(true);
	}

	private void addButtonAction() {
		frame.addConnectButtonListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new connectionThread().start();
			}
		});

		frame.addScanButtonListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Application.stopFlag_Scan = false;
				frame.setStartScanState(true);
				new AnalysisThread().start();
			}
		});
	}

	private void addDialog2TableAction() {
		dialog2.addTablePrimaryListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Column col = dialog2.getPrimaryColumnFromTable();

				if (e.getClickCount() >= 2
						&& dialog2.getPrimaryCountFromTable() > 0) {
					openDialog(col);
					// tableActions();
				}
			}
		});

		dialog2.addTableForeignListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Column col = dialog2.getForeignColumnFromTable();
				if (e.getClickCount() >= 2
						&& dialog2.getForeignCountFromTable() > 0) {
					openDialog(col);
					// tableActions();
				}
			}
		});
	}

	private void tableActions() {
		Column col = frame.getColumnFromTable();
		frame.setTextAreaText(col.getSql());

		if (col.isForeignKey() || col.isPrimaryKey()) {
			List<Column> columns = null;
			int result = 0;

			if (col.isForeignKey() && col.isPrimaryKey()) {
				Object[] options = { "Default Query",
						ConfigData.primaryKey + " Query",
						ConfigData.foreignKey + " Query" };
//				result = JOptionPane
//						.showOptionDialog(
//								null,
//								"Please choose which Query you want to execute",
//								"Please choose", JOptionPane.YES_OPTION,
//								JOptionPane.QUESTION_MESSAGE, null, options,
//								options[0]);

				if (result == 0)
					openDialog(col);
				if (result == 1) {
					Constraint c = dbInfo.getConstraintByColumn(col,
							ConfigData.primaryKey);
					if (c != null) {
						List<Constraint> cs = dbInfo.getConstraintByConstraint(
								c, ConfigData.primaryKey);
						columns = dbInfo.getColumnByConstraint(cs);
					}
					List l = new ArrayList<Column>();
					l.add(col);
					openDialog2(l, initColumnsInfo(columns),
							ConfigData.primaryKey);
				}
				if (result == 2) {
					Constraint c = dbInfo.getConstraintByColumn(col,
							ConfigData.foreignKey);
					if (c != null) {
						List<Constraint> cs = dbInfo.getConstraintByConstraint(
								c, ConfigData.foreignKey);
						columns = dbInfo.getColumnByConstraint(cs);
					}
					List l = new ArrayList<Column>();
					l.add(col);
					openDialog2(initColumnsInfo(columns), l,
							ConfigData.foreignKey);

				}
			}

			if (!col.isForeignKey() && col.isPrimaryKey()) {
				Object[] options = { "Default Query",
						ConfigData.primaryKey + " Query" };
//				result = JOptionPane
//						.showOptionDialog(
//								null,
//								"Please choose which Query you want to execute",
//								"Please choose", JOptionPane.YES_OPTION,
//								JOptionPane.QUESTION_MESSAGE, null, options,
//								options[0]);

				if (result == 0)
					openDialog(col);
				if (result == 1) {
					Constraint c = dbInfo.getConstraintByColumn(col,
							ConfigData.primaryKey);
					if (c != null) {
						List<Constraint> cs = dbInfo.getConstraintByConstraint(
								c, ConfigData.primaryKey);
						columns = dbInfo.getColumnByConstraint(cs);
					}
					List l = new ArrayList<Column>();
					l.add(col);
					openDialog2(l, initColumnsInfo(columns),
							ConfigData.primaryKey);
				}
			}
			if (col.isForeignKey() && !col.isPrimaryKey()) {
				Object[] options = { "Default Query",
						ConfigData.foreignKey + " Query" };
//				result = JOptionPane
//						.showOptionDialog(
//								null,
//								"Please choose which Query you want to execute",
//								"Please choose", JOptionPane.YES_OPTION,
//								JOptionPane.QUESTION_MESSAGE, null, options,
//								options[0]);

				if (result == 0)
					openDialog(col);
				if (result == 1) {
					Constraint c = dbInfo.getConstraintByColumn(col,
							ConfigData.foreignKey);
					if (c != null) {
						List<Constraint> cs = dbInfo.getConstraintByConstraint(
								c, ConfigData.foreignKey);
						columns = dbInfo.getColumnByConstraint(cs);
					}
					List l = new ArrayList<Column>();
					l.add(col);
					openDialog2(initColumnsInfo(columns), l,
							ConfigData.foreignKey);
				}
			}
		} else {
			openDialog(col);
		}
	}

	private void addTableAction() {
		frame.addTableListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					tableActions();
				}
			}
		});
	}

	private List<Column> initColumnsInfo(List<Column> columns) {
		List<Column> result = new ArrayList<Column>();

		for (Column col : columns) {
			String sql = buildSql(col, frame.getSelectedCondition(), frame
					.isCaseSensitiveCheckBoxSelected(), frame.getScanData());
			int frequency = 0;
			try {
				frequency = Integer.parseInt(Conn.getSingleResult(sql)
						.toString());
			} catch (Exception e) {
			}

			col.setSql(sql.replace("count(*)", "*"));
			col.setScanValue(frame.getScanData());
			String s = dbInfo.getConstraintTypeByColumn(col.getSchemaName(),
					col.getTableName(), col.getColumnName());
			if (s != null) {
				if (s.indexOf("F") >= 0)
					col.setIsForeignKey(true);
				if (s.indexOf("P") >= 0)
					col.setIsPrimaryKey(true);
			} else {
				col.setIsForeignKey(false);
				col.setIsPrimaryKey(false);
			}
			col.setCount(frequency);
			result.add(col);
		}
		return result;
	}

	private void openDialog2(List<Column> primary, List<Column> foreign,
			String title) {
		dialog2 = new Dialog2();
		Utils.setWindowAtCentre(dialog2);
		dialog2.setVisible(true);
		dialog2.setTitle(title);

		// initActionForDialog();
		// initDialogTree();
		dialog2.insertDataToTable(primary, foreign);
		addDialog2TableAction();
	}

	private void openDialog(Column col) {
		dialog = new Dialog(frame);
		dialog.setSchema(col.getSchemaName());
		dialog.setTable(col.getTableName());
		// dialog.setModal(false);
		dialog.setSize(1200, 700);
		Utils.setWindowAtCentre(dialog);
		dialog.setVisible(true);
		dialog.setTitle(col.getSchemaName() + "." + col.getTableName());
		dialog.setSqlStatement(col.getSql());

		initActionForDialog();
		initDialogTree();

		Table t = dbInfo.getTableByColumn(col);

		if (t != null) {
			Utils.openTreeByNode(dialog.getTree(), root, t);
		}

		querySqlInDialog();
	}

	private void initDialogTree() {
		root = new DefaultMutableTreeNode(dbInfo.getCatalogName());
		DefaultMutableTreeNode schema = null;
		DefaultMutableTreeNode tType = null;
		DefaultMutableTreeNode table = null;

		for (String s : dbInfo.getSchemaNames()) {
			schema = new DefaultMutableTreeNode(s);
			root.add(schema);

			for (String tableType : dbInfo.getTableTypes(s)) {
				tType = new DefaultMutableTreeNode(tableType);
				schema.add(tType);

				for (Table t : dbInfo.getTablesByType(s, tableType)) {
					table = new DefaultMutableTreeNode(t);
					tType.add(table);
				}
			}
		}
		dialog.initTree(root);
	}

	private void querySqlInDialog() {
		String sql = dialog.getSqlStatement();

		Object[] objs = Conn.getResultWithHeader(sql);

		dialog.insertIntoTableData(objs);
		sqls.add(dialog.getSqlStatement().trim());
		sqlNum++;
	}

	private void initActionForDialog() {
		dialog.addQueryButtonAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setSchema("");
				dialog.setTable("");
				querySqlInDialog();
			}
		});

		dialog.addLeftButtonAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sqlNum >= 0) {
					sqlNum = sqlNum - 1;
					dialog.setSqlStatement(sqls.get(sqlNum));

					if (sqlNum < sqls.size())
						dialog.setButtonRightEnable(true);

					if (sqlNum == 0)
						dialog.setButtonLeftEnable(false);
				}
			}
		});

		dialog.addRightButtonAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sqlNum < sqls.size() - 1) {
					sqlNum = sqlNum + 1;
					dialog.setSqlStatement(sqls.get(sqlNum));

					if (sqlNum >= 0)
						dialog.setButtonLeftEnable(true);

					if (sqlNum == sqls.size() - 1)
						dialog.setButtonRightEnable(false);
				}
			}
		});

		dialog.setTreeMouseAction(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() < 2)
					return;

				TreePath tp = dialog.getTree().getPathForLocation(me.getX(),
						me.getY());
				if (tp != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
							.getLastPathComponent();
					if (node != null) {
						if (node.isLeaf() && me.getClickCount() >= 2
								&& node.getLevel() == 3) {
							Table t = (Table) node.getUserObject();
							dialog.setSqlStatement("Select * From " + "\""
									+ t.getSchemaName() + "\"."
									+ t.getTableName());
							dialog.setSchema(t.getSchemaName());
							dialog.setTable(t.getTableName());
							querySqlInDialog();
						}
					}
				}
			}
		});

		dialog.setTableMouseAction(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (dialog.isCheckBoxSelected()) {
					Object t = dialog.getTable().getValueAt(
							dialog.getTable().getSelectedRow(),
							dialog.getTable().getSelectedColumn());

					if (t == null)
						return;

					String s = dialog.getSqlStatement().trim();
					if (s.toUpperCase().indexOf("WHERE") > 0) {
						s = s
								+ " And "
								+ dialog.getTable().getColumnName(
										dialog.getTable().getSelectedColumn())
								+ " = '" + t.toString() + "'";
					} else {
						s = s
								+ " Where "
								+ dialog.getTable().getColumnName(
										dialog.getTable().getSelectedColumn())
								+ " = '" + t.toString() + "'";
					}
					dialog.setSqlStatement(s);
					querySqlInDialog();
				}
			}
		});
	}

	private class connectionThread extends Thread {
		public void run() {
			connection();
		}
	}

	private class AnalysisThread extends Thread {
		public void run() {
			analysis();
		}
	}
}
