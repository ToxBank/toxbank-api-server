package org.toxbank.rest.protocol.db.test;

import java.math.BigInteger;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.toxbank.client.resource.Alert.RecurrenceFrequency;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.alerts.db.AddAlert;
import org.toxbank.rest.user.alerts.db.DBAlert;
import org.toxbank.rest.user.alerts.db.DeleteAlert;
import org.toxbank.rest.user.alerts.db.UpdateAlertSentTimeStamp;

public class Alert_crud_test  extends CRUDTest<DBUser,DBAlert>  {


	@Override
	protected IQueryUpdate<DBUser,DBAlert> createQuery() throws Exception {
		DBAlert alert = new DBAlert();
		alert.setQueryString("rabbit");
		alert.setRecurrenceFrequency(RecurrenceFrequency.monthly);
		alert.setTitle("Query");
		DBUser user = new DBUser(2);
		return new AddAlert(alert,user);
	}

	@Override
	protected void createVerify(IQueryUpdate<DBUser,DBAlert> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idquery,iduser,query,qformat,rfrequency,rinterval,username from alert join user using(iduser) where iduser=2"));
		
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("rabbit",table.getValue(0,"query"));
		Assert.assertEquals("monthly",table.getValue(0,"rfrequency"));
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"rinterval"));
		Assert.assertEquals("FREETEXT",table.getValue(0,"qformat"));
		c.close();
	}

	@Override
	protected IQueryUpdate<DBUser,DBAlert> deleteQuery() throws Exception {
		DBAlert ref = new DBAlert(2);
		return new DeleteAlert(ref,null);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<DBUser,DBAlert> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT iduser,idquery FROM alert where iduser=1");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<DBUser,DBAlert> updateQuery() throws Exception {
		DBAlert ref = new DBAlert();
		ref.setID(2);

		return new UpdateAlertSentTimeStamp(ref,null);
	}

	@Override
	protected void updateVerify(IQueryUpdate<DBUser,DBAlert> query)
			throws Exception {

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT sent FROM alert where idquery=2");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<DBUser,DBAlert> createQueryNew()
			throws Exception {
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<DBUser,DBAlert> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}