package org.simoes.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class WebUtils {
	
	/**
	 * Converts the String passed in to a url friendly string.
	 * For example if we pass in "New York" -> "new-york"
	 * @param a
	 * @return
	 */
	public static String convertToUrlFriendly(String a) {
		String result = a;
		// first trim string
		// convert to lowercase everything
		// replace whitespace with dash -
		if(StringUtils.isNotEmpty(result)) {
			result = result.trim();
			result = StringUtils.lowerCase(result);
			result = StringUtils.normalizeSpace(result);
			result = StringUtils.replace(result, " ", "-");
		}
		return result;
	}

	/**
	 * Converts the String passed in from a url friendly string.
	 * For example if we pass in "new-york" -> "New York"
	 * @param a
	 * @return
	 */
	public static String convertFromUrlFriendly(String a) {
		String result = a;
		// replace dashes with whitespace
		// convert first letters to uppercase
		if(StringUtils.isNotEmpty(result)) {
			result = StringUtils.replace(result, "-", " ");
			result = WordUtils.capitalize(result);
		}
		return result;
	}
}

