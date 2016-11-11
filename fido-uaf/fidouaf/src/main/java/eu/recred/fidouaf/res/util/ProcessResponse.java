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

package eu.recred.fidouaf.res.util;

import eu.recred.fidouaf.ops.AuthenticationResponseProcessing;
import eu.recred.fidouaf.ops.RegistrationResponseProcessing;
import eu.recred.fidouaf.storage.AuthenticatorRecord;
import eu.recred.fidouaf.storage.RegistrationRecord;
import org.apache.commons.codec.binary.Base64;
import eu.recred.fido.uaf.msg.AuthenticationResponse;
import eu.recred.fido.uaf.msg.RegistrationResponse;

import java.security.SecureRandom;
import java.util.logging.Logger;

public class ProcessResponse {

	private static final int SERVER_DATA_EXPIRY_IN_MS = 5 * 60 * 1000;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private StorageImpl storage = StorageImpl.getInstance();

	// Gson gson = new Gson ();

	public AuthenticatorRecord[] processAuthResponse(AuthenticationResponse resp) {
		AuthenticatorRecord[] result = null;
		try {
			result = new AuthenticationResponseProcessing(
					SERVER_DATA_EXPIRY_IN_MS, NotaryImpl.getInstance()).verify(
					resp, storage);
			String authenticationId = generateAuthenticationId();
			int len = result.length;
			for (int i = 0; i < len; i++) {
				result[i].authenticationId = authenticationId;
				storage.saveAuthenticated(authenticationId, result[i].username);
				storage.updateLastAuth(result[i]);
			}
		} catch (Exception e) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!..............................."
							+ e.getMessage());
			result = new AuthenticatorRecord[1];
			result[0] = new AuthenticatorRecord();
			result[0].status = e.getMessage();
		}
		return result;
	}

	public String generateAuthenticationId() {
		SecureRandom random = new SecureRandom();
      	byte bytes[] = new byte[12];
      	random.nextBytes(bytes);

      	return "fido_auth_id_" + Base64.encodeBase64String(bytes);
	}

	public RegistrationRecord[] processRegResponse(RegistrationResponse resp) {
		RegistrationRecord[] result = null;
		try {
			result = new RegistrationResponseProcessing(
					SERVER_DATA_EXPIRY_IN_MS, NotaryImpl.getInstance())
					.processResponse(resp);
		} catch (Exception e) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!..............................."
							+ e.getMessage());
			result = new RegistrationRecord[1];
			result[0] = new RegistrationRecord();
			result[0].status = e.getMessage();
		}
		return result;
	}
}