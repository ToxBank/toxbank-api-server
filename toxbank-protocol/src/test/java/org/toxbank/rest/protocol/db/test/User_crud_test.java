/* ReferenceCRUDTest.java
 * Author: nina
 * Date: Mar 28, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package org.toxbank.rest.protocol.db.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.db.CreateUser;
import org.toxbank.rest.user.db.DeleteUser;
import org.toxbank.rest.user.db.UpdateUser;

public final class User_crud_test  extends CRUDTest<Object,DBUser>  {


	@Override
	protected IQueryUpdate<Object,DBUser> createQuery() throws Exception {
		DBUser ref = new DBUser();
		ref.setFirstname("QWERTY");
		ref.setLastname("ASDFG");
		return new CreateUser(ref);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,DBUser> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT iduser,username,firstname,lastname from user where firstname='QWERTY'"));
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,DBUser> deleteQuery() throws Exception {
		DBUser ref = new DBUser(3);
		return new DeleteUser(ref);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,DBUser> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT iduser FROM user where iduser=3");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	public void testUpdate() throws Exception {
		//TODO Not implemented
	}
	@Override
	protected IQueryUpdate<Object,DBUser> updateQuery() throws Exception {
		DBUser ref = new DBUser();
		ref.setLastname("NEW");
		ref.setID(2);

		return new UpdateUser(ref);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,DBUser> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT lastname FROM user where iduser=3");
		Assert.assertEquals(1,table.getRowCount());

		Assert.assertEquals("NEW",table.getValue(0,"lastname"));
		
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object, DBUser> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, DBUser> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}
