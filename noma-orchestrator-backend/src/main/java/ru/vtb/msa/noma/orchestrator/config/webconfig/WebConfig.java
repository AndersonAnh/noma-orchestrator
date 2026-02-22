package ru.vtb.msa.noma.orchestrator.config.webconfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.vtb.msa.noma.orchestrator.interceptor.MaskingInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MaskingInterceptor maskingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maskingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/metrics");
    }
}