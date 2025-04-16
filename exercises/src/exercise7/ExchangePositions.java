package exercise7;

import java.util.Arrays;

/**
 * @author Hedy Huang
 * @version 1.0
 */

// Write a generic method to exchange the positions of two different elements in an array.

public class ExchangePositions {

    public static <T> void swap(T[] array, int index1, int index2) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (index1 < 0 || index1 >= array.length || index2 < 0 || index2 >= array.length) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        if (index1 == index2) return; // No need to swap the same index

        T temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    public static void main(String[] args) {
        System.out.println("Example usage with Integer array:");
        Integer[] numbers = {1, 2, 3, 4, 5};
        System.out.println("Before swap:" + Arrays.toString(numbers));
        swap(numbers, 1, 3);
        System.out.println("After swap:" + Arrays.toString(numbers));

        System.out.println("\nExample usage with String array:");
        String[] words = {"Generic", "Array", "Index", "Exchange"};
        System.out.println("Before swap:" + Arrays.toString(words));
        swap(words, 0, 3);
        System.out.println("After swap:" + Arrays.toString(words));

        System.out.println("\nExample usage with Object array:");
        Student[] students = {new Student("Hedy", 9),
                            new Student("Alice",8),
                            new Student("Bob",10),
                            new Student("Cally",7)};
        System.out.println("Before swap:" + Arrays.toString(students));
        swap(students,2,3);
        System.out.println("After swap:" + Arrays.toString(students));
    }
}

class Student{
    private String name;
    private int quizScore;

    public Student(String name, int quizScore) {
        this.name = name;
        this.quizScore = quizScore;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", quizScore=" + quizScore +
                '}';
    }
}

