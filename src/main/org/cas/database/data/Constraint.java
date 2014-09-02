package org.cas.database.data;

public class Constraint 
{
	private String table_catalog = "";
	private String table_schema = "";
	private String table_name = "";
	private String column_name = "";
	private String constraint_catalog = "";
	private String constraint_schema = "";
	private String constraint_name = "";
	private String constraint_type = "";
	
	public String getTable_catalog() 
	{
		return table_catalog;
	}
	
	public void setTable_catalog(String table_catalog) 
	{
		this.table_catalog = table_catalog;
	}
	
	public String getTable_schema() 
	{
		return table_schema;
	}
	
	public void setTable_schema(String table_schema) 
	{
		this.table_schema = table_schema;
	}
	
	public String getTable_name() 
	{
		return table_name;
	}
	
	public void setTable_name(String table_name) 
	{
		this.table_name = table_name;
	}
	
	public String getColumn_name() 
	{
		return column_name;
	}
	
	public void setColumn_name(String column_name) 
	{
		this.column_name = column_name;
	}
	
	public String getConstraint_catalog() 
	{
		return constraint_catalog;
	}
	
	public void setConstraint_catalog(String constraint_catalog) 
	{
		this.constraint_catalog = constraint_catalog;
	}
	
	public String getConstraint_schema() 
	{
		return constraint_schema;
	}
	
	public void setConstraint_schema(String constraint_schema) 
	{
		this.constraint_schema = constraint_schema;
	}
	
	public String getConstraint_name() 
	{
		return constraint_name;
	}
	
	public void setConstraint_name(String constraint_name) 
	{
		this.constraint_name = constraint_name;
	}
	
	public String getConstraint_type() 
	{
		return constraint_type;
	}
	
	public void setConstraint_type(String constraint_type) 
	{
		this.constraint_type = constraint_type;
	}
}
