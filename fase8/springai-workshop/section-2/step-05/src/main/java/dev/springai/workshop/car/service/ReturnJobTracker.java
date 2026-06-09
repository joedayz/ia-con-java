package dev.springai.workshop.car.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReturnJobTracker {

    public enum State {
        IDLE,
        RUNNING,
        AWAITING_APPROVAL,
        COMPLETED,
        FAILED
    }

    public record Status(State state, String message) {
    }

    private final Map<Integer, Status> jobs = new ConcurrentHashMap<>();

    public void started(int carNumber) {
        jobs.put(carNumber, new Status(State.RUNNING, "Processing return"));
    }

    public void awaitingApproval(int carNumber) {
        jobs.put(carNumber, new Status(State.AWAITING_APPROVAL, "Waiting for human approval"));
    }

    public void completed(int carNumber, String message) {
        jobs.put(carNumber, new Status(State.COMPLETED, message));
    }

    public void failed(int carNumber, String message) {
        jobs.put(carNumber, new Status(State.FAILED, message));
    }

    public Status get(int carNumber) {
        return jobs.getOrDefault(carNumber, new Status(State.IDLE, ""));
    }
}
