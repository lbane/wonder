package ognl.helperfunction;

import java.util.Enumeration;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver._private.WOBundle;
import com.webobjects.appserver._private.WOComponentDefinition;
import com.webobjects.appserver._private.WOComponentReference;
import com.webobjects.appserver._private.WODeclaration;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.appserver._private.WOGenerationSupport;
import com.webobjects.appserver._private.WOHTMLBareString;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation._NSUtilities;

public class WOHTMLWebObjectTag {
	
	private static final Logger logger = LoggerFactory.getLogger(WOHTMLWebObjectTag.class);
	
	private String _name;
	private WOHTMLWebObjectTag _parent;
	private NSMutableArray<Object> _children;

	private void extractName(String s) throws WOHelperFunctionHTMLFormatException {

		StringTokenizer stringtokenizer = new StringTokenizer(s, "=");
		if (stringtokenizer.countTokens() != 2) {
			throw new WOHelperFunctionHTMLFormatException("<WOHTMLWebObjectTag cannot initialize WebObject tag " + s + "> . It has no NAME=... parameter");
		}

		stringtokenizer.nextToken();
		String s1 = stringtokenizer.nextToken();

		int i = s1.indexOf('"');
		if (i != -1) {
			int j = s1.lastIndexOf('"');
			if (j > i) {
				_name = s1.substring(i + 1, j);
			}
		}
		else {
			StringTokenizer stringtokenizer1 = new StringTokenizer(s1);
			_name = stringtokenizer1.nextToken();
		}

		if (_name == null) {
			throw new WOHelperFunctionHTMLFormatException("<WOHTMLWebObjectTag cannot initialize WebObject tag " + s + "> . Failed parsing NAME parameter");
		}
	}

	public WOHTMLWebObjectTag() {
		_name = null;
	}

	public WOHTMLWebObjectTag(String s, WOHTMLWebObjectTag wohtmlwebobjecttag) throws WOHelperFunctionHTMLFormatException {
		_parent = wohtmlwebobjecttag;
		extractName(s);
	}

	public String name() {
		return _name;
	}

	public WOHTMLWebObjectTag parentTag() {
		return _parent;
	}

	public WOElement template() {
		NSMutableArray<WOElement> nsmutablearray = null;
		if (_children == null) {
			return null;
		}
		Enumeration<Object> enumeration = _children.objectEnumerator();
		if (enumeration != null) {
			nsmutablearray = new NSMutableArray<>(_children.count());
			StringBuilder stringbuffer = new StringBuilder(128);
			while (enumeration.hasMoreElements()) {
				Object obj1 = enumeration.nextElement();
				if (obj1 instanceof String s) {
					stringbuffer.append(s);
				}
				else {
					if (stringbuffer.length() > 0) {
						WOHTMLBareString wohtmlbarestring1 = new WOHTMLBareString(stringbuffer.toString());
						nsmutablearray.addObject(wohtmlbarestring1);
						stringbuffer.setLength(0);
					}
					nsmutablearray.addObject(obj1);
				}
			}
			if (stringbuffer.length() > 0) {
				WOHTMLBareString wohtmlbarestring = new WOHTMLBareString(stringbuffer.toString());
				stringbuffer.setLength(0);
				nsmutablearray.addObject(wohtmlbarestring);
			}
		}
		WOElement obj = null;
		if (nsmutablearray != null && nsmutablearray.count() == 1) {
			WOElement obj2 = nsmutablearray.objectAtIndex(0);
			if (obj2 instanceof WOComponentReference) {
				obj = new WODynamicGroup(_name, null, obj2);
			}
			else {
				obj = obj2;
			}
		}
		else {
			obj = new WODynamicGroup(_name, null, nsmutablearray);
		}
		return obj;
	}

	public void addChildElement(Object obj) {
		if (_children == null) {
			_children = new NSMutableArray<>();
		}
		_children.addObject(obj);
	}

	public WOElement dynamicElement(NSDictionary<String, WODeclaration> declarations, NSArray<String> languages) throws WOHelperFunctionDeclarationFormatException, ClassNotFoundException {
		String s = name();
		WOElement woelement = template();
		WODeclaration wodeclaration = declarations.objectForKey(s);
		return _elementWithDeclaration(wodeclaration, s, woelement, languages);
	}

	private static WOElement _componentReferenceWithClassNameDeclarationAndTemplate(String s, WODeclaration wodeclaration, WOElement woelement, NSArray<String> languages) throws ClassNotFoundException {
		WOComponentReference wocomponentreference = null;
		WOComponentDefinition wocomponentdefinition = WOApplication.application()._componentDefinition(s, languages);
		if (wocomponentdefinition != null) {
			NSDictionary<String, WOAssociation> nsdictionary = wodeclaration.associations();
			wocomponentreference = wocomponentdefinition.componentReferenceWithAssociations(nsdictionary, woelement);
		}
		else {
			throw new ClassNotFoundException("Cannot find class or component named \'" + s + "\" in runtime or in a loadable bundle");
		}
		return wocomponentreference;
	}

	private static WOElement _elementWithClass(Class class1, WODeclaration wodeclaration, WOElement woelement) {
		WOElement woelement1 = WOApplication.application().dynamicElementWithName(class1.getName(), wodeclaration.associations(), woelement, null);
		if (logger.isDebugEnabled()) {
			logger.debug("<WOHTMLWebObjectTag> Created Dynamic Element with name : {}\nDeclaration : {}\nElement : {}", class1.getName(), wodeclaration, woelement1);
		}
		return woelement1;
	}

	private static WOElement _elementWithDeclaration(WODeclaration wodeclaration, String s, WOElement woelement, NSArray<String> languages) throws ClassNotFoundException, WOHelperFunctionDeclarationFormatException {
		WOElement woelement1 = null;
		if (wodeclaration != null) {
			String s1 = wodeclaration.type();
			if (s1 != null) {
				logger.debug("<WOHTMLWebObjectTag> will look for {} in the java runtime.", s1);
				Class class1 = _NSUtilities.classWithName(s1);
				if (class1 == null) {
					logger.debug("<WOHTMLWebObjectTag> will look for com.webobjects.appserver._private.{} .", s1);
					class1 = WOBundle.lookForClassInAllBundles(s1);
					if (class1 == null) {
						logger.error("WOBundle.lookForClassInAllBundles({}) failed!", s1);
					}
					else if (!(com.webobjects.appserver.WODynamicElement.class).isAssignableFrom(class1)) {
						class1 = null;
					}
				}

				if (class1 != null) {
					logger.debug("<WOHTMLWebObjectTag> Will initialize object of class {}", s1);
					if ((com.webobjects.appserver.WOComponent.class).isAssignableFrom(class1)) {
						logger.debug("<WOHTMLWebObjectTag> will look for {} in the Compiled Components.", s1);
						woelement1 = _componentReferenceWithClassNameDeclarationAndTemplate(s1, wodeclaration, woelement, languages);
					}
					else {
						woelement1 = _elementWithClass(class1, wodeclaration, woelement);
					}
				}
				else {
					logger.debug("<WOHTMLWebObjectTag> will look for {} in the Frameworks.", s1);
					woelement1 = _componentReferenceWithClassNameDeclarationAndTemplate(s1, wodeclaration, woelement, languages);
				}
			}
			else {
				throw new WOHelperFunctionDeclarationFormatException("<WOHTMLWebObjectTag> declaration object for dynamic element (or component) named " + s + "has no class name.");
			}
		}
		else {
			throw new WOHelperFunctionDeclarationFormatException("<WOHTMLTemplateParser> no declaration for dynamic element (or component) named " + s);
		}

		WOGenerationSupport.insertInElementsTableWithName(woelement1, s, wodeclaration.associations());

		return woelement1;
	}
}