package eu.recred.fidouafjava.client.mvp.presenters;

import org.json.JSONObject;
import eu.recred.fidouafjava.client.util.KeyGuardMock;
import eu.recred.fidouafjava.fido.uaf.client.op.Auth;
import eu.recred.fidouafjava.fido.uaf.client.op.Reg;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sorin.teican on 8/22/2016.
 */
public class UAFPresenter {

    private Logger mLogger = Logger.getLogger(this.getClass().getName());
    private Reg mReg;
    private Auth mAuth;

    public UAFPresenter() {
        mReg = new Reg();
        mAuth = new Auth();
    }

    public boolean authenticate(String title) {
        return KeyGuardMock.getInstance().authenticate(title);
    }

    public String getResponse(String message) {
        String msg = "";
        try {
            if (message != null && message.length()>0) {
                msg = processOp (message);
            }
        } catch (Exception e){
            mLogger.log(Level.WARNING, "Not able to get registration response", e);
        }

        return msg;
    }

    private String processOp (String inUafOperationMsg){
        String msg = "";
        String inMsg = extract(inUafOperationMsg);
        if (inMsg.contains("\"Reg\"")) {
            msg = mReg.register(inMsg);
        } else if (inMsg.contains("\"Auth\"")) {
            msg = mAuth.auth(inMsg);
        } else if (inMsg.contains("\"Dereg\"")) {

        }
        return msg;
    }

    private String extract(String inMsg) {
        try {
            JSONObject tmpJson = new JSONObject(inMsg);
            String uafMsg = tmpJson.getString("uafProtocolMessage");
            uafMsg.replace("\\\"", "\"");
            return uafMsg;
        } catch (Exception e){
            mLogger.log(Level.WARNING, "Input message is invalid!", e);
            return "";
        }

    }
}
