package org.cas.database.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cas.database.business.Conn;

public class DatabaseInfo 
{
	private String catalogName = "";
	private String schemaOwner = "";
	
	private List<String> schemaNames = new ArrayList<String>(); 
	private List<Table> tableNames = new ArrayList<Table>();
	private List<Table> scanTableNames = new ArrayList<Table>();
	private List<Column> columnNames = new ArrayList<Column>();
	private List<Constraint> constraintNames = new ArrayList<Constraint>();
	private List<ReferentialConstraints> referentialConstraints = new ArrayList<ReferentialConstraints>();
	
	private String sql_schema = "Select catalog_name, schema_owner, schema_name From Information_schema.schemata";
	private String sql_table = "Select table_catalog, table_schema, table_name, table_type From Information_schema.tables Order by table_catalog, table_schema";
	private String sql_column = "Select table_catalog, table_schema, table_name, column_name, ordinal_position, data_type From Information_schema.columns order by table_catalog, table_schema, table_name, ordinal_position";
	private String sql_constraint = 	
		"select table_catalog, table_schema, table_name, column_name, constraint_catalog, constraint_schema, constraint_name," +
		" (select constraint_type " +
		"  from information_schema.table_constraints " +
		"  where constraint_catalog = c.constraint_catalog " +
		"  and   constraint_schema = c.constraint_schema " +
		"  and   constraint_name = c.constraint_name " +
		"  and   table_catalog = c.table_catalog " +
		"  and   table_schema = c.table_schema " +
		"  and   table_name = c.table_name " +
		" ) constraint_type " + 
		" from information_schema.key_column_usage c "; 
	
	private String sql_referential_constraints = "Select constraint_catalog, constraint_schema, constraint_name, unique_constraint_catalog, unique_constraint_schema, unique_constraint_name From information_schema.referential_constraints";
	
	public DatabaseInfo(Boolean isAutoLoadInfo)
	{
		if(isAutoLoadInfo)
		{
			this.loadSchemaInfo();
			this.loadTableInfo();
			this.loadColumnInfo();
			this.loadConstraintInfo();
		}	
	}
	
	public void clean()
	{
		this.cleanSchemaInfo();
		this.cleanTableInfo();
		this.cleanColumnInfo();
		this.cleanConstraintInfo();
		this.cleanReferentialConstraintsInfo();
	}
	
	public int getTotalRecords()
	{
		int records = schemaNames.size() + tableNames.size() + columnNames.size() + constraintNames.size() + referentialConstraints.size();
		return records;
	}
	
	public void setCatalogName(String catalogName)
	{
		this.catalogName = catalogName;
	}
	
	public String getCatalogName()
	{
		return this.catalogName;
	}
	
	public void setSchemaOwner(String schemaOwner)
	{
		this.schemaOwner = schemaOwner;
	}
	
	public String getSchemaOwner()
	{
		return this.schemaOwner;
	}
	
	public List<String> getSchemaNames()
	{
		return this.schemaNames;
	}
	
	public List<String> getTableTypes(String schema)
	{
		List<String> l = new ArrayList<String>();
		String ty = "";
		
		for(int i = 0; i < tableNames.size(); i ++)
		{
			ty = tableNames.get(i).getTableType();
			
			if(!l.contains(ty))
				l.add(ty);
		}
		return l;
	}

	public List<Table> getTables(String schema)
	{
		List<Table> l = new ArrayList<Table>();
		
		for(int i = 0; i < tableNames.size(); i ++)
		{
			Table t = tableNames.get(i);
			if(t.getSchemaName().equals(schema))
			{
				l.add(t);
			}	
		}
		
		return l;
	}
	
	public List<Table> getTablesByType(String schema, String type)
	{
		List<Table> l = new ArrayList<Table>();
		
		for(int i = 0; i < tableNames.size(); i ++)
		{
			Table t = tableNames.get(i);
			if(t.getSchemaName().equals(schema) && t.getTableType().equals(type))
			{
				l.add(t);
			}	
		}
		
		//Collections.sort(l);
		return l;
	}
	
	public Table getTableByColumn(Column c)
	{
		for(int i = 0; i < tableNames.size(); i ++)
		{
			Table t = tableNames.get(i);
			if(t.getSchemaName().equals(c.getSchemaName()) && t.getTableName().equals(c.getTableName()))
			{
				return t;
			}	
		}
		return null;
	}

	public List<Table> getScanTables(boolean onlyTables)
	{
		if(onlyTables)
			return scanTableNames;
		else
			return tableNames;
	}
	
	public List<Table> getScanTables(String schema, boolean onlyTables)
	{
		if(schema == null || schema.length() == 0 || schema.equals("All"))
		{
			if(onlyTables)
				return scanTableNames;
			else
				return tableNames;
		}
		
		List<Table> l = new ArrayList<Table>();

		List<Table> ts;
		
		if(onlyTables)
			ts = scanTableNames;
		else
			ts = tableNames;
		
		
		for(int i = 0; i < ts.size(); i ++)
		{
			Table t = ts.get(i);
			if(t.getSchemaName().equals(schema))
			{
				l.add(t);
			}	
		}
		return l;
	}
	
	public List<Column> getColumn(String schema, String table, boolean isTable)
	{
		List<Column> l = new ArrayList<Column>();
		
		if(table.indexOf("\"") >= 0)
		{
			table = table.substring(1, table.length() - 1);
		}	
		
		for(int i = 0; i < columnNames.size(); i ++)
		{
			Column c = columnNames.get(i);
			if(c.getSchemaName().equals(schema) && c.getTableName().equals(table))
			{
				if(!isTable)
				{
					c.setTableName("\"" + c.getTableName() + "\"");
					l.add(c);
				}	
				else
					l.add(c);
			}	
		}
		return l;
	}
	
	public List<Constraint> getConstraint(String schema, String table, String column)
	{
		List<Constraint> l = new ArrayList<Constraint>();
		
		for(int i = 0; i < constraintNames.size(); i ++)
		{
			Constraint c = constraintNames.get(i);
			if(c.getTable_schema().equals(schema) && c.getTable_name().equals(table) && c.getColumn_name().equals(column))
			{
				l.add(c);
			}	
		}
		return l;
	}

	public List<Constraint> getConstraintByC(String constraint_catalog, String constraint_schema, String constraint_name)
	{
		List<Constraint> l = new ArrayList<Constraint>();
		
		for(int i = 0; i < constraintNames.size(); i ++)
		{
			Constraint c = constraintNames.get(i);
			if(c.getConstraint_catalog().equals(constraint_catalog) && c.getConstraint_schema().equals(constraint_schema) && c.getConstraint_name().equals(constraint_name))
			{
				l.add(c);
			}	
		}
		return l;
	}
	
	public String getConstraintTypeByColumn(String schema, String table, String column)
	{
		List<Constraint> l = getConstraint(schema, table, column);
		
		if(l.size() > 0)
		{
			String s = "";
			int i = 0;
			for(Constraint c : l)
			{
				if(i > 0)
					s = s + ", ";

				s = s + c.getConstraint_type().substring(0, 1);
				i++;
			}	
			return s;
		}	
		else
		{
			return null;
		}	
		
	}
	
	public List<Column> getColumnByForeignKey(String catalog, String schema, String name)
	{
		List<Column> cols = new ArrayList<Column>();
		Column col = null;
		
		for(int i = 0; i < referentialConstraints.size(); i ++)
		{
			ReferentialConstraints r = referentialConstraints.get(i);
			if(r.getUnique_Constraint_schema().equals(schema) && r.getUnique_Constraint_name().equals(name) && r.getUnique_Constraint_catalog().equals(catalog))
			{
				List<Constraint> cs = new ArrayList<Constraint>();
				
				for(Constraint c : constraintNames)
				{
					if(c.getConstraint_catalog().equals(r.getConstraint_catalog()) && c.getConstraint_schema().equals(r.getConstraint_schema()) && c.getConstraint_name().equals(r.getConstraint_name()))
					{
						cs.add(c);
					}	
				}	
				
				for(Constraint c : cs)
				{
					col = new Column();
					col.setCatalogName(c.getTable_catalog());
					col.setSchemaName(c.getTable_schema());
					col.setTableName(c.getTable_name());
					col.setColumnName(c.getColumn_name());
					cols.add(col);
				}	
			}	
		}
		
		return cols;
	}
	
	public Constraint getConstraintByColumn(Column col, String constraintType)
	{
		List<Constraint> cs = this.getConstraint(col.getSchemaName(), col.getTableName(), col.getColumnName());
		
		if(cs == null || cs.size() == 0)
			return null;
		
		for(Constraint c : cs)
		{
			if(c.getConstraint_type().equals(constraintType))
			{
				return c;
			}	
		}	
		return null;
	}
	
	public List<Constraint> getConstraintByConstraint(Constraint constraint, String constraintType)
	{
		if(constraint == null)
			return null;
		
		List<Constraint> cs = null;
		List<Constraint> result = new ArrayList<Constraint>();
		
		for(ReferentialConstraints r : referentialConstraints)
		{
			if(constraintType.equals(ConfigData.primaryKey))
			{
				if(r.getUnique_Constraint_catalog().equals(constraint.getConstraint_catalog()) &&
					r.getUnique_Constraint_schema().equals(constraint.getConstraint_schema()) &&
					r.getUnique_Constraint_name().equals(constraint.getConstraint_name()) )
				{
					cs = this.getConstraintByC(r.getConstraint_catalog(), r.getConstraint_schema(), r.getConstraint_name());
				}	
			}
			else
			{
				if(r.getConstraint_catalog().equals(constraint.getConstraint_catalog()) &&
						r.getConstraint_schema().equals(constraint.getConstraint_schema()) &&
						r.getConstraint_name().equals(constraint.getConstraint_name()) )
					{
						cs = this.getConstraintByC(r.getUnique_Constraint_catalog(), r.getUnique_Constraint_schema(), r.getUnique_Constraint_name());
					}	
			}	
			
			if(cs != null && cs.size() > 0)
			{
				for(Constraint ccc : cs)
					result.add(ccc);
				cs = null;
			}	
		}	
		
		if(result == null || result.size() == 0)
			return null;
		else
			return result;
		
	}

	public List<Column> getColumnByConstraint(List<Constraint> cs)
	{
		List<Column> cols = new ArrayList<Column>();
		Column col = null;
		
		if(cs == null)
			return cols;
		
		for(Constraint c : cs)
		{
			col = new Column();
			col.setCatalogName(c.getTable_catalog());
			col.setSchemaName(c.getTable_schema());
			col.setTableName(c.getTable_name());
			col.setColumnName(c.getColumn_name());
			cols.add(col);
		}
		return cols;
	}

	public void loadSchemaInfo()
	{
		cleanSchemaInfo();
		
		Object[] objs = Conn.getResult(sql_schema);
		int rowNum = Integer.parseInt(objs[1].toString());
		List list = (List)objs[2];
		
		for(int i = 0; i < rowNum; i ++)
		{
			List l = (List)list.get(i);
			
			this.setCatalogName(l.get(0).toString());
			this.setSchemaOwner(l.get(1).toString());
			this.schemaNames.add(l.get(2).toString());
		}	
		
		Collections.sort(schemaNames);
	}
	
	public void cleanSchemaInfo()
	{
		this.setCatalogName("");
		this.setSchemaOwner("");
		this.schemaNames.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void loadTableInfo()
	{
		cleanTableInfo();
		
		Object[] objs = Conn.getResult(sql_table);
		int rowNum = Integer.parseInt(objs[1].toString()); 
		List list = (List)objs[2];
		Table table = null;
		List tmp = new ArrayList();
		
		for(int i = 0; i < rowNum; i ++)
		{
			List l = (List)list.get(i);
			
			table = new Table();
			table.setCatalogName(l.get(0).toString());
			table.setSchemaName(l.get(1).toString());
			table.setTableName(l.get(2).toString());
			table.setTableType(l.get(3).toString());
			table.setIsTable(l.get(3).toString().equals("BASE TABLE"));
			
			if(table.isTable())
			{
				tableNames.add(table);
				scanTableNames.add(table);
			}
			else
			{
				table.setTableName("\"" + table.getTableName() + "\"");
				tableNames.add(table);
			}	
		}
		
		Collections.sort(tableNames);
	}
	
	public void cleanTableInfo()
	{
		tableNames.clear();
		scanTableNames.clear();
	}
	
	public void loadColumnInfo()
	{
		cleanColumnInfo();
		
		Object[] objs = Conn.getResult(sql_column);
		int rowNum = Integer.parseInt(objs[1].toString()); 
		List list = (List)objs[2];
		Column column = null;
		
		for(int i = 0; i < rowNum; i ++)
		{
			List l = (List)list.get(i);
			
			column = new Column();
			column.setCatalogName(l.get(0).toString());
			column.setSchemaName(l.get(1).toString());
			column.setTableName(l.get(2).toString());
			column.setColumnName(l.get(3).toString());
			column.setSequence(Integer.parseInt(l.get(4).toString()));
			column.setDataType(l.get(5).toString());
				
			columnNames.add(column);
		}	
	}
	
	public void cleanColumnInfo()
	{
		columnNames.clear();
	}
	
	public void loadConstraintInfo()
	{
		cleanConstraintInfo();
		
		Object[] objs = Conn.getResult(sql_constraint);
		int rowNum = Integer.parseInt(objs[1].toString()); 
		List list = (List)objs[2];
		Constraint constraint = null;
		
		for(int i = 0; i < rowNum; i ++)
		{
			List l = (List)list.get(i);
			
			constraint = new Constraint();
			constraint.setTable_catalog(l.get(0).toString());
			constraint.setTable_schema(l.get(1).toString());
			constraint.setTable_name(l.get(2).toString());
			constraint.setColumn_name(l.get(3).toString());
			constraint.setConstraint_catalog(l.get(4).toString());
			constraint.setConstraint_schema(l.get(5).toString());
			constraint.setConstraint_name(l.get(6).toString());
			constraint.setConstraint_type(l.get(7).toString());
			
			constraintNames.add(constraint);
		}	
		
	}
	
	public void cleanConstraintInfo()
	{
		constraintNames.clear();
	}	
	
	public void loadReferentialConstraintsInfo()
	{
		cleanReferentialConstraintsInfo();
		
		Object[] objs = Conn.getResult(sql_referential_constraints);
		int rowNum = Integer.parseInt(objs[1].toString()); 
		List list = (List)objs[2];
		ReferentialConstraints keyColumnUsage = null;
		
		for(int i = 0; i < rowNum; i ++)
		{
			List l = (List)list.get(i);
			
			keyColumnUsage = new ReferentialConstraints();
			
			keyColumnUsage.setConstraint_catalog(l.get(0).toString());
			keyColumnUsage.setConstraint_schema(l.get(1).toString());
			keyColumnUsage.setConstraint_name(l.get(2).toString());
			keyColumnUsage.setUnique_Constraint_catalog(l.get(3).toString());
			keyColumnUsage.setUnique_Constraint_schema(l.get(4).toString());
			keyColumnUsage.setUnique_Constraint_name(l.get(5).toString());
				
			referentialConstraints.add(keyColumnUsage);
		}		
	}

	public void cleanReferentialConstraintsInfo()
	{
		referentialConstraints.clear();
	}
}
