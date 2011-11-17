package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.p.QueryExecutor;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Before;
import org.junit.Test;


public abstract class QueryTest<T extends IQueryObject> extends DbUnitTest {
	protected T query;
	protected QueryExecutor<T> executor;
	protected String dbFile = "src/test/resources/org/toxbank/protocol/tb.xml";	
	public String getDbFile() {
		return dbFile;
	}

	public void setDbFile(String dbFile) {
		this.dbFile = dbFile;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		query = createQuery();
		query.setId(-1);
		executor = new QueryExecutor<T>();
	}
	
	@Test
	public void testSelect() throws Exception {
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(query); 
			Assert.assertNotNull(rs);
			verify(query,rs);
		} finally {
			if (rs != null) rs.close();
			c.close();
		}
	}
	protected abstract T createQuery() throws Exception;
	protected abstract void verify(T query, ResultSet rs) throws Exception ;
}
