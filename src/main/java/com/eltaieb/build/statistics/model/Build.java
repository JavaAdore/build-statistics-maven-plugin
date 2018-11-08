package com.eltaieb.build.statistics.model;

import java.io.Serializable;
import java.util.Date;
 
public class Build implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String moduleName;
 	private Long endDate;
	private boolean parent;



	public Build() {

	}

	public Build(String moduleName, boolean parent ) {
		super();
		this.moduleName = moduleName;
 		this.parent = parent;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
 

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
	
	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return "Record [moduleName=" + moduleName +  ", endDate=" + endDate + "]";
	}

	public void updateEndDate() {

	endDate =  new Date().getTime();
	}

}
