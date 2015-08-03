package com.foryou.truck.net;

import java.io.IOException;
import java.io.InputStream;

public interface NetConstants {

	public static final String ENCODING = "UTF-8";
	public static final String HTTP_SAFETY_SCHEME = "https";
	public static final String HTTP_SCHEME = "http";
	public static final String HTTP_HOST = "dev.krakenjs.avosapps.com";
	
	public static final String METHOD = "item";
	public static final String HTTP_URL = HTTP_SCHEME + "://" + HTTP_HOST;

	public static final boolean HTTP_METHOD = true;
	public static final String MD = "mlhttp";
	
	

	/**
	 * 对流转化成字符串
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String getContentByString(InputStream is) throws IOException;

	/**
	 * 关闭连接
	 */
	public void close();
}
