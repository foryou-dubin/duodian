package com.foryou.truck;

import java.io.Serializable;

public class FirstTabInitValue implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//public String order_type;  //运单类型 1 为普通客户新增,2 为合同客户新增
	public String sender_regions_id; //省市区,逗号分割(如 3,2,1)
	public String sender_regions_text;
	public String receiver_regions_id;
	public String receiver_regions_text;
	public String goods_loadtime;
	public String goods_loadtime_desc;
	public String goods_unloadtime;
	public String goods_unloadtime_desc;
	public String goods_name;
	public String goods_cubage;
	public String goods_weight;
	public String car_model;
	public String car_model_text;
	public String car_length;
	public String car_length_text;
	public String pay_type;
	public String pay_type_text;
	public String receipt;  //是否开票
	public String car_type; //运输类型
	public String occupy_length; 
	public String remark;
	public String expect_price;
	public String need_back;
}
