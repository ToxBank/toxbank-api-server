package org.toxbank.rest.protocol;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.toxbank.client.resource.Organisation;
import net.toxbank.client.resource.Project;
import net.toxbank.client.resource.User;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.metadata.Document;
import org.toxbank.rest.user.DBUser;

public class ProtocolFactory {
	
	public static DBProtocol getProtocol(List<FileItem> items, long maxSize) throws ResourceException {
		
		DBProtocol protocol = new DBProtocol();
		for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
			FileItem fi = it.next();
		//	System.out.println(String.format("%s\t%s", fi.getFieldName(),fi.getString()));
			try {
				ReadProtocol.fields field  = null;
				try { 
					String fname = fi.getFieldName();
					if (fname!=null)
						field = ReadProtocol.fields.valueOf(fname);
					
				} catch (Exception x) {
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
					protocol.setAbstract(fi.getString());
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
				case project_uri: {
					Project p = protocol.getProject();
					if (p==null) { p = new DBProject(); protocol.setProject(p);}
					if (fi.getString().startsWith("http"))
						p.setResourceURL(new URL(fi.getString()));
					else p.setTitle(fi.getString());
					break;					
				}
				case user_uri: {
					User p = protocol.getOwner();
					if (p==null) { p = new DBUser(); protocol.setOwner(p);}
					if (fi.getString().startsWith("http"))
						p.setResourceURL(new URL(fi.getString()));
					else p.setUserName(fi.getString());
					break;					
				}				
				case organisation_uri: {
					Organisation p = protocol.getOrganisation();
					if (p==null) { p = new DBOrganisation(); protocol.setOrganisation(p);}
					if (fi.getString().startsWith("http"))
						p.setResourceURL(new URL(fi.getString()));
					else p.setTitle(fi.getString());
					break;					
				}				
				case title: {
					protocol.setTitle(fi.getString());
					break;
				}
				case iduser: {
					DBUser user = new DBUser();
					if (fi.getString().startsWith("http"))
						user.setResourceURL(new URL(fi.getString()));
					else user.setTitle(fi.getString());
					protocol.setOwner(user);
					break;
				}
				case summarySearchable: {
					try {
					protocol.setSummarySearchable(Boolean.parseBoolean(fi.getString()));
					} catch (Exception x) { protocol.setSummarySearchable(false);}
					break;					
				}
				case keywords: {
					try {
						protocol.addKeyword(fi.getString().trim());
						} catch (Exception x) { }
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
