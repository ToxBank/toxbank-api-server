package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.toxbank.resource.IProtocol;

/**
 * Generates URI for {@link ReferenceResource}
 * @author nina
 *
 * @param <Q>
 */
public class ProtocolQueryURIReporter <Q extends IQueryRetrieval<IProtocol>> extends QueryURIReporter<IProtocol, Q> {
	String suffix = "";
	

	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public ProtocolQueryURIReporter(Request baseRef,String suffix) {
		super(baseRef,null);	
		this.suffix = suffix;
	}
	public ProtocolQueryURIReporter(Request baseRef) {
		this(baseRef,"");
	}
	public ProtocolQueryURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, IProtocol item) {

		return String.format("%s%s/P%d%s",ref,IProtocol.resource,item.getID(),suffix);
	}

}
