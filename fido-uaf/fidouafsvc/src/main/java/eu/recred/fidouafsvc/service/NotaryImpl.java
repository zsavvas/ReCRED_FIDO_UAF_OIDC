package eu.recred.fidouafsvc.service;

/**
 * Created by georgeg on 04/07/16.
 */
public interface NotaryImpl {
    public String sign(String dataToSign);
    public boolean verify(String dataToSign, String signature);
}

