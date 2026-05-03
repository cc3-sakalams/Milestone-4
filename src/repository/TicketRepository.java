package repository;
import model.Ticket;
import java.util.List;
public interface TicketRepository extends Repository<Ticket, String> { List<Ticket> findAvailable(); }
