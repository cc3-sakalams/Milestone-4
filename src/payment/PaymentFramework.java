// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Auto-run CLI demo | Full Java implementation

package payment;

/**
 * ============================================================
 * CLASS: PaymentFramework  (Abstract)
 * ============================================================
 * Defines the standard template for processing payments.
 * Enforces validation, tax computation (12% VAT-inclusive),
 * discount application, and transaction finalization through
 * the Template Method design pattern. Concrete subclasses must
 * implement the abstract steps while reusing the
 * processInvoice() template.
 * ============================================================
 */
public abstract class PaymentFramework {

    // ── Inner Exception Class ─────────────────────────────────

    /**
     * Custom exception thrown when payment validation or
     * processing fails.
     */
    public static class PaymentException extends Exception {

        public PaymentException(String message) {
            super(message);
        }

        public PaymentException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ── Attributes ────────────────────────────────────────────

    /** The current available balance or credit of the payer. */
    protected double balance;

    /**
     * Constant VAT-inclusive tax rate fixed at 12% (0.12).
     * Shared across all instances.
     */
    protected static final double TAX_RATE = 0.12;

    /**
     * The discount percentage to be applied to the transaction
     * amount (e.g., 0.10 for 10%).
     */
    protected double discountRate;

    /**
     * The original gross transaction amount before tax and
     * discount adjustments.
     */
    protected double transactionAmount;

    /**
     * Flag indicating whether the transaction has been
     * successfully completed and locked.
     */
    protected boolean isTransactionFinalized;

    /**
     * Stores the discounted amount from the last
     * processInvoice() call. Used by getTransactionSummary()
     * to avoid recomputing with side-effectful methods.
     */
    private double lastDiscountedAmount;

    /**
     * Stores the final tax-adjusted amount from the last
     * processInvoice() call. Used by getTransactionSummary()
     * to avoid recomputing with side-effectful methods.
     */
    private double lastFinalAmount;

    // ── Abstract Methods ──────────────────────────────────────

    /**
     * Validates that the payer has sufficient balance or a
     * valid payment method. Must be implemented by subclass.
     * @throws PaymentException if validation fails.
     */
    public abstract void validatePayment() throws PaymentException;

    /**
     * Applies the 12% VAT-inclusive tax to the given amount.
     * Returns the tax-adjusted total.
     * Formula: amount * (1 + TAX_RATE).
     * Must be implemented by subclass.
     * @param amount the base amount to apply tax to
     * @return the tax-adjusted total
     */
    protected abstract double applyTax(double amount);

    /**
     * Applies the configured discountRate to the amount.
     * Returns the discounted value.
     * Formula: amount * (1 - discountRate).
     * Must be implemented by subclass.
     * @param amount the amount to apply discount to
     * @return the discounted amount
     */
    protected abstract double applyDiscount(double amount);

    /**
     * Commits and locks the payment transaction.
     * Sets isTransactionFinalized to true and records
     * the transaction. Must be implemented by subclass.
     */
    public abstract void finalizeTransaction();

    // ── Concrete Methods ──────────────────────────────────────

    /**
     * Template Method that orchestrates the full payment
     * workflow in order:
     *   1. validatePayment()
     *   2. applyDiscount(transactionAmount)
     *   3. applyTax to the discounted amount
     *   4. finalizeTransaction()
     * Updates internal tracking attributes.
     *
     * @param transactionAmount the gross amount to process
     * @return the final tax-adjusted amount
     * @throws PaymentException if validation fails
     */
    public double processInvoice(double transactionAmount)
            throws PaymentException {

        this.transactionAmount = transactionAmount;

        // Step 1: Validate — stops entire flow if invalid
        validatePayment();

        // Step 2: Apply discount to base amount
        lastDiscountedAmount = applyDiscount(transactionAmount);

        // Step 3: Apply VAT tax to discounted amount
        lastFinalAmount = applyTax(lastDiscountedAmount);

        // Step 4: Finalize and lock transaction
        finalizeTransaction();

        return lastFinalAmount;
    }

    /**
     * Returns a formatted summary of the transaction.
     * Uses stored 'last' amounts for consistent reporting
     * without re-running side-effectful methods.
     * @return formatted transaction summary string
     */
    public String getTransactionSummary() {
        if (!isTransactionFinalized) {
            return "  Transaction not yet finalized. " +
                   "Please process invoice first.";
        }

        double discountApplied = transactionAmount - lastDiscountedAmount;
        double taxApplied      = lastFinalAmount   - lastDiscountedAmount;

        return String.format(
            "  Transaction Summary:%n"                            +
            "  ──────────────────────────────────────%n"         +
            "  Original Amount   :  PHP %,10.2f%n"               +
            "  Discount (-%.0f%%)   : -PHP %,10.2f%n"            +
            "  Subtotal          :  PHP %,10.2f%n"               +
            "  VAT    (+%.0f%%)    : +PHP %,10.2f%n"             +
            "  ──────────────────────────────────────%n"         +
            "  Final Amount      :  PHP %,10.2f%n",
            transactionAmount,
            discountRate * 100,
            discountApplied,
            lastDiscountedAmount,
            TAX_RATE * 100,
            taxApplied,
            lastFinalAmount
        );
    }

    // ── Getters & Setters ─────────────────────────────────────

    public double  getBalance()                 { return balance; }
    public void    setBalance(double balance)   { this.balance = balance; }
    public double  getDiscountRate()            { return discountRate; }
    public void    setDiscountRate(double rate) { this.discountRate = rate; }
    public double  getTransactionAmount()       { return transactionAmount; }
    public boolean isTransactionFinalized()     { return isTransactionFinalized; }
    public double  getLastFinalAmount()         { return lastFinalAmount; }
}
