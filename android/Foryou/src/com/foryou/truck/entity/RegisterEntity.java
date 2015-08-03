package com.foryou.truck.entity;

public class RegisterEntity extends BaseEntity {
	
	public UserInfo data;
	public static class UserInfo {
		public String uid;
		public String key;
	}
}
