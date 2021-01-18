package com.example.traintrack;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
//, StudentPopWindow.DialogListener
public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    public boolean checkClasscode;  // variable which receives the boolean after validating the classcode
    private String stuClassCode;   //classcode which saves to the student's db
    private String userType;
    private TextInputLayout textInputFullName;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private boolean validUser = false;
    Spinner spinner;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);  //syntax pattern to match the email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        spinner = findViewById(R.id.signup_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        textInputFullName = findViewById(R.id.signup_fullname);
        textInputEmail = findViewById(R.id.signup_email);
        textInputPassword = findViewById(R.id.signup_password);
    }

    // Function to validate the input username
    private boolean validateName(){
        String name = textInputFullName.getEditText().getText().toString().trim();

        if (name.isEmpty()){
            textInputFullName.setError("Field can't be empty"); // Cause an error message when the field is empty
        } else{
            textInputFullName.setError(null);
            return true;
        }

        return false;
    }
    // Function to validate the input email
    private boolean validateEmail(){
        String email = textInputEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            textInputEmail.setError("Field cannot be empty"); // Cause an error message when the field is empty
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()){
            textInputEmail.setError("Please enter a valid email"); // Cause an error message when the email input has wrong syntax
        } else {
            textInputEmail.setError(null);
            return true;
        }
        return false;
    }

    // Function to validate the input password
    private boolean validatePassword(){
        String password = textInputPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()){
            textInputPassword.setError("Field cannot be empty"); // Cause an error message when the field is empty
        } else if (password.length() < 8){
            textInputPassword.setError("Password must have at least 8 characters"); // Cause an error message when the input password has less than 8 characters
        } else {
            return true;
        }
        return false;
    }



    // Onclick function for the Sign Up Button
    public void confirm(View button){
        // Validation on the all the user inputs. Returns when at least anyone of the inputs are typed wrong or empty
        if (!validateName() || !validateEmail() || !validatePassword() || !validUser){
            Toast.makeText(this, "Cannot create account", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = textInputFullName.getEditText().getText().toString().trim();
        String email = textInputEmail.getEditText().getText().toString().trim();
        String password = textInputPassword.getEditText().getText().toString().trim();

        // Creating the User and saving the user information to firebase db
        fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                FirebaseUser user = fAuth.getCurrentUser();
                Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                DocumentReference documents = fStore.collection("Users").document(user.getUid()); // Connecting the firebase with current user
                Map<String, Object> userInfo = new HashMap<>();  // save the information to the firebase db as map<string,object>
                userInfo.put("FullName", name);
                userInfo.put("Email", email);
                userInfo.put("Password", password);
                userInfo.put("UserType", userType);
                userInfo.put("UserID", user.getUid());

                documents.set(userInfo);  // saving the information to the firebase db as map
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }).addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Failed to Create Account"+e, Toast.LENGTH_SHORT).show());

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String type = parent.getItemAtPosition(position).toString();
        if (type.equals(getResources().getStringArray(R.array.user_types)[0])){
            TextView errorText = (TextView)spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select a user type");
        } else {
            validUser = true;
            userType = type;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}