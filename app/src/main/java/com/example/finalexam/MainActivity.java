package com.example.finalexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.finalexam.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button buttonSignIn, buttonRegister;
    FirebaseAuth auth; //for register
    FirebaseDatabase db; //for connecting to database
    DatabaseReference users; //for work with users

    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignIn = findViewById(R.id.button_signIn); //find button by id
        buttonRegister = findViewById(R.id.button_register); //find button by id

        root = findViewById(R.id.root_element);// find RelativeLayout

        auth = FirebaseAuth.getInstance(); // Starting authorization in DB with getInstance
        db = FirebaseDatabase.getInstance(); // Connecting to DB
        users = db.getReference("Users"); //


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterWindow();
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignInWindow();
            }
        });
    }

    //Sign In Window
    private void showSignInWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this); //Special class to create popup windows
        dialog.setTitle("Sign in"); //show title pf page
        dialog.setMessage("Enter email and password for sign in"); //show message

        LayoutInflater inflater = LayoutInflater.from(this); //Special class that can create a View element from the contents of the layout file
        View sign_in_window = inflater.inflate(R.layout.sign_in_window, null); //get sign in window xml file
        dialog.setView(sign_in_window);

        final MaterialEditText email = sign_in_window.findViewById(R.id.email); //get email
        final MaterialEditText password = sign_in_window.findViewById(R.id.password); // get password

        // setNegative button allows to set the cancel button
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); //Sign in window close
            }
        });

        //setPositiveButton
        dialog.setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Enter your email", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().length()<6) {
                    Snackbar.make(root, "Your password must be longer than 6 characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Error" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });

        dialog.show();
    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("Enter all data for registration");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window, null);
        dialog.setView(register_window);

        final MaterialEditText email = register_window.findViewById(R.id.email); //const
        final MaterialEditText password = register_window.findViewById(R.id.password);
        final MaterialEditText name = register_window.findViewById(R.id.name);
        final MaterialEditText phone = register_window.findViewById(R.id.phone);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Enter your email", Snackbar.LENGTH_SHORT).show();
                    return;
                } // isEmpty checks if a string is empty
                //Snackbar allows to display errors in pop-up windows

                if (TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(root, "Enter your name", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(root, "Enter your phone", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().length()<6) {
                    Snackbar.make(root, "Your password must be longer than 6 characters", Snackbar.LENGTH_SHORT).show();
                    return; //password mast be over 6 characters
                }

                //Registration below
                //function to create user with email and password
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User(); //class with objects which contains data
                        user.setEmail(email.getText().toString());//set email
                        user.setName(name.getText().toString()); //set name
                        user.setPassword(password.getText().toString());// set password
                        user.setPhone(phone.getText().toString());// set phone

                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Snackbar.make(root, "User added successfully", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        dialog.show();
    }


}