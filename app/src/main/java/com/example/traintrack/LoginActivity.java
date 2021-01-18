package com.example.traintrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private String email;
    private String password;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        textInputEmail = findViewById(R.id.login_email);
        textInputPassword = findViewById(R.id.login_password);
    }

    // Function to validate the email when login
    private boolean validateEmail(){
        email = textInputEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            textInputEmail.setError("Field cannot be empty");
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()){
            textInputEmail.setError("Please enter a valid email");
        } else {
            textInputEmail.setError(null);
            return true;
        }
        return false;
    }

    // Function to distinguish whether the user is a student or teacher, so that it will move to the next activity accordingly
    private void isAdmin(String uid){
        DocumentReference documents = fStore.collection("Users").document(uid);
        documents.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                if (documentSnapshot.getString("UserType").equals("Teacher")){
                    startActivity(new Intent(getApplicationContext(), TeacherClassListActivity.class)); // if teacher, move to teacher homepage
                } else {
                    startActivity(new Intent(getApplicationContext(), StudentValidateClasscodeActivity.class)); // if student, move to student homepage
                }
                finish();
            }
        });
    }

    // Function to validate the password input
    private boolean validatePassword(){
        password = Objects.requireNonNull(textInputPassword.getEditText()).getText().toString().trim();
        if (password.isEmpty()){
            textInputPassword.setError("Field cannot be empty");
        } else {
            return true;
        }
        return false;
    }

    // Function to authenticate the login inputs
    public void authentication(View view) {
        // Returns when the either the email or password has been typed wrong
        if (!validateEmail() || !validatePassword()){
            return;
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            isAdmin(authResult.getUser().getUid());
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show());

    }

    // Move to forgotPassword activity
    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}
