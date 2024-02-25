package com.xuri.bookmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuri.bookmanagement.model.Book;

import java.util.List;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private int selectedPos = RecyclerView.NO_POSITION;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    public int getSelectedBookId() {
        if (selectedPos != RecyclerView.NO_POSITION) {
            return bookList.get(selectedPos).getId();
        } else {
            return -1; // or any other invalid ID to indicate that there's no selection
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_layout, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bind(book, position);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView idView;
        private TextView titleView;
        private TextView authorView;
        private TextView yearView;
        private TextView isbnView;

        public BookViewHolder(View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.book_id);
            titleView = itemView.findViewById(R.id.book_title);
            authorView = itemView.findViewById(R.id.book_author);
            yearView = itemView.findViewById(R.id.book_publicationYear);
            isbnView = itemView.findViewById(R.id.book_isbn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                }
            });
        }

        public void bind(Book book, int position) {
            idView.setText(String.valueOf(book.getId()));
            titleView.setText(book.getTitle());
            authorView.setText(book.getAuthor());
            yearView.setText(book.getPublicationYear());
            isbnView.setText(book.getIsbn());

            itemView.setBackgroundColor(selectedPos == position ? 0xFFECEFF1 : 0xFFFFFFFF); // Highlight color when selected
        }
    }
}


