/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.recred.fidouaf.clientop;

import android.util.Base64;

import com.google.gson.Gson;

import eu.recred.fidouaf.asm.ASM;
import eu.recred.fidouaf.msg.AuthenticatorRegistrationAssertion;
import eu.recred.fidouaf.msg.FinalChallengeParams;
import eu.recred.fidouaf.msg.OperationHeader;
import eu.recred.fidouaf.msg.RegistrationRequest;
import eu.recred.fidouaf.msg.RegistrationResponse;
import eu.recred.fidouaf.msg.asm.ASMRequest;
import eu.recred.fidouaf.msg.asm.ASMResponse;
import eu.recred.fidouaf.msg.asm.Request;
import eu.recred.fidouaf.msg.asm.obj.RegisterIn;
import eu.recred.fidouaf.msg.asm.obj.RegisterOut;
import eu.recred.fidouaf.util.Preferences;


public class RegistrationRequestProcessor {

	private ASM asm = new ASM();
	
	public RegistrationResponse processRequest(RegistrationRequest regRequest) {
		RegistrationResponse response = new RegistrationResponse();
		//RegAssertionBuilder builder = new RegAssertionBuilder(keyPair);
		Gson gson = new Gson();


		setAppId(regRequest, response);
		response.header = new OperationHeader();
		response.header.serverData = regRequest.header.serverData;
		response.header.appID = regRequest.header.appID;
		response.header.op = regRequest.header.op;
		response.header.upv = regRequest.header.upv;

		FinalChallengeParams fcParams = new FinalChallengeParams();
		fcParams.appID = regRequest.header.appID;
		Preferences.setSettingsParam("appID", fcParams.appID);
		fcParams.facetID = getFacetId();
		fcParams.challenge = regRequest.challenge;
		response.fcParams = Base64.encodeToString(gson.toJson(
				fcParams).getBytes(), Base64.URL_SAFE);

		RegisterIn registerIn = new RegisterIn();
		registerIn.appID = fcParams.appID;
		registerIn.username = Preferences.getSettingsParam("username");
		registerIn.attestationType = 0;
		registerIn.finalChallenge = response.fcParams;

		ASMRequest<RegisterIn> asmRequest = new ASMRequest<>();
		asmRequest.requestType = Request.Register;
		asmRequest.asmVersion = regRequest.header.upv;
		asmRequest.authenticatorIndex = 0;
		asmRequest.args = registerIn;

		ASMResponse<RegisterOut> asmResponse = asm.register(asmRequest);

		response.assertions = new AuthenticatorRegistrationAssertion[1];
		response.assertions[0] = new AuthenticatorRegistrationAssertion();
		response.assertions[0].assertionScheme = "UAFV1TLV";
		response.assertions[0].assertion = asmResponse.responseData.assertion;

		//setAssertions(response,builder);
		return response;
	}

	private String getFacetId() {
		return "";
	}

	private void setAssertions(RegistrationResponse response, RegAssertionBuilder builder) {
		response.assertions = new AuthenticatorRegistrationAssertion[1];
		try {
			response.assertions[0] = new AuthenticatorRegistrationAssertion();
			response.assertions[0].assertion = builder.getAssertions(response);
			response.assertions[0].assertionScheme = "UAFV1TLV";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void setAppId(RegistrationRequest regRequest,
			RegistrationResponse response) {
		// TODO Auto-generated method stub
		
	}

}
