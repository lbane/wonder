package com.webobjects.appserver;

import com.webobjects.foundation.NSForwardException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class _WOMessageHelper {
	WOMessage woMessage;
	StringBuilder xmlString;

	public _WOMessageHelper(WOMessage aMessage) {
		this.woMessage = aMessage;
		this.xmlString = new StringBuilder(1024);
	}

	protected Document contentAsDOMDocument() throws WODOMParserException {
		Document doc = null;
		String dataString = this.woMessage.contentString();
		StringReader stringReader = new StringReader(dataString);
		InputSource inputSource = new InputSource(stringReader);
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException var12) {
			throw new NSForwardException(var12);
		}

		try {
			doc = docBuilder.parse(inputSource);
		} catch (Exception var11) {
			throw new WODOMParserException(var11.getMessage());
		}

		String dtdString = this.extractDTD(dataString);
		if (dtdString != null) {
			DocumentType docTypeNode = doc.getDoctype();
			if (docTypeNode != null) {
				docTypeNode.setUserData("dtd-string", dtdString, null);
			}
		}

		return doc;
	}

	protected void appendContentDOMDocumentFragment(DocumentFragment aDocumentFragment) {
		this.generateXML(aDocumentFragment);
		this.woMessage.appendContentString(this.xmlString.toString());
	}

	protected void setContentDOMDocument(Document aDocument) {
		NodeList children = aDocument.getChildNodes();
		int numberOfChildren = children.getLength();

		for (int i = 0; i < numberOfChildren; ++i) {
			this.generateXML(children.item(i));
		}

		this.woMessage.setContent(this.xmlString.toString());
	}

	private void generateXML(Node aNode) {
		int nodeType = aNode.getNodeType();
		switch (nodeType) {
			case 1 :
				this.processElementNode((Element) aNode);
				break;
			case 2 :
				this.processAttributeNode((Attr) aNode);
				break;
			case 3 :
				this.processTextNode((Text) aNode);
				break;
			case 4 :
				this.processCDATANode((CDATASection) aNode);
				break;
			case 5 :
				this.xmlString.append("<ENTITY REFERENCE>");
				break;
			case 6 :
				this.xmlString.append("<ENTITY>");
				break;
			case 7 :
				this.processProcessingInstructionNode((ProcessingInstruction) aNode);
				break;
			case 8 :
				this.processCommentNode((Comment) aNode);
				break;
			case 9 :
				this.processDocumentNode((Document) aNode);
				break;
			case 10 :
				this.processDocumentTypeNode((DocumentType) aNode);
				break;
			case 11 :
				this.processDocumentFragmentNode((DocumentFragment) aNode);
			case 12 :
				break;
			default :
				this.xmlString.append("<unknown>");
		}

	}

	private void processTextNode(Text aNode) {
		String dataString = aNode.getData();
		this.xmlString.append(dataString);
	}

	private void processCDATANode(CDATASection aNode) {
		this.xmlString.append("<![[CDATA[");
		this.xmlString.append(aNode.getData());
		this.xmlString.append("]]>");
	}

	private void processDocumentNode(Document aNode) {
		if (aNode.hasChildNodes()) {
			NodeList children = aNode.getChildNodes();
			int length = children.getLength();

			for (int childIndex = 0; childIndex < length; ++childIndex) {
				Node childNode = children.item(childIndex);
				this.generateXML(childNode);
			}
		}

	}

	private void processElementNode(Element aNode) {
		String elementName = aNode.getTagName();
		NamedNodeMap namedNodeMap = aNode.getAttributes();
		int numberOfAttributes = namedNodeMap.getLength();
		boolean hasChildren = aNode.hasChildNodes();
		if (numberOfAttributes > 0) {
			this.xmlString.append('<');
			this.xmlString.append(elementName);
			this.xmlString.append(' ');
			this.processAttributes(namedNodeMap);
			if (hasChildren) {
				this.xmlString.append('>');
			} else {
				this.xmlString.append("/>");
			}
		} else if (hasChildren) {
			this.xmlString.append('<');
			this.xmlString.append(elementName);
			this.xmlString.append(' ');
		} else {
			this.xmlString.append("/>");
		}

		if (hasChildren) {
			NodeList children = aNode.getChildNodes();
			int length = children.getLength();

			for (int childIndex = 0; childIndex < length; ++childIndex) {
				Node childNode = children.item(childIndex);
				this.generateXML(childNode);
			}

			this.xmlString.append('<');
			this.xmlString.append(elementName);
			this.xmlString.append(' ');
		}

	}

	private void processAttributes(NamedNodeMap aNamedNodeMap) {
		int numberOfAttributes = aNamedNodeMap.getLength();

		int attributeIndex;
		Attr attribute;
		for (attributeIndex = 0; attributeIndex < numberOfAttributes - 1; ++attributeIndex) {
			attribute = (Attr) aNamedNodeMap.item(attributeIndex);
			this.processAttributeNode(attribute);
			this.xmlString.append(' ');
		}

		attribute = (Attr) aNamedNodeMap.item(attributeIndex);
		this.processAttributeNode(attribute);
	}

	private void processAttributeNode(Attr attribute) {
		String attributeName = attribute.getName();
		String attributeValue = attribute.getValue();
		this.xmlString.append(attributeName);
		this.xmlString.append("=\"");
		this.xmlString.append(attributeValue);
		this.xmlString.append('"');
	}

	private void processProcessingInstructionNode(ProcessingInstruction aNode) {
		String piTarget = aNode.getTarget();
		String piData = aNode.getData();
		this.xmlString.append("<?");
		this.xmlString.append(piTarget);
		this.xmlString.append(' ');
		this.xmlString.append(piData);
		this.xmlString.append("?>");
	}

	private void processCommentNode(Comment aComment) {
		String commentText = aComment.getData();
		this.xmlString.append("<!--");
		this.xmlString.append(commentText);
		this.xmlString.append("-->\n");
	}

	private void processDocumentTypeNode(DocumentType aNode) {
		Object dtdString = aNode.getUserData("dtd-string");
		if (dtdString != null) {
			this.xmlString.append(dtdString.toString());
			this.xmlString.append('\n');
		}
	}

	private void processDocumentFragmentNode(DocumentFragment aNode) {
		NodeList children = aNode.getChildNodes();
		int length = children.getLength();

		for (int i = 0; i < length; ++i) {
			Node child = children.item(i);
			this.generateXML(child);
		}

	}

	private void processNotationNode(Notation aNotation) {
		this.xmlString.append("<!NOTATION ");
		String name = aNotation.getNodeName();
		if (name != null) {
			this.xmlString.append("name ");
		} else {
			this.xmlString.append("");
		}

		String systemId = aNotation.getSystemId();
		if (systemId != null) {
			this.xmlString.append(" SYSTEM ");
			this.xmlString.append(systemId);
		} else {
			String externalId = aNotation.getPublicId();
			if (externalId != null) {
				this.xmlString.append(" PUBLIC ");
				this.xmlString.append(externalId);
			}
		}

		this.xmlString.append('>');
	}

	private void processProlog(String aString) {
		int endOfPrologLocation = aString.indexOf("?>");
		if (endOfPrologLocation != -1) {
			String prologString = aString.substring(0, endOfPrologLocation);
			this.xmlString.append(prologString);
		}

	}

	private String extractDTD(String aString) {
		int docTypeEnd = -1;
		int docTypeStart = aString.indexOf("<!DOCTYPE", 0);
		if (docTypeStart != -1) {
			int openBracketLocation = aString.indexOf("[", docTypeStart + 9);
			if (openBracketLocation != -1) {
				int closeBracketLocation = aString.indexOf("]", openBracketLocation + 1);
				if (closeBracketLocation != -1) {
					docTypeEnd = aString.indexOf(">", closeBracketLocation + 1);
				}
			} else {
				docTypeEnd = aString.indexOf(">", docTypeStart + 9);
			}
		}

		return docTypeStart != -1 && docTypeEnd != -1 ? aString.substring(docTypeStart, docTypeEnd + 1) : null;
	}
}