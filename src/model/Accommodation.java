// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package model;
import java.time.LocalDate;
public class Accommodation {
    private String accommodationID, roomType;
    private LocalDate checkInDate, checkOutDate;
    private boolean available;
    public Accommodation(String id, String roomType, boolean available) {
        this.accommodationID = id; this.roomType = roomType; this.available = available;
    }
    public boolean book(LocalDate ci, LocalDate co) {
        if (!available) { System.out.println("[ACCOMMODATION] ❌ Room unavailable: " + roomType); return false; }
        this.checkInDate = ci; this.checkOutDate = co; this.available = false;
        System.out.println("[ACCOMMODATION] ✅ Room reserved: " + roomType + " | " + ci + " → " + co);
        return true;
    }
    public String    getAccommodationID()            { return accommodationID; }
    public String    getRoomType()                   { return roomType; }
    public LocalDate getCheckInDate()                { return checkInDate; }
    public LocalDate getCheckOutDate()               { return checkOutDate; }
    public boolean   isAvailable()                   { return available; }
    public void      setAvailable(boolean a)         { this.available = a; }
    public void      setCheckInDate(LocalDate d)     { this.checkInDate = d; }
    public void      setCheckOutDate(LocalDate d)    { this.checkOutDate = d; }
    @Override public String toString() {
        return String.format("Accommodation{ id=%-6s type=%-16s available=%s }", accommodationID, roomType, available ? "YES" : "NO");
    }
}
