package eu.recred.fidouaf.authenticator;

import eu.recred.fidouaf.tlv.UnsignedUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;

import javax.crypto.SecretKey;

/**
 * Created by sorin.teican on 10/13/2016.
 */
public class RawKeyHandle {

    private byte[] khAccessToken;
    private PrivateKey uAuthPriv;
    private String username;

    public byte[] toByteArray() throws IOException {
        if (khAccessToken == null || khAccessToken.length == 0)
            return null;
        if (uAuthPriv == null)
            return null;
        if (username == null || username.length() == 0)
            return null;

        ByteArrayOutputStream byteout = new ByteArrayOutputStream();

        byteout.write(khAccessToken);
        byteout.write(uAuthPriv.getEncoded());
        byteout.write(UnsignedUtil.encodeInt(username.length()));
        byteout.write(username.getBytes());

        return byteout.toByteArray();
    }

    public void setKhAccessToken(byte[] khAccessToken) {
        this.khAccessToken = khAccessToken;
    }

    public void setuAuthPriv(PrivateKey uAuthPriv) {
        this.uAuthPriv = uAuthPriv;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
