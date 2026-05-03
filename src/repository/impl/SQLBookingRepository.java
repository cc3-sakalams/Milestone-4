// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package repository.impl;
import db.DatabaseConnection;
import model.Booking;
import repository.BookingRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
public class SQLBookingRepository implements BookingRepository {
    private Connection conn(){return DatabaseConnection.getInstance().getConnection();}
    @Override public void save(Booking b){
        try(PreparedStatement ps=conn().prepareStatement("INSERT OR IGNORE INTO bookings(bookingID,bookingReference,userID,checkInDate,checkOutDate,status,accommodationID)VALUES(?,?,?,?,?,?,?)")){
            ps.setString(1,b.getBookingID());ps.setString(2,b.getBookingReference());ps.setString(3,b.getUserID());
            ps.setString(4,b.getCheckInDate().toString());ps.setString(5,b.getCheckOutDate().toString());
            ps.setString(6,b.getStatus());ps.setString(7,b.getAccommodation()!=null?b.getAccommodation().getAccommodationID():null);
            ps.executeUpdate();System.out.println("[DB] Booking saved: "+b.getBookingReference());
            for(var t:b.getTickets()){try(PreparedStatement p2=conn().prepareStatement("INSERT OR IGNORE INTO booking_tickets(bookingID,ticketID)VALUES(?,?)")){p2.setString(1,b.getBookingID());p2.setString(2,t.getTicketID());p2.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}
    }
    @Override public Optional<Booking> findById(String id){
        try(PreparedStatement ps=conn().prepareStatement("SELECT * FROM bookings WHERE bookingID=?")){ps.setString(1,id);ResultSet rs=ps.executeQuery();if(rs.next())return Optional.of(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return Optional.empty();
    }
    @Override public Optional<Booking> findByReference(String ref){
        try(PreparedStatement ps=conn().prepareStatement("SELECT * FROM bookings WHERE bookingReference=?")){ps.setString(1,ref);ResultSet rs=ps.executeQuery();if(rs.next())return Optional.of(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return Optional.empty();
    }
    @Override public List<Booking> findAll(){
        List<Booking> l=new ArrayList<>();
        try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM bookings")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;
    }
    @Override public void update(Booking b){
        try(PreparedStatement ps=conn().prepareStatement("UPDATE bookings SET status=?,checkInDate=?,checkOutDate=?,accommodationID=? WHERE bookingID=?")){
            ps.setString(1,b.getStatus());ps.setString(2,b.getCheckInDate().toString());ps.setString(3,b.getCheckOutDate().toString());
            ps.setString(4,b.getAccommodation()!=null?b.getAccommodation().getAccommodationID():null);ps.setString(5,b.getBookingID());ps.executeUpdate();
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}
    }
    @Override public void delete(String id){try(PreparedStatement ps=conn().prepareStatement("DELETE FROM bookings WHERE bookingID=?")){ps.setString(1,id);ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    private Booking map(ResultSet rs)throws SQLException{
        Booking b=new Booking(rs.getString("bookingID"),rs.getString("bookingReference"),rs.getString("userID"),LocalDate.parse(rs.getString("checkInDate")),LocalDate.parse(rs.getString("checkOutDate")));
        b.setStatus(rs.getString("status"));return b;
    }
}
