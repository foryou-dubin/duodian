package com.foryou.truck.parser;

import com.foryou.truck.entity.AgentCommentListEntity;

public class AgentCommentListJsonParser extends BaseJsonParser{
	public AgentCommentListEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, AgentCommentListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}