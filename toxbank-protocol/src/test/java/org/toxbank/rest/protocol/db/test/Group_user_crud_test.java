package org.toxbank.rest.protocol.db.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.user.db.AddGroupsPerUser;
import org.toxbank.rest.groups.user.db.DeleteGroupsPerUser;
import org.toxbank.rest.user.DBUser;

public class Group_user_crud_test  extends CRUDTest<DBUser,List<IDBGroup>> {

	@Override
	protected IQueryUpdate<DBUser, List<IDBGroup>> createQuery()
			throws Exception {
		return new AddGroupsPerUser(new DBUser(3),new DBProject(1));
	}

	@Override
	protected IQueryUpdate<DBUser, List<IDBGroup>> createQueryNew()
			throws Exception {
		return new AddGroupsPerUser<IDBGroup>(new DBUser(3),new DBOrganisation(1));
	}

	@Override
	protected IQueryUpdate<DBUser, List<IDBGroup>> updateQuery()
			throws Exception {
		return null;
	}

	@Override
	protected IQueryUpdate<DBUser, List<IDBGroup>> deleteQuery()
			throws Exception {
		DBUser ref = new DBUser(1);
		List<IDBGroup> p = new ArrayList<IDBGroup>();
		p.add(new DBProject(1));
		DeleteGroupsPerUser q = new DeleteGroupsPerUser();
		q.setGroup(ref);
		q.setObject(p);
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<DBUser, List<IDBGroup>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT idproject from user_project where iduser=3 and idproject=1");
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<DBUser, List<IDBGroup>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT idorganisation from user_organisation where iduser=3 and idorganisation=1");
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

	@Override
	protected void updateVerify(IQueryUpdate<DBUser, List<IDBGroup>> query)
			throws Exception {
		
	}

	@Override
	protected void deleteVerify(IQueryUpdate<DBUser, List<IDBGroup>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT idproject from user_project where iduser=1 and idproject=1");
		
		Assert.assertEquals(0,table.getRowCount());
		c.close();
	}

	@Override
	public void testUpdate() throws Exception {
	}
}
