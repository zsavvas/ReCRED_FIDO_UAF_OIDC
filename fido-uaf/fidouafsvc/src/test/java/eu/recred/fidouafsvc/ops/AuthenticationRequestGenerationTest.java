package eu.recred.fidouafsvc.ops;

import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import eu.recred.fido.uaf.crypto.Notary;
import eu.recred.fido.uaf.msg.AuthenticationRequest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//@Ignore
public class AuthenticationRequestGenerationTest {
	private static final String TEST_SIGNATURE = "test_signature";
	Gson gson = new Gson();

	@Ignore("Useless!")
	@Test
	public void notNull() {
		AuthenticationRequest authRequest = new AuthenticationRequestGeneration().createAuthenticationRequest(new NotaryImpl());
		assertNotNull(authRequest);
	}

	@Test
	public void withPolicy() {
		NotaryImpl notary = new NotaryImpl();
		String[] aaids = {"ABCD#ABCD"};
		AuthenticationRequest authRequest = new AuthenticationRequestGeneration(RegistrationRequestGeneration.APP_ID, aaids).createAuthenticationRequest(notary);
		assertNotNull(authRequest);
		String serverData = authRequest.header.serverData;
		serverData = new String(Base64.decode(serverData));
		assertTrue(notary.verify(serverData, serverData));
		//assertTrue(RegistrationRequestGeneration.APP_ID.equals(authRequest.header.appID));
		assertNotNull(authRequest.policy);
		assertTrue(authRequest.challenge.length() > 8);
		//assertTrue(authRequest.header.serverData.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"));
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