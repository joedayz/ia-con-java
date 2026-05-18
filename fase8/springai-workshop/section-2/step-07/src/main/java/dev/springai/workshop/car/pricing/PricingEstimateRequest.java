package dev.springai.workshop.car.pricing;

public record PricingEstimateRequest(
        String carMake,
        String carModel,
        Integer carYear,
        String carCondition) {
}
