import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Portfolio {
    public double cashBalance;
    public List<Holding> holdings;
    public List<String> watchlistSymbols; // Store only symbols for persistence

    public Portfolio() {
        this.cashBalance = 10000.0; // Starting balance
        this.holdings = new ArrayList<>();
        this.watchlistSymbols = new ArrayList<>();
    }

    // Getters
    public double getCashBalance() { return cashBalance; }
    public List<Holding> getHoldings() { return holdings; }
    public List<String> getWatchlistSymbols() { return watchlistSymbols; }

    // Setters (primarily for Gson loading)
    public void setCashBalance(double cashBalance) { this.cashBalance = cashBalance; }
    public void setHoldings(List<Holding> holdings) { this.holdings = holdings; }
    public void setWatchlistSymbols(List<String> watchlistSymbols) { this.watchlistSymbols = watchlistSymbols; }


    // Portfolio management methods
    public Optional<Holding> findHolding(String symbol) {
        return holdings.stream()
                .filter(h -> h.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
    }

    public void addHolding(Holding holding) {
        holdings.add(holding);
    }

    public void removeHolding(Holding holding) {
        holdings.remove(holding);
    }

    public void increaseCash(double amount) {
        this.cashBalance += amount;
    }

    public void decreaseCash(double amount) {
        if (amount > this.cashBalance) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        this.cashBalance -= amount;
    }

     public void addWatchlistSymbol(String symbol) {
        String upperSymbol = symbol.toUpperCase();
        if (!watchlistSymbols.contains(upperSymbol)) {
            watchlistSymbols.add(upperSymbol);
        }
    }

    public void removeWatchlistSymbol(String symbol) {
         watchlistSymbols.removeIf(s -> s.equalsIgnoreCase(symbol));
    }

    // Calculate total value of all stock holdings
    public double calculateHoldingsValue() {
        return holdings.stream()
                       .mapToDouble(Holding::getTotalValue) // Assumes updateCalculatedValues called
                       .sum();
    }

     // Calculate total portfolio value (Cash + Holdings)
    public double calculateTotalPortfolioValue() {
        return cashBalance + calculateHoldingsValue();
    }

    // Calculate total P/L based on current prices vs purchase prices
    public double calculateTotalProfitLoss() {
        return holdings.stream()
                       .mapToDouble(Holding::getProfitLoss) // Assumes updateCalculatedValues called
                       .sum();
    }
}
