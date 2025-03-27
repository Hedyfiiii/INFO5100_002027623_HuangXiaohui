package exercise8;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Books_JSON {
    public static void main(String[] args) {
//        Original JSON
        String booksJSON = "{\n" +
                " \"BookShelf\": {\n" +
                "   \"Book\": [\n" +
                "      {\n" +
                "        \"title\": \"Head First Java: A Brain-Friendly Guide\",\n" +
                "        \"publishedYear\": 2022,\n" +
                "        \"numberOfPages\": 752,\n" +
                "        \"authors\": [\"Kathy Sierra\", \"Bert Bates\", \"Trisha Gee\"]\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"Effective Java\",\n" +
                "        \"publishedYear\": 2017,\n" +
                "        \"numberOfPages\": 416,\n" +
                "        \"authors\": [\"Joshua Bloch\"]\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"Java Concurrency in Practice\",\n" +
                "        \"publishedYear\": 2006,\n" +
                "        \"numberOfPages\": 432,\n" +
                "        \"authors\": [\"Brian Goetz\", \"Tim Peierls\", \"Joshua Bloch\", \"Joseph Bowbeer\", \"David Holmes\", \"Doug Lea\"]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        try {
//          Parse the original JSON
            JSONArray books = new JSONObject(booksJSON)
                    .getJSONObject("BookShelf")
                    .getJSONArray("Book");

//          Print out the original JSON
            System.out.println("---------the original JSON---------");
            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);
                System.out.println("\nBook " + (i + 1) + ":");
                System.out.println("Title: " + book.getString("title"));
                System.out.println("Year: " + book.getInt("publishedYear"));
                System.out.println("Pages: " + book.getInt("numberOfPages"));
                System.out.println("Authors: " + String.join(", ", getAuthors(book)));
            }

//          Add new book
            JSONObject newBook = new JSONObject();
            newBook.put("title", "Think Java: How to Think Like a Computer Scientist");
            newBook.put("publishedYear", 2019);
            newBook.put("numberOfPages", 326);
            JSONArray authorArray = new JSONArray();
            authorArray.put(0, "Allen B. Downey");
            authorArray.put(1, "Chris Mayfield");
            newBook.put("authors", authorArray);
//          Add new book to "Book"
            books.put(newBook);

//          Print out the updated JSON
            System.out.println("\n---------the updated JSON---------");
            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);
                System.out.println("\nBook " + (i + 1) + ":");
                System.out.println("Title: " + book.getString("title"));
                System.out.println("Year: " + book.getInt("publishedYear"));
                System.out.println("Pages: " + book.getInt("numberOfPages"));
                System.out.println("Authors: " + String.join(", ", getAuthors(book)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    Helper methods
    private static List<String> getAuthors(JSONObject book) {
        List<String> authors = new ArrayList<>();
        JSONArray authorsArr = book.getJSONArray("authors");
        for (int i = 0; i < authorsArr.length(); i++) {
            authors.add(authorsArr.getString(i));
        }
        return authors;
    }
}
