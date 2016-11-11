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

import eu.recred.fidouaf.storage.*;

import java.util.HashMap;
import java.util.Map;

public class StorageImpl implements StorageInterface {

	private static StorageImpl instance = new StorageImpl();
	private Map<String, RegistrationRecord> db = new HashMap<String, RegistrationRecord>();
	private Map<String, AuthenticatorRecord> dbLastAuth = new HashMap<String, AuthenticatorRecord>();
	private Map<String, String> authIdDb = new HashMap<String, String>();

	private StorageImpl() {
		// Init
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static StorageImpl getInstance() {
		return instance;
	}

	public void storeServerDataString(String username, String serverDataString) {
		// TODO Auto-generated method stub
	}

	public String getUsername(String serverDataString) {
		// TODO Auto-generated method stub
		return null;
	}

	public void store(RegistrationRecord[] records)
			throws DuplicateKeyException, SystemErrorException {
		if (records != null && records.length > 0) {
			for (int i = 0; i < records.length; i++) {
				if (db.containsKey(records[i].authenticator.toString())) {
					throw new DuplicateKeyException();
				}
				db.put(records[i].authenticator.toString(), records[i]);
			}

		}
	}

	public void updateLastAuth(AuthenticatorRecord authRecord) {
		dbLastAuth.put(authRecord.username, authRecord);
	}

	public AuthenticatorRecord getLastAuth(String username) {
		return (AuthenticatorRecord) dbLastAuth.get(username);
	}

	public RegistrationRecord readRegistrationRecord(String key) {
		return db.get(key);
	}

	public void update(RegistrationRecord[] records) {
		// TODO Auto-generated method stub

	}

	public void saveAuthenticated(String key, String username) {
		if (key == null || key.isEmpty() ||
			username == null || username.isEmpty())
			return;
		authIdDb.put(key, username);
	}

	public String getAuthenticated(String key) {
		if (key == null || key.isEmpty())
			return null;
		return authIdDb.get(key);
	}

	public void deleteRegistrationRecord(String key) {
		if (db != null && db.containsKey(key)) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!....................deleting object associated with key="
							+ key);
			db.remove(key);
		}
	}

	public Map<String, RegistrationRecord> dbDump() {
		return db;
	}

}
