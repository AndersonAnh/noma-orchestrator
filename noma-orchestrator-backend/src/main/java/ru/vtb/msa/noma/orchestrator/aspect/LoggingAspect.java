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

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    /**
     * pointcut -определяет, где именно срабатывает аспект, по пути- ru.vtb.msa.noma.orchestrator.controller
     * execution - ключевое слово,определяющее что аспект должен применятся к методам, соответствующим шаблоном в скобках,
     * public на все публичные методы 1я * означает что возвращаемое значение метода может быть любое,
     * ru.vtb.msa.noma.orchestrator.controller - пакет означающий где находится контролеры
     * 2я * - любой класс в пакете включает этот метод
     * 3я * - любой метод в любом классе попадает под воздействие этой точки пересечения
     * (..) - метод может принимать любое количество параметров
     */
    @Pointcut("execution(public * ru.vtb.msa.noma.orchestrator.controller.*.*(..))")
    public void controllerLog() {
    }

    @Pointcut("execution(public * ru.vtb.msa.noma.orchestrator.service.*.*(..))")
    public void serviceLog() {
    }

    /**
     * @param joinPoint - содержит информацию о текущем методе
     */
    @Before("controllerLog()")
    public void logController(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        if (request != null) {
            log.info(
                    "IP: {}, http метод: {}, метод контроллера {},{}",
                    request.getRemoteAddr(),
                    request.getRequestURI(),
                    request.getMethod(),
                    joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()
            );
        }
    }

    /**
     * @param proceedingJoinPoint - в себе содержит метод который мы выполняем,и в нем содержится сам метод
     *                            proceed - выполняет целевой метод
     */
    @Around("controllerLog()")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = proceedingJoinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("Выполняемый метод: {}, {}, время выполнения: {}",
                proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName(),
                executionTime
        );
        return proceed;
    }

}
