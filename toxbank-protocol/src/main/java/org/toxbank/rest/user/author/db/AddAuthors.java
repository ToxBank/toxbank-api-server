package org.toxbank.rest.user.author.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;
import net.toxbank.client.resource.User;

import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.db.exceptions.InvalidUserException;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.user.DBUser;

public class AddAuthors extends AbstractObjectUpdate<DBProtocol> {
	
	public AddAuthors(DBProtocol protocol) {
		super(protocol);
	}
	public AddAuthors() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidProtocolException();
		if (getObject().getAuthors()==null || getObject().getAuthors().size()==0) throw new InvalidUserException("No authors!");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (User author: getObject().getAuthors()) {
			params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
			params.add(new QueryParam<Integer>(Integer.class, getObject().getVersion()));
			if (author instanceof DBUser) {
				if (((DBUser)author).getID()<=0)
					throw new InvalidUserException(author.getResourceURL().toString());
				params.add(new QueryParam<Integer>(Integer.class, ((DBUser)author).getID()));
			} else throw new InvalidUserException(author.getResourceURL().toString());
		}
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		b.append(AddAuthor.sql_addAuthor);
		if (getObject()==null || getObject().getID()<=0) throw new InvalidProtocolException();
		if (getObject().getAuthors()==null ) throw new InvalidUserException("No authors!");
		String d = "";
		for (User author: getObject().getAuthors()) {
			b.append(d);
			b.append("(?,?,?)");
			d = ",";
		}
		return new String[] {b.toString()};
	}
	public void setID(int index, int id) {
			
	}
}