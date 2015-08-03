package com.foryou.truck.entity;

public class UserDetailEntity extends BaseEntity{
	public UserDetail data;
	public static class UserDetail{
		public String name;
		public String mobile;
		public String gender;
		public String type; //0:普通客户 1:合同客户 
		public AccountType account_type;
		public String vip;
		
		public static class AccountType{
			public String key;
			public String text;
		}
	}
}
