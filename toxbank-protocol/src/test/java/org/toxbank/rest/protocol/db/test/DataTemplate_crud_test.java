package org.toxbank.rest.protocol.db.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.DataTemplate;
import org.toxbank.rest.protocol.Protocol;
import org.toxbank.rest.protocol.db.template.UpdateDataTemplate;

public class DataTemplate_crud_test  extends CRUDTest<Object,IProtocol> {

	@Override
	protected IQueryUpdate<Object,IProtocol> createQuery() throws Exception {
		return null;
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {

	}

	@Override
	public void testCreate() throws Exception {

	}
	@Override
	public void testDelete() throws Exception {
		//TODO Not iplemented
	}

	
	@Override
	protected IQueryUpdate<Object,IProtocol> updateQuery() throws Exception {
		Protocol protocol = new Protocol(1);
		protocol.setTemplate(new DataTemplate("ABCDEFGH"));
		return new UpdateDataTemplate(protocol);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idprotocol,uncompress(template) as t from protocol where idprotocol=1"));
		
		Assert.assertEquals(1,table.getRowCount());
		//dbunit is confused ...
		//Assert.assertEquals("ABCDEFGH",table.getValue(0,"t"));
		c.close();
	}

	@Override
	protected IQueryUpdate<Object, IProtocol> createQueryNew()
			throws Exception {

		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, IProtocol> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

	@Override
	protected IQueryUpdate<Object, IProtocol> deleteQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object, IProtocol> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
