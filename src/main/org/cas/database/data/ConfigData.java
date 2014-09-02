package org.cas.database.data;

public interface ConfigData 
{
	String databaseUser = "postgres";
	String databasePassword = "2ert2ert";//"ollwin123";
	String databaseDriver = "org.postgresql.Driver";
	String databaseUrl = "jdbc:postgresql://127.0.0.1:5432/bigbang";
	String verion = "V1.2011.04.04";//3.25";
	
	String foreignKey = "FOREIGN KEY";
	String primaryKey = "PRIMARY KEY";
	
	String prefix_system_table = "pg_";
}
