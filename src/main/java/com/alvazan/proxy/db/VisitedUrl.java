package com.alvazan.proxy.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Entity
@NamedQuery(name="findByUrl", query="select v from VisitedUrl v where v.url = :url")
public class VisitedUrl {
	
	@Column(name="Id")
	@Id()
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length=1000, nullable=false)
	private String url;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	public static VisitedUrl findByUrl(EntityManager mgr, String url) {
		Query query = mgr.createNamedQuery("findByUrl");
		query.setParameter("url", url);
		try {
			return (VisitedUrl) query.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}
}
