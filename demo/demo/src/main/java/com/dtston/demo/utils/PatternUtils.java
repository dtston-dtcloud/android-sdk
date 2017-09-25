package com.dtston.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

	/**
	 * 判断手机号的书写是否有误
	 * @param phone
	 * @return bool
	 */
	public static boolean isPhone(String phone){
		String regexMobile = "^[1][34578]\\d{9}$";
		
		Pattern moblie = Pattern.compile(regexMobile);
		Matcher matcherMoblie = moblie.matcher(phone);
		if(matcherMoblie.matches()){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 判断6-20个字符，字母和数字组成
	 * @param phone
	 * @return bool
	 */
	public static boolean isCharacterByLetterOrNumber (String text){
		String regexMobile = "^[a-zA-z0-9_@\\.]{6,20}$";
		
		Pattern pattern = Pattern.compile(regexMobile);
		Matcher matcher = pattern.matcher(text);
		if(matcher.matches()){
			return true;
		}
		
		return false;
	}
	
}
