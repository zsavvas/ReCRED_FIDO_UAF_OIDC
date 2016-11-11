package eu.recred.fidouafsvc.dao;

import eu.recred.fidouafsvc.model.AuthenticationIdModel;
import eu.recred.fidouafsvc.storage.DuplicateKeyException;
import eu.recred.fidouafsvc.storage.RegistrationRecord;
import eu.recred.fidouafsvc.storage.StorageInterface;
import eu.recred.fidouafsvc.storage.SystemErrorException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@Repository
public class StorageDao implements StorageInterface {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RegistrationRecordDao registrationRecordDao;

    @Override
    public void storeServerDataString(String s, String s1) {

    }

    @Override
    public String getUsername(String s) {
        return null;
    }

    public void store(RegistrationRecord[] registrationRecords) throws DuplicateKeyException, SystemErrorException {
        try {
            registrationRecordDao.addRegistrationRecords(registrationRecords);
        } catch (HibernateException e) {
            throw new DuplicateKeyException();
        }
    }

    @Override
    public RegistrationRecord readRegistrationRecord(String s) {
        return registrationRecordDao.getByAuthenticator(s);
    }


    public void update(RegistrationRecord[] registrationRecords) {

    }

    public void deleteRegistrationRecord(String authenticator) {
        registrationRecordDao.deleteRecord(authenticator);
    }

    @Transactional
    public void saveAuthenticationId(String id, String username) {
        Session session = sessionFactory.getCurrentSession();

        AuthenticationIdModel model = new AuthenticationIdModel();
        model.setAuthenticationId(id);
        model.setUsername(username);
        //System.out.println("saving fido_auth_id: " + id);
        session.save(model);
    }

    @Transactional
    public String getAuthenticated(String id) {
//        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AuthenticationIdModel.class);
//        AuthenticationIdModel model = (AuthenticationIdModel) criteria.add(Restrictions.eq("AUTHID", id))
//                .uniqueResult();
//
//        return model.getUsername();
    	// System.out.println("searching fido_auth_id: " + id);
    	String hql = "from AuthenticationIdModel aid where aid.authenticationId = :authenticationId";
    	List result = sessionFactory.getCurrentSession().createQuery(hql).setString("authenticationId", id).list();
    	
    	if (result.isEmpty())
    		return null;
    	return ((AuthenticationIdModel)result.get(0)).getUsername();
    }
}
