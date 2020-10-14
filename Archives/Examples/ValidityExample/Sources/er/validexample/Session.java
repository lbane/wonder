package er.validexample;
//
// Session.java
// Project ValidityExample
//
// Created by msacket on Mon Jun 11 2001
//

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver.WOSession;

public class Session extends WOSession {

    public Session() {
        super();
        
        /* ** Put your per-session initialization code here ** */
    }

    @Override
	public void takeValuesFromRequest(WORequest request, WOContext context) {
        request.setDefaultFormValueEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
        super.takeValuesFromRequest(request, context);
    }

    @Override
	public void appendToResponse(WOResponse response, WOContext context) {
        response.setContentEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
        super.appendToResponse(response, context);
        response.setHeader("text/html; charset=UTF-8", "Content-Type");
    }
    
}
