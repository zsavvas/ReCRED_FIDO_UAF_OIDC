package eu.recred.fidouafsvc.dao;

import com.google.gson.Gson;
import eu.recred.fidouafsvc.model.RegistrationRecordModel;
import eu.recred.fidouafsvc.storage.RegistrationRecord;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@Repository("registrationRecordDao")
public class RegistrationRecordDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Gson gson = new Gson();

    @Transactional
    public void addRegistrationRecords(RegistrationRecord[] records) {
        Session session = sessionFactory.getCurrentSession();

        for (RegistrationRecord record : records) {
            RegistrationRecordModel recordModel = new RegistrationRecordModel();
            System.out.println("Authenticator AAID: " + record.authenticator.AAID);
            System.out.println("Authenticator KeyID: " + record.authenticator.KeyID);
            recordModel.setAuthenticator(record.authenticator.toString());
            recordModel.setRecord(gson.toJson(record));
            session.save(recordModel);
        }
    }

    @Transactional
    public RegistrationRecord getByAuthenticator(String authenticator) {
//        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RegistrationRecordModel.class);
//        RegistrationRecordModel model = (RegistrationRecordModel) criteria.add(Restrictions.eq("authenticator", authenticator))
//                .uniqueResult();
//
//        RegistrationRecord record = gson.fromJson(model.getRecord(), RegistrationRecord.class);
//        return record;
    	String hql = "from RegistrationRecordModel rr where rr.authenticator = :authenticator";
    	List<RegistrationRecordModel> result = sessionFactory.getCurrentSession()
    			.createQuery(hql).setString("authenticator", authenticator).list();
    	
    	return gson.fromJson(result.get(0).getRecord(), RegistrationRecord.class);
    }

    @Transactional
    public void deleteRecord(String authenticator) {
    	
//        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RegistrationRecordModel.class);
//        RegistrationRecordModel model = (RegistrationRecordModel) criteria.add(Restrictions.eq("authenticator", authenticator))
//                .uniqueResult();
    	String hql = "delete from RegistrationRecordModel rr where rr.authenticator = :authenticator";
    	sessionFactory.getCurrentSession().createQuery(hql).setString("authenticator", authenticator).executeUpdate();
    }
}
