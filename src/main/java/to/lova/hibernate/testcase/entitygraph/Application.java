package to.lova.hibernate.testcase.entitygraph;

import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jpa.QueryHints;

import to.lova.hibernate.testcase.entitygraph.entity.Order;

public class Application {

    private SessionFactory sessionFactory;

    public static void main(String[] args) {
        Application application = new Application();
        application.setup();
        application.populate();
        application.run();
        System.exit(0);
    }

    public void setup() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        this.sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public void populate() {
        EntityManager em = this.sessionFactory.createEntityManager();

        em.getTransaction().begin();

        for (int i = 1; i < 20; i++) {
            em.persist(new Order());
        }

        em.getTransaction().commit();

        em.close();
    }

    public void run() {
        EntityManager em = this.sessionFactory.createEntityManager();

        em.getTransaction().begin();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Order> criteriaQuery = cb.createQuery(Order.class);

        criteriaQuery.from(Order.class);

        TypedQuery<Order> typedQuery = em.createQuery(criteriaQuery);

        EntityGraph<Order> entityGraph = em.createEntityGraph(Order.class);

        entityGraph.addAttributeNodes("products");

        /*
         * Adding this to the graph will throw MultipleBagFetchException: cannot
         * simultaneously fetch multiple bags since collections in entity graphs
         * default to JOIN.
         */
        // entityGraph.addAttributeNodes("tags");

        typedQuery.setHint(QueryHints.HINT_FETCHGRAPH, entityGraph);

        List<Order> orders = typedQuery.setFirstResult(5).setMaxResults(10).getResultList();

        /*
         * Also, defaulting to JOIN will trigger in-memory pagination. Imagine
         * an Order table with +100K records!
         */

        for (Order order : orders) {
            System.out.printf("Order %d has %d products.\n", order.getId(), order.getProducts().size());
        }

        em.getTransaction().commit();

        em.close();
    }

}
