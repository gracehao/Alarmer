package org.cas.database.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.cas.database.data.ConfigData;

public class Conn 
{
	private Connection co = null;
	public static Conn conn = new Conn();
	private String url = "";
	private String user = "";
	private String pass = "";
	
	private Conn() 
	{
		try
		{
			Class.forName(ConfigData.databaseDriver);
		}
		catch(Exception e)
		{
			System.out.println("Load database driver error!");
			e.printStackTrace();
		}
	}
	
	public String connect(String url, String user, String pass)
	{
		this.url = url;
		this.user = user;
		this.pass = pass;
		return connect();
	}
	
	public String connect()
	{
		if(co != null)
			disConnect();
		
		String result = "";
		try
		{
			co = DriverManager.getConnection(url, user, pass);
		}
		catch(Exception e)
		{
			result = e.getMessage();
			System.out.println("Connect to " + url + " error!");
			e.printStackTrace();
		}
		return result;
	}
	
	public Connection getConnection()
	{
		if(co == null)
			this.connect();
		
		return co;
	}
	
	public void disConnect()
	{
		if(co != null)
			co = null;
	}
	
	public static Conn getInstance()
	{
		return conn;
	}
	
	public static Object[] getResult(String sql) 
	{
		Statement stmt = null;
		ResultSet rs = null;
		List list = new ArrayList();
		int colNum = 0;
		int rowNum = 0;
		
		try
		{
			stmt = Conn.getInstance().getConnection().createStatement();
			rs = stmt.executeQuery(sql);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			colNum = rsmd.getColumnCount();

			while(rs.next())
			{
				List l = new ArrayList();
				
				for(int i = 0; i < colNum; i ++)
				{
					l.add(rs.getObject(i+1));
				}	
				
				list.add(l);
				rowNum = rowNum + 1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return new Object[]{colNum, rowNum, list};
	}
	
	public static Object[] getResultWithHeader(String sql) 
	{
		Statement stmt = null;
		ResultSet rs = null;
		List list = new ArrayList();
		int colNum = 0;
		int rowNum = 0;
		
		String names[] = null;
		Object infos[][] = null;
		
		try
		{
			stmt = Conn.getInstance().getConnection().createStatement();
			rs = stmt.executeQuery(sql);
		  
			ResultSetMetaData rsmd = rs.getMetaData();
			colNum = rsmd.getColumnCount();
			
			names = new String[colNum];
			
			for (int i = 1; i <= colNum; i++) 
			{	
				names[i-1] = rsmd.getColumnName(i);
			}
			
			List ll = new ArrayList();
			
			while (rs.next()) 
			{
				List c = new ArrayList();
				for (int j = 1; j <= colNum; j++)
				{
					c.add(rs.getObject(j));
				}
				ll.add(c);
				rowNum = rowNum + 1;
			}
			
			infos = new Object[ll.size()][];
			
			for(int i = 0; i < ll.size(); i++)
			{
				infos[i] = new Object[colNum];
				List c = (List)ll.get(i);
				for (int j = 1; j <= colNum; j++)
				{
					infos[i][j-1]= c.get(j - 1);
				}
			}	
		}
		catch(Exception e)
		{
			list.add(e);
			//e.printStackTrace();
			return new Object[]{e};
		}			
		list.add(names);
		list.add(infos);
		return new Object[]{names, rowNum, infos};
	}
		
	public static Object getSingleResult(String sql) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			stmt = Conn.getInstance().getConnection().createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				return rs.getObject(1);
			}
		}
		catch(Exception e)
		{
		}		
		return null;
	}
		
}



