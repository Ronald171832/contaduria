package com.informaciones.facultad.contaduriaalacima.RegistroDeDatos;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.PantallaPrincipal.MainActivity;
import com.informaciones.facultad.contaduriaalacima.R;

public class Registro_Datos extends AppCompatActivity {
    private ImageView foto;
    private EditText nombre;
    StorageReference storageReference;
    SharedPreferences sharedPreferences;
    private FirebaseStorage storage;


    private Uri imguri;
    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_datos);
        iniciar();
    }

    private void iniciar() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();
        foto = (ImageView) findViewById(R.id.iv_perfil_foto);
        nombre = (EditText) findViewById(R.id.et_perfil_nombre);
    }

    public void elegirImagenPerfil(View v) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imguri = data.getData();
            foto.setImageURI(imguri);
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void registrarDatos(View view) {
        if (imguri == null) {
            Toast.makeText(getApplicationContext(), "Elije una imagen porfavor!", Toast.LENGTH_SHORT).show();

            return;
        }
        if (nombre.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Ingresa tu nombre porfavor!", Toast.LENGTH_SHORT).show();
            return;
        }
        sharedPreferences.edit().putString("nombre", nombre.getText().toString().trim()).apply();
        Uri u = imguri;
        final ProgressDialog subirImagen = new ProgressDialog(this);
        subirImagen.setTitle("Cargando imagen...");
        subirImagen.setCancelable(false);
        subirImagen.show();
        /////////////////////////////////////////////////////////////////////////
        String idFotoUsuario = "";
        boolean generarID = sharedPreferences.getBoolean("generar", true);
        if (generarID) {
            sharedPreferences.edit().putBoolean("generar", false).commit();
            idFotoUsuario = System.currentTimeMillis() + "";
            sharedPreferences.edit().putString("id", idFotoUsuario).commit();
        }

        storageReference = storage.getReference("foto_perfil");//imagenes_chat
        final StorageReference fotoReferencia = storageReference.child(idFotoUsuario);
        fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri u = taskSnapshot.getDownloadUrl();
                String fotoPerfilCadena = u.toString();
                sharedPreferences.edit().putString("fotoPerfil", fotoPerfilCadena).commit();
                sharedPreferences.edit().putString("urlFoto", u.toString()).apply();
                sharedPreferences.edit().putBoolean("registrarDatos", false).apply();
                startActivity(new Intent(Registro_Datos.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Bienvenido  " + nombre.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                subirImagen.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        if (progress >= 100) {
                            subirImagen.dismiss();
                        } else {
                            subirImagen.setMessage("Cargando....." + (int) progress + "%");
                        }
                    }
                });

    }

    double progress;

    @Override
    public void onBackPressed() {
        //
    }
}
