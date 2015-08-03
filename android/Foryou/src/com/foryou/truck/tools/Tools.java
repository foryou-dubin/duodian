package com.foryou.truck.tools;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.foryou.truck.entity.OrderDetailEntity.LocationNew;
import com.foryou.truck.util.Constant;

public class Tools {
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String UnicodeDecode(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '\\' && chars[i + 1] == 'u') {
				char cc = 0;
				for (int j = 0; j < 4; j++) {
					char ch = Character.toLowerCase(chars[i + 2 + j]);
					if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
						cc |= (Character.digit(ch, 16) << (3 - j) * 4);
					} else {
						cc = 0;
						break;
					}
				}
				if (cc > 0) {
					i += 5;
					sb.append(cc);
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static String getStaticMapImageUrl(int latitude, int longitude,
			int width, int height) {
		float lat = ((float) latitude) / 1000000;
		float longit = ((float) longitude) / 1000000;
		String uri = "http://api.map.baidu.com/staticimage?center=" + longit
				+ "," + lat + "&width=320&height=100&zoom=15&markers=" + longit
				+ "," + lat + "&scale=2";
		return uri;
	}

	public static String getMutiMarkMapImageUrl(List<LocationNew> marklist,
			int width, int heidht) {
		String baseUri = "http://api.map.baidu.com/staticimage?center="
				+ marklist.get(marklist.size()-1).lng + "," + marklist.get(marklist.size()-1).lat
				+ "&width=640&height=200&zoom=5&markers=";
		for (int i = 0; i < marklist.size(); i++) {
			if(i!=0){
				baseUri += "|" + marklist.get(i).lng + "," + marklist.get(i).lat;
			}else{
				baseUri += marklist.get(i).lng + "," + marklist.get(i).lat;
			}
		}
		baseUri += "&bbox=109.2,36.3;117.2,40.5";
		return baseUri;
	}

	public static String getStaticMapImageUrl(double latitude,
			double longitude, int width, int height) {
		float lat = (float) latitude;
		float longit = (float) longitude;
		String uri = "http://api.map.baidu.com/staticimage?center=" + longit
				+ "," + lat + "&width=640&height=200&zoom=15&scale=2&bbox=109.2,36.3;117.2,40.5";
		return uri;
	}

	public static String getMapUrlWitchLat(double latitude, double longitude) {
		return "http://api.map.baidu.com/geocoder/v2/?" + "ak=" + Constant.AK
				+ "&callback=" + "renderReverse&location=" + latitude + ","
				+ longitude + "&output=json&pois=1" + "&mcode="
				+ Constant.MCODE;
	}

	public static int getVersionCode(Context context) {
		int versionCode = 1;
		try {
			versionCode = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}
}
