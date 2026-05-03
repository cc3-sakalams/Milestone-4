// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package model;
public class Feedback {
    private String feedbackID, userID, comment;
    private int rating;
    public Feedback(String feedbackID, String userID, int rating, String comment) {
        this.feedbackID = feedbackID; this.userID = userID; this.rating = rating; this.comment = comment;
    }
    public boolean submit() {
        if (rating < 1 || rating > 5) { System.out.println("[FEEDBACK] ❌ Invalid rating (" + rating + "). Must be 1–5."); return false; }
        if (comment == null || comment.isBlank()) { System.out.println("[FEEDBACK] ❌ Comment cannot be empty."); return false; }
        System.out.println("[FEEDBACK] ✅ Submitted! Rating: " + rating + "/5 | \"" + comment + "\"");
        System.out.println("[FEEDBACK] 🎁 Reward: 10% off your next visit!"); return true;
    }
    public String getFeedbackID() { return feedbackID; }
    public String getUserID()     { return userID; }
    public int    getRating()     { return rating; }
    public String getComment()    { return comment; }
    @Override public String toString() {
        return String.format("Feedback{ id=%-8s userID=%-8s rating=%d/5 }", feedbackID, userID, rating);
    }
}
