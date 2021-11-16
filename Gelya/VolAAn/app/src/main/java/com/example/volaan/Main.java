package com.example.volaan;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.tasks.Tasks.await;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.volaan.Adapter.ToDoAdapter;
import com.example.volaan.Models.ToDoModel;
import com.example.volaan.Models.User;
import com.example.volaan.Utils.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main extends AppCompatActivity implements DialogCloseListener{
    //todobegin
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private DatabaseHandler dbtask;
    private List<ToDoModel> taskList;
    //todoend
    User user;
    Context context;
    SharedPreferences sPref;
    TextView userName_TextView;
    String userEmail;
    String userPass;
    FirebaseDatabase db;
    DatabaseReference userthis;
    EditText edittext;

    //new_page begin
    //new_page end


    public NavDestination navDestination;
    String destinationLabel = "new_page";
    //settings
    static final int GALLERY_REQUEST = 1;
    ImageView imageViewProfile;
    //settings end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext();
        sPref = context.getSharedPreferences("Auth", context.MODE_PRIVATE);
        userEmail = loadText("userEmail");
        userPass = loadText("userPass");
        db = FirebaseDatabase.getInstance("https://volaan-41af9-default-rtdb.europe-west1.firebasedatabase.app/");
        dbtask = new DatabaseHandler(this);
        dbtask.openDatabase();

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        userName_TextView = header.findViewById(R.id.userNameforHeader);
        if (sPref.contains("userName")) onUserLoaded(loadText("userName"));
        else findUserName();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                navDestination = destination;
                Log.e("TAG", String.valueOf(navDestination.getLabel()));
                destinationLabel = String.valueOf(navDestination.getLabel());
                }
        });

}


    private void onUserLoaded(String userName)
    {
        userName_TextView.setText(userName);
    }
    private void saveText(String name, String value)
    {
        SharedPreferences.Editor ed =sPref.edit();
        ed.putString(name, value);
        ed.commit();

    }

    private String loadText(String name) {
        if (!sPref.contains(name)) return "";
        return sPref.getString(name, "");
    }

    private void findUserName()
    {
        User user;
        String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userthis = db.getReference("Users").child(UserId);

       ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e("TAG", String.valueOf(dataSnapshot.getValue()));
            User user = dataSnapshot.getValue(User.class);
            Log.e("TAG", user.getName());
         saveText("userName",user.getName());
         onUserLoaded(user.getName());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
        }
    };
        userthis.child("Auth").addValueEventListener(userListener);

    }

    public void new_page(ImageView bad1, ImageView well1, ImageView good1, ImageView excellent1, LinearLayout insideLayout1){

        ImageView bad = bad1;
        ImageView well = well1;
        ImageView good = good1;
        ImageView excellent = excellent1;
        LinearLayout insideLayout = insideLayout1;
        bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("TAG","Кнопка bad нажата");


                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Уф");
                dialog.setMessage("Уф");
                dialog.setTitle("Зарегистрироваться");
                dialog.setMessage("Введите данные");
                LayoutInflater inflater = LayoutInflater.from(context);
                View card_share = inflater.inflate(R.layout.card_share, null);
                dialog.setView(card_share);
                dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {


                    }});
                        Log.e("TAG", "Кнопка bad нажата");

                    }

            });

        well.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG","Кнопка well нажата");


            }
        });
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG","Кнопка good нажата");


            }
        });
        excellent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG","Кнопка excellent нажата");

            }
        });
    }
    public void settings(ImageView imageview) {
        imageViewProfile = imageview;

        Log.e("Image",String.valueOf(imageViewProfile) );

           imageViewProfile.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Log.e("Image",String.valueOf(imageViewProfile) );
                   try {
                           Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                           photoPickerIntent.setType("image/*");
                           startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

                   }
                   catch (Exception e) {
                       e.printStackTrace();
                   }

               }

           });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;


        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageViewProfile.setImageBitmap(bitmap);
                }
        }
    }
    public void to_do(RecyclerView rc1, FloatingActionButton fab1) {
        taskList=new ArrayList<>();
        RecyclerView tasksRecyclerView = rc1;
        if (rc1 != null) {
            tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            tasksRecyclerView.setAdapter(tasksAdapter);
            tasksAdapter = new ToDoAdapter(dbtask,Main.this);
            tasksRecyclerView.setAdapter(tasksAdapter);
            ItemTouchHelper itemTouchHelper = new
                    ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
            itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

            fab = fab1;

            taskList = dbtask.getAllTasks();
            Collections.reverse(taskList);

            tasksAdapter.setTasks(taskList);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
                }
            });




        } else {
            Log.e("TAG", "PROBLEMS");
        }
    }
@Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = dbtask.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

}