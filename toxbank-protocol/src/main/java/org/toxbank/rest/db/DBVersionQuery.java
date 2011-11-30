package org.toxbank.rest.db;

import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.NumberCondition;
import net.idea.modbcum.q.query.AbstractQuery;

public class DBVersionQuery extends AbstractQuery<String, String, NumberCondition, DBVersion> 
										implements IQueryRetrieval<DBVersion>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -915159203431471645L;
	public DBVersionQuery() {
		super();
		setPage(0);
		setPageSize(1);
	}
	public List<QueryParam> getParameters() throws AmbitException {

		return null;
	}

	public String getSQL() throws AmbitException {
		return "Select idmajor,idminor,date,comment from version order by idmajor,idminor desc";
	}
	public double calculateMetric(DBVersion object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	public DBVersion getObject(ResultSet rs) throws AmbitException {
		
		
		DBVersion db = new DBVersion();
		try {
			db.setDbname(rs.getMetaData().getCatalogName(1));
			db.setMajor(rs.getInt(1));
			db.setMinor(rs.getInt(2));
			db.setCreated(rs.getLong(3));
			db.setComments(rs.getString(4));
			return db;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
}
