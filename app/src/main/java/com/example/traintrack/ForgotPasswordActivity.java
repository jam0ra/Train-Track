package com.example.traintrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        textInputEmail = findViewById(R.id.user_email);
    }

    private boolean validateEmail(){
        String email = textInputEmail.getEditText().getText().toString().trim();

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


    public void resetPassword(View view) {
        if (!validateEmail()){
            return;
        }
        Toast.makeText(this, "Reset Password Link Sent", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
    }
}