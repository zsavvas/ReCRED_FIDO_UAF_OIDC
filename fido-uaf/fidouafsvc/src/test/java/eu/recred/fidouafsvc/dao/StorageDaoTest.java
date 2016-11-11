package eu.recred.fidouafsvc.dao;

import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fidouafsvc.storage.DuplicateKeyException;
import eu.recred.fidouafsvc.storage.RegistrationRecord;
import eu.recred.fidouafsvc.storage.StorageInterface;
import eu.recred.fidouafsvc.storage.SystemErrorException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/fidouafsvc-servlet.xml"})
public class StorageDaoTest {

	@Autowired @Qualifier("storageDao")
    StorageInterface storageDao;

    RegistrationRecord[] records;
    
    @Test
    public void readRegistrationTest() {
    	// Create the records.
    	records = new RegistrationRecord[3];
        for (int i = 0; i < 3; i++) {
            RegistrationRecord record = new RegistrationRecord();
            record.username = "username" + i;
            AuthenticatorRecord authenticator = new AuthenticatorRecord();
            authenticator.username = "username" + i;
            authenticator.AAID = "AAID" + i;
            authenticator.KeyID = "KeyID" + i;
            record.authenticator = authenticator;
            records[i] = record;
        }
        
        try {
			storageDao.store(records);
		} catch (DuplicateKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemErrorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // Create three authenticationIds.
        for (int i = 0; i < 3; i++)
            storageDao.saveAuthenticationId("123" + i, "username" + i);
        
        System.out.println("Stored records");
        
        // Test if records inserted properly.
        for (RegistrationRecord record : records) {
            RegistrationRecord temp = storageDao.readRegistrationRecord(record.authenticator.toString());
            assertNotNull(temp);
        }
        
        System.out.println("Checked records");
        
        // Test authenticationIds.
        for (int i = 0; i < 3; i++) {
            String username = storageDao.getAuthenticated("123" + i);
            assertEquals("username" + i, username);
        }
        
        System.out.println("Checked authenticationIds");
        
        for (RegistrationRecord record : records)
            storageDao.deleteRegistrationRecord(record.toString());
        
        // Now test if records still exist.
        for (RegistrationRecord record : records) {
            try {
                RegistrationRecord temp = storageDao.readRegistrationRecord(record.toString());
                assertNull(temp);
            } catch (Exception e) {
                assertNotNull(e);
            }
        }
    }
}
