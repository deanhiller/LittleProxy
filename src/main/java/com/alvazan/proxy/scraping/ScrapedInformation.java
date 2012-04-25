package com.alvazan.proxy.scraping;

import java.util.ArrayList;
import java.util.List;

public class ScrapedInformation {
	private List<String> domains = new ArrayList<String>();
	//leave this null if on the last page so it doesn't keep going
	private String nextPageUrl;
	public List<String> getDomains() {
		return domains;
	}
	public void setDomains(List<String> domains) {
		this.domains = domains;
	}
	public String getNextPageUrl() {
		return nextPageUrl;
	}
	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}
	
	
}
