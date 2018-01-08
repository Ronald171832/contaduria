package com.informaciones.facultad.contaduriaalacima.Chat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    String id;
    SharedPreferences sharedPreferences;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        iniciar();
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_foto_perfil:
                cambiarFotoDePerfil();
                break;
            case R.id.item_nombre:
                cambiarNombre();
                break;
            case R.id.item_imagen_subir:
                enviarImagenGaleria();
                break;
            case R.id.item_salir:
                onBackPressed();
                break;
            case R.id.item_fondo:
                cambiarFondo();
                break;
        }
        return true;
    }

    private void enviarImagenGaleria() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/jpeg");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_SEND);
    }

    private void cambiarFondo() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/jpeg");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), 3);
    }

    private void iniciar() {
        linearLayout = (LinearLayout) findViewById(R.id.ll_chat);
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
     /*   boolean generarID = sharedPreferences.getBoolean("generar", false);
        if (!generarID) {
            sharedPreferences.edit().putBoolean("generar", true).commit();
            sharedPreferences.edit().putString("id", System.currentTimeMillis() + "").commit();
        }*/
        id = sharedPreferences.getString("id", "");
        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(txtMensaje.getText())) {
                    btnEnviar.setBackground(getDrawable(R.drawable.send_out));
                    btnEnviar.setEnabled(false);
                } else {
                    btnEnviar.setBackground(getDrawable(R.drawable.send_in));
                    btnEnviar.setEnabled(true);
                }
            }
        });

        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");//Sala de chat (nombre)
        storage = FirebaseStorage.getInstance();
        String nombre_perfil = sharedPreferences.getString("nombre", "Anonimo");
        String url_fotoPerfil = sharedPreferences.getString("urlFoto", "");
        if (!url_fotoPerfil.isEmpty()) {
            Glide.with(Chat.this).load(url_fotoPerfil).into(fotoPerfil);
            fotoPerfilCadena = url_fotoPerfil;
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
                String mensajeSend = txtMensaje.getText().toString().trim();
                if (mensajeSend.isEmpty()) {
                    txtMensaje.setError("Debe escribir algo...");
                    txtMensaje.requestFocus();
                    return;
                } else {
                    bloqueado();
                    boolean b = sharedPreferences.getBoolean("bloqueado", false);
                    if (b) {
                        txtMensaje.setError("Usted esta bloqueado!");
                        txtMensaje.requestFocus();
                    } else {
                        Date date = new Date();
                        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String fechaHora = hourdateFormat.format(date);
                        databaseReference.child(fechaHora).setValue(new MensajeEnviar(mensajeSend, nombre.getText().toString(), fotoPerfilCadena, "1", fechaHora, id, null));
                        txtMensaje.setText("");
                    }
                }
            }
        });
        // TODO: VALIDAR QUE SE CARGUE DE LAS IMAGENES DE FIREBASE COMO DE LA GALERIA LOCAL
        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PHOTO_SEND);
                /*Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);*/
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


    private void bloqueado() {
        DatabaseReference reference = database.getReference("bloqueados");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String b = snapshot.getValue(String.class);
                    if (b.equals(id)) {
                        sharedPreferences.edit().putBoolean("bloqueado", true).commit();
                    }
                }
                //sharedPreferences.edit().putBoolean("bloqueado", false).commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void updateFotoPerfil() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/jpeg");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_PERFIL);
    }

    public void cambiarFotoDePerfil() {
        final Dialog dialog = new Dialog(Chat.this);
        dialog.setContentView(R.layout.chat_seleccionar_fotoperfil);
        final ImageView imgEliminarFP = (ImageView) dialog.findViewById(R.id.imgEliminarFP);
        final ImageView imgSelectFP = (ImageView) dialog.findViewById(R.id.imgSelectFP);
        // int width = (int) (Chat.this.getResources().getDisplayMetrics().widthPixels * 0.9);
        // set height for dialog
        // int height = (int) (Chat.this.getResources().getDisplayMetrics().heightPixels * 0.55);
        //  dialog.getWindow().setLayout(width, height);
        dialog.show();
        imgEliminarFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoPerfil.setImageResource(R.drawable.user);
                fotoPerfilCadena = "";
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

    public void cambiarNombre() {
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

    private void setScrollbar() {
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK) { // ENVIAR FOTO DE PERFIL
            final Uri ur = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(ur.getLastPathSegment());
            fotoReferencia.putFile(ur).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri u = taskSnapshot.getDownloadUrl();
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String fechaHora = hourdateFormat.format(date);
                    MensajeEnviar m = new MensajeEnviar(nombre.getText().toString() + " envio una foto", u.toString(), nombre.getText().toString(), fotoPerfilCadena, "2", fechaHora, id, ur.toString());
                    databaseReference.push().setValue(m);
                }
            });
        } else if (requestCode == PHOTO_PERFIL && resultCode == RESULT_OK) {
            Uri u = data.getData();
            String idFotoUsuario = sharedPreferences.getString("id", "");
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(idFotoUsuario);
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri u = taskSnapshot.getDownloadUrl();
                    fotoPerfilCadena = u.toString();
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String fechaHora = hourdateFormat.format(date);
                    //ServerValue.TIMESTAMP;
                    MensajeEnviar m = new MensajeEnviar(nombre.getText().toString() + " actualizo su foto de perfil", u.toString(), nombre.getText().toString(), fotoPerfilCadena, "2", fechaHora, id, null);
                    databaseReference.push().setValue(m);
                    Glide.with(Chat.this).load(u.toString()).into(fotoPerfil);
                    //Glide.with(Chat.this).load(u.toString()).into(fotoPerfilMensaje);
                    sharedPreferences.edit().putString("urlFoto", u.toString()).apply();
                }
            });
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            Uri yourUri = data.getData();
            Drawable yourDrawable = null;
            try {
                InputStream inputStream = getContentResolver().openInputStream(yourUri);
                yourDrawable = Drawable.createFromStream(inputStream, yourUri.toString());
                //  sharedPreferences.edit().putString("urlFotoFondo", yourUri.toString()).apply();
            } catch (FileNotFoundException e) {
                // yourDrawable = getResources().getDrawable(R.drawable.default_image);
            }
            linearLayout.setBackground(yourDrawable);
            //linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.fondo_atras));
        }

    }
}
