package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEdit,contentEdit;
    ImageButton saveNoteBtn;
    TextView pageTitle;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEdit = findViewById(R.id.notes_title);
        contentEdit = findViewById(R.id.notes_content);
        saveNoteBtn = findViewById(R.id.savenote_btn);
        pageTitle = findViewById(R.id.page_title);
        deleteNoteBtn = findViewById(R.id.deletenote_btn);

        //receive Data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEdit.setText(title);
        contentEdit.setText(content);
        if(isEditMode){
            pageTitle.setText("Edit Note");
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }


        saveNoteBtn.setOnClickListener((v -> saveNote()));
        deleteNoteBtn.setOnClickListener((v) -> deleteNoteFromFirebase());
    }

    void saveNote(){
        String noteTitle = titleEdit.getText().toString();
        String noteContent = contentEdit.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEdit.setError("Title is Required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else{
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note added
                    Utility.showToast(NoteDetailsActivity.this,"Note Added Successfully");
                    finish();
                }
                else{
                    //note not added
                    Utility.showToast(NoteDetailsActivity.this,"Failed While Adding Note");
                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note Deleted Successfully");
                    finish();
                }
                else{
                    //note not deleted
                    Utility.showToast(NoteDetailsActivity.this,"Failed While Deleting Note");
                }
            }
        });
    }
}