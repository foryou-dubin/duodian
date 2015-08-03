package com.foryou.truck.parser;

import com.foryou.truck.entity.QuoteListEntity;

public class QuoteListJsonParser extends BaseJsonParser{
	public QuoteListEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, QuoteListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
