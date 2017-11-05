package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Publicaciones extends AppCompatActivity {
    private DatabaseReference dbPublicaciones;
    private List<PublicacionesModel> listaPublicaciones;
    private ListView lv;
    private PublicacionesListAdapter adapter;
    private ProgressDialog progressDialog;
    TextToSpeech talk;
    String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicaciones_principal);
        iniciar();
        tocarListView();
    }


    private void iniciar() {
        listaPublicaciones = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewPublicaciones);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Publicaciones...");
        progressDialog.show();
        categoria = getIntent().getStringExtra("categoria");
        dbPublicaciones = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/publicaciones");
        dbPublicaciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(listaPublicaciones.size()<=0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PublicacionesModel img = snapshot.getValue(PublicacionesModel.class);
                        listaPublicaciones.add(img);
                    }
                    adapter = new PublicacionesListAdapter(Publicaciones.this, R.layout.publicacion_card, listaPublicaciones);
                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void tocarListView() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                ImageView comentario = view.findViewById(R.id.cardComentarPublicacion);
                ImageView hablar = view.findViewById(R.id.cardHablarPublicacion);
                ImageView compartir = view.findViewById(R.id.cardCompartirPublicacion);
                ImageView like = view.findViewById(R.id.cardLikePublicaciones);
                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     /*   String titulo = imgList.get(i).getTitulo();
                        databasevISITAS = FirebaseDatabase.getInstance().getReference("publicaciones/" + titulo);
                        int visitas = imgList.get(i).getVisitas() + 1;
                        databasevISITAS.child("visitas").setValue(visitas);*/
                    }
                });

                compartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  Bitmap icon = BitmapFactory.decodeResource(getResources(), R.id.item_iv_image);
//Se guarda la imagen en la SDCARD
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                       // icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "tmp" + File.separator + "peter.jpg");
                        try {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
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
                hablar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        talk = new TextToSpeech(Publicaciones.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    Locale locSpanish = new Locale("es_ES");
                                    talk.setLanguage(locSpanish);
                                    talk.speak(listaPublicaciones.get(i).getTitulo(), TextToSpeech.QUEUE_FLUSH, null);
                                    talk.setPitch(8.8f);
                                } else {
                                    Toast.makeText(Publicaciones.this, "ERROR...", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                comentario.setOnClickListener(new View.OnClickListener() {
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
    DatabaseReference dbComentar;

    public void abrirVentanaComentario(final int i) {
        final String titulo = listaPublicaciones.get(i).getTitulo();
        dbComentar = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/publicaciones/" + titulo + "/comentario");
        final Dialog dialog = new Dialog(Publicaciones.this);
        dialog.setContentView(R.layout.publicaciones_comentario);
        final EditText msj = (EditText) dialog.findViewById(R.id.et_comentario);
        lvComentarios = (ListView) dialog.findViewById(R.id.lv_comentarios);
        final Button boton = (Button) dialog.findViewById(R.id.btnComentarioEnviar);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbComentar.push().setValue(msj.getText().toString().trim());
                dialog.cancel();
            }
        });
        dbComentar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    item.add((String) snapshot.getValue());
                }
                ad = new ArrayAdapter<String>(Publicaciones.this, R.layout.publicaciones_comentario_estilo, item);
                lvComentarios.setAdapter(ad);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        dialog.getWindow().setLayout(width, height);
        dialog.show();
    }
}
