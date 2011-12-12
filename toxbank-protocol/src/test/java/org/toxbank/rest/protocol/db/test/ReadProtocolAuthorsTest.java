package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.ReadAuthor;

public class ReadProtocolAuthorsTest extends QueryTest<ReadAuthor> {

	@Override
	protected ReadAuthor createQuery() throws Exception {
		return new ReadAuthor(new DBProtocol(1,1),null);
	}

	@Override
	protected void verify(ReadAuthor query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBUser user = query.getObject(rs);
			Assert.assertEquals(1,user.getID());
			Assert.assertEquals("guest",user.getUserName());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}