package eu.recred.fidouafsvc.dao;

import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fidouafsvc.storage.RegistrationRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/fidouafsvc-servlet.xml"})
public class RegistrationRecordDaoTest {

    @Autowired
    RegistrationRecordDao registrationRecordDao;

    RegistrationRecord[] records = null;

    @Before
    public void setUp() {
        records = createRecords();
        registrationRecordDao.addRegistrationRecords(records);
    }

    @Test
    public void basic() {
        // Check to see if the records were inserted.
        for (RegistrationRecord record : records)
            assertNotNull(registrationRecordDao.getByAuthenticator(record.authenticator.toString()));

        // Remove then check.
        for (RegistrationRecord record : records) {
            try {
                registrationRecordDao.deleteRecord(record.authenticator.toString());
                assertNull(registrationRecordDao.getByAuthenticator(record.authenticator.toString()));
            } catch (IndexOutOfBoundsException e) {
                assertNotNull(e);
            }
        }
    }

    private RegistrationRecord[] createRecords() {
        List<RegistrationRecord> registrationRecords = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RegistrationRecord record = new RegistrationRecord();
            record.username = "user" + i;
            AuthenticatorRecord authenticator = new AuthenticatorRecord();
            authenticator.AAID = "aaid" + i;
            authenticator.KeyID = "keyid" + i;
            authenticator.username = "user" + i;
            record.authenticator = authenticator;

            registrationRecords.add(record);
        }
        RegistrationRecord[] recordsArr = registrationRecords.
            toArray(new RegistrationRecord[registrationRecords.size()]);

        return recordsArr;
    }
}