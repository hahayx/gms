package com.hh.spd;

import com.hh.common.data.MapData;
import com.hh.common.utils.ELUtil;
import com.hh.common.utils.WebUtil;

public class SmallAppDataTranform {

	public static MapData tranformByJson(String json){
		MapData jsonObj = WebUtil.unserialize(json);
		return tranformByJsonObj(jsonObj);
	}
	
	
	public static MapData tranformByJsonObj(MapData jsonObj){
		MapData data = new MapData();
		data.put("id", jsonObj.getInt("id"));
		data.put("name", jsonObj.getString("name"));
		data.put("created_by", jsonObj.getString("created_by"));
		data.put("description", jsonObj.getString("description"));
		data.put("status", jsonObj.getString("status"));
		data.put("url", jsonObj.getString("url"));
		data.put("created_at", jsonObj.getlong("created_at")*1000);
		data.put("qrcode", ELUtil.getValue(jsonObj, "qrcode.image"));
		data.put("icon", ELUtil.getValue(jsonObj, "icon.image"));
		
		data.put("tag", jsonObj);
		data.put("screenshot", jsonObj);
		
		
		return null;
	}
}
