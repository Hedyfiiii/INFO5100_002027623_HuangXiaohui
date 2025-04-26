/**
 * @author Hedy Huang
 * @version 1.0
 */
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageFileImp implements ImageFile {
    private final File originalFile;
    private final String originalName;
    private String format;

    private int originalWidth;
    private int originalHeight;
    private String cameraModel;
    private String location;

    private byte[] convertedBytes; // Store converted output streams
    private long convertedSize;
    private int width;
    private int height;

    // Constructor
    public ImageFileImp(File file) {
        this.originalFile = file;
        this.originalName = file.getName();
        this.format = getFileExtension(file);

        try {
            // Use ImageIO to read the image file and get metadata (width, height and camera info) of images
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                this.originalWidth = img.getWidth();
                this.originalHeight = img.getHeight();
                this.cameraModel = String.valueOf(ExifIFD0Directory.TAG_MODEL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Use ImageMetadataReader to read the image file and get metadata(latitude and longitude) of images
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            Double latitude = null;
            Double longitude = null;
            if (gpsDirectory != null) {
                // Read latitude and longitude
                latitude = gpsDirectory.getDouble(GpsDirectory.TAG_LATITUDE);
                longitude = gpsDirectory.getDouble(GpsDirectory.TAG_LONGITUDE);
            }

            this.location = (latitude == null | longitude == null) ? "null" : "latitude:" + latitude + "; longitude:" + longitude;
        } catch (MetadataException | ImageProcessingException | IOException e) {
            // Don't do anything
        }
    }

    // Use ImageIO.write() to convert image to target format, and save output as ByteArrayOutputStream for downloading later
    public void ConvertImage(BufferedImage image, String formatName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, baos);
            baos.flush();
             // Set the converted image properties
            this.convertedBytes = baos.toByteArray();
            this.convertedSize = convertedBytes.length;
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.format = formatName;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConverted() {
        return convertedBytes != null;
    }

    // Get images properties (height, width, camera, location, etc.)
    public String getProperties() {
        StringBuilder properties = new StringBuilder();

        properties.append("Name: ").append(originalName).append("\n");
        properties.append("Format: ").append(format != null ? format : "N/A").append("\n");
        properties.append("Resolution: ").append(originalWidth).append(" x ").append(originalHeight).append("\n");
        properties.append("Camera Model: ").append(cameraModel).append("\n");
        properties.append("Location: ").append(location).append("\n").append("\n");

        if (isConverted()) {
            properties.append("Converted Size: ").append(convertedSize / 1024).append(" KB\n");
            properties.append("Converted Resolution: ").append(width).append(" x ").append(height).append("\n");
        } else {
            properties.append("⚠ Not converted yet\n");
        }

        return properties.toString();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1).toLowerCase() : "";
    }

    // Getters
    public File getOriginalFile() {
        return originalFile;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getFormat() {
        return format;
    }

    public byte[] getConvertedBytes() {
        return convertedBytes;
    }

    public long getConvertedSize() {
        return convertedSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }
}

