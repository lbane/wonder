package er.excel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;

import er.extensions.appserver.ERXResponse;
import er.extensions.components.ERXComponentUtilities;
import er.extensions.components.ERXNonSynchronizingComponent;

/**
 * Class for Excel Component EGWrapper.
 *
 * @binding sample sample binding explanation
 *
 * @author ak on Thu Mar 04 2004
 */
public class EGWrapper extends ERXNonSynchronizingComponent {
	/**
	 * Do I need to update serialVersionUID?
	 * See section 5.6 <cite>Type Changes Affecting Serialization</cite> on page 51 of the 
	 * <a href="http://java.sun.com/j2se/1.4/pdf/serial-spec.pdf">Java Object Serialization Spec</a>
	 */
	private static final long serialVersionUID = 1L;

	/** logging support */
	private static final Logger log = LoggerFactory.getLogger(EGWrapper.class);
    
	private String _fileName;
	private NSDictionary _styles;
	private NSDictionary _fonts;
    
    /**
     * Public constructor
     * @param context the context
     */
    public EGWrapper(WOContext context) {
        super(context);
    }
    
    public boolean isEnabled() {
    	return ERXComponentUtilities.booleanValueForBinding(this, "enabled",false);
    }
    
    public String fileName() {
    	if(_fileName == null) {
    		_fileName = (String)valueForBinding("fileName");
    	}
    	return _fileName;
    }
    public void setFileName(String value) {
    	_fileName = value;
    }
    
    public NSDictionary styles() {
    	if (_styles == null) {
    		_styles = (NSDictionary) valueForBinding("styles");
    	}
    	return _styles;
    }
    public void setStyles(NSDictionary value) {
    	_styles = value;
    }
    
    
    public NSDictionary fonts() {
    	if (_fonts == null) {
			_fonts = (NSDictionary) valueForBinding("fonts");
		}
		return _fonts;
    }
    public void setFonts(NSDictionary value) {
    	_fonts = value;
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        if (isEnabled()) {
            ERXResponse newResponse = new ERXResponse();

            super.appendToResponse(newResponse, context);

            String contentString = newResponse.contentString();
            contentString = contentString.replaceAll("&nbsp;", "");
            log.debug("Converting content string:\n{}", contentString);
            byte[] bytes = contentString.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            InputStream stream = new ByteArrayInputStream(bytes);
            EGSimpleTableParser parser = parser(stream);
            try {
                NSData data = parser.data();
                if((hasBinding("data") && canSetValueForBinding("data")) ||
                        (hasBinding("stream") && canSetValueForBinding("stream"))
                        ) {
                    if(hasBinding("data")) {
                        setValueForBinding(data, "data");
                    }
                    if(hasBinding("stream")) {
                        setValueForBinding(data.stream(), "stream");
                    }
                    response.appendContentString(contentString);
                } else {
                    String fileName = fileName();
                    if(fileName == null) {
                        fileName = defaultFilename();
                    }

                    response.disableClientCaching();
                    response.appendHeader(String.valueOf( data.length()), "Content-Length" );
                    response.setContent(data); // Changed by ishimoto because it was sooooo buggy and didn't work in Japanese

                    response.setHeader("inline; filename=\"" + fileName + "\"", "content-disposition");
                    response.setHeader(contentType(), "content-type");
                }
            } catch (Exception ex) {
                if (ex.getCause() instanceof SAXParseException) {
                    SAXParseException parseException = (SAXParseException)ex.getCause();
                    String logMessage = "'"+context().page().getClass().getName()+"' caused a SAXParseException";
                    logMessage += "\nMessage: '"+parseException.getMessage()+"'";
                    // weird but true, getLineNumber is off by 1 (for display purposes I think - mhast)
                    logMessage += "\nLine   : "+(parseException.getLineNumber() - 1);
                    logMessage += "\nColumn : "+parseException.getColumnNumber();
                    logMessage += "\n--- content begin ---";
                    logMessage += addLineNumbers(contentString);
                    logMessage += "--- content end ---";
                    log.error(logMessage);
                    throw new NSForwardException(ex);
                }

                throw new NSForwardException(ex);
            }
        } else {
            super.appendToResponse(response, context);
        }
    }
    
	protected String addLineNumbers(String in) {
		String out = "";
		int i = 1, beginIndex = 0;
		int endIndex = in.indexOf('\n');
		while (endIndex != -1) {
			out += in.substring(beginIndex, endIndex+1);
			beginIndex = endIndex+1;
			endIndex = in.indexOf('\n', beginIndex);
			// only want to add line numbers if we have a next newline
			if (endIndex != -1) out +=  (i++) + " ";
		}
		return out;
	}
	
	protected String defaultFilename() {
		return "results.xls";
	}
	
	protected String contentType() {
		return "application/vnd.ms-excel";
	}
	
	protected EGSimpleTableParser parser(InputStream stream) {
		return new EGSimpleTableParser(stream, fonts(), styles());
	}
}
