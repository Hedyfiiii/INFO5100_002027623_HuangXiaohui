package exercise2;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Triangle extends Shape {
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

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getSide1() {
        return side1;
    }

    public void setSide1(double side1) {
        this.side1 = side1;
    }

    public double getSide2() {
        return side2;
    }

    public void setSide2(double side2) {
        this.side2 = side2;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getAngle1() {
        return angle1;
    }

    public void setAngle1(double angle1) {
        this.angle1 = angle1;
    }

    public double getAngle2() {
        return angle2;
    }

    public void setAngle2(double angle2) {
        this.angle2 = angle2;
    }

    public double getAngle3() {
        return angle3;
    }

    public void setAngle3(double angle3) {
        this.angle3 = angle3;
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "base=" + base +
                ", side1=" + side1 +
                ", side2=" + side2 +
                ", height=" + height +
                ", angle1=" + angle1 +
                ", angle2=" + angle2 +
                ", angle3=" + angle3 +
                '}';
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
