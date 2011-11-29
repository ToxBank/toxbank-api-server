package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.db.ReadUser;

public class ReadUserTest  extends QueryTest<ReadUser> {

	@Override
	protected ReadUser createQuery() throws Exception {
		DBUser user = new DBUser(1);
		return new ReadUser(user);
	}

	@Override
	protected void verify(ReadUser query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBUser user = query.getObject(rs);
			Assert.assertEquals(1,user.getID());
			Assert.assertEquals("guest",user.getUserName());
			Assert.assertEquals("abcdef",user.getFirstname());
			Assert.assertEquals("ABCDEF",user.getLastname());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}


}
