package eu.recred.fidouaf.authenticator;

import eu.recred.fidouaf.msg.Version;
import eu.recred.fidouaf.msg.asm.obj.AuthenticatorInfo;

/**
 * Created by sorin.teican on 11-Nov-16.
 */
public class AuthenticatorDiscoverOp {

    public AuthenticatorInfo discover() {
        AuthenticatorInfo authenticatorInfo = new AuthenticatorInfo();

        authenticatorInfo.authenticatorIndex = 1;
        authenticatorInfo.asmVersions = new Version[1];
        authenticatorInfo.asmVersions[0] = new Version(1, 0);
        authenticatorInfo.isUserEnrolled = true;
        authenticatorInfo.hasSettings = true;
        authenticatorInfo.aaid = Authenticator.AAID;
        authenticatorInfo.assertionScheme = "UAFV1TLV";
        // UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW 0x01
        authenticatorInfo.authenticationAlgorithm = 0x01;
        authenticatorInfo.attestationTypes = new short[1];
        // TAG_ATTESTATION_BASIC_FULL 0x3E07
        authenticatorInfo.attestationTypes[0] = 0x3E07;
        // USER_VERIFY_FINGERPRINT 0x02 | USER_VERIFY_PASSCODE 0x04 | USER_VERIFY_PATTERN 0x80
        authenticatorInfo.userVerification = 0x02 | 0x04 | 0x80;
        // KEY_PROTECTION_SOFTWARE 0x01
        authenticatorInfo.keyProtection = 0x01;
        // MATCHER_PROTECTION_SOFTWARE 0x01
        authenticatorInfo.matcherProtection = 0x01;
        // ATTACHMENT_HINT_INTERNAL 0x01
        authenticatorInfo.attachmentHint = 0x01;
        authenticatorInfo.isSecondFactorOnly = false;
        authenticatorInfo.isRoamingAuthenticator = false;
        authenticatorInfo.supportedExtensionIDs = new String[0];
        // TRANSACTION_CONFIRMATION_DISPLAY_PRIVILEGED_SOFTWARE 0x02
        authenticatorInfo.tcDisplay = 0x02;
        authenticatorInfo.tcDisplayContentType = "text/plain";
        authenticatorInfo.tcDisplayPNGCharacteristics = null;
        authenticatorInfo.title = "Android UAF";
        authenticatorInfo.description = "Internal Android Authenticator";
        authenticatorInfo.icon = "data:image/png;base64,iVBORw0KGgoAAA";


        return authenticatorInfo;
    }

}
