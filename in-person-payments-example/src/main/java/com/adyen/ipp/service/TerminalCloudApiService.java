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
        if(applicationProperty.getAdyenTerminalApiCloudEndpoint() != null) {
            /// Default: null, unless you want to override this to point to a different endpoint based on your region.
            /// See https://docs.adyen.com/point-of-sale/design-your-integration/terminal-api/#cloud.
            /// Optionally, if you do not own an Adyen Terminal/POS (yet), you can test this application using Adyen's Mock Terminal-API Application on GitHub: https://github.com/adyen-examples/adyen-mock-terminal-api (see README).
            client.getConfig().setTerminalApiCloudEndpoint(applicationProperty.getAdyenTerminalApiCloudEndpoint());
        }
        terminalCloudAPI = new TerminalCloudAPI(client);
    }

    public TerminalCloudAPI getTerminalCloudApi() {
        return terminalCloudAPI;
    }
}
