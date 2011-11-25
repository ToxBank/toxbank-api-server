package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.groups.DBGroup;
import org.toxbank.rest.groups.DBGroup.GroupType;
import org.toxbank.rest.groups.db.ReadGroup;

public class ReadProjectTest  extends QueryTest<ReadGroup> {

	@Override
	protected ReadGroup createQuery() throws Exception {
		return new ReadGroup(GroupType.PROJECT,new Integer(2));
	}

	@Override
	protected void verify(ReadGroup query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBGroup group = query.getObject(rs);
			Assert.assertEquals(2,group.getID());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}
