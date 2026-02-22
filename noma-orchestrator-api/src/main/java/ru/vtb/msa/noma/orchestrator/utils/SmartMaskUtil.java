package ru.vtb.msa.noma.orchestrator.utils;

import java.util.regex.Pattern;

/**
 * Утилита для маскирования чувствительных данных.
 * Размещена в API модуле для использования в DTO.
 */
public final class SmartMaskUtil {

    private SmartMaskUtil() {
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

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

    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() >= 10) {
            return digits.substring(0, 2) + "*".repeat(digits.length() - 6) + digits.substring(digits.length() - 4);
        }
        return phone;
    }

    public static String maskTaxId(String taxId) {
        if (taxId == null || taxId.isEmpty()) {
            return taxId;
        }
        if (taxId.length() >= 8) {
            return taxId.substring(0, 4) + "****" + taxId.substring(taxId.length() - 4);
        }
        return taxId;
    }

    public static String maskCreditCard(String card) {
        if (card == null || card.isEmpty()) {
            return card;
        }
        String digits = card.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            return "*".repeat(digits.length() - 4) + digits.substring(digits.length() - 4);
        }
        return card;
    }

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

    public static String maskUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return uuid;
        }
        if (uuid.length() >= 36) {
            return uuid.substring(0, 4) + "****-****-****-****-*******" + uuid.substring(uuid.length() - 5);
        }
        return uuid;
    }

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