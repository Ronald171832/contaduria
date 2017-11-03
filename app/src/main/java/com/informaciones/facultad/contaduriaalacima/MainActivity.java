package com.informaciones.facultad.contaduriaalacima;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private List<ItemLayout> imgList;
    private ListView lv;
    private ImageListAdapter adapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewImage);
        tocarListView();
        //Show progress dialog during list image loading
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait loading list image...");
        progressDialog.show();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("publicaciones");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //Fetch image data from firebase database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //ImageUpload class require default constructor
                    ItemLayout img = snapshot.getValue(ItemLayout.class);
                    imgList.add(img);
                }

                adapter = new ImageListAdapter(MainActivity.this,R.layout.image_item,imgList);
                //Init adapter
                //Set adapter for listview
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }


    DatabaseReference database;
    private void tocarListView() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                ImageView imageView=view.findViewById(R.id.item_iv_comentar);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this,"comentario "+ i,Toast.LENGTH_SHORT).show();
                        String Id = "publicacion "+i;
                        database = FirebaseDatabase.getInstance().getReference("image/"+Id);
                        database.child("comentario").setValue("dad");
                    }
                });
            }
        });
    }

}
