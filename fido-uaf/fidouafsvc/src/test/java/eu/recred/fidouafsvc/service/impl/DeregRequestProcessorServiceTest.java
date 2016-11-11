package eu.recred.fidouafsvc.service.impl;

import eu.recred.fidouafsvc.dao.RegistrationRecordDao;
import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fidouafsvc.storage.RegistrationRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/fidouafsvc-servlet.xml"})
public class DeregRequestProcessorServiceTest {

    @Autowired
    RegistrationRecordDao registrationRecordDao;
    
    @Autowired
    DeregRequestProcessorService deregRequestProcessor;

    @Before
    public void setUp() {
        AuthenticatorRecord authenticatorRecord = new AuthenticatorRecord();
        authenticatorRecord.AAID = "ABCD#ABCD";
        authenticatorRecord.KeyID = "ZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNg";
        RegistrationRecord registrationRecord = new RegistrationRecord();
        registrationRecord.authenticator = authenticatorRecord;
        RegistrationRecord registrationRecords[] = new RegistrationRecord[1];
        registrationRecords[0] = registrationRecord;
        registrationRecordDao.addRegistrationRecords(registrationRecords);
    }

    @Test
    public void basic() {
        String response = deregRequestProcessor.process(getDeregRequest());
        assertEquals("Success", response);
    }

    String getDeregRequest() {
        return "[{\"header\":{\"op\":\"Dereg\",\"upv\":{\"major\":1,\"minor\":0},\"appID\":\"https://uaf-test-1.noknoktest.com:8443/SampleApp/uaf/facets\"},\"authenticators\":[{\"aaid\":\"ABCD#ABCD\",\"keyID\":\"ZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNg\"}]}]";
    }
}
