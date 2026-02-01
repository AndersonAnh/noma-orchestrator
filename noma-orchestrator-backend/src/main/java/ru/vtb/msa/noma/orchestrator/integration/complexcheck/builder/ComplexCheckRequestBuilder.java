package ru.vtb.msa.noma.orchestrator.integration.complexcheck.builder;

import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ClientAddressInfo;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ClientCardInfo;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ClientInfo;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;

import java.util.List;

@Component
public class ComplexCheckRequestBuilder {

    private static final String CLIENT_BANK = "VTB";
    private static final String CLIENT_CARD_NUMBER = "5008-7890-1111-7778";
    private static final String CLIENT_SALE_POINT = "1139";
    private static final String CLIENT_CITY = "msk";
    private static final String CLIENT_INDEX = "123879";
    private static final String CLIENT_STREET = "Street";
    private static final String CLIENT_NUMBER = "20";
    private static final String CURRENCY = "RUB";
    public static final String USER_NAME = "Иванов Иван Иванович";
    public static final String USER_TAX_ID = "123456789012";
    public static final String USER_PHONE = "+79001234567";
    public static final String USER_EMAIL = "ivan.ivanov@example.com";

    public ComplexCheckRequest buildComplexCheckRequest(CreateAccountRequest request) {
        ClientInfo clientInfo = buildClientInfo(request);
        List<ClientCardInfo> cards = buildClientCard(request);
        ClientAddressInfo address = buildClientAddress();
        return ComplexCheckRequest.builder()
                .client(clientInfo)
                .cards(cards)
                .address(address)
                .currency(request.currency())
                .build();
    }

    public ComplexCheckRequest buildComplexCheckRequest(InsuranceLifeRequest request) {
        ClientInfo clientInfo = buildClientInfo(request);
        List<ClientCardInfo> cards = buildClientCard(request);
        ClientAddressInfo address = buildClientAddress();
        return ComplexCheckRequest.builder()
                .client(clientInfo)
                .cards(cards)
                .address(address)
                .currency(CURRENCY)
                .build();
    }


    private ClientInfo buildClientInfo(InsuranceLifeRequest request) {

        return ClientInfo.builder()
                .fullName(USER_NAME)
                .taxId(USER_TAX_ID)
                .phone(USER_PHONE)
                .email(USER_EMAIL)
                .build();
    }


    private ClientInfo buildClientInfo(CreateAccountRequest request) {
        var user = request.user();

        return ClientInfo.builder()
                .fullName(user.name())
                .taxId(user.taxId())
                .phone(user.phone())
                .email(user.email())
                .build();
    }


    private List<ClientCardInfo> buildClientCard(CreateAccountRequest request) {
        return List.of(ClientCardInfo.builder()
                .bank(CLIENT_BANK)
                .number(CLIENT_CARD_NUMBER)
                .salePoint(CLIENT_SALE_POINT)
                .currency(request.currency())
                .build());
    }

    private List<ClientCardInfo> buildClientCard(InsuranceLifeRequest request) {
        return List.of(ClientCardInfo.builder()
                .bank(CLIENT_BANK)
                .number(CLIENT_CARD_NUMBER)
                .salePoint(CLIENT_SALE_POINT)
                .currency(CURRENCY)
                .build());
    }

    private ClientAddressInfo buildClientAddress () {
        return ClientAddressInfo.builder()
                .city(CLIENT_CITY)
                .index(CLIENT_INDEX)
                .street(CLIENT_STREET)
                .number(CLIENT_NUMBER)
                .build();
    }
}
