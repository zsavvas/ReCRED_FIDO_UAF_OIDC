package eu.recred.fidouafsvc.dao;

import eu.recred.fidouafsvc.model.TrustedFacet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by georgeg on 22.07.2016.
 */
@Repository("trustedFacetDao")
public class TrustedFacetDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void populateTrustedFacets() {
        Session session = sessionFactory.getCurrentSession();

        TrustedFacet t;

        t = new TrustedFacet();
        t.setName("https://www.head2toes.org");
        t.setDescription("Head to Toes Facet");
        session.save(t);

        t = new TrustedFacet();
        t.setName("https://openidconnect.ebay.com");
        t.setDescription("Ebay Facet");
        session.save(t);

        t = new TrustedFacet();
        t.setName("android:apk-key-hash:Df+2X53Z0UscvUu6obxC3rIfFyk");
        t.setDescription("First Android Facet");
        session.save(t);

        t = new TrustedFacet();
        t.setName("android:apk-key-hash:bE0f1WtRJrZv/C0y9CM73bAUqiI");
        t.setDescription("Second Android Facet");
        session.save(t);

        t = new TrustedFacet();
        t.setName("android:apk-key-hash:Lir5oIjf552K/XN4bTul0VS3GfM");
        t.setDescription("Third Android Facet");
        session.save(t);

        t = new TrustedFacet();
        t.setName("android:apk-key-hash:MCbbE5mV14rEvgk99uN6qN/MNCI");
        t.setDescription("Forth Android Facet");
        session.save(t);
        
        t = new TrustedFacet();
        t.setName("android:apk-key-hash:RVimey8gA1qIjM9FAc5yCjAbZcQ");
        t.setDescription("Fifth Android Facet");
        session.save(t);
    }

    @Transactional
    public List<TrustedFacet> listAllTrustedFacets() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TrustedFacet.class);
        //sessionFactory.getCurrentSession().get()
        return (List<TrustedFacet>) criteria.list();
    }
}
