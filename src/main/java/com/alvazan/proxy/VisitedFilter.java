package com.alvazan.proxy;

import java.net.InetSocketAddress;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.joda.time.LocalDateTime;
import org.littleshoot.proxy.HttpRequestFilter;
import org.littleshoot.proxy.HttpServerPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.proxy.db.Domain;
import com.alvazan.proxy.db.User;
import com.alvazan.proxy.db.VisitedUrl;

public class VisitedFilter implements HttpRequestFilter {

    private static final Logger log = 
            LoggerFactory.getLogger(HttpServerPipelineFactory.class);
    
    private EntityManagerFactory factory;
	private TimeNow timeSystem;
    
    public VisitedFilter(EntityManagerFactory f, TimeNow system) {
    	this.factory = f;
    	this.timeSystem = system;
    }

    
	public void filter(HttpRequest httpRequest, Channel channel) {
		EntityManager mgr = factory.createEntityManager();
		mgr.getTransaction().begin();
		try {
			filter(httpRequest, channel, mgr);
		} finally {
			silentCommit(mgr);
			slientClose(mgr);
		}
	}


	private void slientClose(EntityManager mgr) {
		try {
			mgr.close();
		} catch(RuntimeException e) {
			log.warn("Exception on close", e);
		}
	}


	private void silentCommit(EntityManager mgr) {
		try {
			mgr.getTransaction().commit();
		} catch(RuntimeException e) {
			log.warn("Exception on commit", e);
		}
	}
	
	public void filter(HttpRequest httpRequest, Channel channel, EntityManager mgr) {
		LocalDateTime now = timeSystem.getCurrentTime();
		
		HttpMethod method = httpRequest.getMethod();
		String uri = httpRequest.getUri();
		String domain = httpRequest.getHeader("Host");
		log.info("remoteUrl="+channel.getRemoteAddress());
		log.info("uri="+uri);
		
		User user = findOrCreateUser(channel, mgr);
		Domain d = findOrCreateDomain(mgr, domain);
		addUrlVisitedToList(mgr, uri, d);
		
		/**
		 * Because one website has MANY requests we only record a single hit
		 * every 10 minutes for a website so if we start seeing lots of repeat websites
		 * we have an issue 
		 */
		if(d.isComplete()) {
			throw new AlreadyVisitedException("This site is already complete, we have an email="+d.getEmailOfPersonAtDomain()+" domain="+d.getDomain());
		} else if(!d.isWasVisited()) {
			d.setFirstTimeAccessed(now);
			d.setFromUser(user);			
			d.setNumberTimesRequested(d.getNumberTimesRequested()+1);
		} else if(!d.isWithinWindow(now)){
			//After 10 minutes, we record the website as being hit a second time
			//too many of these repeat hits means we are wasting money
			d.setLastTimeAccessed(now);
			d.setNumberTimesRequested(d.getNumberTimesRequested()+1);
			throw new AlreadyVisitedException("domain="+domain+" already visited.  url accessed="+uri+" This domain visited numtimes="+d.getNumberTimesRequested());
		}
		
		if(method.equals(HttpMethod.POST)) {
			log.info("post");
			ChannelBuffer content = httpRequest.getContent();
			int num = content.readableBytes();
			byte[] data = new byte[num];
			content.readBytes(data);
			String toPost = new String(data);
			if(toPost.contains("jeff@alvazan.com"))
				d.setWasPostedToWithEmail(true);
			
			String posted = toPost.substring(0, 390);
			
			log.info("posted data="+toPost);
			d.setFirstPostedContent(posted);
		}
	}


	private void addUrlVisitedToList(EntityManager mgr, String uri, Domain d) {
		VisitedUrl visited = VisitedUrl.findByUrl(mgr, uri);
		if(visited == null) {
			visited = new VisitedUrl();
			visited.setUrl(uri);
			visited.setDomain(d);
			mgr.persist(visited);
		}
	}


	private Domain findOrCreateDomain(EntityManager mgr, String domain) {
		Domain d = Domain.findByDomain(mgr, domain);
		if(d == null) {
			//This only happens when 
			//1. The domain was not pre-loaded by our scraper
			//2. The domain did not exist from someone previously hitting the domain(ie. it was found by human not scraper)
			d = new Domain();
			d.setDomain(domain);
			mgr.persist(d);
		}

		return d;
	}


	private User findOrCreateUser(Channel channel, EntityManager mgr) {
		InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
		String address = remoteAddress.getAddress().getHostAddress();
		User user = User.findByIp(mgr, address);
		if(user == null) {
			user = new User();
			user.setFromAddress(address);
			mgr.persist(user);
		}
		return user;
	}

	private String parseOutDomain(String uri) {
		if(uri.startsWith("http://"))
			uri = uri.substring("http://".length());
		
		if(!uri.contains("/")) {
			return uri;
		}
		
		return null;
	}

}
