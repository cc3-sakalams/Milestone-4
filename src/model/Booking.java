// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class Booking {
    private String bookingID, bookingReference, userID, status;
    private LocalDate checkInDate, checkOutDate;
    private List<Ticket> tickets;
    private Accommodation accommodation;
    private CheckInOut checkInOut;
    public Booking(String bookingID, String bookingReference, String userID, LocalDate ci, LocalDate co) {
        this.bookingID = bookingID; this.bookingReference = bookingReference;
        this.userID = userID; this.checkInDate = ci; this.checkOutDate = co;
        this.status = "PENDING"; this.tickets = new ArrayList<>();
    }
    public void confirmReservation() {
        this.status = "CONFIRMED";
        System.out.println("[BOOKING] ✅ Confirmed! Ref: " + bookingReference + " | " + checkInDate + " → " + checkOutDate);
    }
    public boolean checkIn() {
        if (!status.equals("CONFIRMED")) { System.out.println("[BOOKING] ❌ Cannot check in — status: " + status); return false; }
        this.status = "CHECKED_IN"; System.out.println("[BOOKING] ✅ Status → CHECKED_IN"); return true;
    }
    public boolean checkOut() {
        if (!status.equals("CHECKED_IN")) { System.out.println("[BOOKING] ❌ Cannot check out — status: " + status); return false; }
        this.status = "CHECKED_OUT"; System.out.println("[BOOKING] ✅ Status → CHECKED_OUT"); return true;
    }
    public void addTicket(Ticket t)            { tickets.add(t); }
    public String        getBookingID()        { return bookingID; }
    public String        getBookingReference() { return bookingReference; }
    public String        getUserID()           { return userID; }
    public LocalDate     getCheckInDate()      { return checkInDate; }
    public LocalDate     getCheckOutDate()     { return checkOutDate; }
    public String        getStatus()           { return status; }
    public List<Ticket>  getTickets()          { return tickets; }
    public Accommodation getAccommodation()    { return accommodation; }
    public CheckInOut    getCheckInOut()       { return checkInOut; }
    public void setStatus(String s)            { this.status = s; }
    public void setAccommodation(Accommodation a){ this.accommodation = a; }
    public void setCheckInOut(CheckInOut c)    { this.checkInOut = c; }
    @Override public String toString() {
        return String.format("Booking{ ref=%-22s status=%-12s %s→%s }", bookingReference, status, checkInDate, checkOutDate);
    }
}
