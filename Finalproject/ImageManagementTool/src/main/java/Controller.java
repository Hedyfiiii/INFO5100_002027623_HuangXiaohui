/**
 * @author Hedy Huang
 * @version 1.0
 */
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Controller {

    @FXML private ComboBox<String> formatBox;
    @FXML private Button convertBtn;
    @FXML private Button downloadBtn;
    @FXML private ProgressBar progressBar;
    @FXML private TextArea statusLabel;
    @FXML private FlowPane previewPane;
    @FXML private TextArea imageInfoArea;

    private final List<ImageFileImp> imageFileImps = new ArrayList<>(); // Allow batch conversion and downloading

    @FXML
    public void initialize() {
        formatBox.getItems().clear();
        formatBox.getItems().addAll("png", "jpg", "bmp", "gif");
        progressBar.setProgress(0);

        // keep the formatBox listener
        formatBox.valueProperty().addListener((obs, oldVal, newVal) -> updateConvertButtonState());
    }

//  Upload image files from the target folder when we click the "Choose Image" button
    @FXML
    public void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Images");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.gif")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
//            Clear previous content
            imageFileImps.clear();
            previewPane.getChildren().clear();
            imageInfoArea.clear();

//            Iterate the list of selected files, create ImageFileImp objects and store them into the list of ImageFileImp
//            Also, show these selected images as thumbnails
            for (File file : selectedFiles) {
                ImageFileImp img = new ImageFileImp(file);
                imageFileImps.add(img);
                addThumbnailToPreview(img);
            }

            statusLabel.setText("Selected " + imageFileImps.size() + " image(s).");
//            Enable convert and download buttons
            convertBtn.setDisable(false);
            downloadBtn.setDisable(true);
        }

        updateConvertButtonState();
    }

    private void addThumbnailToPreview(ImageFileImp imgFile) {
        try (FileInputStream fis = new FileInputStream(imgFile.getOriginalFile())) {
            Image fxImage = new Image(fis, 100, 100, true, true);
            ImageView imageView = new ImageView(fxImage);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            StackPane wrapper = new StackPane(imageView);
            wrapper.setStyle("-fx-padding: 3; -fx-border-color: transparent;");

//            When we click thumbnail of selected image, the properties would be presented at imageInfoArea.
//            Also, the border of the clicked thumbnail would be highlighted
            wrapper.setOnMouseClicked(e -> {
                showImageProperties(imgFile);
                highlightSelected(wrapper);
            });

            previewPane.getChildren().add(wrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void highlightSelected(StackPane selectedWrapper) {
        // Remove highlight from the previous selection
        for (javafx.scene.Node node : previewPane.getChildren()) {
            if (node instanceof StackPane) {
                node.setStyle("-fx-padding: 3; -fx-border-color: transparent;");
            }
        }

        // Add highlight to the selected thumbnail
        selectedWrapper.setStyle("-fx-padding: 3; -fx-border-color: #0078D7; -fx-border-width: 2;");
    }

    private void showImageProperties(ImageFileImp imageFileImp) {
        imageInfoArea.setText(imageFileImp.getProperties());
    }

//    Execute conversion when we click the "Convert" button
    @FXML
    public void handleConvert() {
//        select the target format from formatBox
        String format = formatBox.getValue();
        if (format == null || format.isEmpty()) {
            statusLabel.setText("Please select a format.");
            return;
        }

//        Disable convert button to prevent multiple clicks
        convertBtn.setDisable(true);
        progressBar.setProgress(0);
        statusLabel.setText("Converting...");

        Executors.newSingleThreadExecutor().submit(() -> {
            int total = imageFileImps.size();
            int count = 0;

//            Iterate the list of selected image files and convert each image file by ImageIO
            for (ImageFileImp img : imageFileImps) {
                try {
                    BufferedImage inputImage = ImageIO.read(img.getOriginalFile());
                    img.ConvertImage(inputImage, format);
                } catch (IOException e) {
                    Platform.runLater(() -> statusLabel.appendText("\nFailed to convert: " + img.getOriginalName()));
                    continue;
                }

//                Set progress of progress bar
                count++;
                int finalCount = count;
                Platform.runLater(() -> progressBar.setProgress((double) finalCount / total));
            }

            Platform.runLater(() -> {
                statusLabel.appendText("\nConversion completed.");
                downloadBtn.setDisable(false); // Undisable download button
            });

            convertBtn.setDisable(false); // Re-enable button after task
        });
    }

//    Download converted images to the target directory when we click the "Download" button
    @FXML
    public void handleDownloadAll() {
//        Select a save directory
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Save Images");
        File dir = chooser.showDialog(new Stage());

        if (dir != null) {
//            Iterate the list of ImageFileImps to download each converted image
            for (ImageFileImp img : imageFileImps) {
//               If the byte array of current ImageFileImp (for storing converted image output stream) is null, skip downloading
                if (!img.isConverted()) continue;

//                Execute download
//                Generate output file name
                File outFile = new File(dir, img.getOriginalName().replaceAll("\\..+$", "") + "." + img.getFormat());

//                Write data to a file through FileOutputStream
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(img.getConvertedBytes());
                } catch (IOException e) {
                    statusLabel.appendText("\nFailed to save: " + outFile.getName());
                }
            }

//            Show the save path after download completed
            statusLabel.appendText("\nAll images saved to: " + dir.getAbsolutePath());
        }
    }

    private void updateConvertButtonState() {
        boolean hasImages = !imageFileImps.isEmpty();
        boolean formatSelected = formatBox.getValue() != null && !formatBox.getValue().isEmpty();
        convertBtn.setDisable(!(hasImages && formatSelected)); // Disable the convert button when there's no image and target format selected
    }
}
