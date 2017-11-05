package com.informaciones.facultad.contaduriaalacima.Chat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnEnviarFoto;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private String fotoPerfilCadena;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_pantalla_principal);
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena="";
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");//Sala de chat (nombre)
        storage = FirebaseStorage.getInstance();
        String nombre_perfil = sharedPreferences.getString("nombre", "Anonimo");
        String url_fotoPerfil=sharedPreferences.getString("urlFoto","");
        if (!url_fotoPerfil.isEmpty()){
            Glide.with(Chat.this).load(url_fotoPerfil).into(fotoPerfil);
            fotoPerfilCadena=url_fotoPerfil;
            //Glide.with(Chat.this).load(url_fotoPerfil).into(fotoPerfilMensaje);
        }
        nombre.setText(nombre_perfil);
        adapter = new AdapterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensajeSend=txtMensaje.getText().toString();
                if (!mensajeSend.isEmpty()){
                    databaseReference.push().setValue(new MensajeEnviar(mensajeSend,nombre.getText().toString(),fotoPerfilCadena,"1", ServerValue.TIMESTAMP));
                    txtMensaje.setText("");
                } else {
                    Toast.makeText(getApplicationContext(),"ESCRIBA EL MENSAJE ANTES DE PRESIONAR \"ENVIAR\"",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Chat.this);
                dialog.setContentView(R.layout.chat_seleccionar_fotoperfil);
                final ImageView imgEliminarFP=(ImageView)dialog.findViewById(R.id.imgEliminarFP);
                final ImageView imgSelectFP=(ImageView)dialog.findViewById(R.id.imgSelectFP);
                int width = (int) (Chat.this.getResources().getDisplayMetrics().widthPixels * 0.9);
                // set height for dialog
                int height = (int) (Chat.this.getResources().getDisplayMetrics().heightPixels * 0.55);
                dialog.getWindow().setLayout(width, height);
                dialog.show();
                imgEliminarFP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fotoPerfil.setImageResource(R.drawable.user);
                        //fotoPerfil.setBackground(getDrawable(R.drawable.user));
                        fotoPerfilCadena="";
                        dialog.dismiss();
                    }
                });

                imgSelectFP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFotoPerfil();

                        dialog.dismiss();
                    }
                });
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                adapter.addMensaje(m);
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

    }


    private void updateFotoPerfil(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/jpeg");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_PERFIL);
    }


    public void cambiarNombre(View view){
            final Dialog dialog = new Dialog(Chat.this);
            dialog.setTitle("Nombre");
            dialog.setContentView(R.layout.chat_nombre);
            final EditText nombrePerfil = (EditText) dialog.findViewById(R.id.et_nombre_equipo_eliminar);
            Button boton = (Button) dialog.findViewById(R.id.bt_eliminarequipo);
            int width = (int) (Chat.this.getResources().getDisplayMetrics().widthPixels * 0.9);
            // set height for dialog
            int height = (int) (Chat.this.getResources().getDisplayMetrics().heightPixels * 0.40);
            dialog.getWindow().setLayout(width, height);
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nombrePerfil.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "INSERTE NOMBRE!", Toast.LENGTH_SHORT).show();
                    } else {
                        sharedPreferences.edit().putString("nombre", nombrePerfil.getText().toString()).apply();
                        nombre.setText(nombrePerfil.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri u = taskSnapshot.getDownloadUrl();// aqui  me sale error cuando lo emulo de una api, podrias abrir el enmulador?si aqui es una api
                    //25 corre bien ahorita corre una api 19, y en la 19 si funcuiona?
                    //ya da :v sos un genio ""d nacda Rmuchas graciASe
                    MensajeEnviar m = new MensajeEnviar(nombre.getText().toString()+" envio una foto",u.toString(),nombre.getText().toString(),fotoPerfilCadena,"2", ServerValue.TIMESTAMP);
                    databaseReference.push().setValue(m);
                }
            });
        }else if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri u = taskSnapshot.getDownloadUrl();
                    fotoPerfilCadena = u.toString();
                    MensajeEnviar m = new MensajeEnviar(nombre.getText().toString()+" actualizo su foto de perfil",u.toString(),nombre.getText().toString(),fotoPerfilCadena,"2", ServerValue.TIMESTAMP);
                    databaseReference.push().setValue(m);
                    Glide.with(Chat.this).load(u.toString()).into(fotoPerfil);
                    //Glide.with(Chat.this).load(u.toString()).into(fotoPerfilMensaje);
                    sharedPreferences.edit().putString("urlFoto", u.toString()).apply();
                }
            });
        }
    }
}
