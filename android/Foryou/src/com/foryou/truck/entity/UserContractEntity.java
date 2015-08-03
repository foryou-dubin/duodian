package com.foryou.truck.entity;

import java.util.List;

//{
//"status": "Y",
//  "code": 200,
//  "msg": "OK",
//  "data": {
//"sender": [ {
//"mobile": "18701471798", "name": "啊啊好", "mobile2": "0", "hd_start_place": "2,52", "address": ""
//}, ],
//    "receiver": [
//      {
//"mobile": "18701471731", "name": "18701471731", "mobile2": "18701471731", "hd_end_place": "0,0", "address": "18701471731"
//}, ]
//}
public class UserContractEntity extends BaseEntity {
	public UserContractData data;

	public static class UserContractData {
		public List<SenderContraceInfo> sender;
		public List<ReceiverContraceInfo> receiver;
	}

	public static class SenderContraceInfo extends ContractInfo {
		public String hd_start_place;
	}

	public static class ReceiverContraceInfo extends ContractInfo {
		public String hd_end_place;
	}

	public static class ContractInfo {
		public String mobile;
		public String name;
		public String mobile2;
		public String address;
	}
}
