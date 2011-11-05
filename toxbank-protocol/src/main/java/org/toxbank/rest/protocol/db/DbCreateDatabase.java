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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;

import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;

public class DbCreateDatabase extends AbstractDBProcessor<String,String> {
	
    /**
	 */
	private static final long serialVersionUID = -335737998721944578L;
	public static final String SQLFile = "org/toxbank/rest/protocol/db/sql/tb.sql";
	protected boolean dropifexist=false;
	
	public DbCreateDatabase() {
		this(false);
	}
	public DbCreateDatabase(Boolean dropifexist) {
		super();
		this.dropifexist = dropifexist;
	}
	

	@Override
	public String process(String database) throws AmbitException {
		try {
	        createDatabase(database,dropifexist);
	        createTables(database);
		} catch (Exception x) {}
		
        try {
        	Preferences.setProperty(Preferences.DATABASE, database.toString());
        	Preferences.saveProperties(getClass().getName());
        } catch (Exception x) {}
        return database;
    }
	
    public void createDatabase(String newDb,boolean dropifexist) throws SQLException {
            Statement t = connection.createStatement();
            if (dropifexist)
            	t.addBatch(String.format("DROP DATABASE IF EXISTS `%s`",newDb));
            t.addBatch(String.format("CREATE SCHEMA IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ",newDb));
            t.addBatch("USE `"+newDb+"`");
            t.executeBatch();
            t.close();
    }
    /*
    public void createTables(String newDB) throws SQLException {
    	 InputStream in = this.getClass().getClassLoader().getResourceAsStream(
                 getSQLFile());
    	try {
	    	ScriptRunner script = new ScriptRunner(connection,false,false);
	    	
	    	script.runScript(new  BufferedReader(new InputStreamReader(in)));
    	} catch (IOException x) {
    		x.printStackTrace();
    		throw new SQLException(x.getMessage());
    	}
    	finally {
    		try {in.close(); } catch (Exception x) {x.printStackTrace();}
    	}
    	
    }
    */
    
    public void createTables(String newDB) throws SQLException, FileNotFoundException {
        try {
        	URL url = this.getClass().getClassLoader().getResource(getSQLFile());
        	if (url ==null) throw new FileNotFoundException(String.format("Can't find %s!",url.getFile()));
        	
            InputStream in = new FileInputStream(url.getFile());
                  
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
  
}
