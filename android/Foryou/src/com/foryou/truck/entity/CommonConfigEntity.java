package com.foryou.truck.entity;

import java.util.List;

public class CommonConfigEntity extends BaseEntity {
	public Config data;

	public static class Config {
		public List<BaseKeyValue> car_length;
		public List<BaseKeyValue> car_model;
		public List<BaseKeyValue> time;
		public List<BaseKeyValue> limit_day;
		public List<BaseKeyValue> system_switch;
		public List<BaseKeyValue> insurance_type;
		public List<BaseKeyValue> account_type;
		public List<BaseKeyValue> pay_type;
		public Type2PayType type2pay;
	}
	
	public static class Type2PayType{
		public List<Integer> type0;
		public List<Integer> type1;
	}

	public static class BaseKeyValue {
		public String key;
		public String value;
	}
}
