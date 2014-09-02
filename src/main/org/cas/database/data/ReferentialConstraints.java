package org.cas.database.data;

public class ReferentialConstraints 
{
	private String constraint_catalog = "";
	private String constraint_schema = "";
	private String constraint_name = ""; 
	private String unique_constraint_catalog = "";
	private String unique_constraint_schema = "";
	private String unique_constraint_name = ""; 
	
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
	
	public String getUnique_Constraint_catalog() 
	{
		return unique_constraint_catalog;
	}
	
	public void setUnique_Constraint_catalog(String constraint_catalog) 
	{
		this.unique_constraint_catalog = constraint_catalog;
	}
	
	public String getUnique_Constraint_schema() 
	{
		return unique_constraint_schema;
	}
	
	public void setUnique_Constraint_schema(String constraint_schema) 
	{
		this.unique_constraint_schema = constraint_schema;
	}
	
	public String getUnique_Constraint_name() 
	{
		return unique_constraint_name;
	}
	
	public void setUnique_Constraint_name(String constraint_name) 
	{
		this.unique_constraint_name = constraint_name;
	}
	
}
