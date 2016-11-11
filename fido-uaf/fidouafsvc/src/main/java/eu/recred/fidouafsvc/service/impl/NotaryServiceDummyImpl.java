package eu.recred.fidouafsvc.service.impl;

import org.apache.commons.codec.binary.Base64;
import eu.recred.fido.uaf.crypto.HMAC;
import eu.recred.fido.uaf.crypto.Notary;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

/**
 * Created by georgeg on 04/07/16.
 */
@Service
public class NotaryServiceDummyImpl implements Notary {

    private String hmacSecret = "HMAC-is-just-one-way";

    public NotaryServiceDummyImpl() {}

    public String   sign(String signData) {
        try {
            return Base64.encodeBase64URLSafeString(HMAC.sign(signData, hmacSecret));
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public boolean  verify(String signData, String signature) {
        try {
            return MessageDigest.isEqual(Base64.decodeBase64(signature), HMAC.sign(signData, hmacSecret));
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }
}
