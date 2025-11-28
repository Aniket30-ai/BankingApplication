package com.nihilent.bank.utility;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	@Pointcut("execution(* com.nihilent.bankingApplication.service.*.*(..))")
	public void allServiceMethods() {
	}

	@Before("allServiceMethods()")
	public void logBefore(JoinPoint joinPoint) {
		logger.info("Before method: {}", joinPoint.getSignature().getName());
	}

	@After("allServiceMethods()")
	public void logAfter(JoinPoint joinPoint) {
		logger.info("After method: {}", joinPoint.getSignature().getName());
	}

	@AfterReturning(pointcut = "allServiceMethods()", returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		logger.info("Method returned: {}", result);
	}

	@AfterThrowing(pointcut = "allServiceMethods()", throwing = "error")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
		logger.error("Method threw exception", error);
	}

	@Around("allServiceMethods()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info("Before and after method: {}", joinPoint.getSignature().getName());
		return joinPoint.proceed();
	}
}
