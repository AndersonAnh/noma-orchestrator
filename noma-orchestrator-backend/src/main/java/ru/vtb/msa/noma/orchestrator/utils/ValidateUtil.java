package ru.vtb.msa.noma.orchestrator.utils;

import lombok.experimental.UtilityClass;
import ru.vtb.msa.noma.orchestrator.exception.XRequestIdNotCorrectException;

import java.util.regex.Pattern;

@UtilityClass
public class ValidateUtil {

    /** Regex: разрешены 4–8 символов — латиница любого регистра и цифры. */
    private static final Pattern AUTH_TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9]{4,8}$");

    /** Проверка заголовка X-Request-Id на обязательность. */
    public void validateXRequestIdHeader(String xRequestId) {
        if (xRequestId == null || xRequestId.isBlank()) {
            throw new XRequestIdNotCorrectException("Заголовок X-Request-Id обязателен");
        }
    }

    /**
     * Проверка заголовка X_AUTH_TOKEN:
     * <ul>
     *   <li>обязателен и не пустой;</li>
     *   <li>длина токена ― от 4 до 8 символов;</li>
     *   <li>только латинские буквы и цифры.</li>
     * </ul>
     *
     * @param authorizationHeader значение заголовка X_AUTH_TOKEN
     * @throws IllegalArgumentException при любой ошибке валидации
     */
    public void validateAuthorizationHeader(String authorizationHeader) {

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Заголовок X_AUTH_TOKEN обязателен");
        }

        String token = authorizationHeader.trim();

        if (token.length() < 4 || token.length() > 8) {
            throw new IllegalArgumentException("Длина токена должна быть от 4 до 8 символов");
        }

        if (!AUTH_TOKEN_PATTERN.matcher(token).matches()) {
            throw new IllegalArgumentException("Токен должен содержать только латинские буквы и цифры");
        }
    }
}
