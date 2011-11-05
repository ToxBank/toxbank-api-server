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

package org.toxbank.rest.protocol.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.Protocol;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.db.DeleteProtocol;
import org.toxbank.rest.protocol.db.UpdateProtocol;
import org.toxbank.rest.protocol.db.update.CRUDTest;

public final class Protocol_crud_test  extends CRUDTest<Object,IProtocol>  {

	@Override
	protected IQueryUpdate<Object,IProtocol> createQuery() throws Exception {
		IProtocol ref = new Protocol();
		return new CreateProtocol(ref);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='newtitle' and url='newurl'");
		
		//Assert.assertEquals(1,table.getRowCount());
		//Assert.assertEquals(_type.Model.toString(),table.getValue(0,"type"));
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,IProtocol> deleteQuery() throws Exception {
		IProtocol ref = new Protocol();
		return new DeleteProtocol(ref);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM catalog_references where title='IUPAC name'");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object,IProtocol> updateQuery() throws Exception {
		IProtocol ref = new Protocol();
		ref.setID(2);

		return new UpdateProtocol(ref);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM catalog_references where title='IUPAC name'");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED_USER","SELECT idreference,title,url,type FROM catalog_references where title='New name'");
		//Assert.assertEquals(1,table.getRowCount());
		//Assert.assertEquals(_type.Algorithm.toString(),table.getValue(0,"type"));
		
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object, IProtocol> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, IProtocol> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}
