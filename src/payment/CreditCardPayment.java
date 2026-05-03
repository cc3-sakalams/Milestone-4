package payment;

public class CreditCardPayment extends PaymentFramework {

    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CreditCardPayment(String cardHolderName,
                              String cardNumber,
                              String expiryDate,
                              double creditBalance) {
        this.cardHolderName         = cardHolderName;
        this.cardNumber             = cardNumber;
        this.expiryDate             = expiryDate;
        this.balance                = creditBalance;
        this.discountRate           = 0.05;   // 5% loyalty discount
        this.isTransactionFinalized = false;
    }

    public void validatePayment() throws PaymentException {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new PaymentException(
                    "Invalid card number. Payment rejected.");
        }
        if (expiryDate == null || expiryDate.isBlank()) {
            throw new PaymentException(
                    "Invalid expiry date. Payment rejected.");
        }
        if (balance < transactionAmount) {
            throw new PaymentException(
                    "Insufficient credit balance. " +
                    "Available: PHP " + String.format("%,.2f", balance) +
                    " | Required: PHP " + String.format("%,.2f", transactionAmount));
        }
        System.out.println("  [CREDIT CARD] ✅ Card validated — " + cardHolderName
                + " | ending: ..." + cardNumber.substring(Math.max(0, cardNumber.length() - 4)));
    }

    protected double applyTax(double amount) {
        double taxed = amount * (1 + TAX_RATE);
        System.out.printf("  [CREDIT CARD]    VAT (12%%): PHP %,.2f → PHP %,.2f%n", amount, taxed);
        return taxed;
    }

    protected double applyDiscount(double amount) {
        double discounted = amount * (1 - discountRate);
        System.out.printf("  [CREDIT CARD]    Loyalty discount (5%%): PHP %,.2f → PHP %,.2f%n",
                amount, discounted);
        return discounted;
    }

    public void finalizeTransaction() {
        double finalAmt = getLastFinalAmount();
        this.balance           -= finalAmt;
        this.isTransactionFinalized = true;
        System.out.printf("  [CREDIT CARD] ✅ Charged: PHP %,.2f | Remaining credit: PHP %,.2f%n",
                finalAmt, balance);
    }

    public String getCardHolderName() { return cardHolderName; }
    public String getCardNumber()     { return cardNumber; }
    public String getExpiryDate()     { return expiryDate; }
}
