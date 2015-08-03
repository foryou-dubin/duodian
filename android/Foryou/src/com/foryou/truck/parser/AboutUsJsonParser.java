package com.foryou.truck.parser;

import com.foryou.truck.entity.AboutUsEntity;

public class AboutUsJsonParser extends BaseJsonParser{
	public AboutUsEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, AboutUsEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}