package com.informaciones.facultad.contaduriaalacima.RegistroDeDatos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookContentProvider;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileManager;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.ImgenCompleta.ImagenCompleta;
import com.informaciones.facultad.contaduriaalacima.ImgenCompleta.Save;
import com.informaciones.facultad.contaduriaalacima.PantallaPrincipal.MainActivity;
import com.informaciones.facultad.contaduriaalacima.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

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
    ImageView perfilImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_datos);
        iniciar();
    }

    private void iniciar() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();
        callbackManager=CallbackManager.Factory.create();
        foto = (ImageView) findViewById(R.id.iv_perfil_foto);
        nombre = (EditText) findViewById(R.id.et_perfil_nombre);
        tipoUsuario = (Button) findViewById(R.id.bt_registro_usuario);
        loginButton=(LoginButton) findViewById(R.id.loginFB);
        loginButton.setReadPermissions("public_profile email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                RequestData();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Registro_Datos.this  ,"Prueba nuevamente",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Registro_Datos.this  ,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    // EMPEZAR A RECUPERAR CAMPOS DE FACEBOOK
    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if(json != null){
                        final String nombre = json.getString("name");//+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        String idFotoUsuario = "";
                        // GUARDAR EL NOMBRE DEL USUARIO
                        sharedPreferences.edit().putString("nombre", nombre).apply();
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

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);

                        //Uri uriImage = Uri.parse("android.resource://" + getPackageName() +"/"+R.drawable.user1);
                        //String urlFB=(String)json.getJSONObject("picture").getJSONObject("data").get("url");
                        //Bitmap profilePic = BitmapFactory.decodeStream(new URL(urlFB).openConnection().getInputStream());
                        String userID=Profile.getCurrentProfile().getId();
                        Bitmap fotoFBBitmap=getFacebookProfilePicture(userID);
                        foto.setImageBitmap(fotoFBBitmap);
                        //Glide.with(getApplicationContext()).load(urlFB).into(foto);
                        Uri uriFotoFB=bitmapToUriConverter(fotoFBBitmap);
                        //foto.setImageURI(Uri.parse(picUrlString));
                        storageReference = storage.getReference("foto_perfil");//imagenes_chat
                        final StorageReference fotoReferencia = storageReference.child(idFotoUsuario);
                        fotoReferencia.putFile(uriFotoFB).addOnSuccessListener(Registro_Datos.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri u = taskSnapshot.getDownloadUrl();
                                String fotoPerfilCadena = u.toString();
                                sharedPreferences.edit().putString("fotoPerfil", fotoPerfilCadena).commit();
                                sharedPreferences.edit().putString("urlFoto", u.toString()).apply();
                                sharedPreferences.edit().putBoolean("registrarDatos", false).apply();
                                // actividad para saludar al usuario
                                Intent inicioIntent = new Intent(Registro_Datos.this, MainActivity.class);
                                inicioIntent.putExtra("saludo", nombre);
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
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            }
                                });

                        //details_txt.setText(Html.fromHtml(text));
                        //profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(getApplicationContext().getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = getApplicationContext().openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    // TODO FUNCION PARA CONVERTIR UN LA FOTO DE PERFIL DE FACEBOOK A BITMAP
    public static Bitmap getFacebookProfilePicture(String userID) throws IOException {
        Bitmap bitmap = null; URL url = new URL("https://graph.facebook.com/"+userID+"/picture?type=large");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e){
            String error=e.getMessage();
            Log.d("MIRA",error);
        } finally { urlConnection.disconnect(); }
        /*URL imageURL = new URL("http://graph.facebook.com/"+userID+"/picture?type=square&type=large&redirect=false");
        HttpURLConnection.setFollowRedirects(false);
        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());*/
        return bitmap;
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
        callbackManager.onActivityResult(requestCode,resultCode,data);
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
                                        tipoUsuario.setText("Estudiante");
                                        break;
                                    case 1:
                                        pedirContra(listItems.get(1));
                                        break;
                                    case 2:
                                        pedirContra(listItems.get(2));
                                        break;
                                }
                                loginButton.setEnabled(true);
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();


    }

    private void pedirContra(final String tipo) {
        final Dialog login_ventana = new Dialog(Registro_Datos.this);
        login_ventana.setTitle("Ingresar Contraseña");
        login_ventana.setContentView(R.layout.gestionar_contra);
        final EditText contra = (EditText) login_ventana.findViewById(R.id.et_gestion_contra);
        Button boton = (Button) login_ventana.findViewById(R.id.btn_gestion_contra);
        int width = (int) (Registro_Datos.this.getResources().getDisplayMetrics().widthPixels * 0.9);
        // set height for dialog
        int height = (int) (Registro_Datos.this.getResources().getDisplayMetrics().heightPixels * 0.5);
        login_ventana.getWindow().setLayout(width, height);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = contra.getText().toString().trim();
                if (c.equals("cp2018")) {
                    sharedPreferences.edit().putString("tipoUsuario", "adm").commit();
                    tipoUsuario.setText(tipo);
                } else {
                    Toast.makeText(Registro_Datos.this, "Incorrecto!", Toast.LENGTH_LONG).show();
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
        if (TIPO.equals("")) {
            Snackbar.make(view, "Elije Tipo de Usuario porfavor!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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
                Intent inicioIntent = new Intent(Registro_Datos.this, MainActivity.class);
                inicioIntent.putExtra("saludo", nombre.getText().toString().trim());
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
