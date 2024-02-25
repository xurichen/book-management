package com.xuri.bookmanagement.service;

import android.util.Base64;

import com.xuri.bookmanagement.model.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookService {

    private static final String BASE_URL = "http://192.168.10.100:8080/books";

    private static HttpURLConnection sendRequest(String path, String method, String jsonInputString, String username, String password) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Create the basic authentication header
            String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            // Set up the connection to send a request
            connection.setRequestMethod(method);
            if (jsonInputString != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            return connection;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }



    public static Book addBook(Book book, String username, String password) {
        try {
            // Convert the Book object to JSON
            Book result = null;
            String jsonInputString = book.toJsonWithoutId();
            HttpURLConnection connection = sendRequest("","POST", book.toJsonWithoutId(), username, password);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED){
                result = responseToBook(connection);
            }
            // Close the connection
            connection.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Book updateBook(Book book, String username, String password) {
        try {
            Book result = null;
            // Convert the Book object to JSON
            String jsonInputString = book.toJsonWithoutId();
            HttpURLConnection connection = sendRequest("/" + book.getId(),"PUT", book.toJsonWithoutId(), username, password);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                result = responseToBook(connection);
            }
            // Close the connection
            connection.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteBook(int id, String username, String password) {
        boolean result = false;
        try {
            HttpURLConnection connection = sendRequest("/" + id, "DELETE", null, username, password);
            result = (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT);
            connection.disconnect();
            return  result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static Book findBookById(int id, String username, String password) {
        try {
            Book book = null;
            // Convert the Book object to JSON
            String jsonInputString = book.toJsonWithoutId();
            HttpURLConnection connection = sendRequest("/" + book.getId(),"GET", book.toJsonWithoutId(), username, password);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                book = responseToBook(connection);
            }
            // Close the connection
            connection.disconnect();
            return book;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Book> getAllBooks(String username, String password) {
        try {
            List<Book> books = null;
            // Convert the Book object to JSON
            HttpURLConnection connection = sendRequest("" ,"GET", null, username, password);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                books = responseToBooks(connection);
            }
            // Close the connection
            connection.disconnect();
            return books;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Book> responseToBooks(HttpURLConnection connection) throws IOException, JSONException {
        StringBuilder response = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        List<Book> books = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(response.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            books.add(Book.jsonToBook(jsonObject));
        }
        return books;
    }

    private static Book responseToBook(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return Book.fromJson(response.toString());
    }
}




