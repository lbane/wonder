package ognl.helperfunction;

import java.util.HashMap;
import java.util.Map;

public class WOHelperFunctionTagRegistry {
	private static Map<String, String> _tagShortcutMap = new HashMap<>();
	private static Map<String, WOTagProcessor> _tagProcessorMap = new HashMap<>();
	private static boolean _allowInlineBindings = false;

	public static Map<String, String> tagShortcutMap() {
		return _tagShortcutMap;
	}
	
	public static Map<String, WOTagProcessor> tagProcessorMap() {
		return _tagProcessorMap;
	}
	
	public static void registerTagShortcut(String fullElementType, String shortcutElementType) {
		_tagShortcutMap.put(shortcutElementType, fullElementType);
	}

	public static void registerTagProcessorForElementType(WOTagProcessor tagProcessor, String elementType) {
		_tagProcessorMap.put(elementType, tagProcessor);
	}

	public static void setAllowInlineBindings(boolean allowInlineBindings) {
		_allowInlineBindings = allowInlineBindings;
	}

	public static boolean allowInlineBindings() {
		return _allowInlineBindings;
	}

	static {
		// slf4j does not let you set logging levels
		// FIXME: after introducing log level set support enable this somehow again
		// WOHelperFunctionTagRegistry.log.setLevel(Level.WARN);

		WOHelperFunctionTagRegistry.registerTagShortcut("ERXLocalizedString", "localized"); // not in 5.4

		WOHelperFunctionTagRegistry.registerTagShortcut("ERXElse", "else");
		WOHelperFunctionTagRegistry.registerTagShortcut("ERXWOConditional", "if");
		WOHelperFunctionTagRegistry.registerTagShortcut("ERXWOConditional", "conditional");
		WOHelperFunctionTagRegistry.registerTagShortcut("ERXWOConditional", "condition"); // not in 5.4
		WOHelperFunctionTagRegistry.registerTagShortcut("WORepetition", "foreach");
        WOHelperFunctionTagRegistry.registerTagShortcut("WORepetition", "repeat");
        WOHelperFunctionTagRegistry.registerTagShortcut("WORepetition", "repetition");
		WOHelperFunctionTagRegistry.registerTagShortcut("WORepetition", "loop"); // not in 5.4
        WOHelperFunctionTagRegistry.registerTagShortcut("WOComponentContent", "content");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOComponentContent", "componentContent");
		WOHelperFunctionTagRegistry.registerTagShortcut("WOString", "str"); // not in 5.4
        WOHelperFunctionTagRegistry.registerTagShortcut("WOString", "string");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOSwitchComponent", "switchComponent");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOSwitchComponent", "switch");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOXMLNode", "XMLNode");
        WOHelperFunctionTagRegistry.registerTagShortcut("WONestedList", "nestedList");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOParam", "param");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOApplet", "applet");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOQuickTime", "quickTime");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOHTMLCommentString", "commentString");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOHTMLCommentString", "comment");
        WOHelperFunctionTagRegistry.registerTagShortcut("WONoContentElement", "noContentElement");
        WOHelperFunctionTagRegistry.registerTagShortcut("WONoContentElement", "noContent");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOBody", "body");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOEmbeddedObject", "embeddedObject");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOEmbeddedObject", "embedded");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOFrame", "frame");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOImage", "image");
		WOHelperFunctionTagRegistry.registerTagShortcut("WOImage", "img"); // not in 5.4
        WOHelperFunctionTagRegistry.registerTagShortcut("WOForm", "form");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOJavaScript", "javaScript");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOVBScript", "VBScript");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOResourceURL", "resourceURL");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOGenericElement", "genericElement");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOGenericElement", "element");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOGenericContainer", "genericContainer");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOGenericContainer", "container");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOActiveImage", "activeImage");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOCheckBox", "checkBox");
		WOHelperFunctionTagRegistry.registerTagShortcut("WOCheckBox", "checkbox"); // not in 5.4 (5.4 is case insensitive)
        WOHelperFunctionTagRegistry.registerTagShortcut("WOFileUpload", "fileUpload");
		WOHelperFunctionTagRegistry.registerTagShortcut("WOFileUpload", "upload");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOHiddenField", "hiddenField");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOHiddenField", "hidden"); // not in 5.4
        WOHelperFunctionTagRegistry.registerTagShortcut("WOImageButton", "imageButton");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOInputList", "inputList");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOBrowser", "browser");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOCheckBoxList", "checkBoxList");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOPopUpButton", "popUpButton");
		WOHelperFunctionTagRegistry.registerTagShortcut("WOPopUpButton", "select"); // not in 5.4
        WOHelperFunctionTagRegistry.registerTagShortcut("WORadioButtonList", "radioButtonList");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOPasswordField", "passwordField");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOPasswordField", "password");
        WOHelperFunctionTagRegistry.registerTagShortcut("WORadioButton", "radioButton");
        WOHelperFunctionTagRegistry.registerTagShortcut("WORadioButton", "radio");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOResetButton", "resetButton");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOResetButton", "reset");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOSubmitButton", "submitButton");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOSubmitButton", "submit");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOText", "text");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOTextField", "textField");
		WOHelperFunctionTagRegistry.registerTagShortcut("WOTextField", "textfield"); // not in 5.4 (5.4 is case insensitive)
        WOHelperFunctionTagRegistry.registerTagShortcut("WOSearchField", "search");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOSearchField", "searchfield");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOHyperlink", "hyperlink");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOHyperlink", "link");
        WOHelperFunctionTagRegistry.registerTagShortcut("WOActionURL", "actionURL");

		WOHelperFunctionTagRegistry.registerTagProcessorForElementType(new NotTagProcessor(), "not");
	}

}
