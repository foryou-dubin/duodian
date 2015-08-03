package com.foryou.truck.entity;

import java.util.List;

public class OrderListEntity extends BaseEntity {
	public OrderList data;
	public static class OrderList {
		public int total;
		public List<OrderContent> list;
	}

	public static class OrderContent {
		public int id;
		public String order_sn;
		public OrderStatus status;
		public String goods_name;
		public String goods_weight;
		public String goods_cubage;
		public String start_place;
		public String end_place;
		public String goods_loadtime;
		public String goods_loadtime_desc;
		public String agent_name;
		public String agent_mobile;
		public String driver_name;
		public String driver_mobile;
	}
}
