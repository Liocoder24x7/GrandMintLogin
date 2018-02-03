package com.apps.sayantan.grandmintlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;

public class signUp extends AppCompatActivity {

    EditText mail, pass, name;
    FirebaseAuth mAuth;
    String mailId, pWord, userName;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mail = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        name = findViewById(R.id.name);
        bar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    //When user clicks log-in button
    public void mailCheck(View view) {

        mailId = mail.getText().toString().trim();
        pWord = pass.getText().toString();
        userName = name.getText().toString().trim();

        if (mailId.length() == 0) {
            Toast.makeText(this, "Mail Id Required For Verification", Toast.LENGTH_SHORT).show();
        } else if (pWord.length() == 0) {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        } else if (userName.length() == 0) {
            Toast.makeText(this, "User Name Cannot Be Null", Toast.LENGTH_SHORT).show();
        } else {

            bar.setVisibility(View.VISIBLE);// To Show ProgressBar
            findViewById(R.id.signup).setEnabled(false);        //Disabling the button
            mAuth.createUserWithEmailAndPassword(mailId, pWord)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> createAccount) {
                            //Account creation
                            if (createAccount.isSuccessful()) {
                                //Account successfully created
                                Toast.makeText(signUp.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .build());      //username attached to the account
                                //email send for verification
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> sendMail) {
                                                bar.setVisibility(View.INVISIBLE);
                                                findViewById(R.id.signup).setEnabled(true);     //Enables the button
                                                if (sendMail.isSuccessful()) {
                                                    Toast.makeText(signUp.this, "Verification Mail Sent", Toast.LENGTH_SHORT).show();
                                                    //Initialises the text fields
                                                    mail.setText("");
                                                    pass.setText("");
                                                    name.setText("");
                                                    //moves to login page
                                                    startActivity(new Intent(signUp.this, logIn.class));
                                                } else {
                                                    Toast.makeText(signUp.this, "Mail Not Sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                //account could be created so exception handling is done to let the user know what the problem is
                                try {
                                    bar.setVisibility(View.INVISIBLE);
                                    findViewById(R.id.signup).setEnabled(true);
                                    throw createAccount.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(signUp.this, "Already Signed Up. Log In Instead.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(signUp.this, createAccount.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });

        }
    }

    //Intent to switch to log-in page
    public void logInPage(View view) {
        startActivity(new Intent(signUp.this, logIn.class));
    }
}
