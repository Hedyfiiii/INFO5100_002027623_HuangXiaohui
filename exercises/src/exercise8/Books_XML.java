package exercise8;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Books_XML {
    public static void main(String[] args) {
//        Original XML
        String booksXML = """
           <?xml version="1.0" encoding="utf-8"?>
            <BookShelf>
                <Book>
                    <title>Java Programming</title>
                    <publishedYear>2020</publishedYear>
                    <numberOfPages>450</numberOfPages>
                    <authors>
                        <author>John Doe</author>
                        <author>Jane Smith</author>
                    </authors>
                </Book>
                <Book>
                    <title>Python Basics</title>
                    <publishedYear>2019</publishedYear>
                    <numberOfPages>320</numberOfPages>
                    <authors>
                        <author>Alice Johnson</author>
                    </authors>
                </Book>
                <Book>
                    <title>Data Structures</title>
                    <publishedYear>2021</publishedYear>
                    <numberOfPages>600</numberOfPages>
                    <authors>
                        <author>Robert Brown</author>
                        <author>Emily Davis</author>
                        <author>Michael Wilson</author>
                    </authors>
                </Book>
            </BookShelf>
           """;

        try {
//            Parse the original XML
            Document xmldoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(booksXML)));

//          Print out the original XML
            System.out.println("---------the original XML---------");
            NodeList books = xmldoc.getElementsByTagName("Book");
            for (int i = 0; i < books.getLength(); i++) {
                Element book = (Element) books.item(i);
                System.out.println("\nBook" + (i + 1) + ":");
                System.out.println("Title: " + getText(book, "title"));
                System.out.println("Year: " + getText(book, "publishedYear"));
                System.out.println("Pages: " + getText(book, "numberOfPages"));
                System.out.println("Authors: " + String.join(", ", getAuthors(book)));
            }

//          Add sub-elements of new book

            Element newBook = xmldoc.createElement("Book");
            newBook.appendChild(createElement(xmldoc, "title","Think Java: How to Think Like a Computer Scientist"));
            newBook.appendChild(createElement(xmldoc,"publishedYear", String.valueOf(2019)));
            newBook.appendChild(createElement(xmldoc,"numberOfPages",String.valueOf(326)));
            Element authorsElement = xmldoc.createElement("authors");
            authorsElement.appendChild(createElement(xmldoc,"author","Allen B. Downey"));
            authorsElement.appendChild(createElement(xmldoc,"author","Chris Mayfield"));
            newBook.appendChild(authorsElement);

//          Add new book to XML
            xmldoc.getDocumentElement().appendChild(newBook);

//          Print out the updated XML
            System.out.println("\n---------the updated XML---------");
            for (int i = 0; i < books.getLength(); i++) {
                Element book = (Element) books.item(i);
                System.out.println("\nBook" + (i + 1) + ":");
                System.out.println("Title: " + getText(book, "title"));
                System.out.println("Year: " + getText(book, "publishedYear"));
                System.out.println("Pages: " + getText(book, "numberOfPages"));
                System.out.println("Authors: " + String.join(", ", getAuthors(book)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Helper methods
    private static String getText(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private static List<String> getAuthors(Element book) {
        List<String> authors = new ArrayList<>();
        NodeList nodes = book.getElementsByTagName("author");
        for (int i = 0; i < nodes.getLength(); i++) {
            authors.add(nodes.item(i).getTextContent());
        }
        return authors;
    }

    private static Element createElement(Document newdoc, String name, String value) {

        Element e = newdoc.createElement(name);
        e.appendChild(newdoc.createTextNode(value));
        return e;
    }
}
