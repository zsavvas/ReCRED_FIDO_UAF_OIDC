package eu.recred.fidouaf.authenticator;

import android.util.Base64;

import eu.recred.fidouaf.crypto.Asn1;
import eu.recred.fidouaf.crypto.BCrypt;
import eu.recred.fidouaf.crypto.KeyCodec;
import eu.recred.fidouaf.crypto.NamedCurve;
import eu.recred.fidouaf.crypto.SHA;
import eu.recred.fidouaf.msg.asm.obj.AuthenticateIn;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.util.Preferences;
import org.spongycastle.jce.interfaces.ECPublicKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by sorin.teican on 10/4/2016.
 */
public class AuthenticatorAuthOp {

    public String getAssertions(AuthenticateIn request) throws Exception {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;

        byteout.write(encodeInt(TagsEnum.TAG_UAFV1_AUTH_ASSERTION.id));
        value = getAuthAssertion(request);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        return Base64.encodeToString(byteout.toByteArray(), Base64.NO_PADDING);
    }

    private byte[] getAuthAssertion(AuthenticateIn request) throws Exception {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;

        byteout.write(encodeInt(TagsEnum.TAG_UAFV1_SIGNED_DATA.id));
        value = getSignedData(request);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byte[] signedDataValue = byteout.toByteArray();

        byteout.write(encodeInt(TagsEnum.TAG_SIGNATURE.id));
        value = getSignature(signedDataValue);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] getSignature(byte[] dataForSigning) throws Exception {

//		PublicKey pub = KeyCodec.getPubKey(Base64
//				.decodeBase64(TestData.TEST_PUB_KEY));

        PublicKey pub =
                KeyCodec.getPubKey(Base64.decode(Preferences.getSettingsParam("pub"), Base64.URL_SAFE));
        PrivateKey priv =
                KeyCodec.getPrivKey(Base64.decode(Preferences.getSettingsParam("priv"), Base64.URL_SAFE));
//				KeyCodec.getPrivKey(Base64
//				.decodeBase64(TestData.TEST_PRIV_KEY));

//        logger.info(" : dataForSigning : "
//                + Base64.encode(dataForSigning, Base64.URL_SAFE));

        BigInteger[] signatureGen = NamedCurve.signAndFromatToRS(priv,
                SHA.sha(dataForSigning, "SHA-256"));

        boolean verify = NamedCurve.verify(
                KeyCodec.getKeyAsRawBytes((ECPublicKey)pub),
                SHA.sha(dataForSigning, "SHA-256"),
                Asn1.decodeToBigIntegerArray(Asn1.getEncoded(signatureGen)));
        if (!verify) {
            throw new RuntimeException("Signatire match fail");
        }
        byte[] ret = Asn1.toRawSignatureBytes(signatureGen);
        //logger.info(" : signature : " + Base64.encode(ret, Base64.URL_SAFE));

        return ret;
    }

    private byte[] getSignedData(AuthenticateIn request) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;

        byteout.write(encodeInt(TagsEnum.TAG_AAID.id));
        value = getAAID();
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_ASSERTION_INFO.id));
        //2 bytes - vendor; 1 byte Authentication Mode; 2 bytes Sig Alg
        value = new byte[] { 0x00, 0x00, 0x01, 0x01, 0x00 };
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_AUTHENTICATOR_NONCE.id));
        value = SHA.sha256(BCrypt.gensalt()).getBytes();
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_FINAL_CHALLENGE.id));
        value = getFC(request);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_TRANSACTION_CONTENT_HASH.id));
        length = 0;
        byteout.write(encodeInt(length));

        byteout.write(encodeInt(TagsEnum.TAG_KEYID.id));
        value = getKeyId();
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_COUNTERS.id));
        value = getCounters();
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] getFC(AuthenticateIn request) throws NoSuchAlgorithmException {
        return SHA.sha(request.finalChallenge.getBytes(), "SHA-256");
    }

    private byte[] getAAID() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = Authenticator.AAID.getBytes();
        byteout.write(value);
        return byteout.toByteArray();
    }


    private byte[] getKeyId() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        String keyId = Preferences.getSettingsParam("keyId");
        byte[] value = keyId.getBytes();
        byteout.write(value);
        return byteout.toByteArray();
    }

    private byte[] getCounters() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byteout.write(encodeInt(0));
        byteout.write(encodeInt(1));
        return byteout.toByteArray();
    }

    private byte[] encodeInt(int id) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (id & 0x00ff);
        bytes[1] = (byte) ((id & 0xff00) >> 8);
        return bytes;
    }
}
