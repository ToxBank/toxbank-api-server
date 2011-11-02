package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.db.CallableQueryProcessor;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.convertors.RDFJenaConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.db.ReadProtocol;

/**
 * Protocol resource
 * @author nina
 *
 * @param <Q>
 */
public class ProtocolDBResource	extends QueryResource<ReadProtocol,IProtocol> {

	
	protected boolean singleItem = false;
	protected boolean editable = true;

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter(),MediaType.TEXT_PLAIN);
			
		} else
		*/ 
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	
						new ProtocolQueryURIReporter(getRequest())
						,MediaType.TEXT_URI_LIST);
				
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
					variant.getMediaType().equals(MediaType.APPLICATION_JSON) ||
					variant.getMediaType().equals(MediaType.TEXT_CSV) 
					
					) {
				return new RDFJenaConvertor<IProtocol, IQueryRetrieval<IProtocol>>(
						new ProtocolRDFReporter<IQueryRetrieval<IProtocol>>(
								getRequest(),variant.getMediaType(),getDocumentation())
						,variant.getMediaType());					
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML))
				return new OutputWriterConvertor(
						new ProtocolQueryHTMLReporter(getRequest(),!singleItem,editable),
						MediaType.TEXT_HTML);
		else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}

	@Override
	protected ReadProtocol createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = request.getResourceRef().getQueryAsForm();
		Object search = null;
		try {
			search = form.getFirstValue("search").toString();
		} catch (Exception x) {
			search = null;
		}		
		try {
			editable = Boolean.parseBoolean(form.getFirstValue("new").toString());
		} catch (Exception x) {
			x.printStackTrace();
			editable = false;
		}
		Object key = request.getAttributes().get(FileResource.resourceKey);		
		try {
			if (key==null) key = search;
			
			if (key==null) {
				ReadProtocol query = new ReadProtocol();
//				query.setFieldname(search.toString());
				return query;
			}			
			else {
				if (key.toString().startsWith("P")) {
					singleItem = true;
					return new ReadProtocol(new Integer(Reference.decode(key.toString().substring(1))));
				} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid protocol id %d",key),
					x
					);
		}
	} 

	@Override
	protected QueryURIReporter<IProtocol, ReadProtocol> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ProtocolQueryURIReporter(getRequest());
	}

	@Override
	public String getConfigFile() {
		return "conf/tbprotocol-db.pref";
	}
	
/*
    @Override
    protected CallableQueryProcessor createCallable(Method method, Form form,
    		IProtocol item) throws ResourceException {
    	return super.createCallable(method, form, item);
    }
    
    @Override
    protected boolean isAllowedMediaType4Upload(MediaType mediaType) {
    	return MediaType.APPLICATION_PDF.equals(mediaType);
    }
    */
}
