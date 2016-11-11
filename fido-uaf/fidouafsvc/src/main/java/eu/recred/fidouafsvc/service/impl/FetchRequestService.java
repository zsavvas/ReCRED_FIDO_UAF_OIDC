package eu.recred.fidouafsvc.service.impl;

import eu.recred.fidouafsvc.model.FidoConfig;
import eu.recred.fidouafsvc.ops.AuthenticationRequestGeneration;
import eu.recred.fidouafsvc.ops.RegistrationRequestGeneration;
import eu.recred.fido.uaf.msg.AuthenticationRequest;
import eu.recred.fido.uaf.msg.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@Service
public class FetchRequestService {

    @Autowired
    private NotaryServiceDummyImpl notaryServiceDummy;

    private String appId = "";
    private String[] aaids = null;

    @Autowired
    public FetchRequestService(FidoConfig config) {
        appId = config.getAppId();
        aaids = config.getAaids();
    }

    public RegistrationRequest getRegistrationRequest(String username) {
        RegistrationRequest request = new RegistrationRequestGeneration(appId, aaids)
                .createRegistrationRequest(username, notaryServiceDummy);

        return request;
    }

    public AuthenticationRequest getAuthenticationRequest() {
        AuthenticationRequest request = new AuthenticationRequestGeneration(appId, aaids)
                .createAuthenticationRequest(notaryServiceDummy);

        return request;
    }
}
