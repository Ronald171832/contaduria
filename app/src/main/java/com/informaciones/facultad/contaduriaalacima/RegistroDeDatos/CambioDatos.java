package com.informaciones.facultad.contaduriaalacima.RegistroDeDatos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.R;

public class CambioDatos extends AppCompatActivity {
    private ImageView foto;
    private EditText nombre;
    private TextView editar_nombre, editar_foto;
    StorageReference storageReference;
    SharedPreferences sharedPreferences;
    LinearLayout linearLayout;
    boolean ventana;
    private FirebaseStorage storage;
    private Uri imguri;
    private static final int PICK_IMAGE = 100;
    String FECHA_IMAGEN;
    String ID_IMAGEN;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editar_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_editar_perfil:
                ventana = !ventana;
                if (ventana) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.GONE);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_cambio_datos);
        iniciar();
    }

    private void iniciar() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        String url_fotoPerfil = sharedPreferences.getString("fotoPerfil", "");
        String nombre_perfil = sharedPreferences.getString("nombre", "");
        FECHA_IMAGEN = sharedPreferences.getString("idCliente", "");
        ID_IMAGEN = sharedPreferences.getString("id", "");


        storage = FirebaseStorage.getInstance();
        foto = (ImageView) findViewById(R.id.iv_perfil_foto_editar);
        nombre = (EditText) findViewById(R.id.et_perfil_nombre_editar);
        linearLayout = (LinearLayout) findViewById(R.id.ll_editarDatos);
        editar_nombre = (TextView) findViewById(R.id.tv_editar_nombre);
        editar_foto = (TextView) findViewById(R.id.tv_editar_foto);
        ventana = false;

        nombre.setText(nombre_perfil);
        Glide.with(CambioDatos.this).load(url_fotoPerfil).into(foto);
        editar_nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editar("nombre", nombre.getText().toString());
            }
        });
        editar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarImagen();
            }
        });
    }

    private void editar(final String titulo, String dato) {
        final Dialog ventana_emergente = new Dialog(CambioDatos.this);
        ventana_emergente.setTitle("Editar " + titulo);
        ventana_emergente.setContentView(R.layout.ventana_emergente);
        final EditText editText = (EditText) ventana_emergente.findViewById(R.id.et_ventana_ingresar);
        Button boton = (Button) ventana_emergente.findViewById(R.id.bt_ventana_aceptar);
        final TextView textView = (TextView) ventana_emergente.findViewById(R.id.tv_ventana_titulo);
        textView.setText("Editar " + titulo);
        editText.setText(dato);
        if (titulo.equals("numero")) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        int width = (int) (CambioDatos.this.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (CambioDatos.this.getResources().getDisplayMetrics().heightPixels * 0.6);
        ventana_emergente.getWindow().setLayout(width, height);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    editText.setError("No dejar vacio el campo...");
                    editText.requestFocus();
                } else {
                   /* DatabaseReference dbBloqueados = FirebaseDatabase.getInstance().getReference("clientes/" + FECHA_IMAGEN + "/" + titulo);
                    dbBloqueados.setValue(editText.getText().toString());*/
                    sharedPreferences.edit().putString(titulo, editText.getText().toString()).apply();
                    ventana_emergente.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        ventana_emergente.show();
    }


    private void editarImagen() {
        storageReference = FirebaseStorage.getInstance().getReference("foto_perfil/");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imguri = data.getData();
            foto.setImageURI(imguri);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Cargando imagen");
            dialog.setCancelable(false);
            dialog.show();
            StorageReference ref = storageReference.child(ID_IMAGEN);
            ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*DatabaseReference dbBloqueados = FirebaseDatabase.getInstance().getReference("clientes/" + FECHA_IMAGEN + "/foto_perfil");
                    dbBloqueados.setValue(taskSnapshot.getDownloadUrl().toString().trim());*/
                    sharedPreferences.edit().putString("fotoPerfil", taskSnapshot.getDownloadUrl().toString().trim()).apply();
                    sharedPreferences.edit().putString("urlFoto", taskSnapshot.getDownloadUrl().toString().trim()).apply();
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
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
        }
    }


}
