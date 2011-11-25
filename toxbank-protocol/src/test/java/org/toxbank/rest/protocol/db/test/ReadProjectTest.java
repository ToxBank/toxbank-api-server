package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.db.ReadProject;

public class ReadProjectTest  extends QueryTest<ReadProject> {

	@Override
	protected ReadProject createQuery() throws Exception {
		DBProject p = new DBProject();
		p.setID(2);
		return new ReadProject(p);
	}

	@Override
	protected void verify(ReadProject query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBProject group = query.getObject(rs);
			Assert.assertEquals(2,group.getID());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}
