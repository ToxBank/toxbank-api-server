package org.toxbank.rest.protocol.resource.db.template;

import java.io.File;
import java.net.URISyntaxException;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.resource.db.DownloadDocumentConvertor;
import org.toxbank.rest.protocol.resource.db.FileReporter;
import org.toxbank.rest.protocol.resource.db.ProtocolDocumentResource;

public class DataTemplateResource extends ProtocolDocumentResource {

	@Override
	public IProcessor<IQueryRetrieval<DBProtocol>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return new DownloadDocumentConvertor(new FileReporter() {
			@Override
			public Object processItem(DBProtocol item) throws AmbitException {
				try {
					File file = new File(item.getDataTemplate().getResourceURL().toURI());
					setOutput(new FileRepresentation(file,MediaType.TEXT_ALL));
					return item;
				} catch (URISyntaxException x) {
					throw new AmbitException(x);
				}
			}
		});
	}

}


