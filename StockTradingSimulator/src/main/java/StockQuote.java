import java.util.Objects;

// Simple class for Watchlist TableView items
public class StockQuote {
    private String symbol;
    private double price;

    public StockQuote(String symbol, double price) {
        this.symbol = symbol.toUpperCase();
        this.price = price;
    }

    // Getters and Setters needed for TableView PropertyValueFactory
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockQuote that = (StockQuote) o;
        return Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}