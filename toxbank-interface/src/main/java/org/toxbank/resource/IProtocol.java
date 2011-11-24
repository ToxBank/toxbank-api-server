package org.toxbank.resource;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author nina
 <pre>
@prefix tb:  <http://onto.toxbank.net/api/> .
 </pre>
 */
public interface IProtocol extends Serializable {
	public static final String resource = "/protocol";
	public int getID();
	public void setID(int id);
	
	public void addKeyword(String keyword);
	public void removeKeyword(String keyword);
	public List<String> getKeywords();
	public IUser getAuthor() ;
	public void setAuthor(IUser author) ;
	public void setTitle(String title);
	public String getTitle();
	public void setIdentifier(String identifier);
	public String getIdentifier();
	public void setAbstract(String abstrakt);
	public String getAbstract();
	public String getVersion();
	public void setVersion(String version);
	public IDocument getDocument();
	public void setDocument(IDocument document);
	public IProject getProject() ;
	public void setProject(IProject project);
	public boolean isSummarySearchable();
	public void setSummarySearchable(boolean isSummarySearchable);
	public ITemplate getTemplate() ;
	public void setTemplate(ITemplate template);
	public IOrganisation getOrganisation() ;
	public void setOrganisation(IOrganisation owner);
	public List<IProtocol> listVersions() ;

	
	/* client
	public URL upload(String server);
	public List<URL> listFiles();

	public List<URL> listTemplates() ;
	public List<Template> getTemplates();

	*/
	///mine

}


