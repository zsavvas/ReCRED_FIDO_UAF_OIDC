package eu.recred.fidouafsvc.service.impl;

import eu.recred.fidouafsvc.ops.AuthenticationResponseProcessing;
import eu.recred.fidouafsvc.ops.RegistrationResponseProcessing;
import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fidouafsvc.storage.RegistrationRecord;
import eu.recred.fidouafsvc.storage.StorageInterface;
import org.apache.commons.codec.binary.Base64;
import eu.recred.fido.uaf.msg.AuthenticationResponse;
import eu.recred.fido.uaf.msg.RegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@Service
public class ProcessResponseService {

    private static final int SERVER_DATA_EXPIRY_IN_MS = 5 * 60 * 1000;

    @Autowired
    private NotaryServiceDummyImpl notaryServiceDummy;

    @Autowired @Qualifier("storageDao")
    private StorageInterface storageDao;

    public AuthenticatorRecord[] processAuthResponse(AuthenticationResponse response) {
        AuthenticatorRecord[] result = null;
        try {
            result = new AuthenticationResponseProcessing(SERVER_DATA_EXPIRY_IN_MS, notaryServiceDummy)
                    .verify(response, storageDao);
            String authenticationId = generateAuthenticationId();
            int len = result.length;
            for (int i = 0; i < len; i++) {
                result[i].authenticationId = authenticationId;
                storageDao.saveAuthenticationId(authenticationId, result[i].username);
            }
        } catch (Exception e) {
            System.out
                    .println("!!!!!!!!!!!!!!!!!!!..............................."
                            + e.getMessage());
            result = new AuthenticatorRecord[1];
            result[0] = new AuthenticatorRecord();
            result[0].status = e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    public String generateAuthenticationId() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[12];
        random.nextBytes(bytes);

        return "fido_auth_id_" + Base64.encodeBase64String(bytes);
    }

    public RegistrationRecord[] processRegResponse(RegistrationResponse response) {
        RegistrationRecord[] result = null;

        try {
            result = new RegistrationResponseProcessing(SERVER_DATA_EXPIRY_IN_MS, notaryServiceDummy)
                    .processResponse(response);
        } catch (Exception e) {
            System.out
                    .println("!!!!!!!!!!!!!!!!!!!..............................."
                            + e.getMessage());
            result = new RegistrationRecord[1];
            result[0] = new RegistrationRecord();
            result[0].status = e.getMessage();
        }

        return result;
    }
}
