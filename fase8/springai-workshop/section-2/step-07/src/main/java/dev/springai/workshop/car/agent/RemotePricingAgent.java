package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.pricing.PricingEstimateRequest;
import dev.springai.workshop.car.pricing.PricingEstimateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Cliente del agente de pricing remoto (equiv. {@code @A2AClientAgent} en Quarkus step-07).
 */
@Service
public class RemotePricingAgent implements PricingAgent {

    private static final Logger log = LoggerFactory.getLogger(RemotePricingAgent.class);

    private final RestClient restClient;
    private final String pricingServiceBaseUrl;

    public RemotePricingAgent(
            RestClient.Builder restClientBuilder,
            @Value("${app.pricing.remote.base-url:http://localhost:8888}") String pricingServiceBaseUrl) {
        this.restClient = restClientBuilder.build();
        this.pricingServiceBaseUrl = pricingServiceBaseUrl;
    }

    @Override
    public String estimateValue(String carMake, String carModel, Integer carYear, String carCondition) {
        log.info("Calling remote pricing agent at {}", pricingServiceBaseUrl);

        PricingEstimateResponse response = restClient.post()
                .uri(pricingServiceBaseUrl + "/api/pricing/estimate")
                .body(new PricingEstimateRequest(carMake, carModel, carYear, carCondition))
                .retrieve()
                .body(PricingEstimateResponse.class);

        if (response == null || response.estimate() == null) {
            throw new IllegalStateException("Empty response from remote pricing service");
        }

        log.debug("Remote pricing response: {}", response.estimate());
        return response.estimate();
    }
}
