package org.toxbank.rest.user.author.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.db.exceptions.InvalidUserException;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.user.DBUser;

/**
 * Adds a protocol author
 * @author nina
 *
 */
public class AddAuthor  extends AbstractUpdate<DBProtocol,DBUser> {
	public static final String sql_addAuthor = "insert ignore into protocol_authors (idprotocol,version,iduser) values ";
	protected static final String[] sql = new String[] {
		String.format("%s (?,?,?)",sql_addAuthor)
		};
	
	public AddAuthor(DBProtocol protocol,DBUser author) {
		super(author);
		setGroup(protocol);
	}
	public AddAuthor() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidUserException();
		if (getGroup()==null || getGroup().getID()<=0 || getGroup().getVersion()<=0) throw new InvalidProtocolException();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getVersion()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidUserException();
		if (getGroup()==null || getGroup().getID()<=0 || getGroup().getVersion()<=0) throw new InvalidProtocolException();
		return sql;
	}
	public void setID(int index, int id) {
			
	}
}