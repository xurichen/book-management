package com.xuri.bookmanagement.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private String publicationYear;
    private String isbn;

    // Constructor
    public Book(){

    }
    public Book(int id, String title, String author, String publicationYear, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public String getIsbn() {
        return isbn;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"title\":\"" + escapeString(title) + "\","
                + "\"author\":\"" + escapeString(author) + "\","
                + "\"publicationYear\":" + escapeString(publicationYear) + ","
                + "\"isbn\":\"" + escapeString(isbn) + "\""
                + "}";
    }

    public String toJsonWithoutId() {
        return "{"
                + "\"title\":\"" + escapeString(title) + "\","
                + "\"author\":\"" + escapeString(author) + "\","
                + "\"publicationYear\":" + escapeString(publicationYear) + ","
                + "\"isbn\":\"" + escapeString(isbn) + "\""
                + "}";
    }

    // Helper method to escape special characters in JSON string values
    private String escapeString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static Book fromJson(String json) {
        Book book = new Book();

        // Remove the leading '{', trailing '}', and split by ','
        String[] keyValuePairs = json.substring(1, json.length() - 1).split(",");

        for (String pair : keyValuePairs) {
            // Split the key and value by ':'
            String[] entry = pair.split(":", 2);

            // Remove the leading and trailing quotes from the key
            String key = entry[0].trim().replace("\"", "");

            // Check which field to set
            switch (key) {
                case "id":
                    book.setId(Integer.parseInt(entry[1].trim()));
                    break;
                case "title":
                    book.setTitle(unescapeString(entry[1].trim().replace("\"", "")));
                    break;
                case "author":
                    book.setAuthor(unescapeString(entry[1].trim().replace("\"", "")));
                    break;
                case "publicationYear":
                    book.setPublicationYear(entry[1].trim());
                    break;
                case "isbn":
                    book.setIsbn(unescapeString(entry[1].trim().replace("\"", "")));
                    break;
            }
        }

        return book;
    }

    // Helper method to unescape special characters in JSON string values
    private static String unescapeString(String input) {
        return input.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    public static Book jsonToBook(JSONObject jsonObject) throws JSONException, JSONException {
        Book book = new Book();
        book.setId(jsonObject.getInt("id"));
        book.setTitle(jsonObject.optString("title",""));
        book.setAuthor(jsonObject.optString("author",""));
        book.setPublicationYear(jsonObject.optString("publicationYear",""));
        book.setIsbn(jsonObject.optString("isbn",""));
        return book;
    }


}

