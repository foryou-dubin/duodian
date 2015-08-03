package com.foryou.truck.parser;

import com.foryou.truck.entity.BdrenderReverseEntity;

public class BdrenderReverseParser extends BaseJsonParser{
	public BdrenderReverseEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, BdrenderReverseEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}