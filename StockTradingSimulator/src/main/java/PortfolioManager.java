import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PortfolioManager {

    private static final String FILE_PATH = "StockTradingSimulator/portfolio.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void savePortfolio(Portfolio portfolio) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(portfolio, writer);
            System.out.println("Portfolio saved to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error saving portfolio: " + e.getMessage());
            // Optionally show an alert to the user
        }
    }

    public static Portfolio loadPortfolio() {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            System.out.println("Portfolio file not found. Creating default portfolio.");
            return new Portfolio(); // Return default portfolio if file doesn't exist
        }

        try (Reader reader = new FileReader(FILE_PATH)) {
            Portfolio portfolio = gson.fromJson(reader, Portfolio.class);
            System.out.println("Portfolio loaded from " + FILE_PATH);
            // Ensure lists are not null if JSON was empty or malformed for lists
            if (portfolio.getHoldings() == null) {
                 portfolio.setHoldings(new ArrayList<>());
            }
             if (portfolio.getWatchlistSymbols() == null) {
                portfolio.setWatchlistSymbols(new ArrayList<>());
            }
            return portfolio;
        } catch (IOException e) {
            System.err.println("Error loading portfolio: " + e.getMessage());
            // Optionally show an alert to the user
            return new Portfolio(); // Return default on error
        } catch (com.google.gson.JsonSyntaxException e) {
             System.err.println("Error parsing portfolio JSON: " + e.getMessage());
             // Handle potentially corrupted file, maybe delete it or return default
             return new Portfolio();
        }
    }
}
