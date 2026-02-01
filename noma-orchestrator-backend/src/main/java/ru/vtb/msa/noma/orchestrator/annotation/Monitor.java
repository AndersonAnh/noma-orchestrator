package ru.vtb.msa.noma.orchestrator.annotation;


import ru.vtb.msa.noma.orchestrator.enums.MetricName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Monitor {
    MetricName metricName ();
}
