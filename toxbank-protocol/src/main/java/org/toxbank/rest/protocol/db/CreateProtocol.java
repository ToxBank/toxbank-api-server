/* CreateReference.java
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

package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.protocol.DBProtocol;

public class CreateProtocol extends AbstractObjectUpdate<DBProtocol>{
	public static final String[] create_sql = {
		"insert into protocol (idprotocol,version,identifier,title,abstract,iduser,summarySearchable,idproject,idorganisation,filename) " +
		"values (?,?,?,?,?,?,?,?,?,?)"
	};

	public CreateProtocol(DBProtocol ref) {
		super(ref);
	}
	public CreateProtocol() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		ReadProtocol.fields[] f = new ReadProtocol.fields[] {
				ReadProtocol.fields.idprotocol,
				ReadProtocol.fields.version,
				ReadProtocol.fields.identifier,
				ReadProtocol.fields.title,
				ReadProtocol.fields.anabstract,
				ReadProtocol.fields.iduser,
				ReadProtocol.fields.summarySearchable,
				ReadProtocol.fields.idproject,
				ReadProtocol.fields.idorganisation,
				ReadProtocol.fields.filename
		};
		for (ReadProtocol.fields field: f) 
			params1.add(field.getParam(getObject()));
		
		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	public void setID(int index, int id) {
		getObject().setID(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
