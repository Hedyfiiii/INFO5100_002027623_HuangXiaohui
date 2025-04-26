import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class StockQuoteService {

    // --- IMPORTANT ---
    // GET YOUR FREE API KEY from https://www.alphavantage.co/support/#api-key
    // Replace "YOUR_API_KEY" with your actual key.
    // DO NOT commit your actual API key to public repositories.
    private static final String API_KEY = "NKQTB0NH03UWTKNL";
    // ---------------

    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private final HttpClient httpClient;

    public StockQuoteService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Fetches the latest price for a given stock symbol.
     * Note: Alpha Vantage free tier has limitations (e.g., 5 calls/min, 100/day).
     * Returns -1.0 if fetching fails or the symbol is invalid.
     */
    public double fetchQuote(String symbol) {
        if (API_KEY == null || API_KEY.equals("YOUR_API_KEY") || API_KEY.trim().isEmpty()) {
            System.err.println("ERROR: Alpha Vantage API Key is not set in StockQuoteService.java");
            return -1.0; // Indicate error
        }
         if (symbol == null || symbol.trim().isEmpty()) {
             System.err.println("ERROR: Symbol cannot be empty.");
             return -1.0;
         }

        String cleanSymbol = symbol.trim().toUpperCase();
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                BASE_URL, cleanSymbol, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15)) // Timeout for the request itself
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // System.out.println("API Response for " + cleanSymbol + ": " + responseBody); // Debugging

                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                // Check for API error messages or empty results
                 if (jsonObject.has("Error Message")) {
                    System.err.println("API Error for " + cleanSymbol + ": " + jsonObject.get("Error Message").getAsString());
                    return -1.0;
                }
                 if (jsonObject.has("Note")) {
                     System.out.println("API Note for "+ cleanSymbol +": "+ jsonObject.get("Note").getAsString());
                     // This often indicates hitting API limits
                 }

                JsonObject globalQuote = jsonObject.getAsJsonObject("Global Quote");
                if (globalQuote != null && globalQuote.has("05. price")) {
                    // Check if the quote object is empty (can happen for invalid symbols sometimes)
                    if (globalQuote.entrySet().isEmpty()) {
                         System.err.println("Received empty Global Quote object for symbol: " + cleanSymbol);
                         return -1.0;
                    }
                    return globalQuote.get("05. price").getAsDouble();
                } else {
                    System.err.println("Could not find price ('05. price') in API response for symbol: " + cleanSymbol);
                     if (globalQuote == null) System.err.println(" -> 'Global Quote' object was null.");
                    return -1.0;
                }
            } else {
                System.err.println("HTTP Error fetching quote for " + cleanSymbol + ": " + response.statusCode());
                System.err.println("Response Body: " + response.body());
                return -1.0;
            }
        } catch (Exception e) { // Catch broader exceptions (InterruptedException, IOException, JsonSyntaxException, etc.)
            System.err.println("Exception fetching quote for " + cleanSymbol + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed stack trace during debugging
            return -1.0;
        }
    }
}