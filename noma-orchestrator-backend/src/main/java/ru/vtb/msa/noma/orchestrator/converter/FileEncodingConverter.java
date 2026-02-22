package ru.vtb.msa.noma.orchestrator.converter;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@UtilityClass
public class FileEncodingConverter {

    public static void convertFileToUtf8(String inputPath, String outputPath) throws IOException {
        Path inputFile = Paths.get(inputPath);
        Path outputFile = Paths.get(outputPath);

        // Читаем как WINDOWS-1251
        byte[] bytes = Files.readAllBytes(inputFile);
        String content = new String(bytes, Charset.forName("WINDOWS-1251"));

        // Заменяем объявление кодировки
        content = content.replace("encoding=\"WINDOWS-1251\"", "encoding=\"UTF-8\"");

        // Сохраняем как UTF-8
        Files.writeString(outputFile, content, StandardCharsets.UTF_8);

        log.info("Файл конвертирован: {} -> {}", inputPath, outputPath);
    }

    /*public static void main(String[] args) {
        try {
            convertFileToUtf8(
                    "D:/Софт/Брак/20251007_ED807_full.xml",  // ваш скачанный файл "D:\Софт\Брак\20251007_ED807_full.xml"
                    "D:/Софт/Брак/20251007_ED807_full_utf8.xml"  // конвертированный файл
            );
            System.out.println("Конвертация завершена успешно!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
     */
}
