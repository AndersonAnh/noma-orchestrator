package ru.vtb.msa.noma.orchestrator.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vtb.msa.noma.orchestrator.model.BicBankDto;

import java.io.IOException;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsr310Customizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

    @Bean
    public Module bicBankDtoSerializer() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BicBankDto.class, new JsonSerializer<>() {
            @Override
            public void serialize(BicBankDto value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();

                // ПРАВИЛЬНЫЙ способ - используем writeString, но предварительно очищаем данные
                String cleanBankName = cleanJsonString(value.bankName());
                gen.writeStringField("bankName", cleanBankName);

                gen.writeStringField("BIC", value.BIC());

                if (value.account() != null) {
                    gen.writeStringField("account", value.account());
                } else {
                    gen.writeNullField("account");
                }
                gen.writeEndObject();
            }

            private String cleanJsonString(String input) {
                if (input == null) return null;

                return input
                        .replace("\"", "")          // Удаляем двойные кавычки
                        .replace("&quot;", "")      // Удаляем HTML entity кавычки
                        .replace("&#34;", "")       // Удаляем numeric entity кавычки
                        .replace("«", "")           // Удаляем открывающие елочки
                        .replace("»", "")           // Удаляем закрывающие елочки
                        .replace("&laquo;", "")     // Удаляем HTML entity открывающие
                        .replace("&raquo;", "")     // Удаляем HTML entity закрывающие
                        .trim();                    // Убираем лишние пробелы
            }
        });
        return module;
    }
}
