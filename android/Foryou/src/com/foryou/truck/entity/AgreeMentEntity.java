package com.foryou.truck.entity;

public class AgreeMentEntity extends BaseEntity {
	public AgreeMent data;

	public static class AgreeMent {
		public String customerName;
		public String driverName;
		public String driverIdCard;
		public String driverPlate;
		public String driverMobile;
		public String confirmTime;
		public String arrangeTime;
	}
}
