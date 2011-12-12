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

import java.net.URL;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.toxbank.client.resource.Document;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.db.DeleteProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.AddAuthors;

public final class Protocol_crud_test  extends CRUDTest<Object,DBProtocol>  {
	String file = "http://localhost/1.pdf";

	@Override
	protected IQueryUpdate<Object,DBProtocol> createQuery() throws Exception {
		DBProtocol ref = new DBProtocol();
		//ref.setID(3);
		//ref.setVersion(1);
		ref.setTitle("title");
		ref.setAbstract("abstract");
		DBUser user = new DBUser();
		user.setID(1);
		ref.setOwner(user);
		ref.setProject(new DBProject(1));	
		ref.setOrganisation(new DBOrganisation(1));
		ref.setSummarySearchable(true);
		ref.setDocument(new Document(new URL(file)));
		return new CreateProtocol(ref);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,DBProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idprotocol,summarySearchable FROM protocol where title='title' and abstract='abstract' and iduser='1' and idproject=1 and idorganisation=1 and filename='%s'",file));
		
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(Boolean.TRUE,table.getValue(0,"summarySearchable"));
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,DBProtocol> deleteQuery() throws Exception {
		DBProtocol ref = new DBProtocol(2,1);
		return new DeleteProtocol(ref);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,DBProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idprotocol FROM protocol where idprotocol=2");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}
	
	/**
	 * Adds authors to a protocol
	 */
	@Override
	protected IQueryUpdate<Object,DBProtocol> updateQuery() throws Exception {
		DBProtocol ref = new DBProtocol(1,1);
		ref.addAuthor(new DBUser(1));
		ref.addAuthor(new DBUser(2));

		return new AddAuthors(ref);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,DBProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idprotocol,version,iduser FROM protocol_authors where idprotocol=1");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<Object, DBProtocol> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, DBProtocol> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}
