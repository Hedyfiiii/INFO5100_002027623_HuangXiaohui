package exercise3b;

import java.io.*;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Deserialization {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //Creat input file path
        String filePath = "exercises/resource/exercise2.txt";
        FileInputStream fileInputStream = new FileInputStream(filePath);

        //Creat new ojectInputStream variable
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        //Read file of Objects
        System.out.println(objectInputStream.readObject());
        System.out.println(objectInputStream.readObject());
        System.out.println(objectInputStream.readObject());
        System.out.println(objectInputStream.readObject());

        //Close objectInputStream
        objectInputStream.close();
    }
}
