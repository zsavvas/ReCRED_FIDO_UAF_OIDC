package eu.recred.fidouafsvc.ops;

import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import eu.recred.fido.uaf.crypto.Notary;
import eu.recred.fido.uaf.msg.RegistrationRequest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//@Ignore
public class RegistrationRequestGenerationTest {

    private static final String TEST_SIGNATURE = "test_signature";
    Gson gson = new Gson();

    @Ignore("Useless!")
    @Test
    public void notNull() {
        RegistrationRequest regReq = new RegistrationRequestGeneration().createRegistrationRequest("Username", new NotaryImpl());
        assertNotNull(regReq);
    }

    @Test
    public void basic() {
        Notary notary = new NotaryImpl();
        RegistrationRequest regReq = new RegistrationRequestGeneration().createRegistrationRequest("Username", notary);
        String serverData = regReq.header.serverData;
        serverData = new String(Base64.decode(serverData));
        assertTrue(notary.verify(serverData, serverData));
        assertTrue(RegistrationRequestGeneration.APP_ID.equals(regReq.header.appID));
        //assertNotNull(regReq.policy);
        assertTrue(regReq.challenge.length() > 8);
        //assertTrue(regReq.header.serverData.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"));
    }

    class NotaryImpl implements Notary {
        public boolean verify(String dataToSign, String signature) {
            return signature.startsWith(TEST_SIGNATURE);
        }

        public String sign(String dataToSign) {
            return TEST_SIGNATURE;
        }
    }
}