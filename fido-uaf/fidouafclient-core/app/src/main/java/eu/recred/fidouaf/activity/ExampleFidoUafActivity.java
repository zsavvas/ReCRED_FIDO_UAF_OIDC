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

package eu.recred.fidouaf.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import eu.recred.fidouaf.R;
import eu.recred.fidouaf.clientop.op.Auth;
import eu.recred.fidouaf.clientop.op.Reg;
import eu.recred.fidouaf.msg.client.UAFIntentType;

import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExampleFidoUafActivity extends Activity {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Gson gson = new Gson();
	private TextView operation;
	private TextView uafMsg;
	private Reg regOp = new Reg();
	private Auth authOp = new Auth();
	private KeyguardManager keyguardManager;
	private int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;
	private Intent _intent;

	TextView tv_operation, tv_relyinParty, tv_transactionConent;
	Button btn_decline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		_intent = getIntent();
		Bundle extras = this.getIntent().getExtras();
		setContentView(R.layout.activity_fido_uaf);
		initViews();
	}

	private void initViews() {
		// Bind view ids.
		tv_operation = (TextView) findViewById(R.id.fc_tv_operation);
		tv_relyinParty = (TextView) findViewById(R.id.fc_tv_relyingParty);
		tv_transactionConent = (TextView) findViewById(R.id.fc_tv_transactionContent);

		// Detect operation.
		String uafMsg = extract(getIntent().getExtras().getString("message"));
		if (uafMsg.contains("\"Reg\""))
			tv_operation.setText("Register");
		else if (uafMsg.contains("\"Auth\""))
			tv_operation.setText("Authenticate");
		else if (uafMsg.contains("\"Dereg\""))
			tv_operation.setText("Deregister");
		else if (uafMsg.contains("\"transcation\""))
			tv_operation.setText("Transaction");

		String appID = "www.relyingparty.com";
		Pattern appIdPattern = Pattern.compile("(\"appID\":\")(http://|https://)(.*?)(:.*?)(\")");
		Matcher m = appIdPattern.matcher(uafMsg);
		if (m.find())
			appID = m.group(3);

		tv_relyinParty.setText(appID);
	}
	
	private void finishWithResult(){
	    Bundle data = new Bundle();
		String inMsg = this.getIntent().getExtras().getString("message");
		String msg = "";
		try {
			if (inMsg != null && inMsg.length()>0) {
				msg = processOp (inMsg);
			}
		} catch (Exception e){
			logger.log(Level.WARNING, "Not able to get registration response", e);
		}
		data.putString("message", msg);
	    Intent intent = new Intent();
	    intent.putExtras(data);
	    setResult(RESULT_OK, intent);
	    finish();
	}

	private void finishWithError(){
		Bundle data = new Bundle();

		data.putString("message", "Unable to complete local authentication, please setup android device authentication(pin, pattern, fingerprint..)");
		Intent intent = new Intent();
		intent.putExtras(data);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	private String processOp (String inUafOperationMsg){
		String msg = "";
		String inMsg = extract(inUafOperationMsg);
		if (inMsg.contains("\"Reg\"")) {
			msg = regOp.register(inMsg);
		} else if (inMsg.contains("\"Auth\"")) {
			msg = authOp.auth(inMsg);
		} else if (inMsg.contains("\"Dereg\"")) {

		}
		return msg;
	}



	public void proceed(View view) {
		Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("UAF","Confirm Identity");
		if (intent != null) {
			startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
		} else {
			finishWithError();
		}

	}

	public void decline(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
			// Challenge completed, proceed with using cipher
			if (resultCode == RESULT_OK) {
				Bundle extras = _intent.getExtras();
				String intentType = extras.getString("UAFIntentType", "NONE");
				if (intentType.equals(UAFIntentType.DISCOVER.name())) {

					return;
				}
				finishWithResult();
			} else {
				// The user canceled or didn’t complete the lock screen
				// operation. Go to error/cancellation flow.
			}
		}
	}
	
	public void back(View view) {
		Bundle data = new Bundle();
		String msg = "";
		logger.info("Registration canceled by user");
		data.putString("message", msg);
		Intent intent = new Intent();
		intent.putExtras(data);
		setResult(RESULT_OK, intent);
		finish();
	}

	private String extract(String inMsg) {
		try {
			JSONObject tmpJson = new JSONObject(inMsg);
			String uafMsg = tmpJson.getString("uafProtocolMessage");
			uafMsg.replace("\\\"", "\"");
			return uafMsg;
		} catch (Exception e){
			logger.log(Level.WARNING, "Input message is invalid!", e);
			return "";
		}

	}
}
