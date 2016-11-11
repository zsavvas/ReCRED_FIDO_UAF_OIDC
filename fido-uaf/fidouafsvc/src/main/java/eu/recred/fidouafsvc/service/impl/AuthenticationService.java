package eu.recred.fidouafsvc.service.impl;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import eu.recred.fido.uaf.msg.AuthenticationRequest;
import eu.recred.fido.uaf.msg.AuthenticationResponse;
import eu.recred.fido.uaf.msg.OperationHeader;
import eu.recred.fido.uaf.msg.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import eu.recred.fidouafsvc.dao.TrustedFacetDao;
import eu.recred.fidouafsvc.model.TrustedFacet;
import eu.recred.fidouafsvc.storage.AuthenticatorRecord;

@Service
public class AuthenticationService {
	
  Gson gson = new Gson();
	
  @Autowired
  TrustedFacetDao trustedFacetDao;
	
  @Autowired
  FetchRequestService fetchRequestService;
  
  @Autowired
  ProcessResponseService processResponseService;

  public AuthenticationRequest[] getAuthReqObj() {
      AuthenticationRequest[] requests = new AuthenticationRequest[1];
      requests[0] = fetchRequestService.getAuthenticationRequest();

      return requests;
  }

  public AuthenticationRequest[] getAuthReqObjAppId(String appId) {
    AuthenticationRequest[] request = getAuthReqObj();
    setAppId(appId, request[0].header);

    return request;
  }

  public AuthenticationRequest[] getAuthReqObjAppIdTrx(String appId, String trx) {
    AuthenticationRequest[] request = getAuthReqObjAppId(appId);
    setTransaction(trx, request);

    return request;
  }

  public AuthenticatorRecord[] response(String payload) {
    if (!payload.isEmpty()) {
        AuthenticationResponse[] responses = gson.fromJson(payload, AuthenticationResponse[].class);
        AuthenticatorRecord[] result = processResponseService.processAuthResponse(responses[0]);
        return result;
    }
    return new AuthenticatorRecord[0];
  }

  private void setAppId(String appId, OperationHeader header) {
      if (appId == null || appId.isEmpty()){
          return;
      }

      String decodedAppId = new String (Base64.decodeBase64(appId));
      List<TrustedFacet> facets = trustedFacetDao.listAllTrustedFacets();
      if (facets == null || facets.isEmpty()) return;
      int len = facets.size();
      for (int i = 0; i < len; i++) {
          if (decodedAppId.equals(facets.get(i).getName())) {
              header.appID = decodedAppId;
              break;
          }
      }
  }

  private void setTransaction(String trxcontent, AuthenticationRequest[] requests) {
      requests[0].transaction = new Transaction[1];
      Transaction t = new Transaction();
      t.content = trxcontent;
      t.content = "text/plain";
      requests[0].transaction[0] = t;
  }

}
