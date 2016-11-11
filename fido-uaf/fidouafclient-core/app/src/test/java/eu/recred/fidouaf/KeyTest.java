package eu.recred.fidouaf;

import android.util.Base64;

import eu.recred.fidouaf.crypto.KeyCodec;
import eu.recred.fidouaf.authenticator.Authenticator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by sorin.teican on 10/11/2016.
 */
public class KeyTest {

    PrivateKey priv;
    PublicKey pub;
    byte[] data;

    @Before
    public void setUp() {
        try {
            priv = KeyCodec.getPrivKey(Base64.decode(Authenticator.priv, Base64.URL_SAFE));
            pub = KeyCodec.getPubKey(Base64.decode(Authenticator.pubCert, Base64.URL_SAFE));
            data = "Some very important data".getBytes();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeyFormat() {
        try {
            Signature s = Signature.getInstance("SHA256withECDSA");
            s.initSign(priv);
            s.update(data);
            byte[] signature = s.sign();

            s.initVerify(pub);
            s.update(data);
            assertTrue(s.verify(signature));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }
}
