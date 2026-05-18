package dev.springai.workshop.pricing.remote.service;

public record PricingEstimateRequest(
        String carMake,
        String carModel,
        Integer carYear,
        String carCondition) {
}
