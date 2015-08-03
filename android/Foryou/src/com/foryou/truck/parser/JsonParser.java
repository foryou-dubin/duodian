package com.foryou.truck.parser;

import com.google.gson.Gson;




public interface JsonParser {
	/**
	 * 序列号json数据类
	 */
	public static final Gson gson = new Gson();
	public int parser(String json);
	
}
