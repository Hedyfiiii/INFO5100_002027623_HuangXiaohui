/**
 * @author  Hedy Huang
 * @version 1.0
 */
abstract public class Shape {
    private static String color = "green"; //static field
    private String name;

    //constructor
    public Shape (String name) {
        this.name = name;
    }

    //getter and setter
    public static String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public static void setColor(String color) {
        Shape.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    //abstraction
    public abstract double calculateArea();

    public abstract double calculatePerimeter();
}