package com.hh.common.spring;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.hh.common.utils.WebUtil;


public class JsonPView extends MappingJacksonJsonView{
	protected void renderMergedOutputModel(Map<String, Object> d, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String, Object> result=new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : d.entrySet()) {
			Object value = entry.getValue();
			if (!(value instanceof BindingResult)) {
				value=transformIntMapToStringMap(value);
				result.put(entry.getKey(), value);
			}
				
		}
		String outPutStr=JSONObject.fromObject(result).toString();
		String callback=req.getParameter("callback");
		if (callback != null) {
			outPutStr=String.format("%s(%s)",callback,outPutStr);
		}
		res.setContentType("application/javascript;charset=UTF-8");
		res.getWriter().print(outPutStr);
	}
	
	///如果是Map<Integer,?>,则转换为Map<String,?>
	@SuppressWarnings("unchecked")
	private static Object transformIntMapToStringMap(Object value) {
		if(value instanceof Map){
			Map<Object, Object> m = (Map<Object, Object>) value;
			if(m.keySet().isEmpty()){
				return null;
			}
			if(m.keySet().iterator().next() instanceof Integer){
				return WebUtil.unserialize(WebUtil.serialize(value));
			}
		}
		return value;
	}

}
