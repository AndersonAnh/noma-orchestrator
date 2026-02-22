package ru.vtb.msa.noma.orchestrator.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.vtb.msa.noma.orchestrator.utils.SmartMaskUtil;

/**
 * HTTP Interceptor для логирования запросов/ответов с маскированием чувствительных данных.
 * Перехватывает все запросы к /api/** и логирует их с маскированием PII.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MaskingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";

    /**
     * Выполняется ДО обработки запроса контроллером.
     * Логирует входящий запрос с маскированными данными.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Сохраняем время начала для расчета длительности
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();

        // Маскируем query параметры если есть
        String maskedQuery = queryString != null ? maskQueryString(queryString) : "";
        String fullPath = maskedQuery.isEmpty() ? uri : uri + "?" + maskedQuery;

        log.info("→ HTTP {} {} | IP: {}", method, fullPath, remoteAddr);

        return true; // Продолжаем обработку запроса
    }

    /**
     * Выполняется ПОСЛЕ обработки запроса контроллером (до рендеринга view).
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Можно добавить логику обработки после контроллера, но до ответа
    }

    /**
     * Выполняется ПОСЛЕ полного завершения запроса (включая рендеринг).
     * Логирует результат и время выполнения.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        if (ex != null) {
            // Маскируем сообщение об ошибке
            String maskedError = maskSensitiveData(ex.getMessage());
            log.error("← HTTP {} {} | Status: {} | Время: {} ms | Ошибка: {}",
                    method, uri, status, duration, maskedError);
        } else {
            String statusCategory = getStatusCategory(status);
            log.info("← HTTP {} {} | Status: {} ({}) | Время: {} ms",
                    method, uri, status, statusCategory, duration);
        }
    }

    /**
     * Маскирует чувствительные данные в query string
     */
    private String maskQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return queryString;
        }

        StringBuilder masked = new StringBuilder();
        String[] params = queryString.split("&");

        for (int i = 0; i < params.length; i++) {
            String[] keyValue = params[i].split("=", 2);
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";

            // Маскируем значение по названию параметра
            String maskedValue = SmartMaskUtil.maskByFieldName(key, value);
            masked.append(key).append("=").append(maskedValue);

            if (i < params.length - 1) {
                masked.append("&");
            }
        }

        return masked.toString();
    }

    /**
     * Маскирует чувствительные данные в строке
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            String masked = data;
            masked = SmartMaskUtil.maskEmail(masked);
            masked = SmartMaskUtil.maskPhone(masked);
            masked = SmartMaskUtil.maskCreditCard(masked);
            masked = SmartMaskUtil.maskTaxId(masked);
            return masked;
        } catch (Exception e) {
            log.warn("Ошибка при маскировании: {}", e.getMessage());
            return "***";
        }
    }

    /**
     * Возвращает категорию HTTP статуса
     */
    private String getStatusCategory(int status) {
        if (status >= 200 && status < 300) {
            return "OK";
        } else if (status >= 300 && status < 400) {
            return "REDIRECT";
        } else if (status >= 400 && status < 500) {
            return "CLIENT_ERROR";
        } else if (status >= 500) {
            return "SERVER_ERROR";
        }
        return "UNKNOWN";
    }
}