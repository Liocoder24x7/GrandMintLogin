package com.apps.sayantan.grandmintlogin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class logIn extends AppCompatActivity {

    FirebaseAuth mAuth;     //Firebase Authentication Object
    EditText mail, pass;
    String M, P;
    ProgressBar bar;
    boolean backPressDialog = false;      //For Back Button Handling(Log-out of account handling)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mail = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        bar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    /*Method works on clicking Sign Up Button*/
    public void userLogIn(View view) {
        M = mail.getText().toString().trim();
        P = pass.getText().toString();
        if (M.length() == 0) {
            Toast.makeText(this, "Enter Mail Id To Proceed", Toast.LENGTH_SHORT).show();
        } else if (P.length() == 0) {
            Toast.makeText(this, "Provide A Password", Toast.LENGTH_SHORT).show();
        } else {
            bar.setVisibility(View.VISIBLE);        //Progress Bar starts
            findViewById(R.id.login).setEnabled(false);     //Button is disabled

            //Sign-in using Firebase Authentication
            mAuth.signInWithEmailAndPassword(M, P)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> logAccount) {

                            bar.setVisibility(View.INVISIBLE);      //Progress Bar Stops
                            findViewById(R.id.login).setEnabled(true);      //Button is enabled

                            //Checks if login is successful or not
                            if (logAccount.isSuccessful()) {

                                //Checks if mail is verified
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    /*Retrieves registered name, sets new view and sets the name in view*/
                                    String text = "Hello " + mAuth.getCurrentUser().getDisplayName();
                                    setContentView(R.layout.user_detail);
                                    TextView msg = findViewById(R.id.userName);
                                    msg.setText(text);
                                    backPressDialog = true;       //Pressing back button will pop up an alert dialog
                                } else {
                                    Toast.makeText(logIn.this, "Email Not Verified.Cant Log in", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(logIn.this, logAccount.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //Back button handling
    @Override
    public void onBackPressed() {
        //backPressDialog is true when user is logged in, otherwise false
        if (backPressDialog) {
            new AlertDialog.Builder(logIn.this)
                    .setCancelable(false)
                    .setMessage("Are you sure to log out?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setContentView(R.layout.activity_log_in);
                            mail.setText("");
                            pass.setText("");
                            backPressDialog = false;
                        }
                    })
                    .show();
        } else {
            //Logs out of account and gets back
            finish();
        }
    }
}
