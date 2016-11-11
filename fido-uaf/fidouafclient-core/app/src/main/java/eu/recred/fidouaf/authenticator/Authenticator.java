package eu.recred.fidouaf.authenticator;

import eu.recred.fidouaf.msg.asm.obj.AuthenticateIn;
import eu.recred.fidouaf.msg.asm.obj.AuthenticateOut;
import eu.recred.fidouaf.msg.asm.obj.AuthenticatorInfo;
import eu.recred.fidouaf.msg.asm.obj.DeregisterIn;
import eu.recred.fidouaf.msg.asm.obj.GetInfoOut;
import eu.recred.fidouaf.msg.asm.obj.RegisterIn;
import eu.recred.fidouaf.msg.asm.obj.RegisterOut;

import java.security.KeyPair;

/**
 * Created by sorin.teican on 10/4/2016.
 */
public class Authenticator {

    public static String base64DERCert = "MIIB-TCCAZ-gAwIBAgIEVTFM0zAJBgcqhkjOPQQBMIGEMQswCQYDVQQGEwJVUzELMAkGA1UECAwCQ0ExETAPBgNVBAcMCFNhbiBKb3NlMRMwEQYDVQQKDAplQmF5LCBJbmMuMQwwCgYDVQQLDANUTlMxEjAQBgNVBAMMCWVCYXksIEluYzEeMBwGCSqGSIb3DQEJARYPbnBlc2ljQGViYXkuY29tMB4XDTE1MDQxNzE4MTEzMVoXDTE1MDQyNzE4MTEzMVowgYQxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTERMA8GA1UEBwwIU2FuIEpvc2UxEzARBgNVBAoMCmVCYXksIEluYy4xDDAKBgNVBAsMA1ROUzESMBAGA1UEAwwJZUJheSwgSW5jMR4wHAYJKoZIhvcNAQkBFg9ucGVzaWNAZWJheS5jb20wWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQ8hw5lHTUXvZ3SzY9argbOOBD2pn5zAM4mbShwQyCL5bRskTL3HVPWPQxqYVM-3pJtJILYqOWsIMd5Rb_h8D-EMAkGByqGSM49BAEDSQAwRgIhAIpkop_L3fOtm79Q2lKrKxea-KcvA1g6qkzaj42VD2hgAiEArtPpTEADIWz2yrl5XGfJVcfcFmvpMAuMKvuE1J73jp4";
    public static String pubCert = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEPIcOZR01F72d0s2PWq4GzjgQ9qZ-cwDOJm0ocEMgi-W0bJEy9x1T1j0MamFTPt6SbSSC2KjlrCDHeUW_4fA_hA";
    public static String priv = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgezOOy1TykYoCiwOdJkKCfScV3-lN1v_E9keawMikuFygCgYIKoZIzj0DAQehRANCAAQ8hw5lHTUXvZ3SzY9argbOOBD2pn5zAM4mbShwQyCL5bRskTL3HVPWPQxqYVM-3pJtJILYqOWsIMd5Rb_h8D-E";

    public static final String AAID = "EBA0#0001";

    private KeyPair keyPair = null;

    public GetInfoOut discover() {
        GetInfoOut getInfoOut = new GetInfoOut();

        getInfoOut.Authenticators = new AuthenticatorInfo[1];
        AuthenticatorInfo authenticatorInfo = new AuthenticatorDiscoverOp().discover();
        getInfoOut.Authenticators[0] = authenticatorInfo;

        return getInfoOut;
    }

    public RegisterOut register(RegisterIn request) throws Exception {
        RegisterOut registerOut = new RegisterOut();

        registerOut.assertion = new AuthenticatorRegOp().getAssertions(request);
        registerOut.assertionScheme = "UAFV1TLV";

        return registerOut;
    }

    public AuthenticateOut authenticate(AuthenticateIn request) throws Exception {
        AuthenticateOut authenticateOut = new AuthenticateOut();

        authenticateOut.assertion = new AuthenticatorAuthOp().getAssertions(request);
        authenticateOut.assertionScheme = "UAFV1TLV";

        return authenticateOut;
    }

    public void deregister(DeregisterIn request) {

    }

//    public byte[] processCmd(ByteInputStream cmd) throws IOException {
//        if (cmd.available() > 0) {
//            Tag t = new Tag();
//            t.id = UnsignedUtil.read_UAFV1_UINT16(cmd);
//            t.length = UnsignedUtil.read_UAFV1_UINT16(cmd);
//
//            if (t.id == TagsEnum.TAG_UAFV1_GETINFO_CMD.id) {
//                return new GetInfoCmd().getInfoCmdProcessor();
//            } else if (t.id == TagsEnum.TAG_UAFV1_REGISTER_CMD.id){
//                //return new RegisterCmd().process(cmd);
//            } else if (t.id == TagsEnum.TAG_UAFV1_SIGN_CMD.id) {
//                //return new SignCmd().process(cmd);
//            } else if (t.id == TagsEnum.TAG_UAFV1_DEREGISTER_CMD.id) {
//                return new DeregCmd().process(cmd);
//            } else if(t.id == TagsEnum.TAG_UAFV1_OPEN_SETTINGS_CMD.id) {
//                return new OpenSettingsCmd().process(cmd);
//            } else {
//                ByteArrayOutputStream byteout = new ByteArrayOutputStream();
//                byteout.write(UnsignedUtil.encodeInt(TagsEnum.UAF_CMD_STATUS_CMD_NOT_SUPPORTED.id));
//                return byteout.toByteArray();
//            }
//        } else {
//            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
//            byteout.write(UnsignedUtil.encodeInt(TagsEnum.UAF_CMD_STATUS_ERR_UNKNOWN.id));
//            return byteout.toByteArray();
//        }
//    }
}
