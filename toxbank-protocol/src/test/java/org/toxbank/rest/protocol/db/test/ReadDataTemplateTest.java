package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.template.ReadFilePointers;

public class ReadDataTemplateTest  extends QueryTest<ReadFilePointers> {

	@Override
	protected ReadFilePointers createQuery() throws Exception {
		DBProtocol protocol = new DBProtocol(1,1);
		return new ReadFilePointers(protocol);
	}

	@Override
	protected void verify(ReadFilePointers query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBProtocol protocol = query.getObject(rs);
			Assert.assertEquals(1,protocol.getID());
			Assert.assertNotNull(protocol.getDataTemplate().getResourceURL());
			
			Assert.assertEquals("http://example.com/protocol/P1/dataTemplate",protocol.getDataTemplate().getResourceURL().toString());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}
