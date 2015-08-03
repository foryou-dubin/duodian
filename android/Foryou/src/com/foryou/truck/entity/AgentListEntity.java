package com.foryou.truck.entity;

import java.util.List;

import com.foryou.truck.entity.OrderDetailEntity.PayType;
//{
//	  "status": "Y",
//	  "code": 200,
//	  "msg": "OK",
//	  "data": {
//	    "start_place": "北京-东城区",
//	    "end_place": "北京-大兴区",
//	    "hd_start_place": "2,52",
//	    "hd_end_place": "2,52",
//	    "list": [
//	      {
//	        "id": "206105",
//	        "agent_id": "505",
//	        "name": "dd",
//	        "mobile": "18618360214",
//	        "credit_point": "100%",
//	        "photo": "http:\\/\\/thuo.fuyoukache.com\\/static\\/images\\/avatar.jpg",
//	        "price": "5555",
//	        "expire_time": "1436990676",
//	        "reopen": "0",
//	        "remark": "",
//	        "time": "1436861076",
//	        "order_m": "1",
//	        "order_a": "9",
//	        "comment": 0
//	      }
//	    ]
//	  }
public class AgentListEntity extends BaseEntity{
	public AgentList data;
	public static class AgentList{
		public String start_place;
		public String end_place;
		public String hd_start_place;
		public String hd_end_place;
		public List<AgentInfo> list;
		public PayType pay_type;
	}
	public static class AgentInfo{
		public String id;
		public String agent_id;
		public String name;
		public String mobile;
		public String credit_point;
		public String photo;
		public String price;
		public String time;
		public String expire_time;
		public String reopen;
		public String remark;
		public String order_m;
		public String order_a;
		public String comment;
	}
}
