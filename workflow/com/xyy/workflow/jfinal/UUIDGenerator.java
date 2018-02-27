package com.xyy.workflow.jfinal;

import java.io.Serializable;

import com.jfinal.plugin.activerecord.entity.IdentifierGenerator;
import com.xyy.util.UUIDUtil;
//com.xyy.workflow.jfinal.UUIDGenerator
public class UUIDGenerator implements IdentifierGenerator {
	@Override
	public Serializable generate() {
		return UUIDUtil.newUUID();
	}

}
