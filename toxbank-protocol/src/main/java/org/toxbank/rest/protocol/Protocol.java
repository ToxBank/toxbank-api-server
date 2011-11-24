package org.toxbank.rest.protocol;

import java.util.List;

import org.toxbank.resource.IDocument;
import org.toxbank.resource.IOrganisation;
import org.toxbank.resource.IProject;
import org.toxbank.resource.IProtocol;
import org.toxbank.resource.ITemplate;
import org.toxbank.resource.IUser;

public class Protocol implements IProtocol {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6990062112970959369L;
	protected int ID;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	protected String title;
	protected  String identifier;
	protected String anAbstract;
	protected IUser author;
	protected List<String> keywords;
	protected IOrganisation owner;
	protected ITemplate template;
	boolean isSummarySearchable;
	protected IProject project;
	protected IDocument document;
	protected String version;
	protected String fileName;
	
	public Protocol() {
	
	}
	
	public Protocol(int id) {
		setID(id);
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getAbstract() {
		return anAbstract;
	}
	public void setAbstract(String anAbstract) {
		this.anAbstract = anAbstract;
	}
	public IUser getAuthor() {
		return author;
	}
	public void setAuthor(IUser author) {
		this.author = author;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	@Override
	public void addKeyword(String keyword) {
		keywords.add(keyword);
		
	}
	@Override
	public void removeKeyword(String keyword) {
		keywords.remove(keyword);
	}
	public IOrganisation getOrganisation() {
		return owner;
	}

	public void setOrganisation(IOrganisation owner) {
		this.owner = owner;
	}
	public ITemplate getTemplate() {
		return template;
	}
	public void setTemplate(ITemplate template) {
		this.template = template;
	}
	public boolean isSummarySearchable() {
		return isSummarySearchable;
	}
	public void setSummarySearchable(boolean isSummarySearchable) {
		this.isSummarySearchable = isSummarySearchable;
	}
	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	public IDocument getDocument() {
		return document;
	}
	public void setDocument(IDocument document) {
		this.document = document;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public List<IProtocol> listVersions() {
		return null;
	}
	@Override
	public String toString() {
		return getIdentifier();
	}
}
