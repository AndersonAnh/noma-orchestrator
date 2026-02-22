package ru.vtb.msa.noma.orchestrator.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.vtb.msa.noma.orchestrator.utils.SmartMaskUtil;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * ru.vtb.msa.noma.orchestrator.controller.*.*(..))")
    public void controllerLog() {
    }

    @Pointcut("execution(public * ru.vtb.msa.noma.orchestrator.service.*.*(..))")
    public void serviceLog() {
    }

    @Before("controllerLog()")
    public void logController(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        if (request != null) {
            String maskedUri = request.getRequestURI();
            log.info(
                    "IP: {}, http метод: {}, URI: {}, контроллер: {}.{}",
                    request.getRemoteAddr(),
                    request.getMethod(),
                    maskedUri,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName()
            );
        }
    }

    @Around("controllerLog()")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = proceedingJoinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        String methodName = proceedingJoinPoint.getSignature().getName();
        log.info("Метод: {}.{}, время выполнения: {} ms",
                proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                methodName,
                executionTime
        );
        return proceed;
    }

    @Around("serviceLog()")
    public Object logServiceExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        Object[] args = proceedingJoinPoint.getArgs();

        // Логируем маскированные аргументы
        if (args.length > 0) {
            String maskedArgs = maskArguments(args);
            log.debug("Вызов сервиса: {}.{}({})",
                    proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                    methodName,
                    maskedArgs);
        }

        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.debug("Сервис: {}.{} выполнен за {} ms",
                proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                methodName,
                executionTime);

        return result;
    }

    private String maskArguments(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                String argStr = args[i].toString();
                String masked = SmartMaskUtil.maskEmail(argStr);
                masked = SmartMaskUtil.maskPhone(masked);
                masked = SmartMaskUtil.maskCreditCard(masked);
                masked = SmartMaskUtil.maskTaxId(masked);
                sb.append(masked);
            }
            if (i < args.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}