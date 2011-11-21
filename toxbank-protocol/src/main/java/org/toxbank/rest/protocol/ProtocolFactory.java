package org.toxbank.rest.protocol;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.metadata.Author;
import org.toxbank.rest.protocol.metadata.Document;
import org.toxbank.rest.protocol.metadata.Project;

public class ProtocolFactory {
	
	public static IProtocol getProtocol(List<FileItem> items, long maxSize) throws ResourceException {
		
		Protocol protocol = new Protocol();
		for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
			FileItem fi = it.next();
		//	System.out.println(String.format("%s\t%s", fi.getFieldName(),fi.getString()));
			try {
				ReadProtocol.fields field  = null;
				try { 
					field = ReadProtocol.fields.valueOf(fi.getFieldName());
					
				} catch (Exception x) {
					x.printStackTrace();
					continue;
				}
				if (field==null) continue;
				switch (field) {
				case idprotocol: continue;
				case identifier: {
					protocol.setIdentifier(fi.getString());
					break;
				}
				case anabstract: {
					protocol.setAnAbstract(fi.getString());
					break;
				}
				case filename: {
					if (fi.isFormField()) {
						protocol.setDocument(new Document(new URI(fi.getString())));
					} else {	
						if (fi.getSize()==0)  throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,"Empty file!"));
						File file = null;
				        if (fi.getName()==null)
				           	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"File name can't be empty!");
				        else
				          	file = new File(
				            		String.format("%s/%s",
				            				System.getProperty("java.io.tmpdir"),
				            				fi.getName()));
				        fi.write(file);
				        protocol.setDocument(new Document(file.toURI()));		
					}
			        break;
				}
				case project: {
					protocol.setProject(new Project(fi.getString()));
					break;					
				}
				case title: {
					protocol.setTitle(fi.getString());
					break;
				}
				case author: {
					protocol.setAuthor(new Author(fi.getString()));
					break;
				}
				case summarySearchable: {
					try {
					protocol.setSummarySearchable(Boolean.parseBoolean(fi.getString()));
					} catch (Exception x) { protocol.setSummarySearchable(false);}
					break;					
				}
				} //switch
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			} 

		}
		if (protocol.getIdentifier()==null) protocol.setIdentifier(String.format("SEURAT-%s",UUID.randomUUID()));
		return protocol;
	}
	
}
