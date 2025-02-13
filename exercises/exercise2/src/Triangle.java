/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Triangle extends Shape{
    private double base;
    private double side1;
    private double side2;
    private double height;
    private double angle1;
    private double angle2;
    private double angle3;

    public Triangle(String name, double base, double side1, double side2, double height, double angle1, double angle2, double angle3) {
        super(name);
        this.base = base;
        this.side1 = side1;
        this.side2 = side2;
        this.height = height;
        this.angle1 = angle1;
        this.angle2 = angle2;
        this.angle3 = angle3;
    }

    @Override
    public double calculateArea(){
        return 0.5 * base * height;
    }

    @Override
    public double calculatePerimeter(){
        return base + side1 + side2;
    }
}
