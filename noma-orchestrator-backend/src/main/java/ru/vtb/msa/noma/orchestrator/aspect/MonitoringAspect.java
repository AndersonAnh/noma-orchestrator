package ru.vtb.msa.noma.orchestrator.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.annotation.Monitor;
import ru.vtb.msa.noma.orchestrator.enums.MetricPostfix;

@Aspect
@Component
@RequiredArgsConstructor
public class MonitoringAspect {
    private final MeterRegistry meterRegistry;

    @Around("@annotation(monitor)")
    public Object monitor(final ProceedingJoinPoint joinPoint, Monitor monitor) throws Throwable {
        String metricName = monitor.metricName().getName();
        meterRegistry.counter(metricName).increment();
        try {
            Object result = joinPoint.proceed();
            meterRegistry.counter(metricName + MetricPostfix.SUCCESS.getPostfix()).increment();
            return result;
        } catch (Exception e) {
            meterRegistry.counter(metricName + MetricPostfix.ERROR.getPostfix()).increment();
            throw e;
        }
    }
}
