package eu.recred.fidouafjava.client.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by sorin on 20.08.2016.
 */
public class Preferences implements Serializable {

    private static Preferences mInstance;
    private Settings mSettings;

    private Preferences() {
        mSettings = new Settings();
//        //loadPrefs();
//
//        // Load endpoints.
//        if (!mSettings.mValues.containsKey("serverEndpoint")) {
//            setEndpointsDefaults();
//        }
        try {
            loadEndpointsConfig();
            System.out.println("Endpoints loaded!");
        } catch (Exception e) {
            System.err.println("Endpoints config file missing or corrupted!!!");
            System.err.println("Aborting mission!");
            System.exit(0);
        }
    }

    public static Preferences getInstance(){
        if (mInstance == null) {
            mInstance = new Preferences();
        }

        return mInstance;
    }

    public String getServer() {
        return getSettingsParam("server");
    }

    public void setServer(String server) {
        if (!empty(server))
            setSettingsParam("server", server);
    }


    public String getAuthResponseEndpoint() {
        return getSettingsParam("authRes");
    }

    public void setAuthResponseEndpoint(String authRes) {
        if (!empty(authRes))
            setSettingsParam("authRes", authRes);
    }


    public String getAuthRequestEndpoint() {
        return getSettingsParam("authReq");
    }

    public void setAuthRequestEndpoint(String authReg) {
        if (!empty(authReg))
            setSettingsParam("authReq", authReg);
    }


    public String getDeregEndpoint() {
        return getSettingsParam("deregReq");
    }

    public void setDeregEndpoint(String dereg) {
        if (!empty(dereg))
            setSettingsParam("deregReq", dereg);
    }


    public String getRegResponseEndpoint() {
        return getSettingsParam("regRes");
    }

    public void setRegResponseEndpoint(String regRes) {
        if (!empty(regRes))
            setSettingsParam("regRes", regRes);
    }


    public String getRegRequestEndpoint() {
        return getSettingsParam("regReq");
    }

    public void setRegRequestEndpoint(String regReg) {
        if (!empty(regReg))
            setSettingsParam("regReq", regReg);
    }

    public void setEndpointsDefaults() {
        setSettingsParam("server", Endpoints.SERVER);
        setSettingsParam("authReq", Endpoints.GET_AUTH_REQUEST);
        setSettingsParam("authRes", Endpoints.POST_AUTH_RESPONSE);
        setSettingsParam("regReq", Endpoints.GET_REG_REQUEST);
        setSettingsParam("regRes", Endpoints.POST_REG_RESPONSE);
        setSettingsParam("deregReq", Endpoints.POST_DEREG_RESPONSE);
    }

    public void setSettingsParam(String paramName, String paramValue) {
        if (!empty(paramName)) {
            mSettings.mValues.put(paramName, paramValue);
            savePrefs();
        }
    }

    public String getSettingsParam(String paramName) {
        if (!empty(paramName)) {
            String param = mSettings.mValues.get(paramName);
            if (param == null) return "";
            else return param;
        }

        return "";
    }

    private void savePrefs() {
        if (mSettings.mValues.isEmpty())
            return;

        try {
            FileOutputStream prefsFile = new FileOutputStream("prefs.ser");
            ObjectOutputStream out = new ObjectOutputStream(prefsFile);
            out.writeObject(mSettings);
            out.close();
            prefsFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean empty(String param) {
        return param == null || param.trim().isEmpty();
    }

    private void loadPrefs() {
        try {
            FileInputStream prefsFile = new FileInputStream("prefs.ser");
            ObjectInputStream in = new ObjectInputStream(prefsFile);
            mSettings= (Settings) in.readObject();
            in.close();
            prefsFile.close();
        } catch (Exception e) {
            // This should be thrown at first use of class.
            e.printStackTrace();
        }
    }

    public void loadEndpointsConfig() throws Exception {
        Scanner scanner = new Scanner(new File(Endpoints.ENDPOINTS_CONFIG_FILE));
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            // check for comment.
            if (line.trim().isEmpty() || line.toCharArray()[0] == '#')
                continue;
            String[] prop = line.split(":=");
            mSettings.mValues.put(prop[0], prop[1]);
        }
    }

    private class Settings implements Serializable {
        public Map<String, String> mValues;

        public Settings() {
            mValues = new HashMap<>();
        }
    }

    private class Endpoints {
        public static final String SERVER =
                "http://openidconnect.ebay.com";

        public static final String ENDPOINTS_CONFIG_FILE = "config\\endpoints.conf";
        public static final String GET_AUTH_REQUEST = "/fidouaf/v1/public/authRequest";
        public static final String POST_AUTH_RESPONSE = "/fidouaf/v1/public/authResponse";
        public static final String POST_DEREG_RESPONSE = "/fidouaf/v1/public/deregRequest";
        public static final String GET_REG_REQUEST = "/fidouaf/v1/public/regRequest/";
        public static final String POST_REG_RESPONSE = "/fidouaf/v1/public/regResponse";
    }

    public boolean loadRegistrationFile(String username) {
        boolean success = false;
        try {
            FileInputStream regFile = new FileInputStream("users/" + username + ".usr");
            ObjectInputStream in = new ObjectInputStream(regFile);
            Map<String, String> regInfo = (HashMap<String, String>) in.readObject();
            in.close();
            regFile.close();
            setSettingsParam("keyId", regInfo.get("keyId"));
            setSettingsParam("keyID", regInfo.get("keyID"));
            setSettingsParam("pub", regInfo.get("pub"));
            setSettingsParam("priv", regInfo.get("priv"));
            setSettingsParam("AAID", regInfo.get("AAID"));
            success = true;
        } catch (Exception e) {
            // This should be thrown at first use of class.
            e.printStackTrace();
        }

        return success;
    }

    public boolean writeRegistrationFile(String username) {
        boolean written = true;

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("keyId", getSettingsParam("keyId"));
        userData.put("keyID", getSettingsParam("keyID"));
        userData.put("pub", getSettingsParam("pub"));
        userData.put("priv", getSettingsParam("priv"));
        userData.put("AAID", getSettingsParam("AAID"));

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
}
