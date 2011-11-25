package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.db.ReadOrganisation;

public class ReadOrganisationTest extends QueryTest<ReadOrganisation> {
	@Override
	protected ReadOrganisation createQuery() throws Exception {
		DBOrganisation p = new DBOrganisation();
		p.setID(2);
		return new ReadOrganisation(p);
	}

	@Override
	protected void verify(ReadOrganisation query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBOrganisation group = query.getObject(rs);
			Assert.assertEquals(2,group.getID());
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}
