package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informaciones.facultad.contaduriaalacima.Categorias.CategoriaModel;
import com.informaciones.facultad.contaduriaalacima.R;
import com.informaciones.facultad.contaduriaalacima.WebServices.Constantes;
import com.informaciones.facultad.contaduriaalacima.WebServices.VolleySingleton;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearPublicacion extends AppCompatActivity {
    private DatabaseReference dbCategorias;
    private List<String> listaCategorias;
    private List<String> listaFechas;
    private Spinner spinnerCategorias;
    ArrayAdapter<String> dataAdapter;
    /////////////////////////////////
    DatabaseReference dbPublicacion;
    StorageReference storageReference;
    private EditText et_titulo, et_descripcion;
    private Uri[] imgsUri;
    private static final int PICK_IMAGE = 100;

    public ViewPager viewImagenes;
    public ImagePagerAdapter adaptadorImgSlide;
    Map<String, String> mapDataNotificacion = new HashMap<>();
    public boolean isPublicacionWithImg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicaciones_crear);
        cargarSpinner();
        iniciar();
    }

    private void iniciar() {
        //dbPublicacion = FirebaseDatabase.getInstance().getReference("categorias");
        storageReference = FirebaseStorage.getInstance().getReference("publicaciones");
        //imageView = (ImageView) findViewById(R.id.imagePublicacion);
        et_titulo = (EditText) findViewById(R.id.et_tituloPublicacion);
        et_descripcion = (EditText) findViewById(R.id.et_descripcionPublicacion);
        viewImagenes = (ViewPager) findViewById(R.id.vpImagenesPublicacion);
        viewImagenes.setVisibility(View.GONE);
    }

    private void cargarSpinner() {
        spinnerCategorias = (Spinner) findViewById(R.id.sp_Categorias);
        dbCategorias = FirebaseDatabase.getInstance().getReference("categorias");
        dbCategorias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaCategorias = new ArrayList<>();
                listaFechas = new ArrayList<>();
                listaCategorias.add("Elija Categoria:");
                listaFechas.add("Elija Categoria:");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CategoriaModel categoria = snapshot.getValue(CategoriaModel.class);
                    listaCategorias.add(categoria.getTitulo());
                    listaFechas.add(categoria.getFecha());
                }
                dataAdapter = new ArrayAdapter<String>(CrearPublicacion.this, android.R.layout.simple_spinner_item, listaCategorias);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategorias.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void elegirImagenesPublicacion(View v) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            //imguri = data.getData();
            isPublicacionWithImg = true;
            try {
                viewImagenes.setVisibility(View.VISIBLE);
                if (data == null) {
                    Toast.makeText(this, "DEBE SELECCIOAR UNA O MAS IMAGENES ... PRESIONAR 2 SEGUNDOS SOBRE LA(S) IMAGENES QUE SELECCIONE", Toast.LENGTH_LONG).show();
                    return;
                }
                if (data.getClipData() != null) {
                    imgsUri = new Uri[data.getClipData().getItemCount()];
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uriImagenActual = clipData.getItemAt(i).getUri();
                        imgsUri[i] = uriImagenActual;
                        //System.err.println(uri.getPath().toString()+"-------------------------------");
                    }
                    adaptadorImgSlide = new ImagePagerAdapter(imgsUri);
                    viewImagenes.setAdapter(adaptadorImgSlide);
                    return;
                }
                if (data.getData() != null) {
                    imgsUri = new Uri[1];
                    imgsUri[0] = data.getData();
                    adaptadorImgSlide = new ImagePagerAdapter(imgsUri);
                    viewImagenes.setAdapter(adaptadorImgSlide);
                    return;
                }
            } catch (Exception e) {
                String error = e.getMessage().toString();
                System.out.println(error);
            }
            /*if (data.getClipData().getItemCount()>0){

            } else {
                Toast.makeText(this,"SELECCIONE POR LO MENOS UNA IMAGEN PARA SUBIR LA PUBLICACION",Toast.LENGTH_SHORT).show();
            }*/

        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void subirImagenPublicacion(View v) {
        if (listaCategorias.isEmpty()) {
            Toast.makeText(this, "Cargando Categorias", Toast.LENGTH_LONG).show();
            return;
        }
        //String nombreCategoria = spinnerCategorias.getSelectedItem().toString();
        String nombreCategoria = listaFechas.get(spinnerCategorias.getSelectedItemPosition());
        if (nombreCategoria.equals("Elija Categoria:")) {
            Toast.makeText(CrearPublicacion.this, "Elejir una Categoria Por favor!", Toast.LENGTH_SHORT).show();
            return;
        }
        dbPublicacion = FirebaseDatabase.getInstance().getReference("categorias/" + nombreCategoria + "/publicaciones");
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Cargando Publicacion...");
        dialog.show();
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String fechaHora = hourdateFormat.format(date);
        String nombrePublicacion = et_titulo.getText().toString().trim();
        String descPublicacion = et_descripcion.getText().toString().trim();
        PublicacionesModel nuevaPublicacionModel = new PublicacionesModel(nombrePublicacion, descPublicacion, fechaHora);

        DatabaseReference refPublicacion = dbPublicacion.child(fechaHora);//.setValue("lkikikip");
        //DatabaseReference refImagenesPublicacion=FirebaseDatabase.getInstance().getReference("categorias/"+nombreCategoria+"/publicaciones/"+nombrePublicacion);

        refPublicacion.setValue(nuevaPublicacionModel);

        mapDataNotificacion.put("titulo", nombrePublicacion);
        mapDataNotificacion.put("mensaje", descPublicacion);
        if (isPublicacionWithImg) {
            imagenesURL_FB = new HashMap<>();
            for (int i = 0; i < imgsUri.length; i++) {
                addImagenPublicacionFB(imgsUri[i], nombreCategoria, i, fechaHora);
            }
        } else {
            notificarUsers(mapDataNotificacion.get("titulo"), mapDataNotificacion.get("mensaje"), null);
        }
        isPublicacionWithImg = false; // volver al estado inicial
        dialog.dismiss();

    }

    Map<String, String> imagenesURL_FB;

    private void addImagenPublicacionFB(Uri uriImg, final String categor, final int pos, final String fechaHora) {
        /*final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Cargando Publicacion...");
        dialog.show();*/
        if (uriImg != null) {
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExt(uriImg));
            try {
                ref.putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DatabaseReference refImagenesPublicacion = FirebaseDatabase.getInstance().getReference("categorias/" + categor + "/publicaciones/" + fechaHora);
                        //Map<String,String> h=new HashMap<>();
                        imagenesURL_FB.put("imagen " + String.valueOf(pos), taskSnapshot.getDownloadUrl().toString().trim());

                        //imagenesURL_FB
                        refImagenesPublicacion.child("imagenes").setValue(imagenesURL_FB);
                        if (pos == 0) {
                            mapDataNotificacion.put("urlimagen", imagenesURL_FB.get("imagen 0"));
                            notificarUsers(mapDataNotificacion.get("titulo"), mapDataNotificacion.get("mensaje"), mapDataNotificacion.get("urlimagen"));
                        }

                        // FORMA DE ENVIAR LA NOTIFICACION CUANDO SE EJECUTO LA ULTIMA IMAGEN

                        //dialog.dismiss();
                        //urlArray.put("imagen ",taskSnapshot.getDownloadUrl().toString().trim());
                        //imagenesURL_FB.put("imagen"+String.valueOf(pos),taskSnapshot.getDownloadUrl().toString().trim());
                        //imagenesURL_FB.add(taskSnapshot.getDownloadUrl());
                        //dialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "Imagen Cargada", Toast.LENGTH_SHORT).show();
                        //final PublicacionesModel categorias = new PublicacionesModel(et_titulo.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString().trim(), et_descripcion.getText().toString().trim(), 0);
                        //String uploadId = et_titulo.getText().toString().trim();
                        //dbPublicacion.child("publicaciones").child(uploadId).setValue(categorias);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //dialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        /*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Cargando....." + (int) progress + "%");
                                dialog.dismiss();
                            }
                        });*/
            } catch (Exception e) {
                String error = e.getMessage().toString();
            }

        }
    }

    private void notificarUsers(String titulo, String messsage, String urlImg) {
        String peticionGET = Constantes.URL_PETICION_NOTIFICACION + "?titulo=" + titulo + "&mensaje=" + messsage;
        if (urlImg != null) { // existe imagen para notificar a los usuarios
            String p1 = urlImg.substring(0, urlImg.indexOf('&'));
            String p2 = urlImg.substring(urlImg.indexOf('&') + 1);
            peticionGET = peticionGET + "&url=" + p1 + "&resto=" + p2;
        }
        VolleySingleton.getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(Request.Method.GET,
                                peticionGET,
                                //Constantes.URL_PETICION_NOTIFICACION + "?titulo=" + titulo + "&mensaje=" + messsage + "&url=" + p1 + "&resto=" + p2,
                                new JSONObject(),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarPeticion(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "Error Volley: " + error.getMessage());
                                    }
                                }
                        ));
    }

    private void procesarPeticion(JSONObject response) {
        // ninguna accion por la peticion
        int valor = 331;

    }

    // CLASE PARA EL VIEW PAGER DE IMAGENES
    public class ImagePagerAdapter extends PagerAdapter {

        //private Bitmap[] mImages;
        private Uri[] urlImages;

        public ImagePagerAdapter(Uri[] urlImages) {
            //this.mImages=imagenes;
            this.urlImages = urlImages;
        }


        @Override
        public int getCount() {
            return urlImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            //return false;
            //return view == ((ImageView) object);
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            /*Context context = PreguntasAdapter.context.getApplicationContext();
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(8);
            imageView.setPadding(padding, padding, padding, padding);
            //imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageBitmap(mImages[position]);
            //imageView.setImageResource(mImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
            //return super.instantiateItem(container, position);*/
            LayoutInflater inflater = LayoutInflater.from(CrearPublicacion.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.slide_images, container, false);
            container.addView(layout);
            ImageView image = (ImageView) layout.findViewById(R.id.imageSlide);

            image.setImageURI(urlImages[position]);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //image.setImageBitmap(mImages[position]);

            return layout;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            //super.destroyItem(container, position, object);
        }
    }

}
