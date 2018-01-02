package com.informaciones.facultad.contaduriaalacima.Documentos;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.R;

public class CrearDocumentos extends AppCompatActivity {
    DatabaseReference dbCategoria;
    StorageReference storageReference;
    private ImageView imageView;
    private EditText et_titulo, et_descripcion;
    private Uri imguri;
    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentos_crear);
        iniciar();
    }

    private void iniciar() {
        dbCategoria = FirebaseDatabase.getInstance().getReference("documentos");
        storageReference = FirebaseStorage.getInstance().getReference("documentos");

        imageView = (ImageView) findViewById(R.id.imageDocumento);
        et_titulo = (EditText) findViewById(R.id.et_tituloDocumento);
        et_descripcion = (EditText) findViewById(R.id.et_descripcionDocumento);
    }

    public void elegirImagenDocumento(View v) {
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
    public void subirImagenDocumento(View v) {
        if (imguri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Cargando imagen");
            dialog.show();
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExt(imguri));
            ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Imagen Cargada", Toast.LENGTH_SHORT).show();
                    final DocumentoModel documento = new DocumentoModel(et_titulo.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString().trim(), et_descripcion.getText().toString().trim(), 0);
                    String uploadId = et_titulo.getText().toString().trim();
                    dbCategoria.child(uploadId).setValue(documento);
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
    public void listarDocumentos(View v) {
        /*Intent i = new Intent(CrearDocumentos.this, Documentos.class);
        startActivity(i);*/


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(imguri, "image/*");
        startActivity(intent);
        /*Intent intent = new Intent(Intent.ACTION_VIEW,imguri);
        startActivity(intent);*/
    }


}
