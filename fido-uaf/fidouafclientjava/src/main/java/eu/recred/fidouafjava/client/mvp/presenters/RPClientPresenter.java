package eu.recred.fidouafjava.client.mvp.presenters;

import org.json.JSONArray;
import org.json.JSONException;
import eu.recred.fidouafjava.client.op.Auth;
import eu.recred.fidouafjava.client.op.Dereg;
import eu.recred.fidouafjava.client.op.Reg;
import eu.recred.fidouafjava.client.util.Preferences;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by sorin.teican on 8/22/2016.
 */
public class RPClientPresenter {

    private Reg mReg;
    private Auth mAuth;
    private Dereg mDereg;

    private UAFPresenter mUafPresener;

    public RPClientPresenter() {
        mReg = new Reg();
        mAuth = new Auth();
        mDereg = new Dereg();

        mUafPresener = new UAFPresenter();
    }

    public boolean register(String username) {
        Preferences.getInstance().setSettingsParam("username", username);
        boolean success = false;

        String regRequest = getUafRegRequest(username);
        String regResponse = mUafPresener.getResponse(regRequest);
        String serverResponse = sendUafRegResponse(regResponse);

        String[] splitted = serverResponse.split("\n\n#ServerResponse\n");
        if (splitted.length > 0) {
            try {
                JSONArray response = new JSONArray(splitted[1]);
                String status = response.getJSONObject(0).getString("status");
                if (status != null && status.equals("SUCCESS"))
                    success = true;
            } catch (JSONException e) {
                System.err.println("Invalid Server response");
                e.printStackTrace();
                success = false;
            }
        }

        if (success)
            writeRegistrationFile(username);

        log("Registration", username, regRequest, regResponse, serverResponse);

        return success;
    }

    public boolean authenticate(String username) {
        Preferences.getInstance().setSettingsParam("username", username);
        boolean success = false;

        if (!loadRegistrationFile(username))
            return false;

        String authRequest = getUafAuthRequest();
        String authResponse = mUafPresener.getResponse(authRequest);
        String serverResponse = sendUafAuthResponse(authResponse);

        String[] splitted = serverResponse.split("\n\n#ServerResponse\n");
        if (splitted.length > 0) {
            try {
                JSONArray response = new JSONArray(splitted[1]);
                String status = response.getJSONObject(0).getString("status");
                if (status != null && status.equals("SUCCESS"))
                    success = true;
            } catch (JSONException e) {
                System.err.println("Invalid Server response");
                e.printStackTrace();
                success = false;
            }
        }

        log("Authentication", username, authRequest, authResponse, serverResponse);

        return success;
    }

    public boolean transaction(String username) {
        Preferences.getInstance().setSettingsParam("username", username);
        boolean success = false;

        if (!loadRegistrationFile(username))
            return false;

        String authRequest = getUafTransactionRequest();
        String authResponse = mUafPresener.getResponse(authRequest);
        String serverResponse = sendUafAuthResponse(authResponse);

        String[] splitted = serverResponse.split("\n\n#ServerResponse\n");
        if (splitted.length > 0) {
            try {
                JSONArray response = new JSONArray(splitted[1]);
                String status = response.getJSONObject(0).getString("status");
                if (status != null && status.equals("SUCCESS"))
                    success = true;
            } catch (JSONException e) {
                System.err.println("Invalid Server response");
                e.printStackTrace();
                success = false;
            }
        }

        log("Transaction", username, authRequest, authResponse, serverResponse);

        return success;
    }

    public boolean dereg(String username) {
        Preferences.getInstance().setSettingsParam("username", username);
        //boolean success = false;

        if (!loadRegistrationFile(username))
            return false;

        String deregRequest = getUafDeregRequest();
        String deregResponse = mUafPresener.getResponse(deregRequest);
        String serverResponse = sendUafDeregResponse(deregRequest);

        log("Dereg", username, deregRequest, deregResponse, serverResponse);

        return true;
    }

    public boolean loadRegistrationFile(String username) {
        boolean success = false;
        try {
            FileInputStream regFile = new FileInputStream("users/" + username + ".usr");
            ObjectInputStream in = new ObjectInputStream(regFile);
            Map<String, String> regInfo = (HashMap<String, String>) in.readObject();
            in.close();
            regFile.close();
            Preferences.getInstance().setSettingsParam("keyId", regInfo.get("keyId"));
            Preferences.getInstance().setSettingsParam("keyID", regInfo.get("keyID"));
            Preferences.getInstance().setSettingsParam("pub", regInfo.get("pub"));
            Preferences.getInstance().setSettingsParam("priv", regInfo.get("priv"));
            Preferences.getInstance().setSettingsParam("AAID", regInfo.get("AAID"));
            success = true;
        } catch (Exception e) {
            // This should be thrown at first use of class.
            e.printStackTrace();
        }

        return success;
    }

    private void log(String type, String username, String request, String response, String serverResponse) {
        Logger logger = Logger.getLogger(type + "_Logger");
        logger.setUseParentHandlers(false);
        FileHandler fileHandler;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");

        try {
            String date = simpleDateFormat.format(new Date());
            File logFile = new File("logs/" + type + "_" + username + "_" + date + ".log");
            if (!logFile.exists())
                logFile.createNewFile();
            fileHandler = new FileHandler("logs/" + type + "_" + username + "_" + date + ".log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            logger.info(type + " Request");
            logger.info(request);
            logger.info(type + " Response");
            logger.info(response);
            logger.info(type + " Server Response");
            logger.info(serverResponse);
        } catch (Exception e) {
            System.err.println("error logging output");
        }
    }

    private boolean writeRegistrationFile(String username) {
        boolean written = true;

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("keyId", Preferences.getInstance().getSettingsParam("keyId"));
        userData.put("keyID", Preferences.getInstance().getSettingsParam("keyID"));
        userData.put("pub", Preferences.getInstance().getSettingsParam("pub"));
        userData.put("priv", Preferences.getInstance().getSettingsParam("priv"));
        userData.put("AAID", Preferences.getInstance().getSettingsParam("AAID"));

        try {
            FileOutputStream userFile = new FileOutputStream("users/" + username + ".usr");
            ObjectOutputStream out = new ObjectOutputStream(userFile);
            out.writeObject(userData);
            out.close();
            userFile.close();
            written = true;
        } catch (IOException e) {
            written = false;
        }

        return written;
    }

    private String getUafRegRequest(String username) {
        return mReg.getUafMsgRegRequest(username, getFacetId());
    }

    private String sendUafRegResponse(String regResponse) {
        return mReg.clientSendRegResponse(regResponse);
    }

    private String getUafAuthRequest() {
        return mAuth.getUafMsgRequest(getFacetId(), false);
    }

    private String sendUafAuthResponse(String authResponse) {
        return mAuth.clientSendResponse(authResponse);
    }

    private String getUafTransactionRequest() {
        return mAuth.getUafMsgRequest(getFacetId(), true);
    }

    private String getUafDeregRequest() {
        return mDereg.getUafMsgRequest();
    }

    public String sendUafDeregResponse(String deregResponse) {
        return mDereg.clientSendDeregResponse(deregResponse);
    }

    private String getFacetId() {
        return "android:apk-key-hash:RVimey8gA1qIjM9FAc5yCjAbZcQ";
    }
}
