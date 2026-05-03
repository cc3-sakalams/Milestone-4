package repository;
import model.Booking;
import java.util.Optional;
public interface BookingRepository extends Repository<Booking, String> { Optional<Booking> findByReference(String ref); }
