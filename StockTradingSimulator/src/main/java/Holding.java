import java.util.Objects;

public class Holding {
    public String symbol;
    public int shares;
    public double averagePurchasePrice;

    // Transient fields are not saved to JSON but used for display
    private transient double currentPrice;
    private transient double totalValue;
    private transient double profitLoss;

    // Default constructor for Gson
    public Holding() {}

    public Holding(String symbol, int shares, double averagePurchasePrice) {
        this.symbol = symbol.toUpperCase();
        this.shares = shares;
        this.averagePurchasePrice = averagePurchasePrice;
    }

    // Getters
    public String getSymbol() { return symbol; }
    public int getShares() { return shares; }
    public double getAveragePurchasePrice() { return averagePurchasePrice; }
    public double getCurrentPrice() { return currentPrice; }
    public double getTotalValue() { return totalValue; }
    public double getProfitLoss() { return profitLoss; }

    // Setters (primarily for internal updates and Gson)
    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public void setShares(int shares) { this.shares = shares; }
    public void setAveragePurchasePrice(double averagePurchasePrice) { this.averagePurchasePrice = averagePurchasePrice; }


    // Method to update calculated fields based on the latest price
    public void updateCalculatedValues(double currentPrice) {
        this.currentPrice = currentPrice;
        this.totalValue = this.shares * this.currentPrice;
        this.profitLoss = (this.currentPrice - this.averagePurchasePrice) * this.shares;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holding holding = (Holding) o;
        return Objects.equals(symbol, holding.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Holding{" +
               "symbol='" + symbol + '\'' +
               ", shares=" + shares +
               ", avgPrice=" + averagePurchasePrice +
               '}';
    }
}