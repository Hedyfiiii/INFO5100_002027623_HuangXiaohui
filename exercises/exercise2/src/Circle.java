/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Circle extends Shape{
    private final double PI = 3.141592;
    private double radius;

    public Circle(String name, double radius) {
        super(name);
        this.radius = radius;
    }

    public double getPI() {
        return PI;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.round(PI * Math.pow(radius, 2) * 100.0) / 100.0;
    }

    @Override
    public double calculatePerimeter() {
        return Math.round(2 * PI * radius * 100.0) / 100.0;
    }
}
