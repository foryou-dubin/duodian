package com.foryou.truck.parser;

import com.foryou.truck.entity.BaseEntity;

public class SimpleJasonParser extends BaseJsonParser{
	public BaseEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, BaseEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
