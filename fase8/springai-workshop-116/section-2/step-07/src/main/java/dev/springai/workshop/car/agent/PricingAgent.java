package dev.springai.workshop.car.agent;

/**
 * Contrato del agente de pricing (implementación local en steps anteriores; remota en step-07).
 */
public interface PricingAgent {

    String estimateValue(String carMake, String carModel, Integer carYear, String carCondition);
}
