package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.alerts.db.DBAlert;
import org.toxbank.rest.user.alerts.db.ReadAlert;

/**
 * Test for {@link ReadAlert}
 * @author nina
 *
 */
public class ReadAlertTest  extends QueryTest<ReadAlert> {

	@Override
	protected ReadAlert createQuery() throws Exception {
		ReadAlert query = new ReadAlert(null);
		query.setFieldname(new DBUser(1));
		return query;
	}

	@Override
	protected void verify(ReadAlert query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			DBAlert alert = query.getObject(rs);
			Assert.assertEquals(1,alert.getUser().getID());
			Assert.assertNotNull(alert.getUser().getUserName());
			Assert.assertNotNull(alert.getSentAt());
			records++;
		}
		Assert.assertEquals(2,records);
	}

}
