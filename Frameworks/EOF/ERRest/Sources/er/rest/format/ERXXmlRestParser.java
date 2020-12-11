package er.rest.format;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import er.rest.ERXRestContext;
import er.rest.ERXRestNameRegistry;
import er.rest.ERXRestRequestNode;
import er.rest.ERXRestUtils;

/**
 * ERXXmlRestRequestParser is an implementation of the IERXRestRequestParser interface that supports XML document
 * requests.
 * 
 * @author mschrag
 */
public class ERXXmlRestParser implements IERXRestParser {

	private static final Logger logger = LoggerFactory.getLogger(ERXXmlRestParser.class);
	
	protected ERXRestRequestNode createRequestNodeForElement(Element element, boolean rootNode, ERXRestFormat.Delegate delegate, ERXRestContext context) {
		String name = element.getTagName();
		ERXRestRequestNode requestNode = new ERXRestRequestNode(name, rootNode);

		String valueStr = element.getNodeValue();

		NamedNodeMap attributeNodes = element.getAttributes();
		for (int attributeNum = 0; attributeNum < attributeNodes.getLength(); attributeNum++) {
			Node attribute = attributeNodes.item(attributeNum);
			requestNode.setAttributeForKey(attribute.getNodeValue(), attribute.getNodeName());
		}
		NodeList childNodes = element.getChildNodes();
		for (int childNodeNum = 0; childNodeNum < childNodes.getLength(); childNodeNum++) {
			Node childNode = childNodes.item(childNodeNum);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				ERXRestRequestNode childRequestNode = createRequestNodeForElement(childElement, false, delegate, context);
				if (childRequestNode != null) {
					String childRequestNodeName = childRequestNode.name();
					// MS: this is a huge hack, but it turns out that it's surprinsingly tricky to
					// identify an array in XML ... I'm cheating here and just saying that if the
					// node name is uppercase, it represents a new object and not an attribute ...
					// this will totally break on rails-style lowercase class names, but for now
					// this flag is just a heuristic
					if (childRequestNodeName == null || Character.isUpperCase(childRequestNodeName.charAt(0))) {
						childRequestNode.setRootNode(true);
						requestNode.setArray(true);
					}
					requestNode.addChild(childRequestNode);
				}
			}
			else if (childNode instanceof Text) {
				String text = childNode.getNodeValue();
				if (text != null) {
					if (!(childNode instanceof CDATASection)) {
						text = text.trim();
					}
					if (valueStr == null) {
						valueStr = text;
					}
					else {
						valueStr += text;
					}
				}
				
				// if there is a text node AND other sibling nodes, this is fake ...
				if (childNodes.getLength() > 1) {
					valueStr = null;
				}
			}
			else {
				// ???
			}
		}

		requestNode.setValue(valueStr);

		delegate.nodeDidParse(requestNode);

		if (valueStr != null) {
			String type = requestNode.type();
			if (type != null) {
				// MS: inverse the types we declare in ERXXmlRestWriter
				if ("datetime".equals(type)) {
					type = "NSTimestamp";
				}
				if ("date".equals(type)) {
					type = "java.time.LocalDate";
				}
				if ("datetime2".equals(type)) {
					type = "java.time.LocalDateTime";
				}
				if ("time".equals(type)) {
					type = "java.time.LocalTime";
				}
				else if ("integer".equals(type)) {
					type = "int";
				}
				else if ("bigint".equals(type)) {
					type = "BigDecimal";
				}
				else if ("enum".equals(type)) {
					type = "Enum";
				}
				Object coercedValue = ERXRestUtils.coerceValueToTypeNamed(valueStr, type, context, false);
				if (coercedValue != null) {
					requestNode.setValue(coercedValue);
				}
			}
		}
		
		return requestNode;
	}

	@Override
	public ERXRestRequestNode parseRestRequest(IERXRestRequest request, ERXRestFormat.Delegate delegate, ERXRestContext context) {
		ERXRestRequestNode rootRequestNode = null;
		String contentString = request.stringContent();
		if (contentString != null && contentString.length() > 0) {
			// MS: Support direct updating of primitive type keys -- so if you don't want to
			// wrap your request in XML, this will allow it
			if (!contentString.trim().startsWith("<")) {
				contentString = "<FakeWrapper>" + contentString.trim() + "</FakeWrapper>";
			}

			// Important security settings: Disable external Entities for any untrusted XML input
			
			// https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
			
			// we set all recommended features for now
			
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			String FEATURE = null;
			try {
				// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all
				// XML entity attacks are prevented
				// Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
				FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
				dbf.setFeature(FEATURE, true);

				// If you can't completely disable DTDs, then at least do the following:
				// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
				// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
				// JDK7+ - http://xml.org/sax/features/external-general-entities
				//This feature has to be used together with the following one, otherwise it will not protect you from XXE for sure
				FEATURE = "http://xml.org/sax/features/external-general-entities";
				dbf.setFeature(FEATURE, false);

				// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
				// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
				// JDK7+ - http://xml.org/sax/features/external-parameter-entities
				//This feature has to be used together with the previous one, otherwise it will not protect you from XXE for sure
				FEATURE = "http://xml.org/sax/features/external-parameter-entities";
				dbf.setFeature(FEATURE, false);

				// Disable external DTDs as well
				FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
				dbf.setFeature(FEATURE, false);

				// and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
				dbf.setXIncludeAware(false);
				dbf.setExpandEntityReferences(false);

				// And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then
				// ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
				// (http://cwe.mitre.org/data/definitions/918.html) and denial
				// of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."

				// remaining parser logic
				Document document = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(contentString)));
				document.normalize();
				Element rootElement = document.getDocumentElement();
				rootRequestNode = createRequestNodeForElement(rootElement, true, delegate, context);
			} catch (ParserConfigurationException e) {
				// This should catch a failed setFeature feature
				logger.error("ParserConfigurationException was thrown. The feature '{}' is probably not supported by your XML processor.");
				throw new IllegalArgumentException("Failed to parse request document.", e);
			} catch (SAXException e) {
				// On Apache, this should be thrown when disallowing DOCTYPE
				logger.warn("A DOCTYPE was passed into the XML document");
				throw new IllegalArgumentException("Failed to parse request document.", e);
			} catch (IOException e) {
				// XXE that points to a file that doesn't exist
				logger.error("IOException occurred, XXE may still possible", e);
				throw new IllegalArgumentException("Failed to parse request document.", e);
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Failed to parse request document.", e);
			}
		}
		else {
			rootRequestNode = new ERXRestRequestNode(null, true);
			rootRequestNode.setNull(true);
		}

		return rootRequestNode;
	}
	

	public static void main(String[] args) {
		String str = "<Company><id>100</id><type>Company</type><name>mDT</name><firstName nil=\"true\"/><employees><Employee id=\"101\" type=\"Employee\"/><Employee id=\"102\"><name>Mike</name></Employee></employees></Company>";
		//String str = "<Employees><Employee id=\"101\" type=\"Employee\"/><Employee id=\"102\"><name>Mike</name></Employee></Employees>";
		ERXRestNameRegistry.registry().setExternalNameForInternalName("Super", "Company");
		ERXRestNameRegistry.registry().setExternalNameForInternalName("Super2", "Employee");
		ERXRestContext context = new ERXRestContext();
		ERXRestRequestNode n = new ERXXmlRestParser().parseRestRequest(new ERXStringRestRequest(str), new ERXRestFormatDelegate(), context);
		ERXStringBufferRestResponse response = new ERXStringBufferRestResponse();
		new ERXXmlRestWriter().appendToResponse(n, response, new ERXRestFormatDelegate(), context);
		System.out.println("ERXXmlRestParser.main: " + response);
	}

}
