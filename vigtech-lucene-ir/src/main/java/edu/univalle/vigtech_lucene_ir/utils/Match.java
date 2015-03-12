package edu.univalle.vigtech_lucene_ir.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Match {
	
	private String docid;
	private String path;
	private String title;
	private String fragment;
	private ArrayList<String> links;
	
	
	public Match(){
		setLinks(new ArrayList<String>());
	}
	


	/**
	 * @return the docid
	 */
	public String getDocid() {
		return docid;
	}



	/**
	 * @param docid the docid to set
	 */
	public void setDocid(String docid) {
		this.docid = docid;
	}



	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the fragment
	 */
	public String getFragment() {
		return fragment;
	}
	/**
	 * @param fragment the fragment to set
	 */
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	/**

	/**
	 * @return the links
	 */
	public ArrayList<String> getLinks() {
		return links;
	}

	/**
	 * @param links the links to set
	 */
	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
	/**
	 * @param link to add
	 */
	public void addLink(String links) {
		this.links.add(links);
	}
	

}
