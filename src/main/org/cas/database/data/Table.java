package org.cas.database.data;

public class Table implements Comparable
{
	private String catalogName = "";
	private String schemasName = "";
	private String tableName = "";
	private String tableType = "";
	private boolean isTable = false;
	
	public void setIsTable(boolean isTable)
	{
		this.isTable = isTable;
	}
	
	public boolean isTable()
	{
		return this.isTable;
	}
	
	public void setTableType(String name)
	{
		this.tableType = name;
	}
	
	public String getTableType()
	{
		return this.tableType;
	}
	
	public void setCatalogName(String name)
	{
		this.catalogName = name;
	}
	
	public String getCatalogName()
	{
		return this.catalogName;
	}
	
	public void setTableName(String name)
	{
		this.tableName = name;
	}
	
	public String getTableName()
	{
		return this.tableName;
	}
	
	public void setSchemaName(String name)
	{
		this.schemasName = name;
	}
	
	public String getSchemaName()
	{
		return this.schemasName;
	}
	
	public String toString()
	{
		return this.tableName ;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof Table)
		{
			Table t = (Table)o;
			return tableName.toUpperCase().compareTo(t.getTableName().toUpperCase());
		}	
		return 0;
	}
}
