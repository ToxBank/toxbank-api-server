package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;

public class ReadProtocolTest extends QueryTest<ReadProtocol> {

	@Override
	protected ReadProtocol createQuery() throws Exception {
		return new ReadProtocol(new Integer(2));
	}

	@Override
	protected void verify(ReadProtocol query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			IProtocol protocol = query.getObject(rs);
			Assert.assertEquals(2,protocol.getID());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}
