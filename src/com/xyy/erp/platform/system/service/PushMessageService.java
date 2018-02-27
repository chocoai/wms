package com.xyy.erp.platform.system.service;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.AbstractTemplate;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.NotyPopLoadTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.jfinal.log.Log;
import com.xyy.erp.platform.common.tools.DictKeys;

/**
 * 消息推送服务
 * 
 * @author caofei
 *
 */
public class PushMessageService {

	private static final Log LOG = Log.getLog(PushMessageService.class);

	private static final String appId = DictKeys.app_id;

	private static final String appKey = DictKeys.app_key;

	private static final String masterSecret = DictKeys.master_secret;

	/**
	 * 推送消息统一入口
	 * @param CID
	 * @param content
	 */
	public void pushMessage(String CID, String title, String notify, String transmission) {
		IGtPush push = new IGtPush(appKey, masterSecret, true);
		AbstractTemplate template = buildNotificationTemplate(title, notify, transmission);
		SingleMessage message = new SingleMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 3600 * 1000);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(CID);
		// target.setAlias(Alias);
		IPushResult ret = null;
		try {
			ret = push.pushMessageToSingle(message, target);
		} catch (RequestException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			ret = push.pushMessageToSingle(message, target, e.getRequestId());
		}
		if (ret != null) {
			System.out.println(ret.getResponse().toString());
		} else {
			System.out.println("服务器响应异常");
		}
	}

	private static LinkTemplate buildLinkTemplate(String appId, String appKey) {
		LinkTemplate template = new LinkTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		Style0 style = new Style0();
		// 设置通知栏标题与内容
		style.setTitle("请输入通知栏标题");
		style.setText("请输入通知栏内容");
		// 配置通知栏图标
		style.setLogo("icon.png");
		// 配置通知栏网络图标
		style.setLogoUrl("");
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		template.setStyle(style);
		// 设置打开的网址地址
		template.setUrl("http://www.ybm100.com");
		return template;
	}

	public static NotificationTemplate buildNotificationTemplate(String title, String notify, String transmission) {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);

		/** Style0系统样式 */
		Style0 style = new Style0();
		// 设置通知栏标题与内容
		style.setTitle("请输入通知栏标题");
		style.setText("请输入通知栏内容");
		// 配置通知栏图标
		style.setLogo("icon.png");
		// 配置通知栏网络图标
		style.setLogoUrl("");
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		template.setStyle(style);

		/** Style1个推样式 */
		/*
		 * Style1 style = new Style1(); // 设置通知栏标题与内容
		 * style.setTitle("请输入通知栏标题"); style.setText("请输入通知栏内容"); // 配置通知栏图标
		 * style.setLogo("icon.png"); // 配置通知栏网络图标 style.setLogoUrl(""); //
		 * 设置通知是否响铃，震动，或者可清除 style.setRing(true); style.setVibrate(true);
		 * style.setClearable(true); template.setStyle(style);
		 */
		/** Style4背景图样式 */

		/*
		 * Style4 style = new Style4(); style.setLogo("icon.png");
		 * style.setBanner_url(
		 * "http://www.getui.com/picture/2017/2/9/1b72c364c34544679859eff3bad3bcf9.jpg"
		 * ); template.setStyle(style);
		 */

		/** Style6展开式通知样式,setBigStyle1/setBigStyle2/setBigStyle3 三种方式选一种 */

		/*
		 * Style6 style = new Style6(); style.setTitle("title1");
		 * style.setText("text2"); switch (1) { case 1: style.setBigStyle1(
		 * "http://www.getui.com/picture/2017/2/9/1b72c364c34544679859eff3bad3bcf9.jpg"
		 * ); break; case 2: style.setBigStyle2("BigStyle2"); break;
		 * 
		 * case 3: style.setBigStyle3(
		 * "http://www.getui.com/picture/2017/2/9/1b72c364c34544679859eff3bad3bcf9.jpg",
		 * "http://www.getui.com/picture/2017/2/9/1b72c364c34544679859eff3bad3bcf9.jpg"
		 * ); break; } template.setStyle(style);
		 */

		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		template.setTransmissionContent(transmission);
		return template;
	}

	public static NotyPopLoadTemplate buildNotyPopLoadTemplate(String appId, String appKey) {
		NotyPopLoadTemplate template = new NotyPopLoadTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 设置通知栏标题与内容
		template.setNotyTitle("请输入通知栏标题");
		template.setNotyContent("请输入通知栏内容");
		// 配置通知栏图标
		template.setNotyIcon("icon.png");
		// 设置通知是否响铃，震动，或者可清除
		template.setBelled(true);
		template.setVibrationed(true);
		template.setCleared(true);
		// 设置弹框标题与内容
		template.setPopTitle("弹框标题");
		template.setPopContent("弹框内容");
		// 设置弹框显示的图片
		template.setPopImage("");
		template.setPopButton1("下载");
		template.setPopButton2("取消");
		// 设置下载标题
		template.setLoadTitle("下载标题");
		template.setLoadIcon("file://icon.png");
		// 设置下载地址
		template.setLoadUrl("http://gdown.baidu.com/data/wisegame/80bab73f82cc29bf/shoujibaidu_16788496.apk");
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
		return template;
	}

	public static TransmissionTemplate buildTransmissionTemplate() {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		template.setTransmissionContent("请输入需要透传的内容");
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
		return template;
	}

	
	/**
	 * IOSTemplate
	 * @return
	 */
	public static TransmissionTemplate getTemplate() {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setTransmissionContent("透传内容");
		template.setTransmissionType(2);
		APNPayload payload = new APNPayload();
		payload.setBadge(1);
		payload.setContentAvailable(1);
		payload.setSound("default");
		payload.setCategory("$由客户端定义");
		// 简单模式APNPayload.SimpleMsg
		payload.setAlertMsg(new APNPayload.SimpleAlertMsg("hello"));
		// 字典模式使用下者
		// payload.setAlertMsg(getDictionaryAlertMsg());
		template.setAPNInfo(payload);
		return template;
	}

	private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg() {
		APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
		alertMsg.setBody("body");
		alertMsg.setActionLocKey("ActionLockey");
		alertMsg.setLocKey("LocKey");
		alertMsg.addLocArg("loc-args");
		alertMsg.setLaunchImage("launch-image");
		// IOS8.2以上版本支持
		alertMsg.setTitle("Title");
		alertMsg.setTitleLocKey("TitleLocKey");
		alertMsg.addTitleLocArg("TitleLocArg");
		return alertMsg;
	}

}
