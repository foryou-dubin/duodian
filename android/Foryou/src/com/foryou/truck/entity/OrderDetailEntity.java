package com.foryou.truck.entity;

import java.util.List;

//{
//	  "status": "Y",
//	  "code": 200,
//	  "msg": "OK",
//	  "data": {
//	    "order_sn": "205271",
//	    "insurance_image": "",
//	    "status": {
//	      "key": "9",
//	      "text": "报价收集中"
//	    },
//	    "agent": {
//	      "agent_name": "",
//	      "agent_mobile": "",
//	      "driver_name": "",
//	      "driver_mobile": "",
//	      "driver_plate": ""
//	    },
//	    "goods": {
//	      "goods_name": "444",
//	      "goods_weight": "222",
//	      "goods_cubage": "222",
//	      "pay_type": {
//	        "key": "2",
//	        "text": "现金支付（收货方付）"
//	      },
//	      "goods_loadtime": "1435852800",
//	      "goods_loadtime_desc": "08:00-08:30",
//	      "goods_unloadtime": "1435939200",
//	      "goods_unloadtime_desc": "",
//	      "order_price": "0",
//	      "receipt": "不需要",
//	      "car_model": "高栏车",
//	      "car_length": "13.5",
//	      "need_back": "0",
//	      "car_type": "2",
//	      "expect_price": "11",
//	      "pay_online_success": "0",
//	      "coupon_code": null,
//	      "coupon_value": null
//	    },
//	    "sender": {
//	      "name": "",
//	      "tel": "",
//	      "tel2": "",
//	      "start_place": "北京-东城区",
//	      "start_address": ""
//	    },
//	    "receiver": {
//	      "name": "",
//	      "tel": "",
//	      "tel2": "",
//	      "end_place": "北京-大兴区",
//	      "end_address": ""
//	    },
//	    "location": []
//	  }

public class OrderDetailEntity extends BaseEntity {
	
	public OrderDetail data;

	public static class OrderDetail {
		public String order_sn;
		public String insurance_image;
		public String insurance;
		public OrderStatus status;
		public Agent agent;
		public Goods goods;
		public Sender sender;
		public Receiver receiver;
		public List<LocationNew> location;
	}
	
	public static class Agent{
		public String agent_name;
		public String agent_mobile;
		public String driver_name;
		public String driver_mobile;
		public String driver_plate;
		public String bs_locate;
	}

	public static class Goods {
		public String goods_name;
		public String goods_weight;
		public String goods_cubage;
		public PayType pay_type;
		public String goods_loadtime;
		public String goods_loadtime_desc;
		public String goods_unloadtime;
		public String goods_unloadtime_desc;
		public String order_price;
		public String receipt;
		public String car_type;
		public String need_back;
		public String car_model;
		public String car_length;
		public String coupon_code;
		public String coupon_value;
		public String expect_price;
		public String pay_online_success;
		public String occupy_length;
	}
	
	public static class PayType{
		public String key;
		public String text;
	}

	public static class Sender {
		public String name;
		public String tel;
		public String tel2;
		public String start_place;
		public String start_address;
	}

	public static class Receiver {
		public String name;
		public String tel;
		public String tel2;
		public String end_place;
		public String end_address;
	}

	public static class Location {
		public LocationItem start;
		public LocationItem end;
		public LocationNew latest;
	}

	public static class LocationItem {
		public String city;
		public String location;
	}

	public static class LocationNew {
		public String lng;
		public String lat;
		public String location;
		public String time;
	}
}
