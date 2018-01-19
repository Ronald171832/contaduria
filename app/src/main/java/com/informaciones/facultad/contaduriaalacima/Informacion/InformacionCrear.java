package com.informaciones.facultad.contaduriaalacima.Informacion;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InformacionCrear extends AppCompatActivity {
    DatabaseReference dbCategoria;
    StorageReference storageReference;
    private ImageView imageView;
    private Uri imguri;
    private TextView titulo, descripcion;
    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_crear);
        iniciar();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_crear_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_registrarBlog:
                registrarBlog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void iniciar() {
        dbCategoria = FirebaseDatabase.getInstance().getReference("info");
        storageReference = FirebaseStorage.getInstance().getReference("info");
        imageView = (ImageView) findViewById(R.id.iv_blog_imagen);
        titulo = (TextView) findViewById(R.id.tv_blog_titulo);
        descripcion = (TextView) findViewById(R.id.tv_blog_descripcion);
    }

    public void elegirImagenBlog(View v) {
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


    public void nombreTituloBlog(View view) {
        final Dialog ventana_emergente = new Dialog(InformacionCrear.this);
        ventana_emergente.setTitle("Ingresar Titulo");
        ventana_emergente.setContentView(R.layout.ventana_emergente2);
        final EditText editText = (EditText) ventana_emergente.findViewById(R.id.et_ventana_ingresar);
        Button boton = (Button) ventana_emergente.findViewById(R.id.bt_ventana_aceptar);
        final TextView textView = (TextView) ventana_emergente.findViewById(R.id.tv_ventana_titulo);
        textView.setText("Ingresar Titulo");
        int width = (int) (InformacionCrear.this.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (InformacionCrear.this.getResources().getDisplayMetrics().heightPixels * 0.5);
        ventana_emergente.getWindow().setLayout(width, height);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titulo.setText(editText.getText().toString().trim());
                ventana_emergente.cancel();
            }
        });
        ventana_emergente.show();
    }

    public void descripcionBlog(View view) {
        final Dialog ventana_emergente = new Dialog(InformacionCrear.this);
        ventana_emergente.setTitle("Ingresar Descripcion");
        ventana_emergente.setContentView(R.layout.ventana_emergente2);
        final EditText editText = (EditText) ventana_emergente.findViewById(R.id.et_ventana_ingresar);
        Button boton = (Button) ventana_emergente.findViewById(R.id.bt_ventana_aceptar);
        final TextView textView = (TextView) ventana_emergente.findViewById(R.id.tv_ventana_titulo);
        textView.setText("Ingresar Descripcion");
        int width = (int) (InformacionCrear.this.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (InformacionCrear.this.getResources().getDisplayMetrics().heightPixels * 0.6);
        ventana_emergente.getWindow().setLayout(width, height);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descripcion.setText(editText.getText().toString().trim());
                ventana_emergente.cancel();
            }
        });
        ventana_emergente.show();
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void registrarBlog() {
        if (imguri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Cargando imagen");
            dialog.setCancelable(false);
            dialog.show();
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExt(imguri));
            ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String nombreImagen = hourdateFormat.format(date);
                    final InformacionModel InformacionModel = new InformacionModel(titulo.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString().trim(), descripcion.getText().toString().trim(), nombreImagen);
                    dbCategoria.child(nombreImagen).setValue(InformacionModel);
                    Toast.makeText(getApplicationContext(), "Informacion creado correctamente!", Toast.LENGTH_SHORT).show();
                    titulo.setText("");
                    descripcion.setText("");
                    imageView.setBackgroundResource(R.drawable.add_picture);
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
}
