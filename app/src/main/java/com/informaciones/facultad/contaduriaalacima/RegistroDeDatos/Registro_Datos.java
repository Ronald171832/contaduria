package com.informaciones.facultad.contaduriaalacima.RegistroDeDatos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.PantallaPrincipal.MainActivity;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;

public class Registro_Datos extends AppCompatActivity {
    private ImageView foto;
    private EditText nombre;
    Button tipoUsuario;
    StorageReference storageReference;
    SharedPreferences sharedPreferences;
    private FirebaseStorage storage;

    // VARIABLES PARA REGISTRO Y LOGGIN CON FACEBOOK
    CallbackManager callbackManager;
    LoginButton loginButton;

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
       // callbackManager=CallbackManager.Factory.create();
        foto = (ImageView) findViewById(R.id.iv_perfil_foto);
        nombre = (EditText) findViewById(R.id.et_perfil_nombre);
        tipoUsuario = (Button) findViewById(R.id.bt_registro_usuario);
       /* loginButton=(LoginButton) findViewById(R.id.loginFB);
        loginButton.setReadPermissions("public_profile email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
                Toast.makeText(Registro_Datos.this  ,"Prueba nuevamente",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Registro_Datos.this  ,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/
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

    public void selecionarTipo(View view) {

        final ArrayList<String> listItems = new ArrayList<>();
        listItems.add("ESTUDIANTE");
        listItems.add("DOCENTE");
        listItems.add("ADMINISTRATIVO");
        new AlertDialog.Builder(Registro_Datos.this)
                .setTitle("Elija una Opción:")
                .setCancelable(false)
                .setAdapter(new ArrayAdapter<String>(Registro_Datos.this, android.R.layout.simple_list_item_1, listItems),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                       /* //Toast.makeText(Cliente.this, pos + "  " + listaClientes.get(pos).getNombre(), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Cliente.this, listItems.get(item), Toast.LENGTH_LONG).show();*/
                                switch (item) {
                                    case 0:
                                        sharedPreferences.edit().putString("tipoUsuario", "est").commit();
                                        tipoUsuario.setText("est");
                                        break;
                                    case 1:
                                        pedirContra();
                                        break;
                                    case 2:
                                        pedirContra();
                                        break;
                                }
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();


    }

    private void pedirContra() {
        final Dialog login_ventana = new Dialog(Registro_Datos.this);
        login_ventana.setTitle("Ingresar Contraseña");
        login_ventana.setContentView(R.layout.gestionar_contra);
        final EditText contra = (EditText) login_ventana.findViewById(R.id.et_gestion_contra);
        Button boton = (Button) login_ventana.findViewById(R.id.btn_gestion_contra);
        int width = (int) (Registro_Datos.this.getResources().getDisplayMetrics().widthPixels * 0.9);
        // set height for dialog
        int height = (int) (Registro_Datos.this.getResources().getDisplayMetrics().heightPixels * 0.9);
        login_ventana.getWindow().setLayout(width, height);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = contra.getText().toString().trim();
                if (c.equals("cp2018")) {
                    sharedPreferences.edit().putString("tipoUsuario", "adm").commit();
                    tipoUsuario.setText("adm");
                }
                login_ventana.cancel();
            }
        });
        login_ventana.show();
    }

    public void registrarDatos(final View view) {
        if (imguri == null) {
            //Toast.makeText(getApplicationContext(), "Elije una imagen porfavor!", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "Elije una imagen porfavor!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        if (nombre.getText().toString().equals("")) {
            //Toast.makeText(getApplicationContext(), "Ingresa tu nombre porfavor!", Toast.LENGTH_SHORT).show();
            nombre.setError("Registro insatisfactorio, Escriba su nombre completo porfavor");
            nombre.requestFocus();
            return;
        }
        String TIPO = sharedPreferences.getString("tipoUsuario", "");
        if(TIPO.equals("")){
            Snackbar.make(view, "Elije una categoria porfavo!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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
        if (generarID) { // solo genera una vez el id de usuario
            sharedPreferences.edit().putBoolean("generar", false).commit();
            idFotoUsuario = String.valueOf(System.currentTimeMillis());
            sharedPreferences.edit().putString("id", idFotoUsuario).commit();
        }
        if (idFotoUsuario.equals("")) {
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
                // actividad para saludar al usuario
                Intent inicioIntent=new Intent(Registro_Datos.this,MainActivity.class);
                inicioIntent.putExtra("saludo",nombre.getText().toString().trim());
                startActivity(inicioIntent);
                        /*
                        Snackbar.make(view, "Bienvenido  " + nombre.getText().toString().trim(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                        * */
                //startActivity(new Intent(Registro_Datos.this, MainActivity.class));
                //Toast.makeText(getApplicationContext(), "Bienvenido  " + nombre.getText().toString().trim(), Toast.LENGTH_SHORT).show();
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
