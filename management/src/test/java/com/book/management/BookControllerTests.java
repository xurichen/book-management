package com.book.management;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.management.controller.BookController;
import com.book.management.entity.Book;
import com.book.management.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
@WithMockUser(username = "user", password = "password", roles = "USER")
public class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Book validBook;
    private List<Book> bookList;

    @BeforeEach
    void setUp() {
        validBook = new Book(1, "1984", "George Orwell", "1949", "1234567890");
        bookList = new ArrayList<>(Arrays.asList(validBook));
    }

    @Test
    void shouldCreateBook() throws Exception {

        mockMvc.perform(post("/books")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(validBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(validBook.getTitle()))
                .andExpect(jsonPath("$.author").value(validBook.getAuthor()));

        verify(bookService).save(any(Book.class));
    }

    @Test
    void shouldReturnListOfBooks() throws Exception {
        given(bookService.getAllBook()).willReturn(bookList);

        mockMvc.perform(get("/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(bookList.size()));

        verify(bookService).getAllBook();
    }

    @Test
    void shouldGetBookById() throws Exception {
        when(bookService.getBookById(1)).thenReturn(validBook);

        mockMvc.perform(get("/books/{id}", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(validBook.getTitle()))
                .andExpect(jsonPath("$.author").value(validBook.getAuthor()));


        verify(bookService).getBookById(1);
    }

    @Test
    void shouldUpdateBook() throws Exception {
        when(bookService.getBookById(1)).thenReturn(validBook);
        mockMvc.perform(put("/books/{id}",1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(validBook)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(validBook.getTitle()))
                .andExpect(jsonPath("$.author").value(validBook.getAuthor()));

        verify(bookService).save(any(Book.class));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        when(bookService.getBookById(1)).thenReturn(validBook);
        mockMvc.perform(delete("/books/{id}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() throws Exception {
       // when(bookService.getBookById(1)).thenThrow(new BookNotFoundException(1));

        mockMvc.perform(get("/books/{id}", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookService).getBookById(1);
    }
}

