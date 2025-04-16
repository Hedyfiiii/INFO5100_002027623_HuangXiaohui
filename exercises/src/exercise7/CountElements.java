package exercise7;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
/**
 * @author Hedy Huang
 * @version 1.0
 */

// Write a generic method to count the number of elements in a collection that have a specific property
// (for example, odd integers, prime numbers, palindromes).

public class CountElements {
    public static <T> int countElements(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            throw new IllegalArgumentException("Collection and predicate cannot be null");
        }
        int count = 0;
        for (T item : collection){
            if (predicate.test(item)){
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {

        // Count odd integers
        Collection<Integer> numbers = List.of(1,2,3,4,5,6,7,8,9);
        int oddCount = countElements(numbers, n -> n % 2 != 0);
        System.out.println("Odd integers count: " + oddCount); // Output should be: 5

        // Count prime numbers
        Predicate<Integer> isPrime = n -> {
            if (n <= 1) return false;
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        };
        int primeCount = countElements(numbers, isPrime);
        System.out.println("Prime numbers count: " + primeCount); // Output: 4

        // Count palindromes
        Collection<String> words = List.of("madam", "radar", "civic", "refer", "level");
        Predicate<String> isPalindrome = str -> {
            int left = 0, right = str.length() - 1;
            while (left < right) {
                if (str.charAt(left++) != str.charAt(right--)) {
                    return false;
                }
            }
            return true;
        };
        long palindromeCount = countElements(words, isPalindrome);
        System.out.println("Palindrome count: " + palindromeCount); // Output: 5
    }
}

