package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.user.DBUser;

public class ReadProtocolByUserTest extends QueryTest<ReadProtocol> {

	@Override
	protected ReadProtocol createQuery() throws Exception {
		ReadProtocol q = new ReadProtocol();
		q.setFieldname(new DBUser(2));
		return q;
	}

	@Override
	protected void verify(ReadProtocol query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBProtocol protocol = query.getObject(rs);
			Assert.assertEquals(2,protocol.getID());
			Assert.assertNotNull(protocol.getKeywords());
			Assert.assertEquals(3,protocol.getKeywords().size());
			Assert.assertNotNull(protocol.getOwner());
			//Assert.assertNotNull(protocol.getOwner().getFirstname());
			records++;
		}
		Assert.assertEquals(2,records);
		
	}

}
