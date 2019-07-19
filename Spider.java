import java.sql.*;

public class Spider {

	static Statement stmt = null; 
	static ResultSet rs = null; 
	static Integer maxRows;
	static int MAX_THREADS=1;
	static Connection conn;

	public static void main(String[] args) 
	{
		try 
		{ 
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			if(args.length!=0)
				inicialitza(new String(args[0]));
			else
				inicialitza("");
			contaQuantsQueden();
	
			while(maxRows.intValue()>0)
			{
				concurrencia conc = new concurrencia();
				SpiderBaixaURL url1= new SpiderBaixaURL(conc,conn,stmt);
				url1.start();
		

				int n_threads=maxRows.intValue()-1;
				
				if(n_threads>MAX_THREADS)
					n_threads=MAX_THREADS;
				
				while((n_threads>1)&&(maxRows.intValue()>0))
				{			
					url1 = new SpiderBaixaURL(conc,conn,stmt);
					url1.sleep(5000);
					url1.start();
					n_threads--;
				}
			
				url1.join();
				contaQuantsQueden();
			}		

			

		}
		catch (Exception ex)
		{ 
				
			ex.printStackTrace();
		}
		finally
			{ 
				if (rs != null) 
				{ 
					try 
					{
						rs.close(); 
					}
					catch (SQLException sqlEx) 
					{ 
					} 

					rs = null; 
				}

				if (stmt != null)
				{ 
					try 
					{ 
						stmt.close(); 
					}
					 catch (SQLException sqlEx) 
					 {
					 } 

					stmt = null; 
				} 
			}

	}
	
	public static void inicialitza(String url)
	{
		try
		{
	
			conn = DriverManager.getConnection("jdbc:mysql://192.168.1.2/spider?user=spider&password=spider");

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE); 

			
			
			if((countItems(stmt.executeQuery("SELECT * FROM perVeure WHERE url='"+url+"'"))==0)
					&&(countItems(stmt.executeQuery("SELECT * FROM vistes WHERE url='"+url+"'"))==0)&&(url!=""))
			{
					

				stmt.execute("insert into perVeure (url) values ('"+url+"')");

			}
		}
		catch(SQLException ex) 
		{

			   System.out.println("SQLException: " + ex.getMessage()); 
			   System.out.println("SQLState: " + ex.getSQLState()); 
			   System.out.println("VendorError: " + ex.getErrorCode()); 
		}
	
	}
	
	public static void contaQuantsQueden()
	{
		try
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM perVeure");
			maxRows=new Integer(countItems(rs));

			rs.close();
		}
		catch(SQLException ex) 
		{
			   System.out.println("SQLException: " + ex.getMessage()); 
			   System.out.println("SQLState: " + ex.getSQLState()); 
			   System.out.println("VendorError: " + ex.getErrorCode()); 
		}

	}
	
	public static int countItems(ResultSet rs)
	{
		int i=0;
		try{
		
		while(rs.next())
			i++;
				
		}
		catch(Exception e)
		{
			System.out.println("ERROR: while counting items");
			e.printStackTrace();
			
		
		}
		return i;
	}
	
}
