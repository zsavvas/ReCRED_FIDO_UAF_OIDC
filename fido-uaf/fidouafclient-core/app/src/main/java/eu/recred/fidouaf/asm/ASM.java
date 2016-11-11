package eu.recred.fidouaf.asm;

import eu.recred.fidouaf.authenticator.Authenticator;
import eu.recred.fidouaf.msg.asm.ASMRequest;
import eu.recred.fidouaf.msg.asm.ASMResponse;
import eu.recred.fidouaf.msg.asm.obj.AuthenticateIn;
import eu.recred.fidouaf.msg.asm.obj.AuthenticateOut;
import eu.recred.fidouaf.msg.asm.obj.DeregisterIn;
import eu.recred.fidouaf.msg.asm.obj.GetInfoOut;
import eu.recred.fidouaf.msg.asm.obj.RegisterIn;
import eu.recred.fidouaf.msg.asm.obj.RegisterOut;

/**
 * Created by sorin.teican on 10/4/2016.
 */
public class ASM {

    public static final int UAF_ASM_STATUS_OK = 0;
    public static final int UAF_STATUS_ERROR = 1;
    public static final int UAF_ASM_STATUS_ACCESS_DENIED = 2;
    public static final int UAF_ASM_STATUS_USER_CANCELED = 3;

    private Authenticator authenticator;

    public ASM() {
        authenticator = new Authenticator();
    }

//    public ASMResponse<? extends Object> processRequest(ASMRequest<?> request) {
//        try {
//            switch (request.requestType) {
//                case Register:
//                    return register((ASMRequest<RegisterIn>) request);
//                case Authenticate:
//                    return authenticate((ASMRequest<AuthenticateIn>) request);
//                case Deregister:
//                    return deregister((ASMRequest<DeregisterIn>) request);
//            }
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//        } finally {
//            ASMResponse<?> response = new ASMResponse<>();
//            response.statusCode = UAF_STATUS_ERROR;
//            return response;
//        }
//    }

    public ASMResponse<GetInfoOut> discover(ASMRequest<Void> request) {
        ASMResponse<GetInfoOut> response = new ASMResponse<>();

        try {
            response.responseData = authenticator.discover();
            response.statusCode = UAF_ASM_STATUS_OK;
        } catch (Exception e) {
            response.responseData = null;
            response.statusCode = UAF_STATUS_ERROR;
        }

        return response;
    }

    public ASMResponse<RegisterOut> register(ASMRequest<RegisterIn> request) {
        ASMResponse<RegisterOut> response = new ASMResponse<>();

        try {
            response.responseData = authenticator.register(request.args);
            response.statusCode = UAF_ASM_STATUS_OK;
        } catch (Exception e) {
            response.responseData = null;
            response.statusCode = UAF_STATUS_ERROR;
        }

        return response;
    }

    public ASMResponse<AuthenticateOut> authenticate(ASMRequest<AuthenticateIn> request) {
        ASMResponse<AuthenticateOut> response = new ASMResponse<>();

        try {
            response.responseData = authenticator.authenticate(request.args);
            response.statusCode = UAF_ASM_STATUS_OK;
        } catch (Exception e) {
            response.responseData = null;
            response.statusCode = UAF_STATUS_ERROR;
        }

        return response;
    }

    private ASMResponse<?> deregister(ASMRequest<DeregisterIn> request) {
        ASMResponse<?> response = new ASMResponse<>();

        response.statusCode = UAF_ASM_STATUS_OK;

        return response;
    }
}
