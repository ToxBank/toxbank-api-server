package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.MyProtocol;
import org.toxbank.rest.protocol.db.template.ReadDataTemplate;

public class ReadDataTemplateTest  extends QueryTest<ReadDataTemplate> {

	@Override
	protected ReadDataTemplate createQuery() throws Exception {
		MyProtocol protocol = new MyProtocol(1);
		return new ReadDataTemplate(protocol);
	}

	@Override
	protected void verify(ReadDataTemplate query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			IProtocol protocol = query.getObject(rs);
			Assert.assertEquals(1,protocol.getID());
			Assert.assertNotNull(protocol.getTemplate());
			Assert.assertEquals("ABCDEFGH",protocol.getTemplate().toString());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}
