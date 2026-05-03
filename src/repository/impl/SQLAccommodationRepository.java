// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package repository.impl;
import db.DatabaseConnection;
import model.Accommodation;
import repository.AccommodationRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
public class SQLAccommodationRepository implements AccommodationRepository {
    private Connection conn(){return DatabaseConnection.getInstance().getConnection();}
    @Override public void save(Accommodation a){
        try(PreparedStatement ps=conn().prepareStatement("INSERT OR IGNORE INTO accommodations(accommodationID,roomType,checkInDate,checkOutDate,available)VALUES(?,?,?,?,?)")){
            ps.setString(1,a.getAccommodationID());ps.setString(2,a.getRoomType());
            ps.setString(3,a.getCheckInDate()!=null?a.getCheckInDate().toString():null);
            ps.setString(4,a.getCheckOutDate()!=null?a.getCheckOutDate().toString():null);
            ps.setInt(5,a.isAvailable()?1:0);ps.executeUpdate();
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}
    }
    @Override public Optional<Accommodation> findById(String id){
        try(PreparedStatement ps=conn().prepareStatement("SELECT * FROM accommodations WHERE accommodationID=?")){
            ps.setString(1,id);ResultSet rs=ps.executeQuery();if(rs.next())return Optional.of(map(rs));
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return Optional.empty();
    }
    @Override public List<Accommodation> findAll(){
        List<Accommodation> l=new ArrayList<>();
        try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM accommodations")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;
    }
    @Override public List<Accommodation> findAvailable(){
        List<Accommodation> l=new ArrayList<>();
        try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM accommodations WHERE available=1")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;
    }
    @Override public void update(Accommodation a){
        try(PreparedStatement ps=conn().prepareStatement("UPDATE accommodations SET roomType=?,checkInDate=?,checkOutDate=?,available=? WHERE accommodationID=?")){
            ps.setString(1,a.getRoomType());ps.setString(2,a.getCheckInDate()!=null?a.getCheckInDate().toString():null);
            ps.setString(3,a.getCheckOutDate()!=null?a.getCheckOutDate().toString():null);ps.setInt(4,a.isAvailable()?1:0);ps.setString(5,a.getAccommodationID());ps.executeUpdate();
        }catch(SQLException e){System.err.println("[DB] "+e.getMessage());}
    }
    @Override public void delete(String id){try(PreparedStatement ps=conn().prepareStatement("DELETE FROM accommodations WHERE accommodationID=?")){ps.setString(1,id);ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    private Accommodation map(ResultSet rs)throws SQLException{
        Accommodation a=new Accommodation(rs.getString("accommodationID"),rs.getString("roomType"),rs.getInt("available")==1);
        String ci=rs.getString("checkInDate"),co=rs.getString("checkOutDate");
        if(ci!=null)a.setCheckInDate(LocalDate.parse(ci));if(co!=null)a.setCheckOutDate(LocalDate.parse(co));return a;
    }
}
