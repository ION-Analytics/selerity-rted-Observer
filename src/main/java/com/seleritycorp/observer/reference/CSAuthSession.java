/*
 * (c) Copyright Selerity, Inc. 2009-2019. All rights reserved. This source code is confidential 
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated 
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination 
 * of, modifications to or creation of derivative works from this source code, whether in source 
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with 
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be 
 * removed from the software by any user thereof.
 */
package com.seleritycorp.observer.reference;

import com.seleritycorp.narwhal.client.*;
import com.seleritycorp.narwhal.client.methods.CS;

import java.net.MalformedURLException;
import java.util.logging.Logger;

/**
 * Gets an authentication token for a second service via first authenticating with core services.
 */
public class CSAuthSession extends AuthenticatedSession {
    private static final Logger log = Logger.getLogger(CSAuthSession.class.getName());

    final Session csSession;

    public CSAuthSession(String serverURL, String username, String client, String csUrl) throws MalformedURLException {
        super(serverURL, username, client);
        csSession = new Session(csUrl, username, client);
    }

    @Override
    protected String authenticate(String password) throws RpcException {

        final Request request = new Request(csSession, CS.AUTHENTICATE, getUsername(), password);
        final Response response = csSession.dispatch(request);
        if (response.hasError()) {
            throw new RemoteException(response.getError());
        }
        return response.getResult().toString();
    }

    @Override
    protected void invalidate() {
        csSession.setToken(getToken());
        final Request request = new Request(csSession, CS.INVALIDATE);
        try {
            final Response response = csSession.dispatch(request);
            if (response.hasError()) {
                throw new RemoteException(response.getError());
            }
        } catch (Exception e) {
            log.info("Failed to invalidate session.");
        }
    }

}
