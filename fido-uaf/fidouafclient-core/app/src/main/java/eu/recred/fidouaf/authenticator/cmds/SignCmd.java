package eu.recred.fidouaf.authenticator.cmds;

import eu.recred.fidouaf.crypto.BCrypt;
import eu.recred.fidouaf.crypto.SHA;
import eu.recred.fidouaf.authenticator.AndroidKeyStoreController;
import eu.recred.fidouaf.authenticator.Authenticator;
import eu.recred.fidouaf.tlv.ByteInputStream;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.tlv.UnsignedUtil;
import eu.recred.fidouaf.util.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.util.Enumeration;

/**
 * Created by sorin.teican on 10/11/2016.
 */
public class SignCmd {

    private AndroidKeyStoreController androidKeyStoreController;

    private int authenticatorIndex;
    private byte[] appId;
    private byte[] finalChallenge;
    private byte[] keyHandleAccessToken;
    private int requestLength;

    public SignCmd() {
        androidKeyStoreController = new AndroidKeyStoreController();
    }

    public byte[] process(ByteInputStream request) throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        //parseRequest(request);

        return byteout.toByteArray();
    }

    private void parseRequest(ByteInputStream request) throws IOException {
        int tag;
        int len;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();

        requestLength = UnsignedUtil.read_UAFV1_UINT16(request);

        while(request.available() > 0) {
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
            } else if (tag == TagsEnum.TAG_KEYHANDLE_ACCESS_TOKEN.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                keyHandleAccessToken = byteout.toByteArray();
            }
            byteout.reset();
        }
    }

    private byte[] getSignResponse() throws IOException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_SIGN_CMD_RESPONSE.id));
        value = buildSignResponse();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildSignResponse() throws IOException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_STATUS_CODE.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(TagsEnum.UAF_CMD_STATUS_OK.id));

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_USERNAME.id));
        value = getUsername();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_KEYHANDLE.id));
        value = getKeyHandle();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_ASSERTION.id));
        value = authAssertion();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] getUsername() {
        return "username".getBytes();
    }

    private byte[] getKeyHandle() {
        return "keyHandle".getBytes();
    }

    private byte[] authAssertion() throws IOException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_AUTH_ASSERTION.id));
        value = buildAuthAssertion();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildAuthAssertion() throws IOException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_SIGNED_DATA.id));
        value = getSignatureData();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byte[] dataToSign = byteout.toByteArray();

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_SIGNATURE.id));
        value = getSignature(dataToSign);
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);


        return byteout.toByteArray();
    }

    private byte[] getSignatureData() throws IOException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AAID.id));
        value = Authenticator.AAID.getBytes();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ASSERTION_INFO.id));
        //2 bytes - vendor; 1 byte Authentication Mode; 2 bytes Sig Alg
        value = new byte[] { 0x00, 0x00, 0x01, 0x01, 0x00 };
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_NONCE.id));
        value = SHA.sha256(BCrypt.gensalt()).getBytes();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_FINAL_CHALLENGE.id));
        byteout.write(UnsignedUtil.encodeInt(finalChallenge.length));
        byteout.write(finalChallenge);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_TRANSACTION_CONTENT_HASH.id));
        length = 0;
        byteout.write(UnsignedUtil.encodeInt(length));

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

        return byteout.toByteArray();
    }

    private byte[] getSignature(byte[] data) throws IOException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        String alias = getAlias();

        byte[] signature = androidKeyStoreController.sign(data, alias);
        if (!androidKeyStoreController.verify(data, signature, alias))
            throw new RuntimeException("Signature match fail");

        return signature;
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
        byteout.write(UnsignedUtil.encodeInt(0));
        byteout.write(UnsignedUtil.encodeInt(1));
        return byteout.toByteArray();
    }

    private String getAlias() throws IOException, KeyStoreException {
        Enumeration<String> aliases = androidKeyStoreController.getAliases();

        String s_appID = new String(appId);

        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (alias.contains(s_appID))
                return alias;
        }

        return null;
    }
}
