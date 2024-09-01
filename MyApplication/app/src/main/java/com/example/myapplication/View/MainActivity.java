package com.example.myapplication.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.example.myapplication.ViewModel.UserViewModel;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ActivityResultLauncher<String> selectImageLauncher;
    private Uri selectedImageUri;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setUsers(users);
            }
        });


        // מאתחלים כפתורים
        Button buttonAddUser = findViewById(R.id.button_add_user);
        Button buttonUpdateUser = findViewById(R.id.button_update_user);
        Button buttonDeleteUser = findViewById(R.id.button_delete_user);

        // טען את הדיאלוג לבחירת תמונה
        selectImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                try {
                    // טען את התמונה מה-Uri ל-Bitmap והגדר אותה ל-ImageView
                    InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonAddUser.setOnClickListener(v -> showAddUserDialog());

        buttonUpdateUser.setOnClickListener(v -> {
            if (!adapter.getUsers().isEmpty()) {
                showUpdateUserDialog(adapter.getUsers().get(0));
            }
        });

        buttonDeleteUser.setOnClickListener(v -> {
            if (!adapter.getUsers().isEmpty()) {
                User userToDelete = adapter.getUsers().get(0);
                userViewModel.delete(userToDelete);
            }
        });
    }

    private void showAddUserDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_user, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle("Add User")
                .setPositiveButton("Add", (dialog, which) -> {
                    EditText nameEditText = view.findViewById(R.id.edit_text_name);
                    EditText emailEditText = view.findViewById(R.id.edit_text_email);
                    if (selectedImageUri != null) {
                        User newUser = new User(nameEditText.getText().toString(), emailEditText.getText().toString(), selectedImageUri.toString());
                        userViewModel.insert(newUser);
                    }
                })
                .setNegativeButton("Cancel", null);
        imageView = view.findViewById(R.id.imageViewUser);
        view.findViewById(R.id.button_upload_image).setOnClickListener(v -> selectImageLauncher.launch("image/*"));
        builder.create().show();
    }

    private void showUpdateUserDialog(User user) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_user, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle("Update User")
                .setPositiveButton("Update", (dialog, which) -> {
                    EditText nameEditText = view.findViewById(R.id.edit_text_name);
                    EditText emailEditText = view.findViewById(R.id.edit_text_email);
                    user.setName(nameEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());
                    if (selectedImageUri != null) {
                        user.setImageUri(selectedImageUri.toString());
                    }
                    userViewModel.update(user);
                })
                .setNegativeButton("Cancel", null);
        imageView = view.findViewById(R.id.imageViewUser);
        view.findViewById(R.id.button_upload_image).setOnClickListener(v -> selectImageLauncher.launch("image/*"));
        // Set existing data
        EditText nameEditText = view.findViewById(R.id.edit_text_name);
        EditText emailEditText = view.findViewById(R.id.edit_text_email);
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        if (user.getImageUri() != null) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(Uri.parse(user.getImageUri()));
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.create().show();
    }
}
