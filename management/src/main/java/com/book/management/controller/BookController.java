package com.book.management.controller;

import com.book.management.entity.Book;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BookController {
    @GetMapping("/")
    public String getHome(){
        return "Home";
    }
    @GetMapping("/books")
    public List<Book> getBooks(){
        var books = new ArrayList<Book>();
        books.add(new Book(1,"Yellow","A","2024","ISBN-111"));
        return books;
    }
}
