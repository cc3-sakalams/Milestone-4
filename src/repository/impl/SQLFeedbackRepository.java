package repository.impl;
import db.DatabaseConnection; import model.Feedback; import repository.FeedbackRepository;
import java.sql.*; import java.util.*;
public class SQLFeedbackRepository implements FeedbackRepository {
    private Connection conn(){return DatabaseConnection.getInstance().getConnection();}
    @Override public void save(Feedback f){try(PreparedStatement ps=conn().prepareStatement("INSERT OR IGNORE INTO feedbacks(feedbackID,userID,rating,comment)VALUES(?,?,?,?)")){ps.setString(1,f.getFeedbackID());ps.setString(2,f.getUserID());ps.setInt(3,f.getRating());ps.setString(4,f.getComment());ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    @Override public Optional<Feedback> findById(String id){try(PreparedStatement ps=conn().prepareStatement("SELECT * FROM feedbacks WHERE feedbackID=?")){ps.setString(1,id);ResultSet rs=ps.executeQuery();if(rs.next())return Optional.of(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return Optional.empty();}
    @Override public List<Feedback> findAll(){List<Feedback> l=new ArrayList<>();try(Statement st=conn().createStatement();ResultSet rs=st.executeQuery("SELECT * FROM feedbacks")){while(rs.next())l.add(map(rs));}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}return l;}
    @Override public void update(Feedback f){try(PreparedStatement ps=conn().prepareStatement("UPDATE feedbacks SET rating=?,comment=? WHERE feedbackID=?")){ps.setInt(1,f.getRating());ps.setString(2,f.getComment());ps.setString(3,f.getFeedbackID());ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    @Override public void delete(String id){try(PreparedStatement ps=conn().prepareStatement("DELETE FROM feedbacks WHERE feedbackID=?")){ps.setString(1,id);ps.executeUpdate();}catch(SQLException e){System.err.println("[DB] "+e.getMessage());}}
    private Feedback map(ResultSet rs)throws SQLException{return new Feedback(rs.getString("feedbackID"),rs.getString("userID"),rs.getInt("rating"),rs.getString("comment"));}
}
