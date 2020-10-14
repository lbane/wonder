package er.extensions.appserver.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.PropertySet;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver.WOSession;

import er.extensions.appserver.ERXRequest;
import er.extensions.appserver.ERXResponse;
import er.extensions.appserver.ERXWOContext;


/**
 * @author mstoll
 *
 * This class maps ERXRequest/ERXResponse to a WSHTTPConnection
 * This enables to jump into the request handling mechanism of JaxWS
 * for handling WebServices from a Servlet.
 *  
 */
public class ERWSWOHTTPConnection
    extends WSHTTPConnection
{
    public static final String ERJAXWS_WOCONTEXT = "com.webobjects.appserver.WOContext";
    public static final String ERJAXWS_ERXWOCONTEXT = "er.extensions.appserver.ERXWOContext";
    private static final Logger log = LoggerFactory.getLogger(ERWSWOHTTPConnection.class);
	
    /** the current ERXRequest */
    ERXRequest woRequest;
    
    /** the output stream JaxWS writes into */
    ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();

    /** the HTTP result status, being set from JaxWS
     *  to be handed into ERXResponse
     */
    int responseStatus = 0;

    /** the current ERXResponse */
    ERXResponse woResponse;
    
    /** the associated session **/
    WOSession session = null;
    
    private static final PropertyMap model = parse(ERWSWOHTTPConnection.class);

    /**
     * The constructor 
     * @param req the current ERXRequest for this RR cycle
     */
    public ERWSWOHTTPConnection(WORequest req)
    {
        woRequest = (ERXRequest)req;
        woResponse = new ERXResponse();
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getInput()
     */
    @Override
    public InputStream getInput() throws IOException
    {
        return woRequest.content().stream();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getOutput()
     */
    @Override
    public OutputStream getOutput() throws IOException
    {
        return responseOutputStream;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getPathInfo()
     */
    @Override
    public String getPathInfo()
    {
        return "";
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getQueryString()
     */
    @Override
    public String getQueryString()
    {
        return woRequest.queryString();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getRequestHeader(java.lang.String)
     */
    @Override
    public String getRequestHeader(String s)
    {
        return woRequest.headerForKey(s);
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getRequestHeaders()
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
	@Property({ "javax.xml.ws.http.request.headers",
	"com.sun.xml.ws.api.message.packet.inbound.transport.headers" })
    @NotNull
    @Override
    public Map<String, List<String>> getRequestHeaders()
    {
        return (Map)woRequest.headers();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getRequestMethod()
     */
    @Override
    public String getRequestMethod()
    {
        return woRequest.method();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getResponseHeaders()
     */
	@Property({ "javax.xml.ws.http.response.headers",
	"com.sun.xml.ws.api.message.packet.outbound.transport.headers" })
    @Override
    public Map<String, List<String>> getResponseHeaders()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getStatus()
     */
    @Override
    public int getStatus()
    {
        return responseStatus;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#getWebServiceContextDelegate()
     */
    @Override
    public WebServiceContextDelegate getWebServiceContextDelegate()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#isSecure()
     */
    @Override
    public boolean isSecure()
    {
        return woRequest.isSecure();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#setContentTypeResponseHeader(java.lang.String)
     */
    @Override
    public void setContentTypeResponseHeader(String s)
    {
        woResponse.setHeader(s, "Content-Type");
    }

//    @Override
//    public void setResponseHeader(String s, List<String> list)
//    {
//        // TODO Auto-generated method stub
//
//    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#setResponseHeaders(java.util.Map)
     */
    @Override
    public void setResponseHeaders(Map<String, List<String>> map)
    {
        woResponse.setHeaders(map);
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.transport.http.WSHTTPConnection#setStatus(int)
     */
    @Override
    public void setStatus(int i)
    {
        responseStatus = i;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.ws.api.PropertySet#getPropertyMap()
     */
    @Override
    protected PropertyMap getPropertyMap() 
    {
        return model;
    }

    /**
     * Generate the response after the ERXRequest has been treaten by JaxWS
     * 
     * @return the generated ERXResponse
     */
    public WOResponse generateResponse()
    {
        try
        {
            responseOutputStream.flush();
            woResponse.setStatus(responseStatus);
            woResponse.setContent(responseOutputStream.toByteArray());
            
            if(context != null && context._session() != null)
            {
            	context._session().setStoresIDsInCookies(true);
            	context._session()._appendCookieToResponse(woResponse);
            	WOApplication.application().saveSessionForContext(context);

            	woResponse._finalizeInContext(context);
            }
        }
        catch(IOException e)
        {
            log.error("Exception on writing response.", e);
            return null;
        }
        
        return woResponse;
    }

	private WOContext context = null;

	@Property({ "com.webobjects.appserver.WOContext" })
	public synchronized WOContext WOContext()
	{
		if(context == null)
		{
			synchronized (this) 
			{
				if(context == null)
				{
				    context = WOApplication.application().createContextForRequest(woRequest);
					
					String sessionID = getSessionIDFromCookie();
					if(sessionID != null)
						context._setRequestSessionID(sessionID);
				}
			}
		}
		
		return context;
	}

    @Property({ "er.extensions.appserver.ERXWOContext" })
    public synchronized ERXWOContext ERXWOContext()
    {
        WOContext c = WOContext();
        
        if(c instanceof ERXWOContext)
        {
            return (ERXWOContext)c;
        }
        
        throw new IllegalArgumentException("WOContext is no sublass of ERXWOContext");
    }
    
    private String getSessionIDFromCookie()
    {
        return woRequest.cookieValueForKey(WOApplication.application().sessionIdKey());
    }

    @Deprecated
    @Override
    public Set<String> getRequestHeaderNames()
    {
        return new HashSet<String>(woRequest.headerKeys());
    }

    @Override
    public List<String> getRequestHeaderValues(String arg0)
    {
        return woRequest.headersForKey(arg0);
    }

    @Override
    public String getRequestScheme()
    {
        return isSecure() ? "https" : "http";
    }

    @Override
    public String getRequestURI()
    {
        return woRequest.uri();
    }

    @Override
    public String getServerName()
    {
        return woRequest._serverName();
    }

    @Override
    public int getServerPort()
    {
        return Integer.valueOf(woRequest._serverPort()).intValue();
    }

    @Override
    public void setResponseHeader(String arg0, List<String> arg1)
    {
        woResponse.setHeaders(arg1, arg0);
    }
	
}
