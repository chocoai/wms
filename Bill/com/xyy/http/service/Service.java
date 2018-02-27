package com.xyy.http.service;

import com.xyy.http.util.Envelop;

/**
 * 服务接口,所有的服务必须实现这个接口
 * 
 * @author evan
 *
 */
public interface Service {
	public Envelop service(Envelop request) throws Exception;// 服务处理器

}
