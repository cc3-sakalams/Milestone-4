// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package model;
public class Ticket {
    private String ticketID, type, qrCode;
    private float price;
    private int availability;
    public Ticket(String ticketID, String type, float price, int availability) {
        this.ticketID = ticketID; this.type = type;
        this.price = price; this.availability = availability; this.qrCode = "";
    }
    public boolean purchase() {
        if (availability <= 0) {
            System.out.println("[TICKET] ❌ No availability for: " + type); return false;
        }
        availability--;
        this.qrCode = "QR-" + ticketID + "-" + System.currentTimeMillis();
        System.out.println("[TICKET] ✅ Ticket reserved: " + type + " | QR: " + qrCode);
        return true;
    }
    public String generateQRCode() { return "QR-" + ticketID + "-" + System.currentTimeMillis(); }
    public String getTicketID()          { return ticketID; }
    public String getType()              { return type; }
    public float  getPrice()             { return price; }
    public int    getAvailability()      { return availability; }
    public String getQrCode()            { return qrCode; }
    public void   setAvailability(int a) { this.availability = a; }
    public void   setQrCode(String q)    { this.qrCode = q; }
    @Override public String toString() {
        return String.format("Ticket{ id=%-6s type=%-12s price=PHP%-8.2f avail=%d }", ticketID, type, price, availability);
    }
}
