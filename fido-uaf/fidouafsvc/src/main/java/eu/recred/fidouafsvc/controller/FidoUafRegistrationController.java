package eu.recred.fidouafsvc.controller;

import com.google.gson.Gson;
import eu.recred.fidouafsvc.dao.RegistrationRecordDao;
import eu.recred.fidouafsvc.dao.TrustedFacetDao;
import eu.recred.fidouafsvc.model.TrustedFacet;
import eu.recred.fidouafsvc.service.impl.DeregRequestProcessorService;
import eu.recred.fidouafsvc.service.impl.FetchRequestService;
import eu.recred.fidouafsvc.service.impl.ProcessResponseService;
import eu.recred.fidouafsvc.service.impl.RegistrationService;
import eu.recred.fidouafsvc.storage.RegistrationRecord;
import org.apache.commons.codec.binary.Base64;
import eu.recred.fido.uaf.msg.OperationHeader;
import eu.recred.fido.uaf.msg.RegistrationRequest;
import eu.recred.fido.uaf.msg.RegistrationResponse;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by georgeg on 02/07/16.
 */
@RestController
@RequestMapping("/v1/registration")
public class FidoUafRegistrationController {

    @Autowired
    private DeregRequestProcessorService deregRequestProcessorService;

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(value="/request/{username}", method=RequestMethod.GET)
    public RegistrationRequest[] getRequestByUsername(@PathVariable(value = "username")String username) {
        return registrationService.regReqUsername(username);
    }

    @RequestMapping(value="/request/{username}/{appid}", method=RequestMethod.GET)
    public RegistrationRequest[] getRequestByUsernameByAppid(@PathVariable(value = "username")String username, @PathVariable(value = "appid")String appid) {
        return registrationService.regReqUsernameAppId(username, appid);
    }

    @RequestMapping(value="/response", method=RequestMethod.POST)
    public RegistrationRecord[] getResponse(@RequestBody String payload) {
    	return registrationService.response(payload);
    }

    @RequestMapping(value = "/dereg", method = RequestMethod.POST)
    public String getDereg(@RequestBody String payload) {
        return deregRequestProcessorService.process(payload);
    }
}
