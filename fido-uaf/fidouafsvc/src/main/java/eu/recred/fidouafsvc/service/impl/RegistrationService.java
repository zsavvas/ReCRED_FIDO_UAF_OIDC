package eu.recred.fidouafsvc.service.impl;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import eu.recred.fido.uaf.msg.OperationHeader;
import eu.recred.fido.uaf.msg.RegistrationRequest;
import eu.recred.fido.uaf.msg.RegistrationResponse;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import eu.recred.fidouafsvc.dao.RegistrationRecordDao;
import eu.recred.fidouafsvc.dao.TrustedFacetDao;
import eu.recred.fidouafsvc.model.TrustedFacet;
import eu.recred.fidouafsvc.storage.RegistrationRecord;

@Service
public class RegistrationService {
	
	Gson gson = new Gson();
	
	@Autowired
	TrustedFacetDao trustedFacetDao;
	
	@Autowired
	RegistrationRecordDao registrationRecordDao;
	
	@Autowired
	FetchRequestService fetchRequestService;
	
	@Autowired
	ProcessResponseService processResponseService;

  public RegistrationRequest[] regReqUsername(String username) {
      RegistrationRequest[] request = new RegistrationRequest[1];
      request[0] = fetchRequestService.getRegistrationRequest(username);

      return request;
  }

  public RegistrationRequest[] regReqUsernameAppId(String username, String appId) {
    RegistrationRequest[] request = regReqUsername(username);
    setAppId(appId, request[0].header);

    return request;
  }

  public RegistrationRecord[] response(String payload) {
    RegistrationRecord[] result = null;
    if(!payload.isEmpty()) {
        RegistrationResponse[] fromJson = gson.fromJson(payload, RegistrationResponse[].class);

        RegistrationResponse response = fromJson[0];
        result = processResponseService.processRegResponse(response);
        if (result[0].status.equals("SUCCESS")) {
            try {
                registrationRecordDao.addRegistrationRecords(result);
            } catch (HibernateException e) {
                result = new RegistrationRecord[1];
                result[0] = new RegistrationRecord();
                result[0].status = "Error: Data couldn't be stored in DB";
            }
        } else {
            result = new RegistrationRecord[1];
            result[0] = new RegistrationRecord();
            result[0].status = "Error: payload could not be empty";
        }
    }

    return result;
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
}
