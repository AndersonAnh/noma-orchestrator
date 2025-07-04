package ru.vtb.msa.noma.orchestrator.integration.complexcheck.builder;

import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ClientAddressInfo;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ClientCardInfo;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ClientInfo;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;

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

    public ComplexCheckRequest buildComplexCheckRequest(CreateAccountRequest request) {
        ClientInfo clientInfo = buildClientInfo(request);
        List<ClientCardInfo> cards = buildClientCard(request);
        ClientAddressInfo address = buildClientAddress();
        return ComplexCheckRequest.builder()
                .client(clientInfo)
                .cards(cards)
                .address(address)
                .build();
    }

    private ClientInfo buildClientInfo(CreateAccountRequest request) {
        var user = request.user(); // если CreateAccountRequest — record с полем UserDto user

        return ClientInfo.builder()
                .fullName(user.getName())
                .taxId(user.getTaxId())
                .phone(user.getPhone())
                .email(user.getEmail())
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

    private ClientAddressInfo buildClientAddress () {
        return ClientAddressInfo.builder()
                .city(CLIENT_CITY)
                .index(CLIENT_INDEX)
                .street(CLIENT_STREET)
                .number(CLIENT_NUMBER)
                .build();
    }
}
