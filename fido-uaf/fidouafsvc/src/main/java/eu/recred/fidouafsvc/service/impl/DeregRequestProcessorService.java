package eu.recred.fidouafsvc.service.impl;

import com.google.gson.Gson;
import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fidouafsvc.storage.StorageInterface;
import eu.recred.fido.uaf.msg.DeregisterAuthenticator;
import eu.recred.fido.uaf.msg.DeregistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@Service
public class DeregRequestProcessorService {

    private Gson gson = new Gson();

    @Autowired @Qualifier("storageDao")
    private StorageInterface storageDao;

    public String process(String payload) {
        if (!payload.isEmpty()) {
            try {
                DeregistrationRequest[] deregFromJson = gson.fromJson(payload, DeregistrationRequest[].class);
                DeregistrationRequest request = deregFromJson[0];

                AuthenticatorRecord record = new AuthenticatorRecord();
                for (DeregisterAuthenticator authenticator : request.authenticators) {
                    record.AAID = authenticator.aaid;
                    record.KeyID = authenticator.keyID;
                    try {
                        String key = record.toString();
                        storageDao.deleteRegistrationRecord(key);
                    } catch (Exception e) {
                    	System.out.println("Dereg " + "Failure: Problem in deleting record from local DB");
                        return "Failure: Problem in deleting record from local DB";
                    }
                }
            } catch (Exception e) {
            	System.out.println("Dereg " + "Failure: problem processing deregistration request");
                return "Failure: problem processing deregistration request";
            }
            System.out.println("Dereg " + "Success");
            return "Success";
        }
        System.out.println("Dereg " + "Failure: problem processing deregistration request");
        return "Failure: problem processing deregistration request";
    }
}
