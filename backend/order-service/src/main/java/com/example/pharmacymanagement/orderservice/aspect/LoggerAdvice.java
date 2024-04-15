package com.example.pharmacymanagement.orderservice.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class LoggerAdvice {
    Logger logger = LoggerFactory.getLogger(LoggerAdvice.class);

    @Pointcut("execution(* com.example.pharmacymanagement.orderservice.controller.*.*(..))")
    public void controllerPointcut() {
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "exception")
    public void logException(ResponseStatusException exception) {
        if (exception.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            logger.error(exception.getMessage());
        }
    }
}