/* DbUnitTest.java
 * Author: nina
 * Date: Jan 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package org.toxbank.rest.protocol.db.test;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.toxbank.rest.protocol.db.DbCreateDatabase;


public abstract class DbUnitTest {
	protected Properties properties;
	
	protected void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(
					"conf/tbprotocol-db.pref");
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}
	protected String getHost() {
		loadProperties();
		String p = properties.getProperty("Host");
		return p==null?"localhost":
			("${toxbank.db.host}".equals(p))?"localhost":p;
	}
	protected String getDatabase() {
		loadProperties();
		String p = properties.getProperty("database.test");
		return (p==null)||("${toxbank.db}".equals(p))?"tb-test":p;
	}
	protected String getPort() {
		loadProperties();
		String p = properties.getProperty("database.test.port");
		return p==null?"3306":p;		
	}
	protected String getUser() {
		loadProperties();
		String p = properties.getProperty("database.user.test");
		return (p==null) || ("${toxbank.db.user.test}".equals(p))?"guest":p;			
	}
	protected String getPWD() {
		loadProperties();
		String p = properties.getProperty("database.user.test.password");
		return (p==null) || ("${toxbank.db.user.test.password}".equals(p))?"guest":p;	
	}
	/*
	protected String getAdminUser() {
		return "root";
	}
	protected String getAdminPWD() {
		loadProperties();
		String p = properties.getProperty("database.user.root.password");
		return (p==null) || ("${toxbank.db.user.root.password}".equals(p))?"":p;	
	}
	*/	
	@Before
	public void setUp() throws Exception {
		IDatabaseConnection c = getConnection(getHost(),getDatabase(),getPort(),getUser(),getPWD());
		Connection conn = c.getConnection();
		conn.setAutoCommit(false);
		try {
				DbCreateDatabase db = new DbCreateDatabase();
				db.setConnection(conn);
				if (!db.dbExists(getDatabase())) {
					db.process(getDatabase());
				} else if (db.tablesExists(getDatabase())==0) {
					db.process(getDatabase());
				}
				conn.commit();
		} catch (Exception x) {
			conn.rollback();
			throw x;
		} finally {
			c.close();
		}

	}
	protected IDatabaseConnection getConnection(String host,String db,String port,String user, String pass) throws Exception {
		  
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF-8&profileSQL=%s",
                		host,port,db,Boolean.toString(isProfileSQL()))
                , user,pass);
//SET NAMES utf8	        
	   return new DatabaseConnection(jdbcConnection);
	}	
	protected boolean isProfileSQL() {
		return false;
	}
	protected IDatabaseConnection getConnection() throws Exception {
	   return getConnection(getHost(),getDatabase(),getPort(),getUser(),getPWD());
	}
    public void setUpDatabase(String xmlfile) throws Exception {
    	//This ensures all tables as defined in the schema are cleaned up, and is a single place to modify if a schema changes
    	initDB("src/test/resources/org/toxbank/protocol/tables.xml",DatabaseOperation.DELETE_ALL,true);
    	//This will import only records, defined in the xmlfile
    	initDB(xmlfile,DatabaseOperation.INSERT,false);
    }
    
    private void initDB(String xmlfile,DatabaseOperation op,boolean admin ) throws Exception {
        IDatabaseConnection connection = admin?getConnection(getHost(),getDatabase(),getPort(),
        		getUser(),getPWD()):getConnection();
        		//getAdminUser(),getAdminPWD()):getConnection();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setCaseSensitiveTableNames(false);
        IDataSet dataSet = builder.build(new File(xmlfile));
        try {
            //DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
            op.execute(connection, dataSet);
        } catch (Exception x) {
        	x.printStackTrace();
        	throw x;
        } finally {
            connection.close();

        }
    }
}
