/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2015 ForgeRock AS. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file at legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 */

package com.verizon.iam.openam;

import java.security.Principal;
import java.util.Map;
import java.util.ResourceBundle;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.LoginException;


import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.shared.debug.Debug;

/**
 * SampleAuth authentication module example.
 *
 * If you create your own module based on this example, you must modify all
 * occurrences of "SampleAuth" in addition to changing the name of the class.
 *
 * Please refer to OpenAM documentation for further information.
 *
 * Feel free to look at the code for authentication modules delivered with
 * OpenAM, as they implement this same API.
 */
public class RecredFidoAuth extends AMLoginModule {

    // Name for the debug-log
    private final static String DEBUG_NAME = "RecredFidoAuth";
    private final static Debug debug = Debug.getInstance(DEBUG_NAME);

    // Name of the resource bundle
    private final static String amAuthRecredFidoAuth = "amAuthRecredFidoAuth";

    // Orders defined in the callbacks file
    private final static int STATE_BEGIN = 1;
    private final static int STATE_AUTH_BEGIN = 2;
    private final static int STATE_AUTH_RETURN = 3;
    private final static int STATE_ERROR = 4;
    private String USERNAME;
    
    private Map<String, String> options;
    private ResourceBundle bundle;
    private Map<String, String> sharedState;

    public RecredFidoAuth() {
        super();
    }


    /**
     * This method stores service attributes and localized properties for later
     * use.
     * @param subject
     * @param sharedState
     * @param options
     */
    @Override
    public void init(Subject subject, Map sharedState, Map options) {

        debug.message("RecredFidoAuth::init");

        this.options = options;
        this.sharedState = sharedState;
        this.bundle = amCache.getResBundle(amAuthRecredFidoAuth, getLoginLocale());
    }

    @Override
    public int process(Callback[] callbacks, int state) throws LoginException {

        debug.message("RecredFidoAuth::process state: {}", state);

        switch (state) {

            case STATE_BEGIN:
                // No time wasted here - simply modify the UI and
                // proceed to next state
            	
            	// Update the FIDO server URL to be returned to the client
				replaceCallback(STATE_AUTH_RETURN, 0, 
						new NameCallback(CollectionHelper.getMapAttr(options, "fido-endpoint"))
				);
            	            	
                return STATE_AUTH_BEGIN;

            case STATE_AUTH_BEGIN:
                // Get data from callbacks. Refer to callbacks XML file.
                NameCallback nc = (NameCallback) callbacks[0];
                USERNAME = nc.getName();

                if (!USERNAME.isEmpty()) {
                	// Username is provided continue flow
                	// Next step is to check if user is registered
                    debug.message("RecredFidoAuth::process User '{}' " +
                            "provided username with success.", USERNAME);
                    
                    return STATE_AUTH_RETURN;
                } else {
                	// Username not provided
                	return STATE_AUTH_BEGIN;
                }
               
            case STATE_AUTH_RETURN:
            	UserLoader ul = new UserLoader();
            	String fidoendpointlastauth = CollectionHelper.getMapAttr(options, "fido-endpoint-last-auth");
            	long allowedtimediff = (long)CollectionHelper.getIntMapAttr(options, "allowed-time-diff", 300, debug);
            	long millinow = System.currentTimeMillis();
            	
            	String fidoendpointisauthed = CollectionHelper.getMapAttr(options, "fido-endpoint-is-authed");
            	FidoAuthenticationLoader fal = new FidoAuthenticationLoader();
            	NameCallback AuthRtnNameCb = (NameCallback) callbacks[0];
            	String AuthID = AuthRtnNameCb.getName();
            	
            	
    			try {
    				FidoAuthenticationResponse far = fal.getAuthDtls(fidoendpointisauthed, AuthID);
    				if (far.isAuthenticated() && far.getUsername().equalsIgnoreCase(USERNAME))
    				{
    					return ISAuthConstants.LOGIN_SUCCEED;
    				} else {
    					return STATE_AUTH_RETURN;
    				}
    			
    			} catch (Exception e) {
    				e.printStackTrace();
    				return STATE_ERROR;
    			}            	

            	/*
			try {
				UserResponse ur = ul.getUser(fidoendpointlastauth, USERNAME);
				if (ur.getTimestamp() == null){
					debug.message("RecredFidoAuth::process User '{}' " + "timestamp is null.", USERNAME);
					return STATE_AUTH_RETURN;
				} else {
					long diff = (millinow-ur.getTimestamp())/1000;
					if (USERNAME.equals(ur.getUserId()) && diff < allowedtimediff) {
						debug.message("RecredFidoAuth::process User '{}' " +
	                            "authenticated with success.", USERNAME);
						return ISAuthConstants.LOGIN_SUCCEED;
					} else {
						debug.message("RecredFidoAuth::process User '{}' " +
								"time difference is bigger then 300s.", USERNAME);
						return STATE_ERROR;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return STATE_ERROR;
			}            	
*/
            case STATE_ERROR:
                return STATE_ERROR;
            default:
                throw new AuthLoginException("invalid state");
        }
    }
    
    @Override
    public Principal getPrincipal() {
        return new RecredFidoAuthPrincipal(USERNAME);
    }
    
}
