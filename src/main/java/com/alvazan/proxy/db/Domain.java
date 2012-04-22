package com.alvazan.proxy.db;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDateTime;

@Entity
@NamedQueries({
@NamedQuery(name="findByDomain", query="select s from Domain s where s.domain = :domain"),
@NamedQuery(name="findAllDomains", query="select d from Domain d")
})
public class Domain {
	@Column(name="Id")
	@Id()
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String domain;
	
	@Column(nullable=false)
	private boolean wasPostedToWithEmail = false;
	
	@Column(nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date firstTimeAccessed;

	@Column(nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastTimeAccessed;
	
	@Column(nullable=false)
	private int numberTimesRequested = 0;
	
	/**
	 * User might be null as this table is populated from a crawler script with domains we
	 * want to visit/post to
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	private User fromUser;

	@OneToMany(fetch=FetchType.LAZY,mappedBy="domain")
	private List<VisitedUrl> urls;
	
	@Column(length=400)
	private String firstPostedContent;
	
	@Column
	private String emailOfPersonAtDomain; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isWasVisited() {
		return firstTimeAccessed != null;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromIp) {
		this.fromUser = fromIp;
	}

	public LocalDateTime getFirstTimeAccessed() {
		if(firstTimeAccessed == null)
			return null;
		return new LocalDateTime(firstTimeAccessed);
	}

	public void setFirstTimeAccessed(LocalDateTime firstTimeAccessed) {
		this.firstTimeAccessed = firstTimeAccessed.toDate();
		this.lastTimeAccessed = this.firstTimeAccessed;
	}

	public LocalDateTime getLastTimeAccessed() {
		if(lastTimeAccessed == null)
			return null;
		return new LocalDateTime(lastTimeAccessed);
	}

	public int getNumberTimesRequested() {
		return numberTimesRequested;
	}
	
	public boolean isWithinWindow(LocalDateTime now) {
		LocalDateTime lastTime = getLastTimeAccessed();
		if(lastTime == null)
			return true;
		
		LocalDateTime tenMinutesAfterLast = lastTime.plusMinutes(10);
		if(now.isBefore(tenMinutesAfterLast))
			return true;
		return false;
	}
	
	
	public void setLastTimeAccessed(LocalDateTime lastTimeAccessed) {
		this.lastTimeAccessed = lastTimeAccessed.toDate();
	}

	public void setNumberTimesRequested(int numberTimesRequested) {
		this.numberTimesRequested = numberTimesRequested;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String host) {
		this.domain = host;
	}

	public String getFirstPostedContent() {
		return firstPostedContent;
	}

	public void setFirstPostedContent(String firstPostedContent) {
		this.firstPostedContent = firstPostedContent;
	}
	
	public static Domain findByDomain(EntityManager mgr, String domain) {
		Query query = mgr.createNamedQuery("findByDomain");
		query.setParameter("domain", domain);
		
		try {
			return (Domain) query.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Domain> findAll(EntityManager mgr) {
		Query query = mgr.createNamedQuery("findAllDomains");
		return query.getResultList();
	}

	public List<VisitedUrl> getUrls() {
		return urls;
	}

	public void setUrls(List<VisitedUrl> urls) {
		this.urls = urls;
	}

	public boolean isWasPostedToWithEmail() {
		return wasPostedToWithEmail;
	}

	public void setWasPostedToWithEmail(boolean wasPostedToWithEmail) {
		this.wasPostedToWithEmail = wasPostedToWithEmail;
	}

	public String getEmailOfPersonAtDomain() {
		return emailOfPersonAtDomain;
	}

	public void setEmailOfPersonAtDomain(String emailOfPersonAtDomain) {
		this.emailOfPersonAtDomain = emailOfPersonAtDomain;
	}

	/**
	 * We should never touch this domain again if we have the person's email!!
	 * @return
	 */
	public boolean isComplete() {
		return emailOfPersonAtDomain != null;
	}
}
