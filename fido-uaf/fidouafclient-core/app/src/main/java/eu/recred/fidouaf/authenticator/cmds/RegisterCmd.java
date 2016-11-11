package eu.recred.fidouaf.authenticator.cmds;

import android.util.Base64;

import eu.recred.fidouaf.crypto.BCrypt;
import eu.recred.fidouaf.authenticator.AndroidKeyStoreController;
import eu.recred.fidouaf.authenticator.RawKeyHandle;
import eu.recred.fidouaf.authenticator.Authenticator;
import eu.recred.fidouaf.tlv.ByteInputStream;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.tlv.UnsignedUtil;
import eu.recred.fidouaf.util.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by sorin.teican on 10/10/2016.
 */
public class RegisterCmd {

    private AndroidKeyStoreController androidKeyStoreController;

    private int authenticatorIndex;
    private byte[] appId;
    private byte[] finalChallenge;
    private byte[] keyHandleAccessToken;
    private byte[] username;
    private int attestationType;
    private int requestLength;
    private String keyAlias;

    private RawKeyHandle keyHandle;

    public RegisterCmd() {
        androidKeyStoreController = new AndroidKeyStoreController();
    }

    public byte[] process(ByteInputStream request) throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        parseRequest(request);

        return byteout.toByteArray();
    }

    private void parseRequest(ByteInputStream request) throws IOException {
        int tag;
        int len;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();

        requestLength = UnsignedUtil.read_UAFV1_UINT16(request);

        while (request.available() > 0) {
            tag = UnsignedUtil.read_UAFV1_UINT16(request);

            if (tag == TagsEnum.TAG_AUTHENTICATOR_INDEX.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request);
                authenticatorIndex = UnsignedUtil.read_UAFV1_UINT16(request);
            } else if (tag == TagsEnum.TAG_APPID.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                appId = byteout.toByteArray();
            } else if (tag == TagsEnum.TAG_FINAL_CHALLENGE.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                finalChallenge = byteout.toByteArray();
            } else if (tag == TagsEnum.TAG_USERNAME.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                username = byteout.toByteArray();
                keyHandle.setUsername(new String(username));
            } else if (tag == TagsEnum.TAG_ATTESTATION_TYPE.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request);
                attestationType = UnsignedUtil.read_UAFV1_UINT16(request);
            } else if (tag == TagsEnum.TAG_KEYHANDLE_ACCESS_TOKEN.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                keyHandleAccessToken = byteout.toByteArray();
                keyHandle.setKhAccessToken(keyHandleAccessToken);
            }
            byteout.reset();
        }
    }

    private byte[] getRegResponse() throws IOException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException, KeyStoreException, InvalidKeyException, SignatureException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_REGISTER_CMD_RESPONSE.id));
        value = buildRegResponse();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildRegResponse() throws IOException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException, KeyStoreException, InvalidKeyException,
            SignatureException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_STATUS_CODE.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(TagsEnum.UAF_CMD_STATUS_OK.id));

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_ASSERTION.id));
        value = buildAssertion();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_KEYHANDLE.id));
        value = androidKeyStoreController.symEncrypt(keyHandle.toByteArray());
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildAssertion() throws IOException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException, KeyStoreException, InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_REG_ASSERTION.id));
        value = regAssertion();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] regAssertion() throws IOException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException, KeyStoreException, InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_KRD.id));
        value = buildKRD();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byte[] dataToSign = byteout.toByteArray();

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ATTESTATION_BASIC_FULL.id));
        value = buildBasicFullAttestation(dataToSign);
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildKRD() throws IOException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AAID.id));
        value = Authenticator.AAID.getBytes();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ASSERTION_INFO.id));
        //2 bytes - vendor; 1 byte Authentication Mode; 2 bytes Sig Alg; 2 bytes Pub Key Alg
        value = new byte[] { 0x00, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01 };
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_FINAL_CHALLENGE.id));
        value = finalChallenge;
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_KEYID.id));
        value = getKeyId();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_COUNTERS.id));
        value = getCounters();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_PUB_KEY.id));
        value = getPubKey();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildBasicFullAttestation(byte[] data) throws IOException,
            NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            InvalidKeyException, SignatureException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_SIGNATURE.id));
        value = getSignature(data);
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ATTESTATION_CERT.id));
        value = Base64.decode(Authenticator.base64DERCert, Base64.URL_SAFE);
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] getSignature(byte[] data) throws IOException,
            NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            InvalidKeyException, SignatureException, RuntimeException
    {
        byte[] signature = androidKeyStoreController.sign(data, keyAlias);
        if (!androidKeyStoreController.verify(data, signature, keyAlias))
            throw new RuntimeException("Signature match fail");

        return signature;
    }

    private byte[] getPubKey()
        throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPair keyPair = androidKeyStoreController.generateKeyPair(keyAlias);
        keyHandle.setuAuthPriv(keyPair.getPrivate());
        return keyPair.getPublic().getEncoded();
    }

    private byte[] getKeyId() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        String s_appId = new String(appId);
        String keyId = s_appId + "keyid-"+ Base64.encodeToString(BCrypt.gensalt().getBytes(), Base64.NO_WRAP);
        keyId = Base64.encodeToString(keyId.getBytes(), Base64.URL_SAFE);
        keyAlias = keyId;
        Preferences.setSettingsParam("keyId", keyId);
        byte[] value = keyId.getBytes();
        byteout.write(value);
        return byteout.toByteArray();
    }

    private byte[] getCounters() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byteout.write(UnsignedUtil.encodeInt(0));
        byteout.write(UnsignedUtil.encodeInt(1));
        byteout.write(UnsignedUtil.encodeInt(0));
        byteout.write(UnsignedUtil.encodeInt(1));
        return byteout.toByteArray();
    }
}
