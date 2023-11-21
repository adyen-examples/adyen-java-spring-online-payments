package com.adyen.ipp.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.ipp.ApplicationProperty;
import com.adyen.service.TerminalCloudAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerminalCloudApiService {
    private ApplicationProperty applicationProperty;
    private Client client;
    private TerminalCloudAPI terminalCloudAPI;

    @Autowired
    public TerminalCloudApiService(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            throw new RuntimeException("ADYEN_API_KEY is UNDEFINED");
        }

        client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        terminalCloudAPI = new TerminalCloudAPI(client);
    }

    public TerminalCloudAPI getTerminalCloudApi() {
        return terminalCloudAPI;
    }
}
