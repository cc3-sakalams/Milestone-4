// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package model;
import java.time.LocalDate;
public class Guest extends User {
    private Membership membership;
    public Guest(String userID, String email) { super(userID, email); }
    public boolean purchaseTicket(Ticket ticket) {
        System.out.println("[GUEST] " + email + " purchasing: " + ticket.getType());
        return ticket.purchase();
    }
    public boolean bookAccommodation(Accommodation a, LocalDate ci, LocalDate co) {
        System.out.println("[GUEST] " + email + " booking: " + a.getRoomType());
        return a.book(ci, co);
    }
    public void viewParkInfo() {
        System.out.println("[PARK INFO] ── Interactive Park Map ──────────────────");
        System.out.println("[PARK INFO]  Roller Coaster   : Wait ≈ 20 min");
        System.out.println("[PARK INFO]  Water Ride        : Wait ≈ 10 min");
        System.out.println("[PARK INFO]  Ferris Wheel      : Wait ≈  5 min");
        System.out.println("[PARK INFO]  Parade Schedule   : 3:00 PM daily");
        System.out.println("[PARK INFO]  Fireworks Show    : 9:00 PM nightly");
        System.out.println("[PARK INFO]  Accessibility     : Wheelchair ramps at all attractions");
        System.out.println("[PARK INFO] ──────────────────────────────────────────");
    }
    public void manageMembership(Membership m) { this.membership = m; m.purchase(); }
    public boolean submitFeedback(Feedback f)  { System.out.println("[GUEST] " + email + " submitting feedback."); return f.submit(); }
    public boolean handleCheckIn(CheckInOut c) { System.out.println("[GUEST] " + email + " initiating check-in."); return c.performSelfServiceCheckIn(); }
    public boolean handleCheckOut(CheckInOut c){ System.out.println("[GUEST] " + email + " initiating check-out."); return c.performSelfServiceCheckOut(); }
    public Membership getMembership()           { return membership; }
    public void       setMembership(Membership m){ this.membership = m; }
}
