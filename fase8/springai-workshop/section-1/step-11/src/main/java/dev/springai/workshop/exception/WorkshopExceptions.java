package dev.springai.workshop.exception;

public final class WorkshopExceptions {

    private WorkshopExceptions() {
    }

    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(String customerName, String customerSurname) {
            super("Customer not found: %s %s".formatted(customerName, customerSurname));
        }
    }

    public static class BookingCannotBeCancelledException extends RuntimeException {
        public BookingCannotBeCancelledException(long bookingId, String reason) {
            super("Booking %d cannot be cancelled because %s - see terms of use".formatted(bookingId, reason));
        }
    }

    public static class BookingNotFoundException extends RuntimeException {
        public BookingNotFoundException(long bookingId) {
            super("Booking %d not found".formatted(bookingId));
        }
    }
}
