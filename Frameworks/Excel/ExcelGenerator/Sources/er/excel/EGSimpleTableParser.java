/*
 * Created on 04.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package er.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNumberFormatter;

import er.extensions.formatters.ERXNumberFormatter;
import er.extensions.foundation.ERXDictionaryUtilities;
import er.extensions.foundation.ERXKeyValueCodingUtilities;


/**
 * Parses an input stream for tables and converts them into excel
 * sheets. You must have a surrounding element as there is only one
 * root element in XML allowed.
 * <blockquote>
 * Eg:<code>&lt;div&gt;&lt;table 1&gt;&lt;table 2&gt;...&lt;/div&gt;</code>
 * </blockquote>
 * <p>
 * You <em>must</em> take care that your content is XML readable.
 * There is support for a CSS-like style tagging. Either supply
 * font and style dictionaries in the constructor or via &lt;style&gt; and &lt;font&gt; tags.
 * The tags are shown in the example, but mainly the attributes are named the same as the properties
 * of the {@link org.apache.poi.hssf.usermodel.HSSFCellStyle HSSFCellStyle} and {@link org.apache.poi.hssf.usermodel.HSSFFont HSSFFont}
 * objects. The symbolic names from theses classes (eg. <code>HorizontalAlignment.RIGHT</code>) are also supported.
 * In addition, the tags <em>must</em> have an <code>id</code> attribute and can specify an
 * <code>extends</code> attribute that contains the ID of the style that is extended - all properties from this
 * style and it's predecessors are copied to the current style.
 * <p>
 * In addition, you can specify an attribute in any &lt;table&gt;, &lt;tr&gt;, &lt;th&gt; and &lt;td&gt; tag.
 * When this happens a new style is created and it applies to the contents of this tag.
 * The value is copied as text from the cell's content, so you better take care that it is parsable
 * and matches the <code>cellStyle</code> and <code>cellFormat</code> definition.
 * <p>
 * The parser also supports the <code>some-name</code> attribute names in addition to
 * <code>someName</code> as using the <b>Reformat</b> command in WOBuilder messes up the case
 * of the tags. When used in .wod files, the attributes must be enclosed in quotes
 * (<code>"cell-type"=foo;</code>). Some care must be taken when the attributes in the current node override the ones
 * from the parent as this is not thoroughly tested.
 * <p>
 * A client would use this class like:
 * <pre><code>
 * EGSimpleTableParser parser = new EGSimpleTableParser(new ByteArrayInputStream(someContentString));
 * NSData result = parser.data();
 * </code></pre>
 * @author ak
 */
public class EGSimpleTableParser {
	private static final Logger log = LoggerFactory.getLogger(EGSimpleTableParser.class);
	
	private InputStream _contentStream;
	private Workbook _workbook;
	private NSMutableDictionary<NSDictionary<String, String>, CellStyle> _styles = new NSMutableDictionary<>();
	private NSMutableDictionary<String, Font> _fonts = new NSMutableDictionary<>();
	private NSMutableDictionary<String, NSDictionary<String, String>> _styleDicts = new NSMutableDictionary<>();
	private NSMutableDictionary<String, NSDictionary<String, String>> _fontDicts = new NSMutableDictionary<>();

	public EGSimpleTableParser(InputStream contentStream) {
		this(contentStream, null, null);
 	}
	
	public EGSimpleTableParser(InputStream contentStream, NSDictionary<String, NSDictionary<String, String>> fontDicts, NSDictionary<String, NSDictionary<String, String>> styleDicts) {
		_contentStream = contentStream;
		if(fontDicts != null) {
			_fontDicts.addEntriesFromDictionary(fontDicts);
		}
		
		if(styleDicts != null) {
			_styleDicts.addEntriesFromDictionary(styleDicts);
		}
	}

	public void writeToStream(OutputStream out) throws IOException {
		workbook().write(out);
		out.close();
	}
	
	public NSData data() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook().write(out);
			out.close();
			
			return new NSData(out.toByteArray());
		} catch (IOException e) {
			log.error("Could not create NSData from workbook.",e);
		}
		return null;
	}

    public Workbook workbook() {
    	if(_workbook == null) {
    		parse();
    	}
    	return _workbook;
    }
    
    private static String nodeValueForKey(Node node, String key, String defaultValue) {
    	NamedNodeMap attributes = node.getAttributes();
    	String result = defaultValue;
        
        if(attributes.getNamedItem(key) != null) {
            result = attributes.getNamedItem(key).getNodeValue();
            if (result == null || result.length() == 0) {
                result = defaultValue;
            }
		}
		return result;
	}
    
    private static String keyPathToAttributeString(String aString) {
        int i, cnt = aString.length();
        StringBuilder result = new StringBuilder(cnt*2);
        for(i = 0; i < cnt; i++) {
            char c = aString.charAt(i);
            if(Character.isUpperCase(c)) {
                result.append('-');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private static String attributeStringToKeyPath(String aString) {
        int i, cnt = aString.length();
        boolean upperNext = false;
        StringBuilder result = new StringBuilder(cnt*2);
        for(i = 0; i < cnt; i++) {
            char c = aString.charAt(i);
            if(upperNext) {
                if(Character.isLowerCase(c)) {
                    c = Character.toUpperCase(c);
                }
                result.append(c);
                upperNext = false;
            } else if(c == '-') {
                upperNext = true;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * @param fontDictionary
     * @param tableNode
     */
    private static void addEntriesFromNode(NSMutableDictionary<String, String> dictionary, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        for(int i = 0; i < attributes.getLength(); i ++) {
            Node n = attributes.item(i);
            String key = attributeStringToKeyPath(n.getNodeName());
            String value = n.getNodeValue();
            if("".equals(value)) {
                dictionary.removeObjectForKey(key);
            } else {
                dictionary.setObjectForKey(value, key);
            }
        }
    }

    private static String dictValueForKey(NSDictionary<String, String> dict, String key, String defaultValue) {
        String result = dict.objectForKey(key);
        if(result == null) {
            result = dict.objectForKey(keyPathToAttributeString(key));
        }
        if(result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    private static void takeBooleanValueForKey(NSDictionary<String, String> dict, String key, Object target, String defaultValue) {
    	String value = dictValueForKey(dict, key, defaultValue);
    	if(value != null) {
    	    NSKeyValueCoding.Utility.takeValueForKey(target, Boolean.valueOf(value), key);
    	}
    }
    
    private static void takeNumberValueForKey(NSDictionary<String, String> dict, String key, Object target, String defaultValue) {
    	String value = dictValueForKey(dict, key, defaultValue);
    	if(value != null) {
    		NSKeyValueCoding.Utility.takeValueForKey(target, Integer.valueOf(value), key);
    	}
    }
    
    private static void takeClassValueForKey(NSDictionary<String, String> dict, String key, Object target, Class<?> source, String defaultValue) {
    	String value = dictValueForKey(dict, key, defaultValue);
    	if(value != null) {
   			Object number = ERXKeyValueCodingUtilities.classValueForKey(source, value);
			NSKeyValueCoding.Utility.takeValueForKey(target, number, key);
    	}
    }

    private void parse() {
    	try {
    		Document document = null;
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		dbf.setNamespaceAware(true);
    		dbf.setIgnoringElementContentWhitespace(true);
    		DocumentBuilder builder = dbf.newDocumentBuilder();
    		InputStream stream = _contentStream;
    		document = builder.parse(stream);
    		
    		_workbook = createWorkbook();
    		
    		log.debug("{}", document.getDocumentElement());
    		
    		NodeList nodes = document.getDocumentElement().getChildNodes();
    		for (int i = 0; i < nodes.getLength(); i++) {
    		    Node node = nodes.item(i);
    		    if(node.getNodeType() == Node.ELEMENT_NODE) {
    		        parseNode(node);
    		    }
    		}
    	} catch(Exception ex) {
    	    throw new NSForwardException(ex);
    	}
    }
    
    protected Workbook createWorkbook() {
    	return new HSSFWorkbook();
    }
    
    private void parseNode(Node node) {
    	String tagName = node.getLocalName().toLowerCase();
    	if("font".equals(tagName)) {
    		parseFont(node);
    	} else if("style".equals(tagName)) {
    		parseStyle(node);
    	} else if("table".equals(tagName)) {
    		parseTable(node);
    	} else {
    		// descend
    		NodeList nodes = node.getChildNodes();
    		for (int i = 0; i < nodes.getLength(); i++) {
    			Node child = nodes.item(i);
    			if(child.getNodeType() == Node.ELEMENT_NODE) {
    				parseNode(child);
    			}
    		}
    	}
    }
    
    private void parseStyle(Node node) {
		String id =  nodeValueForKey(node, "id", null);
		if(id != null) {
			// we're only handling styles with IDs
			NSMutableDictionary<String, String> dict = new NSMutableDictionary<>();
			
			String extendsID = nodeValueForKey(node, "extends", null);
			if(extendsID != null) {
				NSDictionary<String, String> otherDict = _styleDicts.objectForKey(extendsID);
				if(otherDict == null) {
					throw new NullPointerException("Extends Style Id not found");
				}
				dict.addEntriesFromDictionary(otherDict);
			}
			addEntriesFromNode(dict, node);
			
			_styleDicts.setObjectForKey(dict, id);
		}
    }
    
    private void parseFont(Node node) {
     	String id =  nodeValueForKey(node, "id", null);
    	if(id != null) {
    		// we're only handling fonts with IDs
    		NSMutableDictionary<String, String> dict = new NSMutableDictionary<>();
    		
    		String extendsID = nodeValueForKey(node, "extends", null);
    		if(extendsID != null) {
    			NSDictionary<String, String> otherDict = _fontDicts.objectForKey(extendsID);
    			if(otherDict == null) {
    				throw new NullPointerException("Extends Font Id not found");
    			}
    			dict.addEntriesFromDictionary(otherDict);
    		}
    		addEntriesFromNode(dict, node);
    		
    		_fontDicts.setObjectForKey(dict, id);
    	}
    }
    
    private void parseTable(Node tableNode) {
    	String sheetName = nodeValueForKey(tableNode, "name", "Unnamed Sheet " + (_workbook.getNumberOfSheets() + 1));
    	NSMutableDictionary<String, String> sheetDict = new NSMutableDictionary<>();
    	addEntriesFromNode(sheetDict, tableNode);
        if(sheetName.matches("[\\/\\\\\\*\\?\\[\\]]")) {
            sheetName = sheetName.replaceAll("[\\/\\\\\\*\\?\\[\\]]", "-");
            log.warn("Illegal characters in sheet name (/\\*?[]): {}", sheetName);
        }
        if(sheetName.length() > 31) {
            sheetName = sheetName.substring(0,31);
            log.warn("Sheet name too long (max 31 Characters): {}", sheetName);
        }
        Sheet sheet = _workbook.createSheet(sheetName);
 
        NodeList rowNodes = tableNode.getChildNodes();
    	
    	//takeNumberValueForKey(tableNode, "defaultColumnWidthInPoints", workbook, null);
    	takeNumberValueForKey(sheetDict, "defaultColumnWidth", sheet, null);
    	takeNumberValueForKey(sheetDict, "defaultRowHeight", sheet, null);
    	takeNumberValueForKey(sheetDict, "defaultRowHeightInPoints", sheet, null);
    	
    	log.debug("Sheet: {}", _workbook.getNumberOfSheets());
    	
    	int rowNum = 0;
    	for (int j = 0; j < rowNodes.getLength(); j++) {
    		Node rowNode = rowNodes.item(j);
    		if(rowNode.getNodeType() == Node.ELEMENT_NODE
    				&& "tr".equals(rowNode.getLocalName().toLowerCase())) {
    			NSMutableDictionary<String, String> rowDict = new NSMutableDictionary<>(sheetDict);
    			addEntriesFromNode(rowDict, rowNode);

                log.debug("Row: {}", rowNum);
    			Row row = sheet.createRow(rowNum);
    			
    			rowNum = rowNum + 1;
    			NodeList cellNodes = rowNode.getChildNodes();
    			for (int k = 0; k < cellNodes.getLength(); k++) {
    				Node cellNode = cellNodes.item(k);
    				if(cellNode.getNodeType() == Node.ELEMENT_NODE
    						&& ("td".equals(cellNode.getLocalName().toLowerCase())
    								|| "th".equals(cellNode.getLocalName().toLowerCase()))) {
    					int currentColumnNumber = row.getPhysicalNumberOfCells();
						Cell cell = row.createCell(currentColumnNumber);
    					Object value = null;
    					if(cellNode.getFirstChild() != null) {
    	   					value = cellNode.getFirstChild().getNodeValue();
     					}
    					
    					NSMutableDictionary<String, String> cellDict = new NSMutableDictionary<>(rowDict);
    					addEntriesFromNode(cellDict, cellNode);
    					
    					String cellTypeName = dictValueForKey(cellDict, "cellType", "NUMERIC");
    					String cellFormatName = dictValueForKey(cellDict, "cellFormat", "0.00;-;-0.00");
    					
    					log.debug("{}: {}-{}", value, cellFormatName, cellTypeName);
    					CellType cellType = CellType.valueOf(cellTypeName);
    					
    					switch(cellType) {
    						case FORMULA:
    						    cell.setCellFormula(value != null ? value.toString() : null);
    						    break;
    						    
    						case NUMERIC:
    						    try {
    						        if(value != null) {
    						            NSNumberFormatter f = ERXNumberFormatter.numberFormatterForPattern(cellFormatName);
    						            Number numberValue = (Number)f.parseObject(value.toString());
    						            log.debug("{}: {}", f.pattern(), numberValue);
    						            if(numberValue != null) {
    						                cell.setCellValue(numberValue.doubleValue());
    						            }
    						        }
    						    } catch (ParseException e1) {
    						        log.info("Could not parse '{}'.", value, e1);
    						    }
    						    break;
    							
    						case BOOLEAN:
    							if (value != null) {
    								try {
    									Integer integer = Integer.parseInt(value.toString());
    									cell.setCellValue(integer > 0);
    								} catch (NumberFormatException ex) {
    									log.debug("Could not parse '{}'.", value, ex);
    	    							cell.setCellValue(Boolean.valueOf(value.toString()));
    								}
    							}
    							break;
    							
    						case STRING:
    						default:
    						    cell.setCellValue(createRichTextString(value));
    						    break;
    					}
    					
    					String cellWidthString = nodeValueForKey(cellNode, "width", null);
    					if(cellWidthString != null && cellWidthString.indexOf("%") < 0) {
    						if ("auto".equalsIgnoreCase(cellWidthString)) {
	      						try {
	      							sheet.autoSizeColumn((short) currentColumnNumber);
	      						} catch (Exception ex) {
	      							log.warn("Exception during autosizing column {}.", currentColumnNumber, ex);
	      						}
    						} else {
        						try {
        							short width = Integer.valueOf(cellWidthString).shortValue();
        							sheet.setColumnWidth(currentColumnNumber, width * 256);
        						} catch (Exception ex) {
        							log.warn("Exception during width change of column {}.", currentColumnNumber, ex);
        						}
    						}
    					}
    					
    					String cellHeightString = nodeValueForKey(cellNode, "height", null);
    					if(cellHeightString != null && cellHeightString.indexOf("%") < 0) {
    						try {
    							short height = Integer.valueOf(cellHeightString).shortValue();
    							row.setHeightInPoints(height);
    						} catch (Exception ex) {
    							log.warn("Exception during height change of row {}", row, ex);
    						}
    					}
    					
    					CellStyle style = styleWithDictionary(cellDict);
    					
    					if(style != null) {
    						cell.setCellStyle(style);
    					}
    					
    					String colspanString = dictValueForKey(cellDict, "colspan", "1");
    					short colspan = Integer.valueOf(colspanString).shortValue();
    					for(int col = 1; col < colspan; col++) {
    						int nextColumnNumber = row.getPhysicalNumberOfCells();
							cell = row.createCell(nextColumnNumber);
    						if(style != null) {
    							cell.setCellStyle(style);
    						}
    					}
    					
    					log.debug("Cell: {}", value);
    				}
    			}
    		}
    	}
    }
    
    protected RichTextString createRichTextString(Object value) {
    	return new HSSFRichTextString(value != null ? value.toString() : null);
    }
    
    private Font fontWithID(String id) {
    	Font font = _fonts.objectForKey(id);
    	if(font == null) {
    		font = _workbook.createFont();
    		
    		NSDictionary<String, String> dict = _fontDicts.objectForKey(id);
    		String value = dictValueForKey(dict, "name", null);
    		if(value != null) {
    			font.setFontName(value);
    		}
    		
    		takeNumberValueForKey(dict, "fontHeight", font, null);
    		takeNumberValueForKey(dict, "fontHeightInPoints", font, null);
    		takeNumberValueForKey(dict, "color", font, null);
    		
    		takeBooleanValueForKey(dict, "italic", font, null);
    		takeBooleanValueForKey(dict, "strikeout", font, null);
    		
    		takeClassValueForKey(dict, "underline", font, Font.class, null);
    		takeClassValueForKey(dict, "typeOffset", font, Font.class, null);
    		takeClassValueForKey(dict, "boldweight", font, Font.class, null);
    		
    		_fonts.setObjectForKey(font, id);
    	}
    	
    	return font;
    }
    
    private static final NSArray<String> STYLE_KEYS = new NSArray<>(new String[] {
    		"font","hidden","locked","wrapText",
			"leftBorderColor","rightBorderColor","topBorderColor","bottomBorderColor",
			"borderLeft","borderRight","borderTop","borderBottom",
			"fillForegroundColor","fillBackgroundColor","fillPattern",
			"rotation","indention", "wrapText",
			"alignment","verticalAlignment","format"
	});
    
    private CellStyle styleWithDictionary(NSDictionary<String, String> aDict) {
    	String cellClass = dictValueForKey(aDict, "class", null);
    	
    	log.debug("before - {}: {}", cellClass, aDict);
    	
    	NSDictionary<String, String> dict = (NSDictionary)ERXDictionaryUtilities.dictionaryFromObjectWithKeys(aDict, STYLE_KEYS);
    	if(cellClass != null) {
    		// first, we pull in the default named styles, remembering
    		// we can have multiple styles like 'class="header bold"
    		String styles[] = cellClass.split(" +");
    		NSMutableDictionary<String, String> stylesFromClass = new NSMutableDictionary<>();
    		for (String string : styles) {
    			NSDictionary<String, String> current = _styleDicts.objectForKey(string);
    			if(current == null) {
    				throw new IllegalArgumentException("Cell Style not found: " + cellClass);
    			}
    			stylesFromClass.addEntriesFromDictionary(current);
    		}
    		stylesFromClass = ((NSDictionary)ERXDictionaryUtilities.dictionaryFromObjectWithKeys(stylesFromClass, STYLE_KEYS)).mutableClone();
    		stylesFromClass.addEntriesFromDictionary(dict);
    		dict = stylesFromClass.immutableClone();
    	}
    	log.debug("after - {}: {}", cellClass, dict);
    	
    	CellStyle cellStyle = _styles.objectForKey(dict);
    	if(cellStyle == null) {
    		cellStyle = _workbook.createCellStyle();
    		
    		String fontID = dictValueForKey(dict, "font", null);
    		if(fontID != null) {
    			Font font = fontWithID(fontID);
    			if(font == null) {
    				throw new IllegalArgumentException("Font ID not found!");
    			}
    			cellStyle.setFont(font);
    		}
    		takeBooleanValueForKey(dict, "hidden", cellStyle, null);
    		takeBooleanValueForKey(dict, "locked", cellStyle, null);
    		takeBooleanValueForKey(dict, "wrapText", cellStyle, null);
    		
    		takeNumberValueForKey(dict, "leftBorderColor", cellStyle, null);
    		takeNumberValueForKey(dict, "rightBorderColor", cellStyle, null);
    		takeNumberValueForKey(dict, "topBorderColor", cellStyle, null);
    		takeNumberValueForKey(dict, "bottomBorderColor", cellStyle, null);
    		
    		takeNumberValueForKey(dict, "fillBackgroundColor", cellStyle, null);
    		takeNumberValueForKey(dict, "fillForegroundColor", cellStyle, null);
    		takeNumberValueForKey(dict, "indention", cellStyle, null);
    		takeNumberValueForKey(dict, "rotation", cellStyle, null);
    		
    		takeClassValueForKey(dict, "borderLeft", cellStyle, BorderStyle.class, null);
    		takeClassValueForKey(dict, "borderRight", cellStyle, BorderStyle.class, null);
    		takeClassValueForKey(dict, "borderTop", cellStyle, BorderStyle.class, null);
    		takeClassValueForKey(dict, "borderBottom", cellStyle, BorderStyle.class, null);
    		
    		takeClassValueForKey(dict, "fillPattern", cellStyle, FillPatternType.class, null);
    		takeClassValueForKey(dict, "alignment", cellStyle, HorizontalAlignment.class, null);
    		takeClassValueForKey(dict, "verticalAlignment", cellStyle, VerticalAlignment.class, null);
    		
    		String formatString = dictValueForKey(dict, "format", null);
    		if(formatString != null) {
    			DataFormat format = _workbook.createDataFormat();
    			short formatId = format.getFormat(formatString);
    			cellStyle.setDataFormat(formatId);
    		}
    		
    		_styles.setObjectForKey(cellStyle, dict);
            log.debug("Created style ({}): {}", cellClass, dict);
    	}
    	return cellStyle;
    }
    
}