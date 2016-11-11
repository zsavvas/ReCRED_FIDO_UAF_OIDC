package eu.recred.fidouafsvc.dao;

import eu.recred.fidouafsvc.model.TrustedFacet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/fidouafsvc-servlet.xml"})
public class TrustedFacetDaoTest {

    @Autowired
    TrustedFacetDao trustedFacetDao;

    @Before
    public void setUp() {
        //trustedFacetDao.populateTrustedFacets();
    }

    @Test
    public void test() {
        List<TrustedFacet> facets = trustedFacetDao.listAllTrustedFacets();
        assertEquals(facets.size(), 7);
    }
}