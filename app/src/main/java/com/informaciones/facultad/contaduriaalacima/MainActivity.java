package com.informaciones.facultad.contaduriaalacima;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

                adapter = new ImageListAdapter(MainActivity.this, R.layout.image_item, imgList);
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
    DatabaseReference databaseComentarios;
    DatabaseReference databasevISITAS;
    TextToSpeech talk;
    private void tocarListView() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final ImageView imagen = view.findViewById(R.id.item_iv_image);

                final TextView visitas = view.findViewById(R.id.item_tv_visitas);
                visitas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String titulo=imgList.get(i).getTitulo();
                        databasevISITAS = FirebaseDatabase.getInstance().getReference("publicaciones/"+titulo);
                        int visitas=imgList.get(i).getVisitas()+1;
                        databasevISITAS.child("visitas").setValue(visitas);
                        Toast.makeText(MainActivity.this,"holaaaaa",Toast.LENGTH_SHORT ).show();
                    }
                });
                final ImageView imageView = view.findViewById(R.id.item_iv_comentar);
                ImageView imageViewHablar = view.findViewById(R.id.item_iv_hablar);
                ImageView imageViewCompartir = view.findViewById(R.id.item_iv_compartir);
                imageViewCompartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.id.item_iv_image);
//Se guarda la imagen en la SDCARD
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File f = new File( Environment.getExternalStorageDirectory() + File.separator + "tmp" + File.separator + "peter.jpg");
                        try {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage() );
                        }
//compartir imagen
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                        share.putExtra(Intent.EXTRA_TEXT, "Mi imagen");
                        startActivity(Intent.createChooser(share, "Compartir imagen"));
                     /*   Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, imgList.get(i).getTitulo());
                        startActivity(Intent.createChooser(sendIntent, "COMPARTIR APLICACIÓN:"));*/
                    }
                });
                imageViewHablar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        talk = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    Locale locSpanish = new Locale("es_ES");
                                    talk.setLanguage(locSpanish);
                                    talk.speak(imgList.get(i).getTitulo(), TextToSpeech.QUEUE_FLUSH, null);
                                    talk.setPitch( 8.8f);
                                } else {
                                    Toast.makeText(MainActivity.this, "ERROR...", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirVentanaComentario(i);
                    }
                });
            }
        });
    }
    ArrayAdapter ad;
    List<String> item;
    ListView lvComentarios;
    public void abrirVentanaComentario(final int i) {
        Toast.makeText(MainActivity.this,"comentario "+ i,Toast.LENGTH_SHORT).show();
        String titulo=imgList.get(i).getTitulo();
        database = FirebaseDatabase.getInstance().getReference("publicaciones/"+titulo);
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.comentario);
        final EditText msj=(EditText)dialog.findViewById(R.id.et_comentario);
        lvComentarios=(ListView) dialog.findViewById(R.id.lv_comentarios);
        final Button boton=(Button)dialog.findViewById(R.id.btnComentarioEnviar);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.child("comentario").push().setValue(msj.getText().toString());
                //lvComentarios.setSelection(item.size());
            }
        });
        databaseComentarios = FirebaseDatabase.getInstance().getReference("publicaciones/"+titulo+"/comentario");

        databaseComentarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item = new ArrayList<String>();
                //Fetch image data from firebase database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //ImageUpload class require default constructor
                    item.add((String) snapshot.getValue());
                    System.out.println((String) snapshot.getValue()+"-----------------------");
                }
                ad = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_ronald, item);
                lvComentarios.setAdapter(ad);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*int width = (int) (Chat.this.getResources().getDisplayMetrics().widthPixels * 0.9);
        // set height for dialog
        int height = (int) (Chat.this.getResources().getDisplayMetrics().heightPixels * 0.55);*/
        //dialog.getWindow().setLayout(width, height);
        dialog.show();
    }

}
