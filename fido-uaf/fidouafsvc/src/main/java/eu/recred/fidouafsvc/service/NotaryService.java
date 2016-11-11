package eu.recred.fidouafsvc.service;


/**
 * Created by georgeg on 04/07/16.
 */
public interface NotaryService {
    public String sign(String dataToSign);
    public boolean verify(String dataToSign, String signature);
}

