package com.example.volaan;
import static com.google.android.gms.tasks.Tasks.await;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.volaan.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Context context;
    SharedPreferences sPref;
    Button btnEnt,btnReg;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    DatabaseReference userthis;
    RelativeLayout root;
    String userEmail;
    String userPass;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        sPref = context.getSharedPreferences("Auth", context.MODE_PRIVATE);
          userEmail =loadText("userEmail");
             userPass = loadText("userPass");
        btnEnt=findViewById(R.id.ent);
        btnReg=findViewById(R.id.reg);
        root=findViewById(R.id.root);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance( "https://volaan-41af9-default-rtdb.europe-west1.firebasedatabase.app/");
        users = db.getReference("Users");

        if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass) )
        {
            auth.signInWithEmailAndPassword(userEmail,userPass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            startActivity(new Intent(MainActivity.this, Main.class));
                        }
                    });
        }

        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showRegisterWindow();
            }
        });

        btnEnt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEnterWindow();
            }
        });
    }
    private void showEnterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Войти");
        dialog.setMessage("Введите данные для входа");
        LayoutInflater inflater = LayoutInflater.from(this);
        View entWindow = inflater.inflate(R.layout.enter_window, null);
        dialog.setView(entWindow);
        final EditText pass = entWindow.findViewById(R.id.passwordEnt);
        final EditText email = entWindow.findViewById(R.id.EMailFieldEnt);
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root,"Введите почту!", Snackbar.LENGTH_SHORT).show();
                    return;}
                if(pass.getText().toString().length()<8){
                    Snackbar.make(root,"Пароль должен содержать минимум 8 символов!", Snackbar.LENGTH_SHORT).show();
                    return;}

                //Если всё ок, авторизуем

                saveText("userEmail", email.getText().toString());
                saveText("userPass", pass.getText().toString());
                userEmail =loadText("userEmail");
                userPass = loadText("userPass");
                auth.signInWithEmailAndPassword(userEmail,userPass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, Main.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root,"Ошибка авторизации. " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });
        dialog.show();
    }


    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Зарегистрироваться");
        dialog.setMessage("Введите данные");
        LayoutInflater inflater = LayoutInflater.from(this);
        View regWindow = inflater.inflate(R.layout.register_window, null);
        dialog.setView(regWindow);
       final EditText nickname = regWindow.findViewById(R.id.LoginField);
        final EditText pass = regWindow.findViewById(R.id.password);
        final EditText email = regWindow.findViewById(R.id.EMailField);
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Зарегистрироваться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root,"Введите почту!", Snackbar.LENGTH_SHORT).show();
                    return;}
                if(TextUtils.isEmpty(nickname.getText().toString())){
                    Snackbar.make(root,"Введите логин!", Snackbar.LENGTH_SHORT).show();
                    return;}
                if(pass.getText().toString().length()<8){
                    Snackbar.make(root,"Пароль должен содержать минимум 8 символов!", Snackbar.LENGTH_SHORT).show();
                    return;}

                //Если всё ок, регистрируем
                auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User(email.getText().toString(),nickname.getText().toString(),pass.getText().toString());
                                String UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                users.child(UserId).child("Auth")
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                saveText("userName", nickname.getText().toString());
                                                saveText("userEmail", email.getText().toString());
                                                saveText("userPass", pass.getText().toString());


                                                Snackbar.make(root, "Успешная регистрация!",Snackbar.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this, Main.class));

                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root,"Пользователь с таким адресом уже существует!", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                ;
            }
        });
        dialog.show();
    }
    private void saveText(String name, String value)
    {
        SharedPreferences.Editor ed =sPref.edit();
        ed.putString(name, value);
        ed.commit();
    }
    private String loadText(String name)
    {
        if(!sPref.contains(name)) return "";
        return sPref.getString(name, "");
    }


}