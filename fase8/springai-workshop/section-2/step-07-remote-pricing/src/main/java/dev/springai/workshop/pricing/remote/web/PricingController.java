package dev.springai.workshop.pricing.remote.web;

import dev.springai.workshop.pricing.remote.service.PricingEstimateRequest;
import dev.springai.workshop.pricing.remote.service.PricingEstimateResponse;
import dev.springai.workshop.pricing.remote.service.RemotePricingEstimator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final RemotePricingEstimator remotePricingEstimator;

    public PricingController(RemotePricingEstimator remotePricingEstimator) {
        this.remotePricingEstimator = remotePricingEstimator;
    }

    /**
     * REST facade del agente remoto (en Quarkus el protocolo A2A usa JSON-RPC sobre este servicio).
     */
    @PostMapping("/estimate")
    public PricingEstimateResponse estimate(@RequestBody PricingEstimateRequest request) {
        String estimate = remotePricingEstimator.estimateValue(
                request.carMake(),
                request.carModel(),
                request.carYear(),
                request.carCondition());
        return new PricingEstimateResponse(estimate);
    }
}
