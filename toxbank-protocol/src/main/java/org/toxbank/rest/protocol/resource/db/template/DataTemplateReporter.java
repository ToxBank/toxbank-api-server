package org.toxbank.rest.protocol.resource.db.template;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.toxbank.rest.protocol.DBProtocol;

/**
 * Reports template content
 * @author nina
 *
 * @param <Q>
 */
public class DataTemplateReporter extends QueryReporter<DBProtocol,IQueryRetrieval<DBProtocol>, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5846954016565174817L;
	@Override
	public void footer(Writer output, IQueryRetrieval<DBProtocol> query) {
	}

	@Override
	public void header(Writer output, IQueryRetrieval<DBProtocol> query) {
	}

	@Override
	public Object processItem(DBProtocol item) throws AmbitException {
		try {
			output.write(item.getDataTemplate().getResourceURL().toString());

		} catch (Exception x) {
			
		}
		return null;
	}

	@Override
	public void open() throws Exception {

		super.open();
	}
}
