package eu.recred.fidouaf.authenticator;

import android.util.Base64;
import android.util.Log;

import eu.recred.fidouaf.crypto.Asn1;
import eu.recred.fidouaf.crypto.BCrypt;
import eu.recred.fidouaf.crypto.KeyCodec;
import eu.recred.fidouaf.crypto.NamedCurve;
import eu.recred.fidouaf.crypto.SHA;
import eu.recred.fidouaf.msg.asm.obj.RegisterIn;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.util.Preferences;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.spongycastle.jce.interfaces.ECPublicKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by sorin.teican on 10/4/2016.
 */
public class AuthenticatorRegOp {
    private KeyPair generatedKeys;
    private String KeyID;

    private void generateKeys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        generatedKeys = KeyCodec.getKeyPair();
    }

    private void saveKeys() {
        Preferences.setSettingsParam("pub", Base64.encodeToString(generatedKeys.getPublic().getEncoded(), Base64.URL_SAFE));
        Preferences.setSettingsParam("priv", Base64.encodeToString(generatedKeys.getPrivate().getEncoded(), Base64.URL_SAFE));
    }

    public String getAssertions(RegisterIn request) throws Exception {
        generateKeys();
        saveKeys();

        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;

        byteout.write(encodeInt(TagsEnum.TAG_UAFV1_REG_ASSERTION.id));
        value = getRegAssertion(request);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        return Base64.encodeToString(byteout.toByteArray(), Base64.URL_SAFE);
    }

    private byte[] getRegAssertion(RegisterIn request) throws Exception {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;

        byteout.write(encodeInt(TagsEnum.TAG_UAFV1_KRD.id));
        value = getSignedData(request);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byte[] signedDataValue = byteout.toByteArray();

        byteout.write(encodeInt(TagsEnum.TAG_ATTESTATION_BASIC_FULL.id));
        value = getAttestationBasicFull(signedDataValue);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] getAttestationBasicFull (byte[] signedDataValue) throws Exception {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;
        byteout.write(encodeInt(TagsEnum.TAG_SIGNATURE.id));
        value = getSignature(signedDataValue);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_ATTESTATION_CERT.id));
        value = Base64.decode(Authenticator.base64DERCert, Base64.URL_SAFE);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);
        return byteout.toByteArray();
    }

    private byte[] getSignedData(RegisterIn request) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value = null;
        int length = 0;

        byteout.write(encodeInt(TagsEnum.TAG_AAID.id));
        value = getAAID();
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_ASSERTION_INFO.id));
        //2 bytes - vendor; 1 byte Authentication Mode; 2 bytes Sig Alg; 2 bytes Pub Key Alg
        value = new byte[] { 0x00, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01 };
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        byteout.write(encodeInt(TagsEnum.TAG_FINAL_CHALLENGE.id));
        value = getFC(request);
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

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

        byteout.write(encodeInt(TagsEnum.TAG_PUB_KEY.id));
        value = getPubKeyId();
        length = value.length;
        byteout.write(encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] getSignature(byte[] dataForSigning) throws Exception {

//		PublicKey pub = KeyCodec.getPubKey(
//				Base64.encode(this.keyPair.getPublic().getEncoded(), Base64.URL_SAFE))
//				;
//		PrivateKey priv = KeyCodec.getPrivKey(Base64
//				.encode(this.keyPair.getPrivate().getEncoded(),Base64.URL_SAFE));
//		PublicKey pub = this.keyPair.getPublic();
        PrivateKey priv =
                KeyCodec.getPrivKey(Base64.decode(Authenticator.priv, Base64.URL_SAFE));
        //this.keyPair.getPrivate();

//        logger.info(" : dataForSigning : "
//                + Base64.encodeToString(dataForSigning, Base64.URL_SAFE));

        BigInteger[] signatureGen = NamedCurve.signAndFromatToRS(priv,
                SHA.sha(dataForSigning, "SHA-256"));

        boolean verify = NamedCurve.verify(
                KeyCodec.getKeyAsRawBytes((ECPublicKey)KeyCodec.getPubKey(Base64.decode(Authenticator.pubCert, Base64.URL_SAFE))),
                //KeyCodec.getKeyAsRawBytes((ECPublicKey)this.keyPair.getPublic()),
                SHA.sha(dataForSigning, "SHA-256"),
                Asn1.decodeToBigIntegerArray(Asn1.getEncoded(signatureGen)));
        if (!verify) {
            throw new RuntimeException("Signatire match fail");
        }
        byte[] ret = Asn1.toRawSignatureBytes(signatureGen);
        //logger.info(" : signature : " + Base64.encodeToString(ret, Base64.URL_SAFE));

        return ret;
    }

    private byte[] getPubKeyId() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        return KeyCodec.getKeyAsRawBytes((BCECPublicKey)this.generatedKeys.getPublic());
    }

    private byte[] getFC(RegisterIn request) throws NoSuchAlgorithmException {
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
        String keyId = "ebay-test-key-"+ Base64.encodeToString(BCrypt.gensalt().getBytes(), Base64.NO_WRAP);
        keyId = Base64.encodeToString(keyId.getBytes(), Base64.URL_SAFE);
        Log.d("FIDO UAF CORE - KEYID", keyId);
        Preferences.setSettingsParam("keyId", keyId);
        Preferences.setSettingsParam("AAID", Authenticator.AAID);
        byte[] value = keyId.getBytes();
        byteout.write(value);
        return byteout.toByteArray();
    }

    private byte[] getCounters() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byteout.write(encodeInt(0));
        byteout.write(encodeInt(1));
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
