package ru.vtb.msa.noma.orchestrator.config.retry;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/*
maxAttempts — максимальное число попыток выполнить операцию, включая первую (например, 3 = 1 первичный вызов + 2 повтора).
initialInterval — задержка перед первой повторной попыткой в миллисекундах (старт бэкоффа).
multiplier — коэффициент экспоненциального роста задержки между повторами: nextDelay = prevDelay * multiplier. Если 1.0 — задержка постоянная; если >1 — растёт.
maxInterval — верхняя граница задержки между повторами (мс). Даже при росте по multiplier задержка не превысит этого значения.
retryableExceptions — список полных имён классов исключений (FQCN), при которых разрешён ретрай (например, java.net.SocketTimeoutException).
connectTimeout — таймаут установления соединения (мс). Сколько ждём, пока сокет/HTTP соединение установится.
readTimeout — таймаут чтения ответа (мс). Сколько ждём данные после успешного соединения.
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "integration.retry.config")
public class IntegrationRetryProperties {
    private int maxAttempts = 3;
    private long initialInterval = 2000L;
    private double multiplier = 2.0;
    private long maxInterval = 10000L;
    private List<String> retryableExceptions;
    private int connectTimeOut = 2000;
    private int ReadTimeOut = 5000;
}
