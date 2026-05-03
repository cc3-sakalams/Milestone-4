// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package repository.impl;
import db.DatabaseConnection;
import model.Ticket;
import repository.TicketRepository;
import java.sql.*;
import java.util.*;
public class SQLTicketRepository implements TicketRepository {
    private Connection conn(){return DatabaseConnection.getInstance().getConnection();}
    @Override public void save(Ticket t){
        try(PreparedStatement ps=conn().prepareStatement("INSERT OR IGNORE INTO tickets(ticketID,type,price,availability,qrCode)VALUES(?,?,?,?,?)")){
            ps.setString(1,t.getTicketID());ps.setString(2,t.getType());ps.setDouble(3,t.getPrice());ps.setInt(4,t.getAvailability());ps.setString(5,t.getQrCode());ps.executeUpdate();
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}
    }
    @Override public Optional<Ticket> findById(String id){
        try(PreparedStatement ps=conn().prepareStatement("SELECT * FROM tickets WHERE ticketID=?")){
            ps.setString(1,id);ResultSet rs=ps.executeQuery();if(rs.next())return Optional.of(map(rs));
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return Optional.empty();
    }
    @Override public List<Ticket> findAll(){
        List<Ticket> l=new ArrayList<>();
        try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM tickets")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;
    }
    @Override public List<Ticket> findAvailable(){
        List<Ticket> l=new ArrayList<>();
        try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM tickets WHERE availability>0")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;
    }
    @Override public void update(Ticket t){
        try(PreparedStatement ps=conn().prepareStatement("UPDATE tickets SET type=?,price=?,availability=?,qrCode=? WHERE ticketID=?")){
            ps.setString(1,t.getType());ps.setDouble(2,t.getPrice());ps.setInt(3,t.getAvailability());ps.setString(4,t.getQrCode());ps.setString(5,t.getTicketID());ps.executeUpdate();
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}
    }
    @Override public void delete(String id){try(PreparedStatement ps=conn().prepareStatement("DELETE FROM tickets WHERE ticketID=?")){ps.setString(1,id);ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    private Ticket map(ResultSet rs)throws SQLException{Ticket t=new Ticket(rs.getString("ticketID"),rs.getString("type"),rs.getFloat("price"),rs.getInt("availability"));t.setQrCode(rs.getString("qrCode")!=null?rs.getString("qrCode"):"");return t;}
}
