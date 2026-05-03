package repository.impl;
import db.DatabaseConnection; import model.Membership; import repository.MembershipRepository;
import java.sql.*; import java.util.*;
public class SQLMembershipRepository implements MembershipRepository {
    private Connection conn(){return DatabaseConnection.getInstance().getConnection();}
    @Override public void save(Membership m){try(PreparedStatement ps=conn().prepareStatement("INSERT OR IGNORE INTO memberships(membershipID,userID,type,active,perks)VALUES(?,?,?,?,?)")){ps.setString(1,m.getMembershipID());ps.setString(2,m.getUserID());ps.setString(3,m.getType());ps.setInt(4,m.isActive()?1:0);ps.setString(5,String.join("|",m.getPerks()));ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    @Override public Optional<Membership> findById(String id){try(PreparedStatement ps=conn().prepareStatement("SELECT * FROM memberships WHERE membershipID=?")){ps.setString(1,id);ResultSet rs=ps.executeQuery();if(rs.next())return Optional.of(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return Optional.empty();}
    @Override public List<Membership> findAll(){List<Membership> l=new ArrayList<>();try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM memberships")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;}
    @Override public void update(Membership m){try(PreparedStatement ps=conn().prepareStatement("UPDATE memberships SET type=?,active=?,perks=? WHERE membershipID=?")){ps.setString(1,m.getType());ps.setInt(2,m.isActive()?1:0);ps.setString(3,String.join("|",m.getPerks()));ps.setString(4,m.getMembershipID());ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    @Override public void delete(String id){try(PreparedStatement ps=conn().prepareStatement("DELETE FROM memberships WHERE membershipID=?")){ps.setString(1,id);ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    private Membership map(ResultSet rs)throws SQLException{Membership m=new Membership(rs.getString("membershipID"),rs.getString("userID"),rs.getString("type"));m.setActive(rs.getInt("active")==1);String p=rs.getString("perks");if(p!=null&&!p.isBlank())m.setPerks(new ArrayList<>(Arrays.asList(p.split("\\|"))));return m;}
}
