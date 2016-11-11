package eu.recred.fidouaf.authenticator;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by sorin.teican on 10/11/2016.
 */
public class AndroidKeyStoreController {
    private boolean allGood = true;

    private KeyStore androidKeyStore;

    public AndroidKeyStoreController() {
        try {
            androidKeyStore = KeyStore.getInstance("AndroidKeyStore");
            androidKeyStore.load(null);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            allGood = false;
            e.printStackTrace();
        }
    }

    public KeyPair generateKeyPair(String keyAlias)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
    {
        if (!allGood)
            return null;

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                        keyAlias,
                        KeyProperties.PURPOSE_SIGN)
                        .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setUserAuthenticationRequired(true)
                        .setUserAuthenticationValidityDurationSeconds(5 * 60)
                        .build());

        return keyPairGenerator.generateKeyPair();
    }

    public SecretKey generateSym() throws NoSuchAlgorithmException, NoSuchProviderException,
        InvalidAlgorithmParameterException
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        keyGenerator.init(new KeyGenParameterSpec.Builder("wrap.sym",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(5 * 60)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());

        return keyGenerator.generateKey();
    }

    public byte[] symEncrypt(byte[] data)
        throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        SecretKey secretKey = (SecretKey) androidKeyStore.getKey("wrap.sym", null);

        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_GCM + "/" + KeyProperties.ENCRYPTION_PADDING_NONE);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public byte[] symDecrypt(byte[] data)
        throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        SecretKey secretKey = (SecretKey) androidKeyStore.getKey("wrap.sym", null);

        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_GCM + "/" + KeyProperties.ENCRYPTION_PADDING_NONE);

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public Enumeration<String> getAliases() throws KeyStoreException {
        return androidKeyStore.aliases();
    }

    public byte[] sign(byte[] data, String keyAlias)
            throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            InvalidKeyException, SignatureException
    {

        KeyStore.Entry entry = androidKeyStore.getEntry(keyAlias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry))
            return null;

        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initSign(((KeyStore.PrivateKeyEntry)entry).getPrivateKey());
        s.update(data);
        return s.sign();
    }

    public byte[] unsecure_key_sign(byte[] data, PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initSign(key);
        s.update(data);
        return s.sign();
    }

    public boolean verify(byte[] data, byte[] signature, String keyAlias)
            throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            InvalidKeyException, SignatureException
    {
        KeyStore.Entry entry = androidKeyStore.getEntry(keyAlias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry))
            return false;

        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initVerify(((KeyStore.PrivateKeyEntry)entry).getCertificate());
        s.update(data);
        return s.verify(signature);
    }

    public boolean verify(byte[] data, byte[] signature, PublicKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initVerify(key);
        s.update(data);
        return s.verify(signature);
    }
}
