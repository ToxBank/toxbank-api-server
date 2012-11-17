package org.toxbank.rest.protocol;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.toxbank.client.policy.AccessRights;
import net.toxbank.client.resource.Document;
import net.toxbank.client.resource.Organisation;
import net.toxbank.client.resource.Project;
import net.toxbank.client.resource.Protocol;
import net.toxbank.client.resource.Protocol.STATUS;
import net.toxbank.client.resource.Template;
import net.toxbank.client.resource.User;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.user.DBUser;

public class ProtocolFactory {
	protected static final String utf8= "UTF-8";
	public static DBProtocol getProtocol(DBProtocol protocol,List<FileItem> items, long maxSize, File dir, AccessRights accessRights) throws ResourceException {
		
		if (protocol==null) protocol = new DBProtocol();
		for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
			FileItem fi = it.next();

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
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s))
						protocol.setIdentifier(s);
					break;
				}
				case published: {
					String s = fi.getString(utf8);
					try {
						protocol.setPublished(Boolean.parseBoolean(s));
					} catch (Exception x) { protocol.setPublished(true);}
					break;
				}
				case anabstract: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s))
					protocol.setAbstract(s);
					break;
				}
				case filename: {
					if (fi.isFormField()) {
						protocol.setDocument(new Document(new URL(fi.getString(utf8))));
					} else {	
						String originalName = "";
						if (fi.getSize()==0)  throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,"Empty file!"));
						File file = null;
				        if (fi.getName()==null)
				           	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"File name can't be empty!");
				        else {
				        	try { 
				        		if ((dir!=null) && !dir.exists())  dir.mkdir();
				        	} catch (Exception x) {dir = null; }
				        	
				        	int extIndex = fi.getName().lastIndexOf(".");
				        	String ext = extIndex>0?fi.getName().substring(extIndex):"";
				        	
				        	//generate new file name
				        	originalName = fi.getName();
				        	String newName = String.format("tb%d_%s%s", protocol.getID()>0?protocol.getID():0,
				        							UUID.randomUUID().toString(),ext);
				          	file = new File(String.format("%s/%s",dir==null?System.getProperty("java.io.tmpdir"):dir,newName));
				        }
				        fi.write(file);
				        protocol.setDocument(new Document(file.toURI().toURL()));		
					}
			        break;
				}
				case template: {
					if (fi.isFormField()) {
						protocol.setDataTemplate(new Template(new URL(fi.getString(utf8))));
					} else {	
						if (fi.getSize()==0)  throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,"Empty file!"));
						File file = null;
				        if (fi.getName()==null)
				           	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"File name can't be empty!");
				        else {
				        	try { 
				        		if ((dir!=null) && !dir.exists())  dir.mkdir();
				        	} catch (Exception x) {dir = null; }
				          	file = new File(
				            		String.format("%s/%s",
				            				dir==null?System.getProperty("java.io.tmpdir"):dir,
				            				fi.getName()));
				        }
				        fi.write(file);
				        protocol.setDataTemplate(new Template(file.toURI().toURL()));		
					}
			        break;
				}				
				case project_uri: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s)) {
						//TODO what do we do if there are projects already ? e.g. in update ?
						Project p = new DBProject(); 
						protocol.addProject(p);
						if (s.startsWith("http"))
							p.setResourceURL(new URL(s));
						else p.setTitle(s);
					}
					break;					
				}
				case user_uri: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s)) {
						User p = protocol.getOwner();
						if (p==null) { p = new DBUser(); protocol.setOwner(p);}
						if (s.startsWith("http"))
							p.setResourceURL(new URL(s));
						else p.setUserName(s);
					}
					break;					
				}				
				case organisation_uri: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s)) {
						Organisation p = protocol.getOrganisation();
						if (p==null) { p = new DBOrganisation(); protocol.setOrganisation(p);}
						if (s.startsWith("http"))
							p.setResourceURL(new URL(s));
						else p.setTitle(fi.getString());
					}
					break;					
				}		
				case author_uri: {
					String s = fi.getString(utf8);
					if ((s!=null) && s.startsWith("http"))
						 protocol.addAuthor(new DBUser(new URL(s)));
					break;	
				}
				case title: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s)) 
						protocol.setTitle(s);
					break;
				}
				case iduser: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s)) {
						DBUser user = new DBUser();
						if (s.startsWith("http"))
							user.setResourceURL(new URL(s));
						else user.setTitle(s);
						protocol.setOwner(user);
					}
					break;
				}
				case summarySearchable: {
					try {
						protocol.setSearchable(Boolean.parseBoolean(fi.getString(utf8)));
					} catch (Exception x) { protocol.setSearchable(false);}
					break;					
				}
				case status: {
					try {
						protocol.setStatus(Protocol.STATUS.valueOf(fi.getString(utf8)));
					} catch (Exception x) { protocol.setStatus(STATUS.RESEARCH);}
					break;					
				}
				case keywords: {
					try {
						if ((fi.getString()!=null) && !"".equals(fi.getString(utf8)))
							protocol.addKeyword(fi.getString().trim());
						} catch (Exception x) { }
						break;	
				}
				case allowReadByUser: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s))
						try {
							DBUser user = null;
							//a bit of heuristic
							if (s.startsWith("http")) { user = new DBUser(new URL(s.trim())); } 
							else { user = new DBUser(); user.setUserName(s.trim()); }	
							accessRights.addUserRule(user,true,null,null,null);
						} catch (Exception x) { 
							x.printStackTrace(); 
						}
					break;						
				}
				case allowReadByGroup: {
					String s = fi.getString(utf8);
					if ((s!=null) && !"".equals(s))
						try {
							String uri = s.trim();
							//hack to avoid queries...
							if (uri.indexOf("/organisation")>0) {
								DBOrganisation org = null;
								if (s.startsWith("http")) { org = new DBOrganisation(new URL(s.trim())); } 
								else { org = new DBOrganisation(); org.setGroupName(s.trim()); }	
								accessRights.addGroupRule(org,true,null,null,null);
								
							} else if (uri.indexOf("/project")>0) {
								DBProject org = null;
								if (s.startsWith("http")) { org = new DBProject(new URL(s.trim())); } 
								else { org = new DBProject(); org.setGroupName(s.trim()); }
								accessRights.addGroupRule(org,true,null,null,null);
							}
						} catch (Exception x) { x.printStackTrace(); }
					break;	
				}				
				} //switch
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			} 

		}
		//if (protocol.getIdentifier()==null) protocol.setIdentifier(String.format("SEURAT-%s",UUID.randomUUID()));
		return protocol;
	}
	
}
