package org.ocr.sevensegments.utils;

import java.util.Map;

public interface OCR7SegmentDictionary {

	Boolean UpdateElement (String element, Integer value);
	Map<String,Integer>  GetAllElements();
	void fillDictionary();
	void restartDictionary();
	String evaluateDictionary();
	
}
