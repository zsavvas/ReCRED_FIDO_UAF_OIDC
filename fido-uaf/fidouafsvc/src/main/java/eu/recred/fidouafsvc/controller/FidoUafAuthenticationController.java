package eu.recred.fidouafsvc.controller;

import com.google.gson.Gson;
import eu.recred.fidouafsvc.service.impl.AuthenticationService;
import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fido.uaf.msg.AuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@RestController
@RequestMapping("/v1/authentication")
public class FidoUafAuthenticationController {

    private Gson _gson = new Gson();

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value="/request", method= RequestMethod.GET)
    public String getRequest() {
        return _gson.toJson(authenticationService.getAuthReqObj());
    }

    @RequestMapping(value = "/request/{appid}", method = RequestMethod.GET)
    public AuthenticationRequest[] getRequestByAppid(@PathVariable(value = "appid")String appid) {
        return authenticationService.getAuthReqObjAppId(appid);
    }

    @RequestMapping(value = "/request/{appid}/{trxcontent}", method = RequestMethod.GET)
    public AuthenticationRequest[] getRequestByAppidByTrxcontent(@PathVariable(value = "appid")String appid, @PathVariable(value = "trxcontent")String trxcontent) {
        return authenticationService.getAuthReqObjAppIdTrx(appid, trxcontent);
    }

    @RequestMapping(value = "/response", method = RequestMethod.POST)
    public AuthenticatorRecord[] getResponse(@RequestBody String payload) {
        return authenticationService.response(payload);
    }
}
