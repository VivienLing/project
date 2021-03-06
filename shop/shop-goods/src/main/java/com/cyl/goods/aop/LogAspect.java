package com.cyl.goods.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.cyl.common.annotation.log.Log;
import com.cyl.common.enums.LogLevel;

import static com.cyl.common.util.MethodUtil.getMethod;

import java.lang.reflect.Method;

/**
*@author 25280
*@date 2019年5月6日
*@time 下午11:41:25
*/
@Order(2)
@Component
public class LogAspect {

	private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
	
	@Pointcut(value="execution(public * com.cyl.*.controller.*.*(..))"
	                +"execution(public * com.cyl.*.service.*.*(..))")
	public void pointcut() {}
	
	@Before(value="pointcut()")
	public void before(JoinPoint jp) {
		Method method = getMethod(jp);
		log(method,LogLevel.INFO,"execute:"+method.getDeclaringClass().getName()+"."+method.getName());
	}
	
	@After(value="pointcut()")
	public void after(JoinPoint jp) {
		Method method = getMethod(jp);
		log(method,LogLevel.INFO,"finished:"+method.getDeclaringClass().getName()+"."+method.getName());
	}
	
	@AfterThrowing(pointcut="pointcut()",throwing="ex")
	public void afterThrowing(JoinPoint jp,Throwable ex) {
		Method method = getMethod(jp);
		log(method,LogLevel.ERROR,"error:"+method.getDeclaringClass().getName()+"."+method.getName()+JSON.toJSONString(jp.getArgs()));
	}
	
	private void log(Method method,LogLevel level,String msg) {
		
		if(!method.isAnnotationPresent(Log.class))
			return;
		Log methodLog = method.getAnnotation(Log.class);
		if(isLog(methodLog,level)) {
			try {
				Method logMethod = log.getClass().getMethod(level.level(), new Class<?>[]{String.class});
				logMethod.invoke(log, msg);
			} catch (Exception e) {}
		}
	}
	
	private boolean isLog(Log log,LogLevel level) {
		return log.level().value()-level.value()<=0;
	}
}
