package eu.recred.fidouaf.authenticator.cmds;

import eu.recred.fidouaf.authenticator.Authenticator;
import eu.recred.fidouaf.tlv.AlgAndEncodingEnum;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.tlv.UnsignedUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sorin.teican on 10/6/2016.
 */
public class GetInfoCmd {

    public byte[] getInfoCmdProcessor() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_GETINFO_CMD_RESPONSE.id));
        value = authenticatorInfo();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] authenticatorInfo() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_STATUS_CODE.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(TagsEnum.UAF_CMD_STATUS_OK.id));

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_API_VERSION.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(1));

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_INFO.id));
        value = auxAuthenticatorInfo();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] auxAuthenticatorInfo() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_INDEX.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(1));

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AAID.id));
        value = Authenticator.AAID.getBytes();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_METADATA.id));
        value = metadata();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ASSERTION_SCHEME.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(0x01)); // (UAFV1TLV).

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ATTESTATION_TYPE.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_ATTESTATION_BASIC_FULL.id));

        return byteout.toByteArray();
    }

    private byte[] metadata() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        // authenticator type.
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(0x0004));

        // max key handles.
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(1));

        // user verification.
        // (USER_VERIFY_FINGERPRINT | USER_VERIFY_PASSCODE | USER_VERIFY_PATTERN).
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(0x02 | 0x04 | 0x80));

        // key protection. (KEY_PROTECTION_SOFTWARE).
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(0x01));

        // matcher protection. (MATCHER_PROTECTION_SOFTWARE).
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(0x01));

        // transaction confirmation display.
        // (TRANSACTION_CONFIRMATION_DISPLAY_PRIVILEGED_SOFTWARE).
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(0x02));

        // authentication alg.
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_DER.id));

        return byteout.toByteArray();
    }

}
