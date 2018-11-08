package com.eltaieb.build.statistics.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Builds implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Map<String, List<Long>> modulesBuildMap = new LinkedHashMap<>();
	Map<Long, Build> builds = new HashMap<>();

	public void addOrUpdateBuild(Long timestamp, Build record) {
		Build r = builds.get(timestamp);
		if (null == r) {
			r = record;
			builds.put(timestamp, record);
			if (modulesBuildMap.get(record.getModuleName()) == null) {
				modulesBuildMap.put(record.getModuleName(), new ArrayList<Long>());
			}
			modulesBuildMap.get(record.getModuleName()).add(timestamp);

		}
		r.updateEndDate();
	}

	@Override
	public String toString() {
		return "Builds [modulesBuildMap=" + modulesBuildMap + ", records=" + builds + "]";
	}

}
