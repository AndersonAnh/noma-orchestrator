package ru.vtb.msa.noma.orchestrator.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.Pattern;

/**
 * SmartMask утилита для маскирования чувствительных данных
 */
@UtilityClass
@Slf4j
public class SmartMaskUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    private static final Pattern PHONE_RU_PATTERN =
            Pattern.compile("(\\+?7|8)?[\\s\\-]?(\\(?\\d{3}\\)?)?[\\s\\-]?(\\d{3})[\\s\\-]?(\\d{2})[\\s\\-]?(\\d{2})");

    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("\\b(?:\\d{4}[\\s\\-]?){3}\\d{4}\\b");

    private static final Pattern PASSPORT_RU_PATTERN =
            Pattern.compile("\\b\\d{2}\\s\\d{2}\\s\\d{6}\\b");

    private static final Pattern TAX_ID_PATTERN =
            Pattern.compile("\\b\\d{10}(?:\\d{2})?\\b");

    /**
     * Маскирует email: john.doe@example.com → j***e@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }

        var matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.find()) {
            String local = matcher.group(1);
            String domain = matcher.group(2);

            if (local.length() <= 2) {
                return "*".repeat(local.length()) + "@" + domain;
            }
            return local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1) + "@" + domain;
        }
        return email;
    }

    /**
     * Маскирует телефон: +7-999-123-45-67 → +7-***-***-45-67
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }

        String digits = phone.replaceAll("\\D", "");
        if (digits.length() >= 10) {
            return digits.substring(0, 3) + "*".repeat(digits.length() - 5) + digits.substring(digits.length() - 2);
        }
        return phone;
    }

    /**
     * Маскирует карту: 4532-1234-5678-9010 → ****-****-****-9010
     */
    public static String maskCreditCard(String card) {
        if (card == null || card.isEmpty()) {
            return card;
        }

        String digits = card.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            String masked = "*".repeat(digits.length() - 4) + digits.substring(digits.length() - 4);
            if (card.contains("-")) {
                return masked.replaceAll("(.{4})", "$1-").replaceAll("-$", "");
            }
            return masked;
        }
        return card;
    }

    /**
     * Маскирует паспорт: 12 34 567890 → 12 ** 56****
     */
    public static String maskPassport(String passport) {
        if (passport == null || passport.isEmpty()) {
            return passport;
        }

        String clean = passport.replace(" ", "");
        if (clean.length() >= 8) {
            return clean.substring(0, 2) + "**" + clean.substring(4, 6) + "****";
        }
        return passport;
    }

    /**
     * Маскирует ИНН: 123456789012 → 1234****9012
     */
    public static String maskTaxId(String taxId) {
        if (taxId == null || taxId.isEmpty()) {
            return taxId;
        }

        if (taxId.length() >= 8) {
            return taxId.substring(0, 4) + "****" + taxId.substring(taxId.length() - 4);
        }
        return taxId;
    }

    /**
     * Маскирует UUID: 123e4567-e89b-12d3-a456-426614174000 → 123e****-****-****-****-426614174000
     */
    public static String maskUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return uuid;
        }

        if (uuid.length() >= 36) {
            return uuid.substring(0, 4) + "****-****-****-****-" + uuid.substring(uuid.length() - 12);
        }
        return uuid;
    }

    /**
     * Маскирует по названию поля
     */
    public static String maskByFieldName(String fieldName, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String fieldLower = fieldName.toLowerCase();

        if (fieldLower.contains("email")) {
            return maskEmail(value);
        } else if (fieldLower.contains("phone")) {
            return maskPhone(value);
        } else if (fieldLower.contains("card") || fieldLower.contains("credit")) {
            return maskCreditCard(value);
        } else if (fieldLower.contains("passport")) {
            return maskPassport(value);
        } else if (fieldLower.contains("tax") || fieldLower.contains("inn")) {
            return maskTaxId(value);
        } else if (fieldLower.contains("password") || fieldLower.contains("secret") || fieldLower.contains("token")) {
            return "***";
        }

        return value;
    }
}