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

import java.net.URI;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.resource.IProject;
import org.toxbank.resource.IProtocol;
import org.toxbank.resource.IUser;
import org.toxbank.rest.protocol.MyProtocol;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.db.DeleteProtocol;
import org.toxbank.rest.protocol.db.UpdateProtocol;
import org.toxbank.rest.protocol.metadata.Document;

public final class Protocol_crud_test  extends CRUDTest<Object,IProtocol>  {
	String file = "http://localhost/1.pdf";

	@Override
	protected IQueryUpdate<Object,IProtocol> createQuery() throws Exception {
		IProtocol ref = new MyProtocol();
		ref.setIdentifier("identifier");
		ref.setTitle("title");
		ref.setAbstract("abstract");
		ref.setAuthor(new IUser() {
			public String toString() { return "author";}
		});
		ref.setProject(new IProject() {
			public String toString() { return "project";}
		});		
		ref.setSummarySearchable(true);
		ref.setDocument(new Document(new URI(file)));
		return new CreateProtocol(ref);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idprotocol,summarySearchable FROM protocol where identifier='identifier' and title='title' and abstract='abstract' and author='author' and project='project' and filename='%s'",file));
		
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(Boolean.TRUE,table.getValue(0,"summarySearchable"));
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,IProtocol> deleteQuery() throws Exception {
		IProtocol ref = new MyProtocol(2);
		return new DeleteProtocol(ref);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idprotocol FROM protocol where idprotocol=2");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	public void testUpdate() throws Exception {
		//TODO Not iplemented
	}
	@Override
	protected IQueryUpdate<Object,IProtocol> updateQuery() throws Exception {
		IProtocol ref = new MyProtocol();
		ref.setAbstract("NEW");
		ref.setID(2);

		return new UpdateProtocol(ref);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,IProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT abstract FROM protocol where idprotocol=2");
		Assert.assertEquals(1,table.getRowCount());

		Assert.assertEquals("NEW",table.getValue(0,"abstract"));
		
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
