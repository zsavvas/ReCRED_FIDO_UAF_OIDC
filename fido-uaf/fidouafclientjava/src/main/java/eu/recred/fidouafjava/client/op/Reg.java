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

package eu.recred.fidouafjava.client.op;

import eu.recred.fidouafjava.client.util.Curl;
import eu.recred.fidouafjava.client.util.Preferences;
import eu.recred.fidouafjava.fido.uaf.msg.AuthenticatorRegistrationAssertion;
import eu.recred.fidouafjava.fido.uaf.msg.ChannelBinding;
import eu.recred.fidouafjava.fido.uaf.msg.FinalChallengeParams;
import eu.recred.fidouafjava.fido.uaf.msg.OperationHeader;
import eu.recred.fidouafjava.fido.uaf.msg.RegistrationRequest;
import eu.recred.fidouafjava.fido.uaf.msg.RegistrationResponse;
import eu.recred.fidouafjava.fido.uaf.msg.asm.obj.RegisterIn;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.recred.fidouafjava.fido.uaf.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Reg {
	
	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public String getUafMsgRegRequest (String username, String facetId){
		String serverResponse = getRegRequest(username);
		return OpUtils.getUafRequest(serverResponse,facetId,false);
	}

	public String getRegRequest (String username){
		String url = Preferences.getInstance().getServer() + Preferences.getInstance().getRegRequestEndpoint()+username;
		return Curl.getInSeparateThread(url);
	}
	
	public RegisterIn getRegIn(String username){
		RegisterIn ret = new RegisterIn();
		String url = Preferences.getInstance().getRegRequestEndpoint()+username;
		String regRespFromServer = Curl.getInSeparateThread(url);
		RegistrationRequest regRequest = null;
		try{
			regRequest = gson.fromJson(regRespFromServer, RegistrationRequest[].class)[0];
			ret.appID = regRequest.header.appID;
			ret.attestationType = 15879;
			ret.finalChallenge = getFinalChalenge(regRequest);
			ret.username = username;
			freezeRegResponse(regRequest);
		} catch (Exception e){
			
		}
		
		return ret;
	}
	
	public String clientSendRegResponse (String uafMessage){
		String serverResponse = OpUtils.clientSendRegResponse(uafMessage,Preferences.getInstance().getServer() + Preferences.getInstance().getRegResponseEndpoint());
		saveAAIDandKeyID(serverResponse);
		return serverResponse;
	}
	
	public String sendRegResponse (String regOut){
		StringBuffer res = new StringBuffer();
		res.append("{regOut}"+regOut);
		String json = getRegResponseForSending(regOut);
		res.append("{regResponse}"+json);
		String headerStr = "Content-Type:Application/json Accept:Application/json";
		res.append("{ServerResponse}");
		String serverResponse = Curl.postInSeparateThread(Preferences.getInstance().getServer() + Preferences.getInstance().getRegResponseEndpoint(), headerStr , json);
		res.append(serverResponse);
		saveAAIDandKeyID(serverResponse);
		return res.toString();
	}
	
	
	private void saveAAIDandKeyID(String res) {
		try{
			String[] splitted = res.split("#ServerResponse\n");
			JSONArray regRecord = new JSONArray(splitted[1]);
			JSONObject authenticator = regRecord.getJSONObject(0).getJSONObject("authenticator");
			Preferences.getInstance().setSettingsParam("AAID", authenticator.getString("AAID"));
			Preferences.getInstance().setSettingsParam("keyID", authenticator.getString("KeyID"));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public String getRegResponseForSending (String regOut){
		String ret = null;
		try{
		RegistrationResponse regResponse = gson.fromJson(Preferences.getInstance().getSettingsParam("regResponse"), RegistrationResponse.class);
		JSONObject assertions = new JSONObject(regOut);
		regResponse.assertions = new AuthenticatorRegistrationAssertion[1];
		regResponse.assertions[0] = new AuthenticatorRegistrationAssertion();
		regResponse.assertions[0].assertionScheme = assertions.getJSONObject("responseData").getString("assertionScheme");
		regResponse.assertions[0].assertion = assertions.getJSONObject("responseData").getString("assertion");
		RegistrationResponse[] forSending = new RegistrationResponse[1];
		forSending[0] = regResponse;
		return gson.toJson(forSending, RegistrationResponse[].class);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}

	private String getFinalChalenge(RegistrationRequest regRequest) {
		FinalChallengeParams fcParams = new FinalChallengeParams();
		fcParams.appID = regRequest.header.appID;
		Preferences.getInstance().setSettingsParam("appID", fcParams.appID);
		fcParams.facetID = getFacetId();
		fcParams.challenge = regRequest.challenge;
		fcParams.channelBinding = new ChannelBinding();
		fcParams.channelBinding.cid_pubkey = "";
		fcParams.channelBinding.serverEndPoint = "";
		fcParams.channelBinding.tlsServerCertificate = "";
		fcParams.channelBinding.tlsUnique = "";
		return Base64.encodeToString(gson.toJson(
				fcParams).getBytes(), Base64.URL_SAFE);
	}

	private String getFacetId() {
		return "";
	}
	
	public void freezeRegResponse (RegistrationRequest regRequest){
		String json = gson.toJson(getRegResponse(regRequest), RegistrationResponse.class);
		Preferences.getInstance().setSettingsParam("regResponse", json);
	}
	
	public RegistrationResponse getRegResponse (RegistrationRequest regRequest){
		RegistrationResponse response = new RegistrationResponse();
		
		response.header = new OperationHeader();
		response.header.serverData = regRequest.header.serverData;
		response.header.appID = regRequest.header.appID;
		response.header.op = regRequest.header.op;
		response.header.upv = regRequest.header.upv;
		response.fcParams = getFinalChalenge(regRequest);
		
		return response;
	}

}
