// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Auto-run CLI demo | Full Java implementation

import db.DatabaseConnection;
import model.*;
import payment.CashPayment;
import payment.CreditCardPayment;
import payment.PaymentFramework;
import repository.*;
import repository.impl.*;
import service.*;

import java.time.LocalDate;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 *   Theme Park + Resort Management System
 *   AUTO-RUN CLI DEMO — PaymentFramework integrated
 *   Guest and Admin are the two main classes (both extend User).
 *   PaymentFramework plugged into ticket purchases + room bookings.
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class Main {

    static UserRepository          userRepo;
    static TicketRepository        ticketRepo;
    static AccommodationRepository accommodationRepo;
    static BookingRepository       bookingRepo;
    static MembershipRepository    membershipRepo;
    static FeedbackRepository      feedbackRepo;
    static ReportRepository        reportRepo;
    static GuestService            guestService;
    static AdminService            adminService;

    public static void main(String[] args) throws InterruptedException {

        // ── Boot ──────────────────────────────────────────────
        printBanner();
        delay(400);

        step("Connecting to SQLite database...");
        DatabaseConnection.getInstance();

        step("Initializing SQL Repositories...");
        userRepo          = new SQLUserRepository();
        ticketRepo        = new SQLTicketRepository();
        accommodationRepo = new SQLAccommodationRepository();
        bookingRepo       = new SQLBookingRepository();
        membershipRepo    = new SQLMembershipRepository();
        feedbackRepo      = new SQLFeedbackRepository();
        reportRepo        = new SQLReportRepository();

        guestService = new GuestService(userRepo, ticketRepo, accommodationRepo,
                                         bookingRepo, membershipRepo, feedbackRepo);
        adminService = new AdminService(userRepo, reportRepo);

        step("Seeding demo data...");
        seedDemoData();
        delay(300);

        System.out.println("\n  ✅ System ready. Starting auto-run demo...\n");
        delay(500);

        // ══════════════════════════════════════════════════════
        //  FEATURE 1 — Register Guest
        // ══════════════════════════════════════════════════════
        featureHeader(1, "Register Guest");

        simulate("User ID", "USR-001");
        simulate("Email",   "maria@themepark.com");
        Guest guest = guestService.registerGuest("USR-001", "maria@themepark.com");
        delay(300);

        simulate("User ID (2nd guest)", "USR-002");
        simulate("Email",               "john@themepark.com");
        Guest guest2 = guestService.registerGuest("USR-002", "john@themepark.com");
        delay(300);

        System.out.println("\n  --- Edge case: duplicate email ---");
        simulate("Email (duplicate)", "maria@themepark.com");
        guestService.registerGuest("USR-003", "maria@themepark.com");
        delay(200);

        // ══════════════════════════════════════════════════════
        //  FEATURE 2 — Purchase Ticket  (via PaymentFramework)
        // ══════════════════════════════════════════════════════
        featureHeader(2, "Purchase Ticket — via PaymentFramework");

        System.out.println("  Available Tickets:");
        ticketRepo.findAvailable().forEach(t -> System.out.println("    → " + t));
        System.out.println();

        // ── Demo 2a: Credit Card Payment (valid) ──────────────
        simulate("Payment method", "CREDIT CARD");
        simulate("Ticket",         "T001 — SINGLE_DAY  PHP 40.00");
        simulate("Card holder",    "Maria Santos");
        simulate("Credit balance", "PHP 10,000.00");

        CreditCardPayment ccPayment1 = new CreditCardPayment(
                "Maria Santos", "4111111111111234", "12/27", 10000.00);
        guestService.purchaseTicket(guest, "T001", ccPayment1);
        delay(300);

        // ── Demo 2b: Cash Payment (valid) ────────────────────
        simulate("Payment method", "CASH");
        simulate("Ticket",         "T002 — MULTI_DAY   PHP 85.00");
        simulate("Cash tendered",  "PHP 200.00");

        CashPayment cashPayment1 = new CashPayment(200.00, 200.00);
        guestService.purchaseTicket(guest2, "T002", cashPayment1);
        delay(300);

        // ── Demo 2c: Edge — insufficient credit balance ───────
        System.out.println("\n  --- Edge case: insufficient credit balance ---");
        simulate("Credit balance (low)", "PHP 5.00");
        simulate("Ticket price",         "PHP 40.00");
        CreditCardPayment ccFail = new CreditCardPayment(
                "Maria Santos", "4111111111111234", "12/27", 5.00);
        guestService.purchaseTicket(guest, "T001", ccFail);
        delay(200);

        // ── Demo 2d: Edge — insufficient cash ────────────────
        System.out.println("\n  --- Edge case: insufficient cash tendered ---");
        simulate("Cash tendered (low)", "PHP 10.00");
        simulate("Ticket price",        "PHP 85.00");
        CashPayment cashFail = new CashPayment(10.00, 10.00);
        guestService.purchaseTicket(guest, "T002", cashFail);
        delay(200);

        // ══════════════════════════════════════════════════════
        //  FEATURE 3 — Book Accommodation (via PaymentFramework)
        // ══════════════════════════════════════════════════════
        featureHeader(3, "Book Accommodation — via PaymentFramework");

        System.out.println("  Available Rooms:");
        accommodationRepo.findAvailable().forEach(a -> System.out.println("    → " + a));
        System.out.println();

        // ── Demo 3a: Credit Card booking ─────────────────────
        simulate("Room",             "A001 — Deluxe Room");
        simulate("Check-in",         "2025-12-20");
        simulate("Check-out",        "2025-12-25  (5 nights)");
        simulate("Cost",             "5 × PHP 2,500 = PHP 12,500 base");
        simulate("Payment method",   "CREDIT CARD  (5% loyalty discount)");
        simulate("Credit balance",   "PHP 20,000.00");

        CreditCardPayment ccBooking = new CreditCardPayment(
                "Maria Santos", "4111111111111234", "12/27", 20000.00);
        Booking booking = guestService.bookAccommodation(
                guest, "A001", "T003",
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 25),
                ccBooking);
        delay(300);

        // ── Demo 3b: Cash booking ────────────────────────────
        simulate("Room",             "A002 — Villa Suite");
        simulate("Check-in",         "2025-12-22");
        simulate("Check-out",        "2025-12-27  (5 nights)");
        simulate("Cost",             "5 × PHP 2,500 = PHP 12,500 base");
        simulate("Payment method",   "CASH  (10% cash discount)");
        simulate("Cash tendered",    "PHP 15,000.00");

        CashPayment cashBooking = new CashPayment(15000.00, 15000.00);
        Booking booking2 = guestService.bookAccommodation(
                guest2, "A002", null,
                LocalDate.of(2025, 12, 22),
                LocalDate.of(2025, 12, 27),
                cashBooking);
        delay(300);

        // ── Demo 3c: Edge — insufficient balance for booking ──
        System.out.println("\n  --- Edge case: insufficient balance for booking ---");
        simulate("Room",            "A003 — Standard Room");
        simulate("Credit balance",  "PHP 100.00 (insufficient for PHP 12,500)");
        CreditCardPayment ccBookFail = new CreditCardPayment(
                "John Reyes", "5500005555555559", "06/26", 100.00);
        guestService.bookAccommodation(
                guest2, "A003", null,
                LocalDate.of(2025, 12, 28),
                LocalDate.of(2025, 12, 30),
                ccBookFail);
        delay(200);

        // ══════════════════════════════════════════════════════
        //  FEATURE 4 — View Park Info
        // ══════════════════════════════════════════════════════
        featureHeader(4, "View Park Info");
        simulate("Action", "Guest requests park map and live info");
        guestService.viewParkInfo(guest);
        delay(300);

        // ══════════════════════════════════════════════════════
        //  FEATURE 5 — Manage Membership
        // ══════════════════════════════════════════════════════
        featureHeader(5, "Manage Membership");

        simulate("Membership ID", "MEM-001");
        simulate("Type",          "SEASON_PASS");
        simulate("Perks",         "Free parking, 10% dining discount, Priority ride access");
        guestService.manageMembership(
                guest, "MEM-001", "SEASON_PASS",
                List.of("Free parking", "10% dining discount", "Priority ride access"));
        delay(200);

        simulate("Membership ID (john)", "MEM-002");
        simulate("Type",                 "VIP");
        simulate("Perks",                "Skip queues, Free meals, Exclusive lounge");
        guestService.manageMembership(
                guest2, "MEM-002", "VIP",
                List.of("Skip queues", "Free meals", "Exclusive lounge"));
        delay(200);

        // ══════════════════════════════════════════════════════
        //  FEATURE 6 — Submit Feedback
        // ══════════════════════════════════════════════════════
        featureHeader(6, "Submit Feedback");

        simulate("Feedback ID", "FB-001");
        simulate("Rating",      "5 / 5");
        simulate("Comment",     "Amazing experience! Rides were thrilling and payment was smooth.");
        guestService.submitFeedback(guest, "FB-001", 5,
                "Amazing experience! Rides were thrilling and payment was smooth.");
        delay(200);

        simulate("Feedback ID (john)", "FB-002");
        simulate("Rating",             "4 / 5");
        simulate("Comment",            "Great resort! The cash discount was a nice bonus.");
        guestService.submitFeedback(guest2, "FB-002", 4,
                "Great resort! The cash discount was a nice bonus.");
        delay(200);

        System.out.println("\n  --- Edge case: invalid rating + empty comment ---");
        simulate("Rating (invalid)", "9");
        simulate("Comment (empty)",  "(blank)");
        guestService.submitFeedback(guest, "FB-BAD", 9, "");
        delay(200);

        // ══════════════════════════════════════════════════════
        //  FEATURE 7 — Check-In / Check-Out
        // ══════════════════════════════════════════════════════
        featureHeader(7, "Self-Service Check-In / Check-Out");

        if (booking != null) {
            String ref = booking.getBookingReference();

            System.out.println("  ── CHECK-IN ────────────────────────────────────");
            simulate("Booking Reference", ref);
            guestService.handleCheckIn(guest, ref);
            delay(200);

            System.out.println("\n  --- Edge case: invalid reference ---");
            simulate("Booking Reference (invalid)", "INVALID-XYZ-999");
            guestService.handleCheckIn(guest, "INVALID-XYZ-999");
            delay(200);

            System.out.println("\n  ── CHECK-OUT ───────────────────────────────────");
            simulate("Booking Reference", ref);
            guestService.handleCheckOut(guest, ref);
            delay(200);
        }

        // ══════════════════════════════════════════════════════
        //  FEATURE 8 — Admin: Generate Reports
        // ══════════════════════════════════════════════════════
        featureHeader(8, "Admin — Generate Reports");

        simulate("Admin ID", "ADM-001");
        simulate("Email",    "admin@themepark.com");
        Admin admin = adminService.registerAdmin("ADM-001", "admin@themepark.com");
        delay(200);

        if (admin != null) {
            for (String type : new String[]{"SALES","OCCUPANCY","VISITOR_STATS","PERFORMANCE"}) {
                simulate("Report type", type);
                adminService.generateReport(admin, type);
                delay(100);
            }
        }

        // ── Final DB Snapshot ─────────────────────────────────
        repositorySnapshot();

        DatabaseConnection.getInstance().close();
        System.out.println("\n[SYSTEM] Demo complete. DB saved → themepark.db. Goodbye! 👋\n");
    }

    // ── Seed Demo Data ────────────────────────────────────────
    static void seedDemoData() {
        List.of("T001","T002","T003").forEach(ticketRepo::delete);
        List.of("A001","A002","A003","A004").forEach(accommodationRepo::delete);
        List.of("USR-001","USR-002","USR-003","ADM-001").forEach(userRepo::delete);

        ticketRepo.save(new Ticket("T001", "SINGLE_DAY",  40.00f, 100));
        ticketRepo.save(new Ticket("T002", "MULTI_DAY",   85.00f,  50));
        ticketRepo.save(new Ticket("T003", "DAY_PASS",    25.00f, 200));

        accommodationRepo.save(new Accommodation("A001", "Deluxe Room",     true));
        accommodationRepo.save(new Accommodation("A002", "Villa Suite",     true));
        accommodationRepo.save(new Accommodation("A003", "Standard Room",   true));
        accommodationRepo.save(new Accommodation("A004", "Family Bungalow", true));

        System.out.println("[SEED] ✅ Tickets: T001 PHP40 | T002 PHP85 | T003 PHP25");
        System.out.println("[SEED] ✅ Rooms  : A001 Deluxe | A002 Villa | A003 Standard | A004 Bungalow");
    }

    // ── Repository Snapshot ───────────────────────────────────
    static void repositorySnapshot() {
        System.out.println();
        divider("═", 66);
        System.out.println("  📊  FINAL REPOSITORY SNAPSHOT  (SQLite DB State)");
        divider("═", 66);
        printSection("USERS",       userRepo.findAll());
        printSection("BOOKINGS",    bookingRepo.findAll());
        printSection("MEMBERSHIPS", membershipRepo.findAll());
        printSection("FEEDBACKS",   feedbackRepo.findAll());
        printSection("REPORTS",     reportRepo.findAll());
        System.out.println("\n  TICKET AVAILABILITY:");
        ticketRepo.findAll().forEach(t -> System.out.println("    → " + t));
        System.out.println("\n  ACCOMMODATION STATUS:");
        accommodationRepo.findAll().forEach(a -> System.out.println("    → " + a));
        divider("═", 66);
    }

    static <T> void printSection(String label, java.util.List<T> items) {
        System.out.println("\n  " + label + " (" + items.size() + " record"
                + (items.size() == 1 ? "" : "s") + "):");
        if (items.isEmpty()) System.out.println("    (none)");
        else items.forEach(i -> System.out.println("    → " + i));
    }

    // ── Display Helpers ───────────────────────────────────────
    static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║   THEME PARK + RESORT MANAGEMENT SYSTEM                         ║");
        System.out.println("║   AUTO-RUN CLI DEMO  |  PaymentFramework integrated             ║");
        System.out.println("║   Guest & Admin (main classes) | Template Method Pattern        ║");
        System.out.println("║   CreditCardPayment & CashPayment as concrete subclasses        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
    }

    static void featureHeader(int n, String title) {
        System.out.println();
        divider("─", 66);
        System.out.printf("  FEATURE %d: %s%n", n, title.toUpperCase());
        divider("─", 66);
    }

    static void simulate(String label, String value) {
        System.out.printf("  > %-32s : %s%n", label, value);
    }

    static void step(String msg) {
        System.out.println("  ⚙  " + msg);
    }

    static void divider(String ch, int len) {
        System.out.println("  " + ch.repeat(len));
    }

    static void delay(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}