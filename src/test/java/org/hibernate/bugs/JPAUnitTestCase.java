package org.hibernate.bugs;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.bugs.entity.Order;
import org.hibernate.jpa.QueryHints;
import org.hibernate.loader.MultipleBagFetchException;
import org.hibernate.testing.TestForIssue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		this.entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		this.entityManagerFactory.close();
	}

	@Test
	@TestForIssue(jiraKey = "HHH-10485")
	public void entityGraphMultipleBagsTest() {
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery( Order.class );
			criteriaQuery.from( Order.class );
			TypedQuery<Order> typedQuery = entityManager.createQuery( criteriaQuery );
			// Create an EntityGraph with multiple collection attributes
			EntityGraph<Order> entityGraph = entityManager.createEntityGraph( Order.class );
			entityGraph.addAttributeNodes( "products" );
			entityGraph.addAttributeNodes( "tags" );
			// Set the EntityGraph as a hint on the query
			typedQuery.setHint( QueryHints.HINT_FETCHGRAPH, entityGraph );
			typedQuery.getResultList();
			entityManager.getTransaction().commit();
		}
		catch (IllegalArgumentException e) {
			if ( e.getCause() instanceof MultipleBagFetchException ) {
				Assert.fail( e.getCause().getMessage() );
			}
			else {
				throw e;
			}
		}
		finally {
			entityManager.close();
		}
	}

}
