// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Auto-run CLI demo | Full Java implementation

package payment;

/**
 * ============================================================
 * CLASS: CashPayment  (Concrete Subclass)
 * ============================================================
 * Handles cash payments for ticket purchases and accommodation
 * bookings in the Theme Park system.
 * Applies a 10% cash discount, validates the cash tendered,
 * and computes the correct change on finalization.
 * ============================================================
 */
public class CashPayment extends PaymentFramework {

    private double cashTendered;  // amount physically handed over
    private double change;        // change to return to customer

    public CashPayment(double cashTendered, double balance) {
        this.cashTendered           = cashTendered;
        this.balance                = balance;
        this.discountRate           = 0.10;   // 10% cash discount
        this.isTransactionFinalized = false;
    }

    /**
     * Validates that cash tendered covers the transaction amount.
     * @throws PaymentException if cash tendered is insufficient
     */
    @Override
    public void validatePayment() throws PaymentException {
        if (cashTendered < transactionAmount) {
            throw new PaymentException(
                    "Insufficient cash tendered. " +
                    "Tendered: PHP " + String.format("%,.2f", cashTendered) +
                    " | Required: PHP " + String.format("%,.2f", transactionAmount));
        }
        System.out.printf("  [CASH] ✅ Cash validated — Tendered: PHP %,.2f%n", cashTendered);
    }

    /**
     * Applies 12% VAT-inclusive tax.
     * Formula: amount * (1 + TAX_RATE)
     */
    @Override
    protected double applyTax(double amount) {
        double taxed = amount * (1 + TAX_RATE);
        System.out.printf("  [CASH]    VAT (12%%): PHP %,.2f → PHP %,.2f%n", amount, taxed);
        return taxed;
    }

    /**
     * Applies 10% cash discount.
     * Formula: amount * (1 - discountRate)
     */
    @Override
    protected double applyDiscount(double amount) {
        double discounted = amount * (1 - discountRate);
        System.out.printf("  [CASH]    Cash discount (10%%): PHP %,.2f → PHP %,.2f%n",
                amount, discounted);
        return discounted;
    }

    /**
     * Computes change, locks the transaction.
     */
    @Override
    public void finalizeTransaction() {
        double finalAmt = getLastFinalAmount();
        this.change             = cashTendered - finalAmt;
        this.isTransactionFinalized = true;
        System.out.printf("  [CASH] ✅ Payment received: PHP %,.2f | Change: PHP %,.2f%n",
                cashTendered, change);
    }

    public double getCashTendered() { return cashTendered; }
    public double getChange()       { return change; }
}
