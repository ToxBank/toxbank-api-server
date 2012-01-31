package org.toxbank.rest.protocol.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

import org.toxbank.rest.policy.PolicyRule;
import org.toxbank.rest.protocol.DBProtocol;

/**
 * 
 * @author nina
 *
 */
public class ReadProtocolAccessLocal extends AbstractQuery<DBProtocol, String, EQCondition, PolicyRule> implements IQueryRetrieval<PolicyRule> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6017803463536586392L;

	public double calculateMetric(PolicyRule object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		if ((getValue() == null) || (getFieldname() == null)) throw new AmbitException("Empty parameters");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getVersion()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return "select idprotocol,version,published,iduser,username from protocol join user using(iduser) where idprotocol=? and version=?";
	}
	/**
	 * If found, will return true always. 
	 */
	public PolicyRule getObject(ResultSet rs) throws AmbitException {
		try {
			boolean sameUsername = getValue().equals(rs.getString("username"));
			boolean published = rs.getBoolean("published");
			return new PolicyRule(getValue(),
					null,
					sameUsername,
					sameUsername & !published,
					sameUsername & !published,
					sameUsername & !published
					);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
				
	}

}