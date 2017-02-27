package com.app.gokitchen.util;

import java.util.Map;

public interface OCR7SegmentDictionary {

	Boolean UpdateElement (String element, Integer value);
	Map<String,Integer>  GetAllElements();
	void fillDictionary();
	void restartDictionary();
	String evaluateDictionary();
	
}
