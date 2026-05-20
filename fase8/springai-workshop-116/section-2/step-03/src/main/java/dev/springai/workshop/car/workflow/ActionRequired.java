package dev.springai.workshop.car.workflow;

final class ActionRequired {

    private ActionRequired() {
    }

    static boolean isRequired(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        String upper = value.toUpperCase();
        return !upper.contains("NOT_REQUIRED") && !upper.contains("NOT REQUIRED");
    }
}
