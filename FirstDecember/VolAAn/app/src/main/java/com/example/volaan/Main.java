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

import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.volaan.Adapter.NewPostAdapter;
import com.example.volaan.Adapter.ToDoAdapter;
import com.example.volaan.Models.Post;
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
import java.util.Date;
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
    public NavDestination navDestination;
    String destinationLabel = "new_page";
    String UserId;

    //new_page begin
    String type;
    DatabaseReference posts;
    DatabaseReference dbnewpost;
    long count;
    //new_page end

    //profilebegin
    private List<Post> postsList;
    private RecyclerView postsRecyclerView;
    private NewPostAdapter postsAdapter;
    long countProfile;
    DatabaseReference dbnewpostProfile;
    //profileend

    //user_search
    String user_name_search;
    DatabaseReference dbsearch;
    String ID="";
    //user_search

    //settings
    static final int GALLERY_REQUEST = 1;
    ImageView imageViewProfile;
    //settings end

    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext();
        sPref = context.getSharedPreferences("Auth", context.MODE_PRIVATE);
        userEmail = loadText("userEmail",sPref);
        userPass = loadText("userPass",sPref);
        db = FirebaseDatabase.getInstance("https://volaan-41af9-default-rtdb.europe-west1.firebasedatabase.app/");
       dbsearch=db.getReference("Users");
        dbtask = new DatabaseHandler(this);
        dbtask.openDatabase();
        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbnewpost = db.getReference("Users").child(UserId).child("Posts");
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        userName_TextView = header.findViewById(R.id.userNameforHeader);
        if (sPref.contains("userName") ) onUserLoaded(loadText("userName",sPref));
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
        new_page(userName);
    }
    private void saveText(String name, String value, SharedPreferences sh)
    {
        SharedPreferences.Editor ed =sh.edit();
        ed.putString(name, value);
        ed.commit();

    }

    private String loadText(String name, SharedPreferences sh) {
        if (!sh.contains(name)) return "";
        return sh.getString(name, "");
    }

    private void findUserName()
    {
        User user;

        userthis = db.getReference("Users").child(UserId);

       ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e("TAG", String.valueOf(dataSnapshot.getValue()));
            User user = dataSnapshot.getValue(User.class);
            Log.e("TAG", user.getName());
         saveText("userName",user.getName(),sPref);
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

    public void new_page(String username){
        Context context = this;
        type="Bad";
        AlertDialog.Builder dialog1 = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(this);
        View new_page = inflater.inflate(R.layout.fragment_new__page, null);
        ImageView bad = new_page.findViewById(R.id.bad);
        ImageView okay = new_page.findViewById(R.id.okay);
        ImageView good = new_page.findViewById(R.id.good);
        ImageView excellent = new_page.findViewById(R.id.excellent);
        bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("TAG","Кнопка bad нажата");
                type = "Bad";
                bad.setBackgroundColor(getResources().getColor(R.color.purple_200));
                okay.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                good.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                excellent.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
            }


        });

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG","Кнопка okay нажата");
                type = "Okay";
                okay.setBackgroundColor(getResources().getColor(R.color.purple_200));
                bad.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                good.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                excellent.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
            }
        });
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG","Кнопка good нажата");
                type = "Good";
                good.setBackgroundColor(getResources().getColor(R.color.purple_200));
                okay.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                bad.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                excellent.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
            }
        });
        excellent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG","Кнопка excellent нажата");
                type = "Excellent";
                excellent.setBackgroundColor(getResources().getColor(R.color.purple_200));
                bad.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                good.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                okay.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));

            }
        });

        dialog1.setView(new_page);
        dialog1.setTitle("Добрый день, "+ username );
        dialog1.setMessage("Как ваше настроение?");
        dialog1.setNegativeButton("Пропустить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog1.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.e("TAG","Запущено");
                LayoutInflater inflater = LayoutInflater.from(context);
                View card_share = inflater.inflate(R.layout.card_share, null);
                TextView textView = (TextView) card_share.findViewById(R.id.messageByApp);
                dialog1.setView(card_share);
                dialog1.setTitle("Поделиться с друзьями?");
                switch(type){
                    case "Bad":textView.setText("Плохое");break;
                    case "Okay":textView.setText("Okay");break;
                    case "Good":textView.setText("Good");break;
                    case "Excellent":textView.setText("Excellent");break;
                    default:textView.setText("Ошибка");break;
                }

                dialog1.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                dialog1.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialog1.setTitle("Создание нового поста");
                        View new_post = inflater.inflate(R.layout.new_post, null);
                        dialog1.setView(new_post);
                        dialog1.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Main.this, Main.class));
                                dialogInterface.dismiss();
                            }
                        });
                        dialog1.setPositiveButton("Опубликовать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                posts = db.getReference("Users").child(UserId).child("Posts");
                                EditText post_text=new_post.findViewById(R.id.newPost);
                                String postText= String.valueOf(post_text.getText());
                                Date currentDate = new Date();
                                Log.e("TAG",String.valueOf(currentDate));
                                Post new_post = new Post(type,postText,currentDate);

                                posts.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        count=  snapshot.getChildrenCount();
                                        Log.e("TAG", String.valueOf(count));
                                        Log.e("TAG",String.valueOf(snapshot));
                                       new_post_number(count, new_post);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                })
                                ;
                            }
                        });
                        dialog1.show();
                    }});
                dialog1.show();

            }
        });

        dialog1.show();



    }
    public void new_post_number(long count, Post post)
    {
        posts.child(String.valueOf(count)).setValue(post);
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

    public void profile(RecyclerView rc, TextView nameProfile)
    {nameProfile.setText(loadText("userName",sPref));
        postsList=new ArrayList<>();
       postsRecyclerView = rc;
       if (ID=="")
       {
           dbnewpostProfile =dbnewpost;
           nameProfile.setText(loadText("userName",sPref));
       }
       else
       {
           dbnewpostProfile = db.getReference("Users").child(ID).child("Auth");
           dbnewpostProfile.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   User Otheruser =snapshot.getValue(User.class);
                   nameProfile.setText(Otheruser.getName().toString());
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
           dbnewpostProfile = db.getReference("Users").child(ID).child("Posts");

       }
       Log.e("Profile","Start");
        Log.e("IDinProfile",ID);
        if (rc != null) {
            postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            postsAdapter = new NewPostAdapter(dbnewpost,Main.this);
            postsRecyclerView.setAdapter(postsAdapter);
            Log.e("Profile","rc!=null");

            dbnewpostProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("ProfileSnap",String.valueOf(snapshot));
                    for(DataSnapshot postSnapshot: snapshot.getChildren())
                    {
                        postsList.add(postSnapshot.getValue(Post.class));
                        Log.e("Profile",String.valueOf(postSnapshot));
                    }
                    countProfile = snapshot.getChildrenCount();
                    profile2(postsList,countProfile);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
    }



         else {
            Log.e("Profile", "PROBLEMS");
        }
    }
public void profile2(List<Post> postslist, long countprofile)
{Log.e("Profile2",String.valueOf(countprofile));
    Log.e("Profile2",String.valueOf(postslist));
    Collections.reverse( postslist);

    postsAdapter.setPosts(postslist);
    ID="";

}
    public void to_do(RecyclerView rc1, FloatingActionButton fab1) {
        taskList=new ArrayList<>();
        RecyclerView tasksRecyclerView = rc1;
        if (rc1 != null) {
            tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    public void user_search(EditText edit, ImageButton b, LinearLayout l){

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.removeAllViews();
                user_name_search = edit.getText().toString();
                dbsearch.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("SearchSnap",String.valueOf(snapshot));
                        for(DataSnapshot postSnapshot: snapshot.getChildren())
                        {User User_Found;
                        User_Found = postSnapshot.child("Auth").getValue(User.class);
                            if (User_Found.getName().toString().equals(user_name_search))
                            { Log.e("SearchFound",String.valueOf(snapshot));
                                TextView userFound = new TextView(context);
                                l.addView(userFound);
                                userFound.setText(user_name_search);
                                userFound.setTextSize(20);
                                userFound.setGravity(Gravity.CENTER_HORIZONTAL);
                                userFound.setWidth(400);
                                userFound.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userFound.setText("Нажали");
                                        ID=postSnapshot.getKey().toString();
                                        Log.e("ID",ID);
                                        navController.navigate(R.id.action_menuSearch_to_menuProfile);

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

}