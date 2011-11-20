package org.toxbank.resource;

import java.io.Serializable;
import java.util.Iterator;

/**
 * 
 * @author nina
 <pre>
@prefix tb:  <http://www.owl-ontologies.com/toxbank.owl#> .
@prefix dcterms:  <http://dublincore.org/documents/dcmi-terms/> .
<http://toxbank.net/services/protocol/Protocol_9>
     a       tb:Protocol ;
     dcterms:title "Protocol title";
     dcterms:identifier "SEURAT-P1234567890";
     tb:hasAbstract "This is the abstract"^^xsd:string ;
     tb:hasAuthor tb:OliviaSanger ;
     tb:hasKeyword ""^^xsd:string ;
     tb:hasOwner tb:ORG5 ;
     tb:hasTemplate tb:Template_12 ;
     tb:isSummarySearchable "true"^^xsd:boolean ;
     tb:project tb:DETECTIVE ;
     tb:hasDocument <http://toxbank.net/services/protocol/Protocol_9/ABC.pdf> ;
     tb:versionInfo "123"^^xsd:string.
tb:ORG5 
 </pre>
 */
public interface IProtocol extends Serializable {
	public static final String resource = "/protocol";
	public int getID();
	public void setID(int id);
	
	public String getTitle();
	public void setTitle(String title);
	public String getIdentifier();
	public void setIdentifier(String identifier);
	public String getAnAbstract();
	public void setAnAbstract(String anAbstract);
	public IAuthor getAuthor() ;
	public void setAuthor(IAuthor author) ;
	public Iterator<String> getKeywords();
	public void setKeywords(Iterator<String> keywords);
	public IOrganisation getOwner() ;
	public void setOwner(IOrganisation owner);
	public ITemplate getTemplate() ;
	public void setTemplate(ITemplate template);
	public boolean isSummarySearchable();
	public void setSummarySearchable(boolean isSummarySearchable);
	public IProject getProject() ;
	public void setProject(IProject project);
	public IDocument getDocument();
	public void setDocument(IDocument document);
	public String getVersion();
	public void setVersion(String version);
}
