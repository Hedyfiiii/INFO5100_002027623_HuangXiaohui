package exercise2;

/**
 * @author Xioahui Huang
 * @version 1.0
 */
public class Main {
    public static void triangleExample (Shape triangle) {
        //Abstaction & Override
        System.out.println(triangle.getName());
        System.out.println("Area = " + triangle.calculateArea());
        System.out.println("Perimeter = " + triangle.calculatePerimeter());
    }

    public static void rectangleExample (Shape rectangle) {
        //Abstaction & Override
        System.out.println(rectangle.getName());
        System.out.println("Area = " + rectangle.calculateArea());
        System.out.println("Perimeter = " + rectangle.calculatePerimeter());
    }

    public static void circleExample (Shape circle) {
        //Abstaction & Override
        System.out.println(circle.getName());
        System.out.println("Area = " + circle.calculateArea());
        System.out.println("Perimeter = " + circle.calculatePerimeter());
    }

    public static void squareExample (Shape square) {
        //Abstaction & Override
        System.out.println(square.getName());
        System.out.println("Area = " + square.calculateArea());
        System.out.println("Perimeter = " + square.calculatePerimeter());
    }


    public static void main(String[] args) {
        //static field
        Shape.setColor("blue"); //modify static field
        System.out.println("Shape's color = " + Shape.getColor());
        System.out.println("======================");

        //Polymorphism
        //Test Triangle
        Shape triangle = new Triangle("triangle",4,5,3,3,30,60,90);
        triangleExample(triangle);
        System.out.println("======================");

        //Test Rectangle
        Shape rectangle = new Rectangle("rectangle",4.5,5.5);
        rectangleExample(rectangle);
        System.out.println("======================");

        //Test Circle
        Shape circle = new Circle("circle",4);
        circleExample(circle);
        System.out.println("======================");

        //Test Square
        Shape square = new Square("square",5);
        squareExample(square);
    }
}