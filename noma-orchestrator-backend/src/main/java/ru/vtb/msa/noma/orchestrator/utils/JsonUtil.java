package ru.vtb.msa.noma.orchestrator.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Набор имён JSON-полей, содержащих чувствительные данные.
     * Сравнение регистронезависимое.
     */
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "email", "phone", "taxid", "tax_id", "taxId",
            "creditcard", "creditCard", "credit_card",
            "passport", "password", "secret", "token"
    );

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static String toMaskedJson(Object obj) {
        try {
            JsonNode tree = objectMapper.valueToTree(obj);
            maskNode(tree);
            return objectMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("JSON masked serialization error", e);
        }
    }

    /**
     * Рекурсивно обходит JSON-дерево и маскирует значения чувствительных полей.
     */
    private static void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode value = entry.getValue();

                if (value.isTextual() && isSensitiveField(fieldName)) {
                    String masked = SmartMaskUtil.maskByFieldName(fieldName, value.asText());
                    objectNode.set(fieldName, new TextNode(masked));
                } else if (value.isObject() || value.isArray()) {
                    maskNode(value);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                maskNode(element);
            }
        }
    }

    private static boolean isSensitiveField(String fieldName) {
        String lower = fieldName.toLowerCase();
        return SENSITIVE_FIELDS.stream().anyMatch(s -> lower.contains(s.toLowerCase()));
    }
}
