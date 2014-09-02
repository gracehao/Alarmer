package org.cas.database.data;

public class Column 
{
	private String catalogName = "";
	private String schemasName = "";
	private String tableName = "";
	private String columnName = "";
	private int sequence = 0;
	private String dataType = "";
	private String sql = "";
	private String scanValue = "";
	private boolean isPrimaryKey = false;
	private boolean isForeignKey = false;
	private int count = 0;
	
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
	
	public void setColumnName(String name)
	{
		this.columnName = name;
	}
	
	public String getColumnName()
	{
		return this.columnName;
	}
	
	public void setDataType(String name)
	{
		this.dataType = name;
	}
	
	public String getDataType()
	{
		return this.dataType;
	}
	
	public void setSchemaName(String name)
	{
		this.schemasName = name;
	}
	
	public String getSchemaName()
	{
		return this.schemasName;
	}
	
	public void setSequence(int seq)
	{
		this.sequence = seq;
	}
	
	public int getSequence()
	{
		return this.sequence;
	}
	
	public void setSql(String sql)
	{
		this.sql = sql;
	}
	
	public String getSql()
	{
		return this.sql;
	}
	
	public void setScanValue(String value)
	{
		this.scanValue = value;
	}
	
	public String getScanValue()
	{
		return this.scanValue;
	}
	
	public String toString()
	{
		return this.columnName ;
	}

	public void setIsPrimaryKey(boolean b)
	{
		this.isPrimaryKey = b;
	}

	public boolean isPrimaryKey()
	{
		return this.isPrimaryKey;
	}

	public void setIsForeignKey(boolean b)
	{
		this.isForeignKey = b;
	}
	
	public boolean isForeignKey()
	{
		return this.isForeignKey;
	}

	public void setCount(int count)
	{
		this.count = count;
	}
	
	public int getCount()
	{
		return this.count;
	}
}
