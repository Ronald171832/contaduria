package com.informaciones.facultad.contaduriaalacima.PantallaPrincipal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.Bloqueados.Bloqueados;
import com.informaciones.facultad.contaduriaalacima.Categorias.CrearCategorias;
import com.informaciones.facultad.contaduriaalacima.Documentos.CrearDocumentos;
import com.informaciones.facultad.contaduriaalacima.GaleriaDeImagenes.GaleriaSubirImagenes;
import com.informaciones.facultad.contaduriaalacima.Informacion.InformacionCrear;
import com.informaciones.facultad.contaduriaalacima.Notificaciones.CrearNotificacion;
import com.informaciones.facultad.contaduriaalacima.Publicaciones.CrearPublicacion;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;

public class GestionSuperUsuario extends AppCompatActivity {
    RelativeLayout categoria, publicaciones, documento, pantalla, bloqueados;
    SharedPreferences sharedPreferences;
    private static final int PICK_IMAGE = 100;
    private Uri imguri;
    ImageView imagen;

    StorageReference storageReference;
    private DatabaseReference dbPublicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_de_publicaciones);
        iniciar();
    }

    private void iniciar() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("superUsuario", true).apply();

        categoria = (RelativeLayout) findViewById(R.id.bt_ges_cate);
        bloqueados = (RelativeLayout) findViewById(R.id.bt_ges_bloque);
        pantalla = (RelativeLayout) findViewById(R.id.bt_ges_pant);
        publicaciones = (RelativeLayout) findViewById(R.id.bt_ges_publi);
        documento = (RelativeLayout) findViewById(R.id.bt_ges_docu);
        Animation animationizq = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        Animation animationder = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animationder.setDuration(1000);
        animationizq.setDuration(1000);
        categoria.setAnimation(animationizq);
        publicaciones.setAnimation(animationder);
        documento.setAnimation(animationizq);
        pantalla.setAnimation(animationder);
        bloqueados.setAnimation(animationizq);

    }

    public void documentos(View view) {
        startActivity(new Intent(this, CrearDocumentos.class));
    }

    public void notificacion(View view) {
        startActivity(new Intent(this, CrearNotificacion.class));
    }

    public void bloqueados(View view) {
        startActivity(new Intent(this, Bloqueados.class));
    }

    public void gestion_galeria(View view) {
        startActivity(new Intent(this, GaleriaSubirImagenes.class));
    }

    public void informacion(View view) {
        startActivity(new Intent(this, InformacionCrear.class));
    }

    public void publicaciones(View view) {
        startActivity(new Intent(this, CrearPublicacion.class));
    }

    public void gestion_categoria(View view) {
        startActivity(new Intent(this, CrearCategorias.class));
    }

    public void fondoPantalla(View view) {
        final ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Actualizar Foto Portada");
        listItems.add("Actulizar Estado");
        new AlertDialog.Builder(GestionSuperUsuario.this)
                .setTitle("Elija una Opción:")
                .setCancelable(false)
                .setAdapter(new ArrayAdapter<String>(GestionSuperUsuario.this, android.R.layout.simple_list_item_1, listItems),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        actualizarPortada();
                                        break;
                                    case 1:
                                        actualizarEstado();
                                        break;
                                }
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();


    }

    private void actualizarEstado() {
        final Dialog ventana_emergente = new Dialog(GestionSuperUsuario.this);
        ventana_emergente.setTitle("Actualizar Estado");
        ventana_emergente.setContentView(R.layout.ventana_emergente2);
        final EditText editText = (EditText) ventana_emergente.findViewById(R.id.et_ventana_ingresar);
        Button boton = (Button) ventana_emergente.findViewById(R.id.bt_ventana_aceptar);
        final TextView textView = (TextView) ventana_emergente.findViewById(R.id.tv_ventana_titulo);
        textView.setText("Actualizar Estado");
        int width = (int) (GestionSuperUsuario.this.getResources().getDisplayMetrics().widthPixels * 0.99);
        int height = (int) (GestionSuperUsuario.this.getResources().getDisplayMetrics().heightPixels * 0.6);
        ventana_emergente.getWindow().setLayout(width, height);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbBloqueados;
                dbBloqueados = database.getReference("estado").child("info");
                if (editText.getText().toString().equals("")) {
                    dbBloqueados.setValue(" ");
                } else {
                    dbBloqueados.setValue(editText.getText().toString().trim());
                }
                ventana_emergente.cancel();
            }
        });
        ventana_emergente.show();


    }

    private void actualizarPortada() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
        final Dialog registrar_gasto = new Dialog(GestionSuperUsuario.this);
        registrar_gasto.setTitle("Actualizar Imagen");
        registrar_gasto.setContentView(R.layout.gestion_fondo_pantalla);
        imagen = (ImageView) registrar_gasto.findViewById(R.id.iv_portadaVistaPrevia);
        Button boton = (Button) registrar_gasto.findViewById(R.id.bt_VistaPrevia);
        Button botonCancelar = (Button) registrar_gasto.findViewById(R.id.bt_VistaPreviaCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar_gasto.dismiss();
            }
        });
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GestionSuperUsuario.this, "Imagen Cargando \n Esperar Segundos para la Actualizacion", Toast.LENGTH_LONG).show();
                registrar_gasto.dismiss();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                dbPublicaciones = database.getReference("perfil");
                storageReference = FirebaseStorage.getInstance().getReference("perfil");
                StorageReference ref;
                ref = storageReference.child("portada." + getImageExt(imguri));
                ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dbPublicaciones.child("imagen").setValue(taskSnapshot.getDownloadUrl().toString().trim());
                    }
                });
            }
        });
        int width = (int) (GestionSuperUsuario.this.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (GestionSuperUsuario.this.getResources().getDisplayMetrics().heightPixels * 0.9);
        registrar_gasto.getWindow().setLayout(width, height);
        registrar_gasto.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imguri = data.getData();
            imagen.setImageURI(imguri);
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
