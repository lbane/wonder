/*
 * Copyright (C) NetStruxr, Inc. All rights reserved.
 *
 * This software is published under the terms of the NetStruxr
 * Public Software License version 0.5, a copy of which has been
 * included with this distribution in the LICENSE.NPL file.  */

/* WOOgnl.java created by max on Fri 28-Sep-2001 */
package ognl.webobjects;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver._private.WOBindingNameAssociation;
import com.webobjects.appserver._private.WOConstantValueAssociation;
import com.webobjects.appserver._private.WOKeyValueAssociation;
import com.webobjects.appserver.parser.WOComponentTemplateParser;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;
import com.webobjects.foundation._NSUtilities;

import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.helperfunction.WOHelperFunctionHTMLParser;
import ognl.helperfunction.WOHelperFunctionParser;
import ognl.helperfunction.WOHelperFunctionTagRegistry;

/**
 * <span class="en">
 * WOOgnl provides a template parser that support WOOgnl associations, Helper Functions, Inline Bindings, and Binding Debugging. 
 * 
 * @property ognl.active - defaults to true, if false ognl support is disabled
 * @property ognl.inlineBindings - if true, inline bindings are supported in component templates
 * @property ognl.parseStandardTags - if true, you can use inline bindings in regular html tags, but requires well-formed templates
 * @property ognl.debugSupport - if true, debug metadata is included in all bindings (but binding debug is not automatically turned on) 
 * </span>
 * 
 * <span class="ja">
 * WOOgnlはテンプレートパーサーに対して、OGNL (Object Graph Navigation Language)機能のヘルプ機能/インラインbinding/bindingデバッグ等の機能を提供する。
 * 
 * OGNL (Object Graph Navigation Language) は，Javaオブジェクトのプロパティにアクセス（setting/getting）する式言語です。
 * Javaオブジェクトのプロパティにアクセスするほか、直接メソッドを呼び出すことなどが可能です。
 * 2010-10-09 日本語追加 by A10
 * 
 * @property ognl.active - デフォルト値はtrue、falseにするとognl機能は無効。
 * @property ognl.inlineBindings - trueにするとコンポーネントテンプレートでのインライン・バインディング機能になる。
 * @property ognl.parseStandardTags - trueにするとhtmlタグ内でのインライン・バインディングが使用できる。しかし、正確なテンプレートを必要とする。
 * @property ognl.debugSupport - trueにするとデバッグ用のメタデータが全てのバインディングに追加される。 (しかし、この機能は自動では追加されない) 
 * </span>
 * 
 * @author mschrag
 * 
 */
public class WOOgnl {
	private static final Logger log = LoggerFactory.getLogger(WOOgnl.class);

	public static final String DefaultWOOgnlBindingFlag = "~";

	protected static Collection<Observer> _retainerArray = new NSMutableArray<>();
	static {
		try {
			Observer o = new Observer();
			_retainerArray.add(o);
			NSNotificationCenter.defaultCenter().addObserver(o, new NSSelector("configureWOOgnl", new Class[] { com.webobjects.foundation.NSNotification.class }), WOApplication.ApplicationWillFinishLaunchingNotification, null);
		}
		catch (Exception e) {
			log.error("Failed to configure WOOgnl.", e);
		}
	}

	private static Map<String, Class> associationMappings = Collections.synchronizedMap(new HashMap<>());

	public static void setAssociationClassForPrefix(Class clazz, String prefix) {
		associationMappings.put(prefix, clazz);
	}

	private static WOAssociation createAssociationForClass(Class clazz, String value, boolean isConstant) {
		return (WOAssociation) _NSUtilities.instantiateObject(clazz, new Class[] { Object.class, boolean.class }, new Object[] { value, Boolean.valueOf(isConstant) }, true, false);
	}

	public static class Observer {
		public void configureWOOgnl(NSNotification n) {
			WOOgnl.factory().configureWOForOgnl();
			NSNotificationCenter.defaultCenter().removeObserver(this);
			_retainerArray.remove(this);
		}
	}

	protected static WOOgnl _factory;

	public static WOOgnl factory() {
		if (_factory == null) {
			_factory = new WOOgnl();
		}
		return _factory;
	}

	public static void setFactory(WOOgnl factory) {
		_factory = factory;
	}

	public ClassResolver classResolver() {
		return NSClassResolver.sharedInstance();
	}

	public String ognlBindingFlag() {
		return DefaultWOOgnlBindingFlag;
	}

	public OgnlContext newDefaultContext() {
		// allow access to everything that is not declared private
		return new OgnlContext(classResolver(), null, new DefaultMemberAccess(false, true, true));
	}

	public void configureWOForOgnl() {
		// Configure runtime.
		// Configure foundation classes.
		OgnlRuntime.setPropertyAccessor(Object.class, new NSObjectPropertyAccessor());
		OgnlRuntime.setPropertyAccessor(NSArray.class, new NSArrayPropertyAccessor());
		OgnlRuntime.setPropertyAccessor(NSDictionary.class, new NSDictionaryPropertyAccessor());

		NSFoundationElementsAccessor e = new NSFoundationElementsAccessor();
		OgnlRuntime.setElementsAccessor(NSArray.class, e);
		OgnlRuntime.setElementsAccessor(NSDictionary.class, e);
		OgnlRuntime.setElementsAccessor(NSSet.class, e);
		// Register template parser
		if (hasProperty("ognl.active", "true")) {
			String parserClassName = System.getProperty("ognl.parserClassName", "ognl.helperfunction.WOHelperFunctionParser54");
			WOComponentTemplateParser.setWOHTMLTemplateParserClassName(parserClassName);
			if (hasProperty("ognl.inlineBindings", "false")) {
				WOHelperFunctionTagRegistry.setAllowInlineBindings(true);
			}
			if (hasProperty("ognl.parseStandardTags", "false")) {
				WOHelperFunctionHTMLParser.setParseStandardTags(true);
			}
			if (hasProperty("ognl.debugSupport", "false")) {
				WOHelperFunctionParser._debugSupport = true;
			}
		}
	}
	
	private static boolean hasProperty(String prop, String def) {
		String property = System.getProperty(prop, def).trim();
		return "true".equalsIgnoreCase(property) || "yes".equalsIgnoreCase(property);
	}

	public void convertOgnlConstantAssociations(NSMutableDictionary<String, WOAssociation> associations) {
		// RS: create a clone of the keys array before iterating over it, because this method may modify the associations dictionary, 
		// and the iteration over the modified dictionary is undefined thereafter
		for (Enumeration<String> e = associations.allKeys().immutableClone().objectEnumerator(); e.hasMoreElements();) {
			String name = e.nextElement();
			WOAssociation association = associations.objectForKey(name);
			boolean isConstant = false;
			String keyPath = null;
			if (association instanceof WOConstantValueAssociation constantAssociation) {
				// AK: this sucks, but there is no API to get at the value
				Object value = constantAssociation.valueInComponent(null);
				keyPath = value != null ? value.toString() : null;
				isConstant = true;
			}
			else if (association instanceof WOKeyValueAssociation) {
				keyPath = association.keyPath();
			}
			else if (association instanceof WOBindingNameAssociation b) {
				// AK: strictly speaking, this is not correct, as we only get the first part of 
				// the path. But take a look at WOBindingNameAssociation for a bit of fun...
				keyPath = "^" + b._parentBindingName;
			}
			if (keyPath != null) {
				if (!associationMappings.isEmpty()) {
					int index = name.indexOf(':');
					if (index > 0) {
						String prefix = name.substring(0, index);
						if (prefix != null) {
							Class c = associationMappings.get(prefix);
							if (c != null) {
								String postfix = name.substring(index + 1);
								WOAssociation newAssociation = createAssociationForClass(c, keyPath, isConstant);
								associations.removeObjectForKey(name);
								associations.setObjectForKey(newAssociation, postfix);
							}
						}
					}
				}
				if (isConstant && keyPath.startsWith(ognlBindingFlag())) {
					String ognlExpression = keyPath.substring(ognlBindingFlag().length(), keyPath.length());
					if (ognlExpression.length() > 0) {
						WOAssociation newAssociation = new WOOgnlAssociation(ognlExpression);
						NSArray<String> keys = associations.allKeysForObject(association);
						//if (log.isDebugEnabled())
						//    log.debug("Constructing Ognl association for binding key(s): "
						//              + (keys.count() == 1 ? keys.lastObject() : keys) + " expression: " + ognlExpression);
						if (keys.count() == 1) {
							associations.setObjectForKey(newAssociation, keys.lastObject());
						}
						else {
							for (Enumeration<String> ee = keys.objectEnumerator(); ee.hasMoreElements();) {
								associations.setObjectForKey(newAssociation, e.nextElement());
							}
						}
					}
				}
			}
		}
	}

	public Object getValue(String expression, Object obj) {
		Object value = null;
		try {
			value = Ognl.getValue(expression, newDefaultContext(), obj);
		}
		catch (OgnlException ex) {
			String message = ex.getMessage();
			// MS: This is SUPER SUPER LAME, but I don't see any other way in OGNL to
			// make keypaths with null components behave like NSKVC (i.e. returning null
			// vs throwing an exception).  They have something called nullHandlers 
			// in OGNL, but it appears that you have to register it per-class and you
			// can't override the factory.
			if (message == null || !message.startsWith("source is null for getProperty(null, ")) {
				throw new RuntimeException("Failed to get value '" + expression + "' on " + obj, ex);
			}
		}
		return value;
	}

	public void setValue(String expression, Object obj, Object value) {
		try {
			Ognl.setValue(expression, newDefaultContext(), obj, value);
		}
		catch (OgnlException ex) {
			throw new RuntimeException("Failed to set value '" + expression + "' on " + obj, ex);
		}
	}

}
