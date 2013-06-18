package org.simoes.classify;

import java.io.Serializable;

import org.apache.commons.lang3.text.WordUtils;

import com.google.common.base.Ascii;

/**
 * The categories that our classifier supports
 * @author csimoes
 *
 */
public enum Category implements Serializable {
	BUSINESS("business", "D1F5FF"),
	ENTERTAINMENT("entertainment", "F2B7B7"),
	FOOD("food", "FFFBAF"),
	HEALTH("health", "DFF0D8"),
	PERSONAL("personal", "FCF8E3"),
	SPORTS("sports", "B6FFBA"),
	TECHNOLOGY("technology", "E6E6E6"),
	OTHER("other", "FFFFFF");

	private final String name;
	private final String colorHex; //color in #FFFFFF format
	
	private Category(String name, String colorHex) {
		this.name = name;
		// consider loading everything except name from the Database
		// by adding DB calls here
		this.colorHex = colorHex;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return WordUtils.capitalize(name);
	}
	
	public String getColorHex() {
		return colorHex;
	}
	
	public static Category[] getAllCategories() {
		return Category.values();
	}
	
	/**
	 * Returns a Category matching on name of the category (ignoring case)
	 * @param a
	 * @return
	 */
	public static Category getCategoryByName(String a) {
		return Category.valueOf(Ascii.toUpperCase(a));
	}
}
