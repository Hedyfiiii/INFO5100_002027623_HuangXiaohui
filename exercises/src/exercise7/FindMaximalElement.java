package exercise7;

import java.util.List;
/**
 * @author Hedy Huang
 * @version 1.0
 */

// Write a generic method to find the maximal element in the range [begin, end) of a list.

public class FindMaximalElement {

    public static <T extends Comparable<T>> T maxInRange(List<T> list, int begin, int end) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List is null or empty");
        }
        if (begin < 0 || end > list.size() || begin >= end) {
            throw new IllegalArgumentException("Invalid range");
        }

        T max = list.get(begin);
        for (int i = begin + 1; i < end; i++) {
            T current = list.get(i);
            if (current.compareTo(max) > 0) {
                max = current;
            }
        }
        return max;
    }

    // Example usage
    public static void main(String[] args) {
        List<Integer> numbers = List.of(3, 7, 2, 9, 1, 5);
        System.out.println("Max (1 to 4): " + maxInRange(numbers, 1, 4)); // Output: 9

        List<String> words = List.of("analysis", "zebra", "honor", "equipment");
        System.out.println("Max (0 to 3): " + maxInRange(words, 0, 3)); // Output: zebra
    }
}
