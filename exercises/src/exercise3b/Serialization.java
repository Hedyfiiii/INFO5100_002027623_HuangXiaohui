package exercise3b;

import exercise2.Circle;
import exercise2.Rectangle;
import exercise2.Square;
import exercise2.Triangle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Serialization {
    public static void main(String[] args) throws IOException {

        //Creat output file path
        String filePath = "exercises/resource/exercise2.txt";
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        //Creat new ojectOutputStream variable
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        //Serialize Objects
        objectOutputStream.writeObject(new Triangle("triangle",4,5,3,3,30,60,90));
        objectOutputStream.writeObject(new Rectangle("rectangle",4.5,5.5));
        objectOutputStream.writeObject(new Circle("square",5));
        objectOutputStream.writeObject(new Square("square",5));

        //Close objectOutputStream
        objectOutputStream.close();
    }
}
