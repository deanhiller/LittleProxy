package com.alvazan.proxy.scraping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.proxy.VisitedFilter;
import com.alvazan.proxy.db.Domain;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebPageScraper {

	private static final Logger log = LoggerFactory.getLogger(WebPageScraper.class);
	private EntityManagerFactory factory;
	private String urlToStart;

	public WebPageScraper(EntityManagerFactory factory, String urlToStart) {
		this.factory = factory;
		this.urlToStart = urlToStart;
	}

	public void start() {
		String url = urlToStart;
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);

		int count = 0;
		while(url != null) {
			log.info("count = "+ count);
			ScrapedInformation info = findPagesListOfDomains(webClient, url);
			saveDomainsToDatabase(info.getDomains());
			url = info.getNextPageUrl();
			count++;
		}
		
		webClient.closeAllWindows();
	}

	private void saveDomainsToDatabase(List<String> domains) {
		EntityManager mgr = factory.createEntityManager();
		mgr.getTransaction().begin();
		try {
			for(String d : domains) {
				Domain domain = new Domain();
				domain.setDomain(d);
				mgr.persist(domain);
			}
		} finally {
			VisitedFilter.silentCommit(mgr);
			VisitedFilter.silentClose(mgr);
		}
	}

	//Main screen scrape goes here..and add each domain to the ScrapeInformation.java object
	//and then add the next pages url as well
	private ScrapedInformation findPagesListOfDomains(WebClient webClient, String url) {
		try {
			return findPagesListOfDomainsImpl(webClient, url);
		} catch (FailingHttpStatusCodeException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private ScrapedInformation findPagesListOfDomainsImpl(WebClient webClient, String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		//http://maps.google.com/maps?q=tax+accountants&hl=en&ll=39.773714,-104.959259&spn=0.553071,1.047821&sll=39.737567,-104.984718&sspn=0.570256,0.862427&t=m&fll=39.837014,-105.037537&fspn=0.552562,1.047821&z=10
		//http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q=tax+accountants&aq=&sll=39.737567,-104.984718&sspn=0.570256,0.862427&vpsrc=6&g=denver,+CO&ie=UTF8&t=m&fll=39.837014,-105.037537&fspn=0.552562,1.047821&st=113699889012030469848&rq=1&ev=zo&split=1&jsv=408d&vps=11&sa=N&start=10
		//http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q=tax+accountants&aq=&sll=39.737567,-104.984718&sspn=0.570256,0.862427&vpsrc=6&g=denver,+CO&ie=UTF8&t=m&fll=39.837014,-105.037537&fspn=0.552562,1.047821&st=113699889012030469848&rq=1&ev=zo&split=1&jsv=408d&vps=11&sa=N&start=20
		HtmlPage page = webClient.getPage(url);
		String asXml = page.asXml();
		log.info("xml="+asXml);

		ScrapedInformation info = new ScrapedInformation();
		List<String> domains = info.getDomains();
		
		findExtensions(asXml, ".net", domains);
		findExtensions(asXml, ".com", domains);
		findExtensions(asXml, ".biz", domains);
		
		if(asXml.contains("Advance America"))
			log.info("we got it");
		

		return null;
	}

	private void findExtensions(String asXml, String postfix, List<String> domains) {
		int indexOf = asXml.indexOf(postfix);
		int begin = indexOf - 100;
		int end = indexOf + postfix.length();
		String url = asXml.substring(begin, end);
		log.info("url="+url);
	}

}
