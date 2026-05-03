// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package model;
import java.util.ArrayList;
import java.util.List;
public class Membership {
    private String membershipID, userID, type;
    private List<String> perks;
    private boolean active;
    public Membership(String membershipID, String userID, String type) {
        this.membershipID = membershipID; this.userID = userID; this.type = type;
        this.perks = new ArrayList<>(); this.active = false;
    }
    public void purchase() {
        this.active = true;
        System.out.println("[MEMBERSHIP] ✅ Activated: " + type);
        System.out.println("[MEMBERSHIP]    Perks: " + (perks.isEmpty() ? "None" : String.join(", ", perks)));
    }
    public void renew() { this.active = true; System.out.println("[MEMBERSHIP] ✅ Renewed: " + type); }
    public void addPerk(String p)          { perks.add(p); }
    public String       getMembershipID()  { return membershipID; }
    public String       getUserID()        { return userID; }
    public String       getType()          { return type; }
    public List<String> getPerks()         { return perks; }
    public boolean      isActive()         { return active; }
    public void         setActive(boolean a){ this.active = a; }
    public void         setPerks(List<String> p){ this.perks = p; }
    @Override public String toString() {
        return String.format("Membership{ id=%-8s type=%-12s active=%-5s perks=%s }", membershipID, type, active?"YES":"NO", perks);
    }
}
