package com.foryou.truck.entity;


//{
//"status":"Y", "msg": data:{
//"order_sn": "202395", "start_place": "北京-崇文区", "end_place": "澳门-澳门-澳门", "goods_name": "123",
//"order_price": "998",
//￼
//"reduce_price": 0,
//"total_price": 998,
//"notify_date": 请在 2015-06-08 14:00:00 之前完成支付! "payurl":支付链接
//} }
public class OrderPayEntity extends BaseEntity{
	public OrderPayData data;
	public static class OrderPayData{
		public String order_sn;
		public String start_place;
		public String end_place;
		public String goods_name;
		public String order_price;
		public String reduce_price;
		public String total_price;
		public String notify_date;
		public String payurl;
	}
}
