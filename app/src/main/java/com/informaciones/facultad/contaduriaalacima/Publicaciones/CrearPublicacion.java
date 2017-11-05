package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.Categorias.CategoriaModel;
import com.informaciones.facultad.contaduriaalacima.Categorias.Categorias;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;
import java.util.List;

public class CrearPublicacion extends AppCompatActivity {
    private DatabaseReference dbCategorias;
    private List<String> listaCategorias;
    private Spinner spinnerCategorias;
    ArrayAdapter<String> dataAdapter;
    /////////////////////////////////
    DatabaseReference dbPublicacion;
    StorageReference storageReference;
    private ImageView imageView;
    private EditText et_titulo, et_descripcion;
    private Uri imguri;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicacion);
        cargarSpinner();
        iniciar();
    }

    private void iniciar() {
        //dbPublicacion = FirebaseDatabase.getInstance().getReference("categorias");
        storageReference = FirebaseStorage.getInstance().getReference("publicaciones");
        imageView = (ImageView) findViewById(R.id.imagePublicacion);
        et_titulo = (EditText) findViewById(R.id.et_tituloPublicacion);
        et_descripcion = (EditText) findViewById(R.id.et_descripcionPublicacion);
    }

    private void cargarSpinner() {
        listaCategorias = new ArrayList<>();
        listaCategorias.add("Elija Categoria:");
        spinnerCategorias = (Spinner) findViewById(R.id.sp_Categorias);
        dbCategorias = FirebaseDatabase.getInstance().getReference("categorias");
        dbCategorias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (listaCategorias.size() <= 1) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CategoriaModel categoria = snapshot.getValue(CategoriaModel.class);
                        listaCategorias.add(categoria.getTitulo());
                        dataAdapter = new ArrayAdapter<String>(CrearPublicacion.this, android.R.layout.simple_spinner_item, listaCategorias);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategorias.setAdapter(dataAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void elegirImagenPublicacion(View v) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imguri = data.getData();
            imageView.setImageURI(imguri);
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void subirImagenPublicacion(View v) {

        String categoria = spinnerCategorias.getSelectedItem().toString();
        if (imguri != null) {
            if (categoria.equals("Elija Categoria:")) {
                Toast.makeText(CrearPublicacion.this, "Elejir una Categoria Por favor!", Toast.LENGTH_SHORT).show();
                return;
            }
            dbPublicacion = FirebaseDatabase.getInstance().getReference("categorias/" + categoria);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Cargando imagen");
            dialog.show();
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExt(imguri));
            ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Imagen Cargada", Toast.LENGTH_SHORT).show();
                    final PublicacionesModel categorias = new PublicacionesModel(et_titulo.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString().trim(), et_descripcion.getText().toString().trim(), 0);
                    String uploadId = et_titulo.getText().toString().trim();
                    dbPublicacion.child("publicaciones").child(uploadId).setValue(categorias);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Cargando....." + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Elegir una Imagen por favor!", Toast.LENGTH_SHORT).show();
        }
    }

    public void listarPublicaciones(View v) {
        Intent i = new Intent(CrearPublicacion.this, Categorias.class);
        startActivity(i);
    }

}
