package com.book.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.management.entity.Book;
import com.book.management.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepo;

    public void save(Book b) {
        bookRepo.save(b);
    }

    public List<Book> getAllBook(){
        return bookRepo.findAll();
    }

    public Book getBookById(int id) {
        return bookRepo.findById(id).get();
    }
    public void deleteById(int id) {
        bookRepo.deleteById(id);
    }
}
