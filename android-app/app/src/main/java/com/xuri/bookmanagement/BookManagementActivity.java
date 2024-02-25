package com.xuri.bookmanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuri.bookmanagement.model.Book;
import com.xuri.bookmanagement.service.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BookManagementActivity extends AppCompatActivity implements AddBookDialogFragment.AddBookDialogListener {

    private EditText searchEditText;
    private Button addButton, deleteButton, updateButton;
    private RecyclerView booksRecyclerView;
    private Book selectedBook; // The book selected from the list
    private ExecutorService executorService;
    private List<Book> books = null;
    private BookAdapter bookAdapter;

    private String username;
    private String password;
    private boolean updateModeOrNot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_management);

        searchEditText = findViewById(R.id.searchBar);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        booksRecyclerView = findViewById(R.id.booksRecyclerView);
        executorService = Executors.newSingleThreadExecutor();
        books = new ArrayList<>();
        bookAdapter = new BookAdapter(books);
        booksRecyclerView.setAdapter(bookAdapter);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        password = intent.getStringExtra("PASSWORD");


        setupSearchBar();
        setupButtons();
        setupRecyclerView();
    }

    private void setupSearchBar() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performBookSearch(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void performBookSearch(String bookId) {
        executorService.execute(() -> {
            Book result = BookService.findBookById(Integer.parseInt(bookId), username, password);
            runOnUiThread(() -> {
                if (result != null) {
                    books.clear();
                    books.add(result);
                    bookAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(BookManagementActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupButtons() {
        addButton.setOnClickListener(v -> {
            updateModeOrNot = false;
            showAddBookDialog(null);
        });

        deleteButton.setOnClickListener(v -> {
            int bookId = bookAdapter.getSelectedBookId();

            if (bookAdapter.getSelectedBookId() != -1) {
                executorService.execute(() -> {
                    Boolean result = BookService.deleteBook(bookId, username, password);
                    runOnUiThread(() -> {
                        if (result) {
                            List<Book> updatedBooks = books.stream()
                                    .filter(book -> book.getId() != bookId)
                                    .collect(Collectors.toList());
                            books.clear();
                            books.addAll(updatedBooks);
                            bookAdapter.notifyDataSetChanged();
                            Toast.makeText(BookManagementActivity.this, "Book Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookManagementActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        updateButton.setOnClickListener(v -> {
            int id = bookAdapter.getSelectedBookId();
            if (id != -1) {
                updateModeOrNot = false;
                showAddBookDialog(findBookById(id));
            }
        });
    }

    private Book findBookById(int bookId) {
        for (Book book : books) {
            if (book.getId() == bookId)
                return book;
        }
        return null;
    }

    private void setupRecyclerView() {
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        executorService.execute(() -> {
            List<Book> result = BookService.getAllBooks("user", "password");
            runOnUiThread(() -> {
                if (result != null) {
                    books.clear();
                    books.addAll(result);
                } else {
                    Toast.makeText(BookManagementActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDialogPositiveClick(Book book) {

        executorService.execute(() -> {
            if (updateModeOrNot) {
                Book result = BookService.addBook(book, username, password);
                runOnUiThread(() -> {
                    if (result != null) {
                        books.add(result);
                        bookAdapter.notifyDataSetChanged();
                        Toast.makeText(BookManagementActivity.this, "Book added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookManagementActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Book result = BookService.updateBook(book, username, password);
                runOnUiThread(() -> {
                    if (result != null) {
                        Book updatedBook = findBookById(result.getId());
                        book.setAuthor(result.getAuthor());
                        book.setTitle(result.getTitle());
                        book.setIsbn(result.getIsbn());
                        book.setPublicationYear(result.getPublicationYear());
                        bookAdapter.notifyDataSetChanged();
                        Toast.makeText(BookManagementActivity.this, "Book updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookManagementActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void showAddBookDialog(Book book) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = AddBookDialogFragment.newInstance(book);
        dialog.show(getSupportFragmentManager(), "AddBookDialogFragment");
    }

    // Additional methods to handle adding, deleting, and updating books...
}
