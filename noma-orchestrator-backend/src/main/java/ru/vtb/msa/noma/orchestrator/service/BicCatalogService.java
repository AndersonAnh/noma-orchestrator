package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.vtb.msa.noma.orchestrator.model.AccountTransferTypeDto;
import ru.vtb.msa.noma.orchestrator.model.BicBankDto;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BicCatalogService {

    public List<BicBankDto> parseBanks(String bicCatalogXml) {
        List<BicBankDto> banks = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new java.io.StringReader(bicCatalogXml)));
            NodeList bicEntries = document.getElementsByTagName("BICDirectoryEntry");

            for (int i = 0; i < bicEntries.getLength(); i++) {
                Element bicElement = (Element) bicEntries.item(i);
                String bic = bicElement.getAttribute("BIC");
                String bankName = null;
                String account = null;

                NodeList piList = bicElement.getElementsByTagName("ParticipantInfo");
                if (piList.getLength() > 0) {
                    Element piElem = (Element) piList.item(0);
                    // Очищаем название банка от лишних экранирований
                    bankName = piElem.getAttribute("NameP");
                }

                NodeList accountsList = bicElement.getElementsByTagName("Accounts");
                if (accountsList.getLength() > 0) {
                    Element accElem = (Element) accountsList.item(0);
                    account = accElem.getAttribute("Account");
                }

                banks.add(new BicBankDto(bankName, bic, account));
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка разбора BIC XML каталога", e);
        }
        return banks;
    }

    /**
     * Очищает название банка от лишних экранирований XML
     */
/**  private String cleanBankName(String rawName) {
        if (rawName == null) return null;

        log.info("ДО очистки: '{}'", rawName);

        String cleaned = rawName
                .replace("\\\"", "\"")
                .replace("&quot;", "\"")
                .replace("&#34;", "\"")
                .replace("&amp;", "&")
                .replace("&#38;", "&")
                .replace("&apos;", "'")
                .replace("&#39;", "'")
                .replace("\\\\", "\\")
                .trim();

        log.info("ПОСЛЕ очистки: '{}'", cleaned);
        return cleaned;
    }
 */

    public List<AccountTransferTypeDto> getTransferNominalTypes() {
        List<AccountTransferTypeDto> transferNominalTypes = new ArrayList<>();
        transferNominalTypes.add(new AccountTransferTypeDto("Выплата зарплаты, отпускных и перечисление другого дохода", "1"));
        transferNominalTypes.add(new AccountTransferTypeDto("Перевод детских пособий, денежных сумм, выплачиваемых в качестве алиментов", "2"));
        transferNominalTypes.add(new AccountTransferTypeDto("Возмещение вреда, причиненного здоровью, и выплата компенсаций гражданам", "3"));
        return transferNominalTypes;
    }
}