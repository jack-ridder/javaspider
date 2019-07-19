import java.util.regex.*;
import java.net.*;
import java.sql.*;

public class coincidencies
{
	static Statement stmt = null; 
	static ResultSet rsPerVeure = null; 
	String paraula="";
	Connection conn;
	

	public coincidencies(URL url,String ascii,String textSencer,Connection conn,Statement stmt)
	{	
			int id_pagina=0,i=0,id_paraula=0;
			
			try
			{ 
				ascii=ascii.replaceAll("&nbsp;"," ");
				ascii=ascii.replaceAll("&nbsp"," ");
				ascii=ascii.replaceAll("nbsp;"," ");
				ascii=ascii.replaceAll("nbsp"," ");
				
				Class.forName("com.mysql.jdbc.Driver").newInstance();
							

				this.conn=conn;
				this.stmt=stmt;
	

				rsPerVeure = stmt.executeQuery("SELECT * FROM vistes WHERE url='"+url.toString()+"'");
				if(this.countItems(rsPerVeure)>0)
				{

				  rsPerVeure.first(); 
				  id_pagina=rsPerVeure.getInt("id");
				}
				else
				{
				  stmt.execute("INSERT INTO vistes (url) VALUES ('"+url.toString()+"')");
				  
				  rsPerVeure = stmt.executeQuery("SELECT id FROM vistes WHERE url='"+url.toString()+"'");

				  rsPerVeure.first(); 
				  id_pagina=rsPerVeure.getInt(1);
				}	

				Pattern p;
				Matcher m;
				boolean resultado;
				p = Pattern.compile("\\w+");
				m = p.matcher(ascii);
				resultado=m.find();

				while(resultado)
				{
					paraula= new String(ascii.subSequence(m.start(),m.end()).toString());
					
					paraula=paraula.replaceAll("\\<.*?\\>","").toLowerCase();


				
					rsPerVeure = stmt.executeQuery("SELECT id FROM paraules WHERE paraula='"+paraula+"'");
					

					if(countItems(rsPerVeure)>0)
					{		

						rsPerVeure.first(); 
						id_paraula=rsPerVeure.getInt(1);
					}
					else 
					{

						if(paraula.length()<50)
						{
							stmt.execute("INSERT INTO paraules (paraula) VALUES ('"+paraula+"')");
							rsPerVeure=stmt.executeQuery("SELECT id FROM paraules WHERE paraula='"+paraula+"'");
							
							rsPerVeure.first(); 
							id_paraula=rsPerVeure.getInt(1);
						}
					}

					if(paraula.length()<50)
					{
						rsPerVeure = stmt.executeQuery("SELECT * FROM coincidencies WHERE paraula='"+id_paraula+"' AND pagina='"+id_pagina+"'");
					
						int n_coincidencies=0,id_coincidencies=0;
						
						if(countItems(rsPerVeure)>0)
						{
						
							rsPerVeure.first(); 
							n_coincidencies=rsPerVeure.getInt("n_coincidencies");
							id_coincidencies=rsPerVeure.getInt("id");
							n_coincidencies++;

							rsPerVeure.updateString("n_coincidencies",new Integer(n_coincidencies).toString());
							rsPerVeure.updateRow();

						}
						else
							stmt.execute("INSERT INTO coincidencies (paraula,pagina,n_coincidencies) VALUES ('"+id_paraula+"','"+id_pagina+"','1')");
					}
								
					resultado=m.find();
				}					
				
					
			comprovaCoincidencies(textSencer,id_pagina,"<b>.+</b>");
			comprovaCoincidencies(textSencer,id_pagina,"<B>.+</B>");
			comprovaCoincidencies(textSencer,id_pagina,"<h1>.+</h1>");
			comprovaCoincidencies(textSencer,id_pagina,"<H1>.+</H1>");

			freeResultSets();
			
		}
		catch(SQLException ex) 
		{  
		   // 错误处理
		   System.out.println("SQLException: " + ex.getMessage()); 
		   System.out.println("SQLState: " + ex.getSQLState()); 
		   System.out.println("VendorError: " + ex.getErrorCode()); 
		   return;
		}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
	
	public void freeResultSets()
	{
		if (rsPerVeure != null) 
		{ 
			try 
			{
				rsPerVeure.close(); 
			}
			catch (SQLException sqlEx) 
			{ 
			} 

		}	
	/*	if (stmt != null)
		{ 
			try 
			{ 
				stmt.close(); 
			}
			 catch (SQLException sqlEx) 
			 {
				// ignore 
			 } 

			stmt = null; 
		} */
		
	
	}
		
		
		public int countItems(ResultSet rs)
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
		
		public int wordCount(String ascii)
		{
			Pattern p;
			Matcher m;
			int wordCount=0;
			boolean resultado;
			
			p = Pattern.compile("\\w+");
			m = p.matcher(ascii);
			resultado=m.find();
			
			while(resultado)
			{
				wordCount++;
				resultado=m.find();
			}
			return wordCount;
		
		
		}

		public void comprovaCoincidencies(String text,int id_pagina,String pattern)
		{
			Pattern p,p1;
			Matcher m,m1;
			boolean resultado;
			String paraula="",subParaula="";
			int id_paraula=0,coincidencies=0;
			
			try
			{
				text=text.toLowerCase();
				p = Pattern.compile(pattern);

				m = p.matcher(text);
				
				resultado=m.find();
	
				while(resultado)
				{
					paraula= new String(text.subSequence(m.start(),m.end()).toString());
					paraula=paraula.replaceAll("\\<.*?\\>","");
					paraula=paraula.replaceAll("!","");
					paraula=paraula.replaceAll("?,"");
					paraula=paraula.replaceAll("\\?","");
					paraula=paraula.replaceAll("?,"");
					paraula=paraula.replaceAll("\\."," ");
					paraula=paraula.replaceAll(","," ");
					paraula=paraula.replaceAll(":"," ");
					paraula=paraula.replaceAll("&nbsp;"," ");
					paraula=paraula.replaceAll(";"," ");
					
					try
					{
						

						p1 = Pattern.compile("\\w+");
						m1 = p1.matcher(paraula);
						resultado=m1.find();

						while(resultado)
						{
							subParaula= new String(paraula.subSequence(m1.start(),m1.end()).toString());
							
							rsPerVeure = stmt.executeQuery("SELECT * FROM paraules WHERE paraula='"+subParaula+"'");
							
							if(countItems(rsPerVeure)>0)
							{		
								rsPerVeure.first(); 
								id_paraula=rsPerVeure.getInt(1);
							}
							
							
							
							
							rsPerVeure = stmt.executeQuery("SELECT * FROM coincidencies WHERE paraula='"+id_paraula+"' AND pagina='"+id_pagina+"'");
							rsPerVeure.first();
							coincidencies=rsPerVeure.getInt("n_coincidencies");

							
							coincidencies++;
							rsPerVeure.updateString("n_coincidencies",new Integer(coincidencies).toString()); 
							rsPerVeure.updateRow();
						
							resultado=m1.find();
						}		
						
					}
					catch(SQLException ex) 
					{  

					   System.out.println("SQLException: " + ex.getMessage()); 
					   System.out.println("SQLState: " + ex.getSQLState()); 
					   System.out.println("VendorError: " + ex.getErrorCode()); 
					   return;
					}
					
					
					resultado=m.find();
				}	
			}
			catch(Exception e)
			{
				System.out.println("ERROR: Pattern!");
				e.printStackTrace();
				
			
			}
		}
		
		
		
}	
