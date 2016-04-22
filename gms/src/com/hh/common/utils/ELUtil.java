package com.hh.common.utils;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.VariableMapper;

import org.apache.el.lang.EvaluationContext;
import org.apache.el.lang.ExpressionBuilder;


/**
 * el取值:
 * 实现:jasper-el.jar,el-api.jar实际上这些jar包tomcat本身都会有不需要依赖第三方包
 * @author huangyongsheng
 *
 */
public class ELUtil {
	public static void main(String[] args) {
		/*
		Recomment recomment=new Recomment();
		recomment.push(Recomment.BusinessName, "sadfasdf");
		recomment.push(Recomment.Id,1111);
		MapData m=new MapData();
		m.put("aa", new int[]{1,2134214});
		recomment.pushContent("title", m);
		System.out.println("普通的el导航1："+getValue(recomment, "content.title"));
		System.out.println("普通的el导航1："+getValue(recomment, "content.title.aa[0]"));
		System.out.println("普通的el导航2："+getValue(recomment, "d1content.aaa"));
		System.out.println("普通的el导航3："+getValue(recomment,Recomment.BusinessName));
		System.out.println("普通的el导航4："+getValue(new MapData().set("name", "n1111"),"name"));
		System.out.println("普通的el导航5："+getValue(recomment,"1+333"));
		System.out.println("普通的el导航6："+getValueAsJsp(new MapData().set("name", "n1111"),"<-3${name}->"));
		*/
	}
	
	public static Object getValue(Object base,String field) {
		return getValueAsJsp(base,genElExp(field));
	}
	public static String genElExp(String field) {
		return "${".concat(field).concat("}");
	}
	////像jsp一样取法
	public static Object getValueAsJsp(Object base,String exp) {
		try {
			EvaluationContext ctx = new EvaluationContext(PjaELContext.getInstance(base), null, null);
			return ExpressionBuilder.createNode(exp).getValue(ctx);
		} catch (Exception e) {
			return null;
		}
	}

	private final static class PjaELContext extends ELContext {
		private static ELResolverImp resolver;
		private Object base;
		private static class ELResolverImp extends CompositeELResolver {
			public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
				if (base == null) {
					PjaELContext myContext = (PjaELContext) context.getContext(PjaELContext.class);
					base = myContext.getBase();
				}
				return super.getValue(context, base, property);
			}
		}
		public static PjaELContext getInstance(Object base) {
			if (resolver == null) {
				synchronized (PjaELContext.class) {
					if (resolver == null) {
						resolver = new ELResolverImp();
						resolver.add(new MapELResolver());
						resolver.add(new ListELResolver());
						resolver.add(new ArrayELResolver());
						resolver.add(new BeanELResolver());
					}
				}
			}
			PjaELContext instance = new PjaELContext();
			instance.base = base;
			instance.putContext(PjaELContext.class, instance);
			return instance;
		}

		@Override
		public ELResolver getELResolver() {
			return resolver;
		}
		public FunctionMapper getFunctionMapper() {
			return null;
		}
		public VariableMapper getVariableMapper() {
			return null;
		}
		public Object getBase() {
			return base;
		}
	}


}
