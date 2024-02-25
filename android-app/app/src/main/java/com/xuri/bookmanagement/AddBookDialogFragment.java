package com.xuri.bookmanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xuri.bookmanagement.model.Book;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBookDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBookDialogFragment extends DialogFragment {

    private Book bookToUpdate;
    private static final String ARG_BOOK = "book";

    public static AddBookDialogFragment newInstance(@Nullable Book book) {
        AddBookDialogFragment fragment = new AddBookDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookToUpdate = (Book) getArguments().getSerializable(ARG_BOOK);
        } else {
            bookToUpdate = null;
        }
    }


    public interface AddBookDialogListener {
        void onDialogPositiveClick(Book book);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    AddBookDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddBookDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddBookDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddBookDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_book_dialog, null);

        final EditText titleInput = view.findViewById(R.id.title);
        final EditText authorInput = view.findViewById(R.id.author);
        final EditText yearInput = view.findViewById(R.id.publicationYear);
        final EditText isbnInput = view.findViewById(R.id.isbn);

        String buttonText = "ADD";
        if (bookToUpdate != null) {
            buttonText = "Updated";
            titleInput.setText(bookToUpdate.getTitle());
            authorInput.setText(bookToUpdate.getAuthor());
            yearInput.setText(bookToUpdate.getPublicationYear());
            isbnInput.setText(bookToUpdate.getIsbn());
        }

        builder.setView(view)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Book newBook = new Book();
                        newBook.setTitle(titleInput.getText().toString());
                        newBook.setAuthor(authorInput.getText().toString());
                        newBook.setPublicationYear(yearInput.getText().toString());
                        newBook.setIsbn(isbnInput.getText().toString());
                        if (bookToUpdate != null)
                            newBook.setId(bookToUpdate.getId());
                        listener.onDialogPositiveClick(newBook);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddBookDialogFragment.this);
                        getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
