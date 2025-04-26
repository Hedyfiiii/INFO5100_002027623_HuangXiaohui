import java.awt.image.BufferedImage;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public interface ImageFile {
    void ConvertImage(BufferedImage image, String formatName);
    boolean isConverted();
    String getProperties();
}
