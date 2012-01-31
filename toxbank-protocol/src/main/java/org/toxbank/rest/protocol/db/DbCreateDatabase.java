/* DbCreateDatabase.java
 * Author: Nina Jeliazkova
 * Date: May 6, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package org.toxbank.rest.protocol.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;

/**
 * Creates tables in an existing database. If the database does not exist, or has tables, the call will fail.
 * Note the behaviour is changed from ambit-db!
 * @author nina
 *
 */
public class DbCreateDatabase extends AbstractDBProcessor<String,String> {
	
	public final static String version = "1.5";
	
    /**
	 */
	private static final long serialVersionUID = -335737998721944578L;
	public static final String SQLFile = "org/toxbank/rest/protocol/db/sql/tb.sql";
	
	public DbCreateDatabase() {
		super();
	}
	

	@Override
	public String process(String database) throws AmbitException {
		try {
			if (!dbExists(database)) 
				throw new AmbitException(
						String.format("Database `%s` does not exist. \nPlease create the database and grant privileges.",database));
						
			List<String> tables = tablesExists(database);
	        if (tables.size()==0)
	        	createTables(database);
	        else if (!tables.contains("version")) { //
	        	dropTables(database,tables);
	        	createTables(database);
	        } else throw new AmbitException(String.format("Empty database `%s` is expected, but it has %d tables!",database,tables));
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
        try {
        	Preferences.setProperty(Preferences.DATABASE, database.toString());
        	Preferences.saveProperties(getClass().getName());
        } catch (Exception x) {
        	throw new AmbitException(x);
        }
        return database;
    }
	/*
    public void createDatabase(String newDb,boolean dropifexist) throws SQLException {
            Statement t = connection.createStatement();
            if (dropifexist)
            	t.addBatch(String.format("DROP DATABASE IF EXISTS `%s`",newDb));
            t.addBatch(String.format("CREATE SCHEMA IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ",newDb));
            t.addBatch(String.format("USE `%s`",newDb));
            t.executeBatch();
            t.close();
    }
    */
	public boolean dbExists(String dbname) throws Exception {
		boolean ok = false;
		ResultSet rs = null;
		Statement st = null;
		try {
    	
			st = connection.createStatement();
			rs = st.executeQuery("show databases");
			while (rs.next()) {
				if (dbname.equals(rs.getString(1))) {
					ok = true;
					//break; there was smth wrong with not scrolling through all records
				}
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}
		return ok;
	}	
	public List<String> tablesExists(String dbname) throws Exception {
		int tables = 0;
		ResultSet rs = null;
		Statement st = null;
		List<String> table_names = new ArrayList<String>();
		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`",dbname)); //just in case
		} catch (Exception x) {
			throw x;			
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}			
		try {
			st = connection.createStatement();
			rs = st.executeQuery("show tables");
			while (rs.next()) {
				tables++;
				table_names.add(rs.getString(1));
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}
		return table_names;
	}		
	public void dropTables(String dbname,List<String> table_names) throws Exception {
			dropTables(dbname, table_names.toArray(new String[table_names.size()]));
	}
	public void dropTables(String dbname,String[] table_names) throws Exception {
		Statement st = null;
		try {
			st = connection.createStatement();
			st.addBatch(String.format("Use `%s`",dbname)); //just in case
			st.addBatch("SET FOREIGN_KEY_CHECKS = 0");
			for (String table : table_names) {
				String sql = String.format("drop table `%s`",table);
				st.addBatch(sql); 
			}
			st.addBatch("SET FOREIGN_KEY_CHECKS = 1");
			st.executeBatch();
		} catch (Exception x) {
			throw x;			
		} finally {
			try {if (st != null) st.close();} catch (Exception x) {}
		}			
	
	}			
	
	public String getDbVersion(String dbname) throws Exception {
		String version = null;
		ResultSet rs = null;
		Statement st = null;
		
		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`",dbname)); //just in case
		} catch (Exception x) {
			throw x;			
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}			
		try {
			st = connection.createStatement();
			rs = st.executeQuery("select concat(idmajor,'.',idminor) from version");
			while (rs.next()) {
				version = rs.getString(1);
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}
		return version;
	}		
    public void createTables(String newDB) throws SQLException, FileNotFoundException {
        try {
        	//URL url = this.getClass().getClassLoader().getResource(getSQLFile());
        	//if (url ==null) throw new FileNotFoundException(String.format("Can't find %s!",url.getFile()));
        	
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(getSQLFile());
                  
            if (in == null) throw new FileNotFoundException(String.format("Can't find %s!",getSQLFile()));
            
                Statement t = connection.createStatement();
                t.execute(String.format("USE `%s`",newDB));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;                         
                StringBuffer table = new StringBuffer();
                String delimiter = ";";
                while (true) {

                        line = reader.readLine();
                        if (line == null) break;
                        if (line.toUpperCase().startsWith("DELIMITER")) {
                            delimiter = line.substring(line.indexOf("DELIMITER")+10).trim();
                            logger.debug(table.toString());
                            //t.execute(table.toString());
                            table = new StringBuffer();                             
                            continue;
                        }
                        
                        if (line.trim().toUpperCase().startsWith("END "+delimiter)) {
                            table.append("END");
                            int ok = t.executeUpdate(table.toString());
                            logger.debug(table.toString());
                            table = new StringBuffer();                            
                            continue;
                        }                        
                        if (line == null) break;
                        if (line.trim().equals("")) continue;
                        if (line.indexOf("--") == 0) continue;
                        table.append(line);
                        table.append("\n");
                        if (line.indexOf(delimiter) >= 0) {
                            //t.addBatch(table.toString());
                            logger.debug(table.toString());
                            t.executeUpdate(table.toString());
                            
                            logger.debug(table.toString());
                            table = new StringBuffer();
                        }

                }
                in.close();
                reader.close();
                
                t.close();
        } catch (IOException x) {
            throw new SQLException(x.getMessage());
        }
        finally {
        	
        }
    }
    
    public synchronized String getSQLFile() {
        return SQLFile;
    }
  
    public boolean isSameVersion(String dbVersion) throws Exception {
    	return version.equals(dbVersion);
    }
}
