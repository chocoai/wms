package com.xyy.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;



public class HttpUtil {
	

	public static String doHttp(String urlStr, Object paraBean)
			throws Exception {
		String charSet = "UTF-8";
		String timeOut = "200000";
		return doHttp(urlStr, charSet, paraBean, timeOut);
	}

	public static String doHttp(String urlStr, Map<String, Object> params)
			throws Exception {
		String charSet = "UTF-8";
		String timeOut = "200000";
		return doHttp(urlStr, charSet, params, timeOut);
	}

	/**
	 * 发送HTTP请示
	 * 
	 * @param urlStr
	 *            ：请求URL
	 * @param charSet
	 *            :编码方式
	 * @param parameters
	 *            :javaBean参数对象，会利用其中的getter方式获取值
	 * @param timeOut
	 *            :超时
	 * @return
	 * @throws Exception
	 */
	private static String doHttp(String urlStr, String charSet, Object paraBean,
			String timeOut) throws Exception {
		String responseString = null;
		int statusCode = 0;
		HttpClient httpclient = new HttpClient();
		httpclient.setConnectionTimeout(new Integer(timeOut).intValue());
		PostMethod postMethod = new PostMethod(urlStr);
		httpclient.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, charSet);
		InputStream is=null;
		InputStreamReader isReader=null;
		BufferedReader reader=null;
		
		try {
			// 组合请求参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Method[] ms = paraBean.getClass().getMethods();
			for (int i = 0; i < ms.length; i++) {
				Method m = ms[i];
				String name = m.getName();
				if (name.startsWith("get")) {
					String param = name.substring(3, name.length());
					param = param.substring(0, 1).toLowerCase()
							+ param.substring(1, param.length());
					if (param.equals("class")) {
						continue;
					}
					Object value = "";
					try {
						value = m.invoke(paraBean, null);
						NameValuePair nvp = new NameValuePair(param,
								value.toString());
						list.add(nvp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw e;
					}
				}
			}
			NameValuePair[] nvps = new NameValuePair[list.size()];
			postMethod.setRequestBody(list.toArray(nvps));
			statusCode = httpclient.executeMethod(postMethod);
			
			
			//responseString = postMethod.getResponseBodyAsString();
			is=postMethod.getResponseBodyAsStream();
			isReader=new InputStreamReader(is);
			reader = new BufferedReader(isReader);
			StringBuffer stringBuffer = new StringBuffer();
			String str = "";
			
			while((str = reader.readLine())!=null){
				stringBuffer.append(str);
			}
			
			responseString = stringBuffer.toString();
			
			if (statusCode < HttpURLConnection.HTTP_OK
					|| statusCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
				System.err.println("失败返回码[" + statusCode + "]");
				throw new Exception("请求接口失败，失败码[ " + statusCode + " ]");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.toString());
			throw e;
		}finally{
			try {
				if(is!=null){
					is.close();
				}
				if(isReader!=null){
					isReader.close();
				}
				if(reader!=null){
					reader.close();
				}
			} catch (Exception e2) {
				
			}
		}
		return responseString;
	}

	/**
	 * 发送HTTP请示
	 * 
	 * @param urlStr
	 *            ：请求URL
	 * @param charSet
	 *            :编码方式
	 * @param parameters
	 *            :javaBean参数对象，会利用其中的getter方式获取值
	 * @param timeOut
	 *            :超时
	 * @return
	 * @throws Exception
	 */
	private static String doHttp(String urlStr, String charSet,
			Map<String, Object> params, String timeOut) throws Exception {
		String responseString = null;
		int statusCode = 0;
		HttpClient httpclient = new HttpClient();
		httpclient.setConnectionTimeout(new Integer(timeOut).intValue());
		PostMethod postMethod = new PostMethod(urlStr);
		httpclient.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, charSet);
		BufferedReader reader=null;
		InputStreamReader isReader=null;
		InputStream is=null;
		
		try {
			// 组合请求参数
			List<NameValuePair> list = convertToNameValuePairs(params);
			NameValuePair[] nvps = new NameValuePair[list.size()];
			postMethod.setRequestBody(list.toArray(nvps));
			statusCode = httpclient.executeMethod(postMethod);
			//responseString = postMethod.getResponseBodyAsString();
			is=postMethod.getResponseBodyAsStream();
			isReader=new InputStreamReader(is);
			reader = new BufferedReader(isReader);
			StringBuffer stringBuffer = new StringBuffer();
			String str = "";
			while((str = reader.readLine())!=null){
				stringBuffer.append(str);
			}
			responseString = stringBuffer.toString();
			
			if (statusCode < HttpURLConnection.HTTP_OK
					|| statusCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
				System.err.println("失败返回码[" + statusCode + "]");
				throw new Exception("请求接口失败，失败码[ " + statusCode + " ]");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.toString());
			throw e;
		}
		finally{//释放流资源
			try {
				if(is!=null){
					is.close();
				}
				if(isReader!=null){
					isReader.close();
				}
				if(reader!=null){
					reader.close();
				}
			} catch (Exception e2) {
				
			}
		}
		return responseString;
	}

	private static List<NameValuePair> convertToNameValuePairs(
			Map<String, Object> maps) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();
		for (String name : maps.keySet()) {
			result.add(new NameValuePair(name, maps.get(name).toString()));
		}

		return result;
	}

}
