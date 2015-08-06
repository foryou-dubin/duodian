package com.foryou.truck.net;

import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.SharePerfenceUtil;

public class MLHttpConnect {

	/***
	 * 如果不需要读取sd卡缓存 可以添加一个参数 parmas.put(MLHttpConnect2.USE_CASH, "false");
	 * 
	 * @param context
	 * @param parmas
	 * @param jsonParser
	 * @param handler
	 */

	public static void getAreasData(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + "/common/region";
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}
	
	public static Message getAreasData2(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + "/common/region";
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}

	public static void getConfigData(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + "/common/config";
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}
	
	public static Message getConfigData2(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + "/common/config";
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}

	public static void GetYanzhengma(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + "/common/verify_code";
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}

	public static void GetOrderList(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/list";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}
	
	public static Message GetOrderList2(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/list";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}

	public static void GetOrderDetail(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/detail";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}
	
	public static Message GetOrderDetail2(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/detail";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}

	public static void QuoteList(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/list";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}
	
	public static Message QuoteList2(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/list";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}
	
	

	public static Message QuoteAgentList(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/agentList";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}
	
	public static Message GetAgreeMent(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/agreement";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}
	
	public static void OrderQuoteList(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/quoteList";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}

	public static void GetAboutUs(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + "/common/about";
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}

	public static void GetContract(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + "/common/contract";
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}

	public static void BindGt(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/bindGt";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void GetUserDetail(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/detail";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getData(context, url, parmas, jsonParser, handler);
	}
	
	public static Message GetUserDetail2(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/detail";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getData2(context, url, parmas, jsonParser);
	}

	public static void QuoteCreate(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/create";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void QuoteConfirm(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/confirm";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}
	
	public static Message QuoteConfirm2(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/confirm";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}

	public static void QuoteChoice(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/choice";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void VerifyYanzhengma(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/forget";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void registerAccount(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/register";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void LoginAccount(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/login";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}
	
	public static Message QuoteReopen(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/quote/reopen";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static void updateUserInfo(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/update";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void UserForGet(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/forget";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void ResetPassword(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser,
			Handler handler) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/reset";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}

	public static void GiveAdvice(Context context, Map<String, String> parmas,
			BaseJsonParser jsonParser, Handler handler) {
		String url = MLHttpConstant.URL_START + "/common/advise";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		MLHttpConnect2.getHttpPostData(context, url, parmas, jsonParser,
				handler);
	}
	
	public static Message CommentAgentList(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/comment/agentList";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message GetOrderPay(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/pay";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message GetReOrder(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/reOrder";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message CuiChuArrayDriver(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/notifyArrange";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message GetCommentContent(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/comment/getComment";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message CommentAdd(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/comment/add";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message RefreshLoc(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/order/locate";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
	
	public static Message getContract(Context context,
			Map<String, String> parmas, BaseJsonParser jsonParser) {
		String url = MLHttpConstant.URL_START + Constant.SERVER_VERSION
				+ "/user/contact";
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		return MLHttpConnect2.getHttpPostData2(context, url, parmas, jsonParser);
	}
}
