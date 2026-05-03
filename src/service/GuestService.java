// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Auto-run CLI demo | Full Java implementation

package service;

import model.*;
import payment.PaymentFramework;
import repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * CLASS: GuestService
 * ============================================================
 * Orchestrates all guest use cases (Features 1–7).
 * Now integrates PaymentFramework for ticket purchases and
 * accommodation bookings — all transactions are processed
 * through the Template Method pattern before confirmation.
 * ============================================================
 */
public class GuestService {

    private final UserRepository          userRepo;
    private final TicketRepository        ticketRepo;
    private final AccommodationRepository accommodationRepo;
    private final BookingRepository       bookingRepo;
    private final MembershipRepository    membershipRepo;
    private final FeedbackRepository      feedbackRepo;

    public GuestService(UserRepository userRepo,
                        TicketRepository ticketRepo,
                        AccommodationRepository accommodationRepo,
                        BookingRepository bookingRepo,
                        MembershipRepository membershipRepo,
                        FeedbackRepository feedbackRepo) {
        this.userRepo          = userRepo;
        this.ticketRepo        = ticketRepo;
        this.accommodationRepo = accommodationRepo;
        this.bookingRepo       = bookingRepo;
        this.membershipRepo    = membershipRepo;
        this.feedbackRepo      = feedbackRepo;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 1: Register Guest
    // ══════════════════════════════════════════════════════════
    public Guest registerGuest(String userID, String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            System.out.println("[GUEST SERVICE] ❌ Email already registered: " + email);
            return null;
        }
        Guest guest = new Guest(userID, email);
        guest.register();
        guest.issueSessionToken();
        userRepo.save(guest);
        System.out.println("[GUEST SERVICE] ✅ Guest saved to DB.\n");
        return guest;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 2: Purchase Ticket  (now with PaymentFramework)
    // ══════════════════════════════════════════════════════════

    /**
     * Purchases a ticket for a guest using the provided
     * PaymentFramework (CreditCardPayment or CashPayment).
     *
     * FLOW:
     *   1. Load ticket from DB — reject if not found.
     *   2. Check availability — reject if sold out.
     *   3. Process payment via payment.processInvoice(price).
     *      — PaymentFramework runs: validate → discount → VAT → finalize.
     *      — Throws PaymentException if payment fails.
     *   4. If payment succeeds: call guest.purchaseTicket()
     *      to generate QR code and decrement availability.
     *   5. Persist updated ticket to DB.
     *   6. Print transaction summary.
     *
     * @param guest    The guest making the purchase
     * @param ticketID The ticket ID to purchase
     * @param payment  The payment method (CreditCard or Cash)
     * @return true if purchase succeeded, false otherwise
     */
    public boolean purchaseTicket(Guest guest,
                                   String ticketID,
                                   PaymentFramework payment) {
        // Step 1: Load ticket
        Optional<Ticket> opt = ticketRepo.findById(ticketID);
        if (opt.isEmpty()) {
            System.out.println("[GUEST SERVICE] ❌ Ticket not found: " + ticketID);
            return false;
        }

        Ticket ticket = opt.get();

        // Step 2: Check availability
        if (ticket.getAvailability() <= 0) {
            System.out.println("[GUEST SERVICE] ❌ No availability for: " + ticket.getType());
            return false;
        }

        // Step 3: Process payment through the framework
        System.out.println("[PAYMENT] Processing ticket payment for: " + ticket.getType()
                + " | Base: PHP " + String.format("%,.2f", (double) ticket.getPrice()));
        try {
            double finalAmt = payment.processInvoice(ticket.getPrice());
            System.out.println("[PAYMENT] Final amount charged: PHP "
                    + String.format("%,.2f", finalAmt));
        } catch (PaymentFramework.PaymentException e) {
            System.out.println("[PAYMENT] ❌ Payment failed: " + e.getMessage() + "\n");
            return false;
        }

        // Step 4: Payment succeeded — purchase the ticket (QR + decrement)
        boolean success = guest.purchaseTicket(ticket);

        // Step 5: Persist updated availability to DB
        if (success) {
            ticketRepo.update(ticket);
            // Step 6: Print summary
            System.out.println(payment.getTransactionSummary());
        }
        return success;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 3: Book Accommodation (now with PaymentFramework)
    // ══════════════════════════════════════════════════════════

    /**
     * Books a resort room for a guest with payment processing.
     *
     * FLOW:
     *   1. Load and verify accommodation availability.
     *   2. Calculate total cost based on nights × price per night.
     *   3. Process payment via payment.processInvoice(totalCost).
     *      — Throws PaymentException if payment fails.
     *   4. If payment succeeds: book the room, create Booking.
     *   5. Bundle ticket if provided (processed without extra payment).
     *   6. Confirm reservation and persist to DB.
     *
     * @param guest           The guest making the booking
     * @param accommodationID Room ID to book
     * @param ticketID        Optional ticket to bundle (null = skip)
     * @param checkIn         Check-in date
     * @param checkOut        Check-out date
     * @param payment         The payment method for accommodation cost
     * @return The confirmed Booking, or null if failed
     */
    public Booking bookAccommodation(Guest guest,
                                      String accommodationID,
                                      String ticketID,
                                      LocalDate checkIn,
                                      LocalDate checkOut,
                                      PaymentFramework payment) {
        // Step 1: Load and verify room availability
        Optional<Accommodation> roomOpt = accommodationRepo.findById(accommodationID);
        if (roomOpt.isEmpty() || !roomOpt.get().isAvailable()) {
            System.out.println("[GUEST SERVICE] ❌ Accommodation unavailable. Available rooms:");
            accommodationRepo.findAvailable()
                    .forEach(a -> System.out.println("   → " + a));
            return null;
        }

        Accommodation room = roomOpt.get();

        // Step 2: Calculate total cost (nights × PHP 2,500 per night base rate)
        long nights    = checkOut.toEpochDay() - checkIn.toEpochDay();
        double baseRate = 2500.00; // base nightly rate in PHP
        double totalCost = nights * baseRate;

        System.out.println("[PAYMENT] Processing accommodation payment for: " + room.getRoomType());
        System.out.printf("[PAYMENT]   Nights: %d × PHP %,.2f = PHP %,.2f%n",
                nights, baseRate, totalCost);

        // Step 3: Process payment through the framework
        try {
            double finalAmt = payment.processInvoice(totalCost);
            System.out.println("[PAYMENT] Final amount charged: PHP "
                    + String.format("%,.2f", finalAmt));
        } catch (PaymentFramework.PaymentException e) {
            System.out.println("[PAYMENT] ❌ Payment failed: " + e.getMessage() + "\n");
            return null;
        }

        // Step 4: Payment succeeded — book the room
        guest.bookAccommodation(room, checkIn, checkOut);
        accommodationRepo.update(room);

        // Create the Booking record
        String bookingID  = "BKG-" + System.currentTimeMillis();
        String bookingRef = "BK-" + guest.getUserID().toUpperCase()
                          + "-" + (System.currentTimeMillis() % 1000);
        Booking booking = new Booking(bookingID, bookingRef,
                guest.getUserID(), checkIn, checkOut);
        booking.setAccommodation(room);

        // Step 5: Bundle ticket if provided
        if (ticketID != null && !ticketID.isBlank()) {
            ticketRepo.findById(ticketID).ifPresent(t -> {
                if (t.getAvailability() > 0) {
                    t.purchase();
                    ticketRepo.update(t);
                    booking.addTicket(t);
                }
            });
        }

        // Step 6: Confirm and persist
        booking.confirmReservation();
        bookingRepo.save(booking);
        System.out.println(payment.getTransactionSummary());
        System.out.println("[GUEST SERVICE] 📧 Confirmation email sent to: "
                + guest.getEmail() + "\n");
        return booking;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 4: View Park Info
    // ══════════════════════════════════════════════════════════
    public void viewParkInfo(Guest guest) {
        guest.viewParkInfo();
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 5: Manage Membership
    // ══════════════════════════════════════════════════════════
    public Membership manageMembership(Guest guest, String membershipID,
                                        String type, List<String> perks) {
        Membership membership = new Membership(membershipID,
                guest.getUserID(), type);
        perks.forEach(membership::addPerk);
        guest.manageMembership(membership);
        membershipRepo.save(membership);
        System.out.println("[GUEST SERVICE] ✅ Membership stored in DB.\n");
        return membership;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 6: Submit Feedback
    // ══════════════════════════════════════════════════════════
    public boolean submitFeedback(Guest guest, String feedbackID,
                                   int rating, String comment) {
        Feedback feedback = new Feedback(feedbackID,
                guest.getUserID(), rating, comment);
        boolean success = guest.submitFeedback(feedback);
        if (success) {
            feedbackRepo.save(feedback);
            System.out.println("[GUEST SERVICE] ✅ Feedback stored in DB.\n");
        }
        return success;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 7a: Handle Check-In
    // ══════════════════════════════════════════════════════════
    public boolean handleCheckIn(Guest guest, String bookingReference) {
        Optional<Booking> bookingOpt = bookingRepo.findByReference(bookingReference);
        if (bookingOpt.isEmpty()) {
            System.out.println("[GUEST SERVICE] ❌ Booking not found: " + bookingReference);
            System.out.println("[GUEST SERVICE] ⚠️  Please visit the front desk.\n");
            return false;
        }
        Booking    booking    = bookingOpt.get();
        CheckInOut checkInOut = new CheckInOut(bookingReference);
        boolean    success    = guest.handleCheckIn(checkInOut);
        if (success) {
            booking.checkIn();
            booking.setCheckInOut(checkInOut);
            bookingRepo.update(booking);
            System.out.println("[GUEST SERVICE] ✅ Check-in status saved to DB.\n");
        }
        return success;
    }

    // ══════════════════════════════════════════════════════════
    //  FEATURE 7b: Handle Check-Out
    // ══════════════════════════════════════════════════════════
    public boolean handleCheckOut(Guest guest, String bookingReference) {
        Optional<Booking> bookingOpt = bookingRepo.findByReference(bookingReference);
        if (bookingOpt.isEmpty()) {
            System.out.println("[GUEST SERVICE] ❌ Booking not found: " + bookingReference);
            return false;
        }
        Booking booking = bookingOpt.get();
        if (!"CHECKED_IN".equals(booking.getStatus())) {
            System.out.println("[GUEST SERVICE] ❌ Cannot check out — status: "
                    + booking.getStatus());
            return false;
        }
        CheckInOut checkInOut = new CheckInOut(bookingReference);
        checkInOut.performSelfServiceCheckIn();
        boolean success = guest.handleCheckOut(checkInOut);
        if (success) {
            booking.checkOut();
            bookingRepo.update(booking);
            System.out.println("[GUEST SERVICE] ✅ Check-out status saved to DB.\n");
        }
        return success;
    }

    // ── Accessors for Main ────────────────────────────────────
    public List<Ticket>        getAvailableTickets()        { return ticketRepo.findAvailable(); }
    public List<Accommodation> getAvailableAccommodations() { return accommodationRepo.findAvailable(); }
    public List<Booking>       getAllBookings()              { return bookingRepo.findAll(); }
}
