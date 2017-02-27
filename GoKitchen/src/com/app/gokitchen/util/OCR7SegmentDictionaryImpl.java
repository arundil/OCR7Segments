package com.app.gokitchen.util;

import java.util.HashMap;
import java.util.Map;

public class OCR7SegmentDictionaryImpl implements OCR7SegmentDictionary {

	private Map<String,Integer> dictionary;
	private static final Integer ITEM_THRESHOLD = 5;
	
	
	public OCR7SegmentDictionaryImpl() {
		dictionary = new HashMap<String, Integer>();
		fillDictionary();
	}

	@Override
	public Boolean UpdateElement (String element, Integer value) {
		if (dictionary.containsKey(element)) {
			dictionary.put(element,dictionary.get(element)+value);
			return true;
		}
		return false;
	}

	@Override
	public Map<String,Integer>  GetAllElements() {
		return dictionary;
	}

	@Override
	public void fillDictionary() {
		
		dictionary.put("1200", 0);
		dictionary.put("1400", 0);
		dictionary.put("1600", 0);
		dictionary.put("1800", 0);
		dictionary.put("2000", 0);
		dictionary.put("800", 0);
		dictionary.put("600", 0);
		dictionary.put("400", 0);
		dictionary.put("200", 0);
		dictionary.put("60", 0);
		dictionary.put("80", 0);
		dictionary.put("100", 0);
		dictionary.put("120", 0);
		dictionary.put("140", 0);
		dictionary.put("160", 0);
		dictionary.put("180", 0);
		dictionary.put("200", 0);
		dictionary.put("220", 0);
		dictionary.put("240", 0);
		dictionary.put("E0", 0);
		dictionary.put("5", 0);
		dictionary.put("10", 0);
		dictionary.put("15", 0);
		dictionary.put("20", 0);
		dictionary.put("25", 0);
		dictionary.put("30", 0);
		dictionary.put("35", 0);
		dictionary.put("40", 0);
		dictionary.put("45", 0);
		dictionary.put("50", 0);
		dictionary.put("55", 0);
		dictionary.put("65", 0);
		dictionary.put("70", 0);
		dictionary.put("75", 0);
		dictionary.put("85", 0);
		dictionary.put("90", 0);
		dictionary.put("95", 0);
		dictionary.put("105", 0);
		dictionary.put("110", 0);
		dictionary.put("115", 0);
		dictionary.put("125", 0);
		dictionary.put("130", 0);
		dictionary.put("135", 0);
		dictionary.put("145", 0);
		dictionary.put("150", 0);
		dictionary.put("155", 0);
		dictionary.put("165", 0);
		dictionary.put("170", 0);
		dictionary.put("175", 0);
		
	}

	@Override
	public void restartDictionary() {
		dictionary.clear();
		fillDictionary();
	}

	@Override
	public String evaluateDictionary() {
		
		String ret = "";
		
		for(String s : dictionary.keySet()) {
			Integer val = dictionary.get(s);
			if (val >= ITEM_THRESHOLD){
				ret = s;
				break;
			}
		}
		return ret;
	}
	
	

}
