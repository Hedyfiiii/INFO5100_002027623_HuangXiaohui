import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class MainApp extends Application {

    private Portfolio portfolio;
    private final PortfolioManager portfolioManager = new PortfolioManager();
    private final StockQuoteService quoteService = new StockQuoteService();

    // UI Elements
    private Label cashBalanceLabel;
    private Label portfolioValueLabel;
    private Label totalProfitLossLabel;
    private TextField symbolInput;
    private TextField sharesInput;
    private TableView<Holding> portfolioTable;
    private TableView<StockQuote> watchlistTable;
    private Button refreshButton;
    private ProgressIndicator progressIndicator;

    private final ObservableList<Holding> portfolioData = FXCollections.observableArrayList();
    private final ObservableList<StockQuote> watchlistData = FXCollections.observableArrayList();

    // Executor for background tasks
    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // Thread pool for API calls

    // Formatting
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        portfolio = portfolioManager.loadPortfolio();

        primaryStage.setTitle("Stock Trader Simulator");

        // --- Layout ---
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: Controls (Input fields and buttons)
        root.setTop(createControlsBox());

        // Center: Portfolio and Watchlist Tables
        root.setCenter(createTablesBox());

        // Bottom: Status Bar (Balance, P/L, Refresh)
        root.setBottom(createStatusBar());

        // --- Initial Population ---
        updatePortfolioTable();
        updateWatchlistTable(); // Initialize watchlist display
        updateStatusLabels();
        performInitialRefresh(); // Fetch initial quotes

        // --- Scene ---
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Save portfolio on close
        primaryStage.setOnCloseRequest(event -> {
            portfolioManager.savePortfolio(portfolio);
            executorService.shutdown(); // Shutdown thread pool
        });
    }

    // --- UI Creation Methods ---

    private VBox createControlsBox() {
        VBox controlsBox = new VBox(10);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setAlignment(Pos.CENTER);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(5);
        inputGrid.setAlignment(Pos.CENTER);

        inputGrid.add(new Label("Stock Symbol:"), 0, 0);
        symbolInput = new TextField();
        symbolInput.setPromptText("e.g., AAPL");
        inputGrid.add(symbolInput, 1, 0);

        inputGrid.add(new Label("Shares:"), 0, 1);
        sharesInput = new TextField();
        sharesInput.setPromptText("e.g., 10");
        inputGrid.add(sharesInput, 1, 1);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button buyButton = new Button("Buy");
        Button sellButton = new Button("Sell");
        Button watchButton = new Button("Watch");
        Button unwatchButton = new Button("Unwatch"); // Added unwatch

        buyButton.setOnAction(e -> handleBuyAction());
        sellButton.setOnAction(e -> handleSellAction());
        watchButton.setOnAction(e -> handleWatchAction());
        unwatchButton.setOnAction(e -> handleUnwatchAction()); // Added handler

        buttonBox.getChildren().addAll(buyButton, sellButton, watchButton, unwatchButton);

        controlsBox.getChildren().addAll(inputGrid, buttonBox);
        return controlsBox;
    }

    private VBox createTablesBox() {
        VBox tablesBox = new VBox(15);
        tablesBox.setPadding(new Insets(10, 0, 10, 0));

        // Portfolio Table
        Label portfolioTitle = new Label("My Portfolio");
        portfolioTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        portfolioTable = new TableView<>(portfolioData);
        setupPortfolioTableColumns();
        VBox.setVgrow(portfolioTable, Priority.ALWAYS); // Allow table to grow

        // Watchlist Table
        Label watchlistTitle = new Label("Watchlist");
        watchlistTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        watchlistTable = new TableView<>(watchlistData);
        setupWatchlistTableColumns();
         watchlistTable.setMaxHeight(150); // Limit watchlist height

        tablesBox.getChildren().addAll(portfolioTitle, portfolioTable, watchlistTitle, watchlistTable);
        return tablesBox;
    }

     private void setupPortfolioTableColumns() {
        TableColumn<Holding, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

        TableColumn<Holding, Integer> sharesCol = new TableColumn<>("Shares");
        sharesCol.setCellValueFactory(new PropertyValueFactory<>("shares"));
        sharesCol.setStyle("-fx-alignment: CENTER-RIGHT;");


        TableColumn<Holding, Double> avgPriceCol = new TableColumn<>("Avg Buy Price");
        avgPriceCol.setCellValueFactory(new PropertyValueFactory<>("averagePurchasePrice"));
        avgPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : currencyFormat.format(price));
            }
        });
        avgPriceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

         TableColumn<Holding, Double> currentPriceCol = new TableColumn<>("Current Price");
        currentPriceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
         currentPriceCol.setCellFactory(tc -> new TableCell<>() {
             @Override
             protected void updateItem(Double price, boolean empty) {
                 super.updateItem(price, empty);
                 if (empty || price == null || price < 0) { // Handle null or error (-1)
                     setText(empty ? null : "N/A");
                 } else {
                     setText(currencyFormat.format(price));
                 }
             }
         });
         currentPriceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

         TableColumn<Holding, Double> totalValueCol = new TableColumn<>("Total Value");
         totalValueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
         totalValueCol.setCellFactory(tc -> new TableCell<>() {
             @Override
             protected void updateItem(Double value, boolean empty) {
                 super.updateItem(value, empty);
                  if (empty || value == null || getTableRow().getItem() == null || ((Holding)getTableRow().getItem()).getCurrentPrice() < 0) {
                     setText(empty ? null : "N/A");
                 } else {
                     setText(currencyFormat.format(value));
                 }
             }
         });
         totalValueCol.setStyle("-fx-alignment: CENTER-RIGHT;");


        TableColumn<Holding, Double> profitLossCol = new TableColumn<>("P/L");
        profitLossCol.setCellValueFactory(new PropertyValueFactory<>("profitLoss"));
        profitLossCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double pl, boolean empty) {
                super.updateItem(pl, empty);
                 if (empty || pl == null || getTableRow().getItem() == null || ((Holding)getTableRow().getItem()).getCurrentPrice() < 0) {
                    setText(empty ? null : "N/A");
                    setTextFill(javafx.scene.paint.Color.BLACK); // Reset color
                } else {
                    setText(currencyFormat.format(pl));
                    // Change text color based on P/L
                    setTextFill(pl >= 0 ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
                }
            }
        });
        profitLossCol.setStyle("-fx-alignment: CENTER-RIGHT;");


        portfolioTable.getColumns().addAll(symbolCol, sharesCol, avgPriceCol, currentPriceCol, totalValueCol, profitLossCol);
        portfolioTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

     private void setupWatchlistTableColumns() {
        TableColumn<StockQuote, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

        TableColumn<StockQuote, Double> priceCol = new TableColumn<>("Current Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
         priceCol.setCellFactory(tc -> new TableCell<>() {
             @Override
             protected void updateItem(Double price, boolean empty) {
                 super.updateItem(price, empty);
                  if (empty || price == null || price < 0) { // Handle null or error (-1)
                     setText(empty ? null : "N/A");
                 } else {
                     setText(currencyFormat.format(price));
                 }
             }
         });
         priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");


        watchlistTable.getColumns().addAll(symbolCol, priceCol);
        watchlistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Distribute space
    }


    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10, 5, 10, 5));
        statusBar.setAlignment(Pos.CENTER_LEFT);

        cashBalanceLabel = new Label();
        portfolioValueLabel = new Label();
        totalProfitLossLabel = new Label();
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(20, 20); // Smaller indicator

        // Use Pane as a spacer to push refresh button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        refreshButton = new Button("Refresh Quotes");
        refreshButton.setOnAction(e -> refreshAllQuotes(false)); // Manual refresh

        statusBar.getChildren().addAll(
                cashBalanceLabel, portfolioValueLabel, totalProfitLossLabel,
                progressIndicator, spacer, refreshButton
        );
        return statusBar;
    }

    // --- Action Handlers ---

    private void handleBuyAction() {
        String symbol = symbolInput.getText().trim().toUpperCase();
        String sharesText = sharesInput.getText().trim();

        if (symbol.isEmpty() || sharesText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Symbol and Shares cannot be empty.");
            return;
        }

        int sharesToBuy;
        try {
            sharesToBuy = Integer.parseInt(sharesText);
            if (sharesToBuy <= 0) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Shares must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Shares.");
            return;
        }

        // Fetch price in background
        runQuoteFetchTask(symbol, currentPrice -> {
            if (currentPrice < 0) { // Error fetching price
                 showAlert(Alert.AlertType.ERROR, "API Error", "Could not fetch price for " + symbol + ". Check symbol or API key/limits.");
                 return;
            }

             double cost = sharesToBuy * currentPrice;
            if (cost > portfolio.getCashBalance()) {
                showAlert(Alert.AlertType.ERROR, "Transaction Error", "Insufficient cash to buy " + sharesToBuy + " shares of " + symbol + ".");
                return;
            }

            // --- Execute Buy ---
            portfolio.decreaseCash(cost);
            Optional<Holding> existingHoldingOpt = portfolio.findHolding(symbol);

            if (existingHoldingOpt.isPresent()) {
                // Update existing holding
                Holding existingHolding = existingHoldingOpt.get();
                double oldTotalCost = existingHolding.getAveragePurchasePrice() * existingHolding.getShares();
                int newTotalShares = existingHolding.getShares() + sharesToBuy;
                double newAveragePrice = (oldTotalCost + cost) / newTotalShares;

                existingHolding.setShares(newTotalShares);
                existingHolding.setAveragePurchasePrice(newAveragePrice);
            } else {
                // Add new holding
                Holding newHolding = new Holding(symbol, sharesToBuy, currentPrice);
                 newHolding.updateCalculatedValues(currentPrice); // Set initial current price
                portfolio.addHolding(newHolding);
            }

             // Add to watchlist if not already there (optional, could be removed)
             portfolio.addWatchlistSymbol(symbol);

            // --- Update UI & Save ---
            Platform.runLater(() -> {
                clearInputs();
                portfolioManager.savePortfolio(portfolio); // Save after successful transaction
                refreshAllQuotes(true); // Refresh UI and quotes after buy
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bought " + sharesToBuy + " shares of " + symbol + " at " + currencyFormat.format(currentPrice));
            });
        });
    }


    private void handleSellAction() {
         String symbol = symbolInput.getText().trim().toUpperCase();
        String sharesText = sharesInput.getText().trim();

        if (symbol.isEmpty() || sharesText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Symbol and Shares cannot be empty.");
            return;
        }

         int sharesToSell;
        try {
            sharesToSell = Integer.parseInt(sharesText);
             if (sharesToSell <= 0) {
                 showAlert(Alert.AlertType.ERROR, "Input Error", "Shares must be a positive number.");
                 return;
             }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Shares.");
            return;
        }

        Optional<Holding> holdingOpt = portfolio.findHolding(symbol);
        if (holdingOpt.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Transaction Error", "You do not own any shares of " + symbol + ".");
            return;
        }

        Holding holding = holdingOpt.get();
        if (sharesToSell > holding.getShares()) {
            showAlert(Alert.AlertType.ERROR, "Transaction Error", "You only own " + holding.getShares() + " shares of " + symbol + ". Cannot sell " + sharesToSell + ".");
            return;
        }

        // Fetch price in background
        runQuoteFetchTask(symbol, currentPrice -> {
             if (currentPrice < 0) { // Error fetching price
                 showAlert(Alert.AlertType.ERROR, "API Error", "Could not fetch price for " + symbol + " for selling. Try again later.");
                 return;
            }

            double proceeds = sharesToSell * currentPrice;

            // --- Execute Sell ---
            portfolio.increaseCash(proceeds);
            holding.setShares(holding.getShares() - sharesToSell);

            if (holding.getShares() == 0) {
                portfolio.removeHolding(holding); // Remove if all shares are sold
            }

            // --- Update UI & Save ---
             Platform.runLater(() -> {
                 clearInputs();
                 portfolioManager.savePortfolio(portfolio); // Save after successful transaction
                 refreshAllQuotes(true); // Refresh UI and quotes after sell
                 showAlert(Alert.AlertType.INFORMATION, "Success", "Sold " + sharesToSell + " shares of " + symbol + " at " + currencyFormat.format(currentPrice));
             });
        });
    }

    private void handleWatchAction() {
        String symbol = symbolInput.getText().trim().toUpperCase();
        if (symbol.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Symbol cannot be empty.");
            return;
        }

        if (portfolio.getWatchlistSymbols().contains(symbol)) {
             showAlert(Alert.AlertType.INFORMATION, "Watchlist", symbol + " is already on the watchlist.");
             return;
        }

        // Add to model and save
        portfolio.addWatchlistSymbol(symbol);
        portfolioManager.savePortfolio(portfolio);

        // Fetch quote and update table
        runQuoteFetchTask(symbol, price -> {
            if (price >= 0) {
                 StockQuote quote = new StockQuote(symbol, price);
                 Platform.runLater(() -> {
                     // Avoid duplicates in the observable list just in case
                     watchlistData.removeIf(q -> q.getSymbol().equals(symbol));
                     watchlistData.add(quote);
                     watchlistTable.sort(); // Keep it sorted if needed
                     showAlert(Alert.AlertType.INFORMATION, "Watchlist", symbol + " added to watchlist.");
                 });
            } else {
                 // Add even if quote fetch failed initially, will update on refresh
                 Platform.runLater(() -> {
                     watchlistData.removeIf(q -> q.getSymbol().equals(symbol));
                     watchlistData.add(new StockQuote(symbol, -1.0)); // Add with error price
                     watchlistTable.sort();
                      showAlert(Alert.AlertType.WARNING, "Watchlist", symbol + " added, but couldn't fetch initial price. Will try on refresh.");
                 });
            }
             Platform.runLater(this::clearInputs);
        });
    }

      private void handleUnwatchAction() {
        String symbol = symbolInput.getText().trim().toUpperCase();
        if (symbol.isEmpty()) {
             // Try getting symbol from selected watchlist item if input is empty
            StockQuote selected = watchlistTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                symbol = selected.getSymbol();
            } else {
                 showAlert(Alert.AlertType.ERROR, "Input Error", "Enter a symbol or select one from the watchlist to unwatch.");
                 return;
            }
        }

        if (!portfolio.getWatchlistSymbols().contains(symbol)) {
             showAlert(Alert.AlertType.INFORMATION, "Watchlist", symbol + " is not on the watchlist.");
             return;
        }

        // Remove from model and save
        portfolio.removeWatchlistSymbol(symbol);
        portfolioManager.savePortfolio(portfolio);

        // Remove from UI
        final String symbolToRemove = symbol; // Final variable for lambda
        Platform.runLater(() -> {
            watchlistData.removeIf(q -> q.getSymbol().equalsIgnoreCase(symbolToRemove));
            showAlert(Alert.AlertType.INFORMATION, "Watchlist", symbolToRemove + " removed from watchlist.");
            clearInputs();
        });
    }


    // --- Data Refresh and UI Update ---

    private void performInitialRefresh() {
        // Fetch quotes for existing portfolio and watchlist on startup
        refreshAllQuotes(false);
    }

     /**
     * Refreshes quotes for all holdings and watchlist items.
     * @param isPostTransaction True if called after a buy/sell, false for manual/initial refresh.
     */
    private void refreshAllQuotes(boolean isPostTransaction) {
        setLoading(true);

        // Combine symbols from holdings and watchlist, ensuring uniqueness
        List<String> symbolsToRefresh = new ArrayList<>(portfolio.getWatchlistSymbols());
        portfolio.getHoldings().stream()
                .map(Holding::getSymbol)
                .filter(symbol -> !symbolsToRefresh.contains(symbol)) // Avoid duplicates
                .forEach(symbolsToRefresh::add);

        if (symbolsToRefresh.isEmpty()) {
             Platform.runLater(() -> {
                 updatePortfolioTable(); // Ensure tables are up-to-date even if empty
                 updateWatchlistTable();
                 updateStatusLabels();
                 setLoading(false);
             });
             return; // Nothing to refresh
         }

        // Use a Task for background execution
        Task<Map<String, Double>> refreshTask = new Task<>() {
            @Override
            protected Map<String, Double> call() throws Exception {
                Map<String, Double> fetchedPrices = new HashMap<>();
                long delayMillis = 13000; // ~13 seconds between calls (5 calls/min limit for AlphaVantage free)

                for (int i = 0; i < symbolsToRefresh.size(); i++) {
                     if (isCancelled()) break;

                     String symbol = symbolsToRefresh.get(i);
                     updateMessage("Fetching " + symbol + "..."); // Update task message
                     double price = quoteService.fetchQuote(symbol);
                     fetchedPrices.put(symbol, price); // Store price (or -1.0 on error)

                     // Apply delay *after* the first call if more calls remain
                     if (i < symbolsToRefresh.size() - 1) {
                         try {
                             Thread.sleep(delayMillis);
                         } catch (InterruptedException e) {
                             if (isCancelled()) break; // Exit if interrupted during sleep
                         }
                     }
                 }
                return fetchedPrices;
            }
        };

         // Update UI on Succeeded
        refreshTask.setOnSucceeded(event -> {
            Map<String, Double> fetchedPrices = refreshTask.getValue();

             // Update holding data with new prices
            portfolio.getHoldings().forEach(holding -> {
                double price = fetchedPrices.getOrDefault(holding.getSymbol(), -1.0); // Use fetched price or error default
                 holding.updateCalculatedValues(price);
            });

             // Update watchlist data
             List<StockQuote> updatedWatchlist = portfolio.getWatchlistSymbols().stream()
                     .map(symbol -> new StockQuote(symbol, fetchedPrices.getOrDefault(symbol, -1.0)))
                     .collect(Collectors.toList());
             watchlistData.setAll(updatedWatchlist);


            // Update UI Tables and Labels
            updatePortfolioTable(); // Refreshes portfolio table with new data
             // Watchlist data was updated directly above with setAll
             watchlistTable.refresh(); // Refresh display
             updateStatusLabels();
            setLoading(false);
             System.out.println("Quote refresh complete.");
             if (!isPostTransaction) { // Only show alert on manual refresh
                 showAlert(Alert.AlertType.INFORMATION, "Refresh Complete", "Stock quotes have been updated.");
             }
        });

        // Handle Failure
        refreshTask.setOnFailed(event -> {
            Throwable error = refreshTask.getException();
            System.err.println("Error during quote refresh task: " + error.getMessage());
             error.printStackTrace(); // For detailed debugging
             Platform.runLater(() -> {
                 updateStatusLabels(); // Update status even on failure
                 setLoading(false);
                 showAlert(Alert.AlertType.ERROR, "Refresh Error", "Failed to refresh all quotes. Check console for details.");
             });
        });

         // Bind progress indicator to task progress (optional)
         // progressIndicator.progressProperty().bind(refreshTask.progressProperty());
         // You could also bind a label to refreshTask.messageProperty()

        // Run the task
        executorService.submit(refreshTask);
    }

     // Helper method to run a single quote fetch task (used by Buy/Sell/Watch)
    private void runQuoteFetchTask(String symbol, java.util.function.Consumer<Double> onComplete) {
        setLoading(true);
        Task<Double> fetchTask = new Task<>() {
            @Override
            protected Double call() throws Exception {
                 updateMessage("Fetching " + symbol + "...");
                 // Add a small delay here if needed to avoid rapid calls after refresh
                 // Thread.sleep(1000);
                return quoteService.fetchQuote(symbol);
            }
        };

        fetchTask.setOnSucceeded(e -> {
             setLoading(false);
            onComplete.accept(fetchTask.getValue());
        });

        fetchTask.setOnFailed(e -> {
             setLoading(false);
             Throwable error = fetchTask.getException();
             System.err.println("Failed to fetch quote for " + symbol + ": " + error.getMessage());
             onComplete.accept(-1.0); // Indicate failure
        });

         executorService.submit(fetchTask);
    }


    private void updatePortfolioTable() {
        portfolioData.setAll(portfolio.getHoldings()); // Update observable list
         portfolioTable.sort(); // Re-apply sort if columns are clicked
    }

    private void updateWatchlistTable() {
         // Rebuild watchlistData based on symbols and potentially cached prices (or fetch)
         // For simplicity, this is handled within refreshAllQuotes now
         // You could implement separate logic if needed
        watchlistTable.sort();
    }

    private void updateStatusLabels() {
        double currentHoldingsValue = portfolio.calculateHoldingsValue();
        double totalPortfolioValue = portfolio.calculateTotalPortfolioValue();
        double totalPL = portfolio.calculateTotalProfitLoss();

        cashBalanceLabel.setText("Cash: " + currencyFormat.format(portfolio.getCashBalance()));
        portfolioValueLabel.setText("Portfolio Value: " + currencyFormat.format(totalPortfolioValue));
        totalProfitLossLabel.setText("Total P/L: " + currencyFormat.format(totalPL));

        // Set P/L label color
        totalProfitLossLabel.setTextFill(totalPL >= 0 ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
    }

    private void setLoading(boolean isLoading) {
         Platform.runLater(() -> {
             progressIndicator.setVisible(isLoading);
             refreshButton.setDisable(isLoading);
             // Consider disabling other buttons (Buy/Sell/Watch) during refresh too
         });
    }


    private void clearInputs() {
        symbolInput.clear();
        sharesInput.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        // Ensure alerts are shown on the JavaFX Application Thread
         if (Platform.isFxApplicationThread()) {
             Alert alert = new Alert(type);
             alert.setTitle(title);
             alert.setHeaderText(null); // No header
             alert.setContentText(message);
             alert.showAndWait();
         } else {
             Platform.runLater(() -> {
                 Alert alert = new Alert(type);
                 alert.setTitle(title);
                 alert.setHeaderText(null);
                 alert.setContentText(message);
                 alert.showAndWait();
             });
         }
    }
}