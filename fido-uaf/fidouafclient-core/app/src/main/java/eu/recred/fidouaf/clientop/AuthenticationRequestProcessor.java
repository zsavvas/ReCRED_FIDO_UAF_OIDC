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
import eu.recred.fidouaf.msg.AuthenticationRequest;
import eu.recred.fidouaf.msg.AuthenticationResponse;
import eu.recred.fidouaf.msg.AuthenticatorSignAssertion;
import eu.recred.fidouaf.msg.FinalChallengeParams;
import eu.recred.fidouaf.msg.OperationHeader;
import eu.recred.fidouaf.msg.asm.ASMRequest;
import eu.recred.fidouaf.msg.asm.ASMResponse;
import eu.recred.fidouaf.msg.asm.Request;
import eu.recred.fidouaf.msg.asm.obj.AuthenticateIn;
import eu.recred.fidouaf.msg.asm.obj.AuthenticateOut;
import eu.recred.fidouaf.util.Preferences;

public class AuthenticationRequestProcessor {

	private ASM asm = new ASM();
	
	public AuthenticationResponse processRequest(AuthenticationRequest request) {
		AuthenticationResponse response = new AuthenticationResponse();
		AuthAssertionBuilder builder = new AuthAssertionBuilder();
		Gson gson = new Gson();


		response.header = new OperationHeader();
		response.header.serverData = request.header.serverData;
		response.header.op = request.header.op;
		response.header.upv = request.header.upv;
		response.header.appID = request.header.appID;

		FinalChallengeParams fcParams = new FinalChallengeParams();
		fcParams.appID = request.header.appID;
		fcParams.facetID = getFacetId();
		fcParams.challenge = request.challenge;
		response.fcParams = Base64.encodeToString(gson.toJson(
				fcParams).getBytes(), Base64.URL_SAFE);

		AuthenticateIn authenticateIn = new AuthenticateIn();
		authenticateIn.finalChallenge = response.fcParams;
		authenticateIn.appID = request.header.appID;
		authenticateIn.keyIDs = new String[1];
		authenticateIn.keyIDs[0] = Preferences.getSettingsParam("keyId");

		ASMRequest<AuthenticateIn> asmRequest = new ASMRequest<>();
		asmRequest.requestType = Request.Authenticate;
		asmRequest.asmVersion = request.header.upv;
		asmRequest.args = authenticateIn;
		asmRequest.authenticatorIndex = 1;

		ASMResponse<AuthenticateOut> asmResponse = asm.authenticate(asmRequest);
		response.assertions = new AuthenticatorSignAssertion[1];
		response.assertions[0] = new AuthenticatorSignAssertion();
		response.assertions[0].assertionScheme = "UAFV1TLV";
		response.assertions[0].assertion = asmResponse.responseData.assertion;

		//setAssertions(response,builder);
		return response;
	}

	private String getFacetId() {
		return "";
	}

	private void setAssertions(AuthenticationResponse response, AuthAssertionBuilder builder) {
		response.assertions = new AuthenticatorSignAssertion[1];
		try {
			response.assertions[0] = new AuthenticatorSignAssertion();
			response.assertions[0].assertion = builder.getAssertions(response);
			response.assertions[0].assertionScheme = "UAFV1TLV";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
