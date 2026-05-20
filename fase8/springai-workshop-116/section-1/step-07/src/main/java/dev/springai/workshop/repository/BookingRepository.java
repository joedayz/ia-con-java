package dev.springai.workshop.repository;

import dev.springai.workshop.domain.Booking;
import dev.springai.workshop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomer(Customer customer);
}
