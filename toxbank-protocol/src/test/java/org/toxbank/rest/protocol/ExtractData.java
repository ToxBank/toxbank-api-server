package org.toxbank.rest.protocol;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.toxbank.rest.protocol.db.test.DbUnitTest;

/**
 * An utility to generate dump of the database in DbUnit format, to be used in tests
 * @author nina
 *
 */
public class ExtractData extends DbUnitTest {
	
    public static void main(String[] args) throws Exception {
    	ExtractData ed = new ExtractData();
    	ed.extract();
    }
    
    protected void extract() throws Exception {
    	IDatabaseConnection connection = null;
    	try {
	        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
	        Connection jdbcConnection = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/tb", getUser(), getPWD());
	        connection = new DatabaseConnection(jdbcConnection);
	
	        // partial database export
	        QueryDataSet partialDataSet = new QueryDataSet(connection);
	        partialDataSet.addTable("protocol", "SELECT * FROM protocol");
	        
	        FlatDtdDataSet.write(partialDataSet, new FileOutputStream(
	        			"src/test/resources/org/toxbank/protocol/partial-dataset.dtd"));
	        FlatXmlDataSet.write(partialDataSet, 
	        		new FileOutputStream("src/test/resources/org/toxbank/protocol/partial-dataset.xml"));
	        
	        /*
	        // full database export
	        IDataSet fullDataSet = connection.createDataSet();
	        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full-dataset.xml"));
	        */
    	} finally {
    		try {connection.close(); } catch (Exception x) {}
    	}
    }
}
