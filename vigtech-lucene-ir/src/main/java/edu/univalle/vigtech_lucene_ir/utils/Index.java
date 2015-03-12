package edu.univalle.vigtech_lucene_ir.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Index {
	
	private int docCount;
	private String id;
	
	
	
	public int getDocCount() {
		return docCount;
	}
	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	

}
