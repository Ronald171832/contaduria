package com.informaciones.facultad.contaduriaalacima.PantallaPrincipal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static final int SELECT_IMGS_PRESENTACION=101;
    private Uri imguri;
    ImageView imagen;
    public ViewPager viewImagenes;
    StorageReference storageReference;
    private DatabaseReference dbPublicaciones;
    public Uri[] imgsUri;
    public ImagePagerAdapter adaptadorImgSlide;
    public EditText txtEstado;

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


    // TODO: TERMINAR LA PARTE DE GESTION DE PRESENTACION DE IMAGENES
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imguri = data.getData();
            imagen.setImageURI(imguri);
        }
        if (resultCode==RESULT_OK && requestCode==SELECT_IMGS_PRESENTACION){
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
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void selectImagenesPresentacion() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(gallery,SELECT_IMGS_PRESENTACION);
    }


    public void setPresentacion(View view) {
        selectImagenesPresentacion();
        final Dialog gestionPresentacionDialog = new Dialog(GestionSuperUsuario.this);
        gestionPresentacionDialog.setTitle("GESTION PRESENTACION INICIAL");
        gestionPresentacionDialog.setContentView(R.layout.gestion_presentacion_imagenes);
        viewImagenes = (ViewPager) gestionPresentacionDialog.findViewById(R.id.vpImgsPresentacion);
        txtEstado=(EditText)gestionPresentacionDialog.findViewById(R.id.txtPresentacionDesc);
        Button btnAceptar = (Button) gestionPresentacionDialog.findViewById(R.id.btnGuardar_presentacion_img);
        Button btnCancelar = (Button) gestionPresentacionDialog.findViewById(R.id.btnCancelar_presentacion_img);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionPresentacionDialog.dismiss();
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(GestionSuperUsuario.this, "Imagen Cargando \n Esperar Segundos para la Actualizacion", Toast.LENGTH_LONG).show();
                gestionPresentacionDialog.dismiss();
                // editar estado
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbEstado;
                dbEstado = database.getReference("estado").child("info");
                if (!txtEstado.getText().toString().equals("")) {
                    dbEstado.setValue(txtEstado.getText().toString().trim());
                }
                // editar el carrusel de imagenes, previamente se eliminara las imagenes anteriores

                addImgPresentacion(imgsUri);
                //Toast.makeText(ges.this, "Porfavor Seleccione al menos una imagen para cargar!!!",Toast.LENGTH_LONG).show();
            }
        });
        int width = (int) (GestionSuperUsuario.this.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (GestionSuperUsuario.this.getResources().getDisplayMetrics().heightPixels * 0.75);
        gestionPresentacionDialog.getWindow().setLayout(width, height);
        gestionPresentacionDialog.show();

    }

    int indice=0;
    private void addImgPresentacion(Uri[] imgsPresentacion) {
        storageReference = FirebaseStorage.getInstance().getReference("presentacion_img");
        StorageReference ref;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbPresentacion;
        dbPresentacion = database.getReference("presentacion");
        dbPresentacion.removeValue();
        if (imgsPresentacion!=null){
            for (int i=0;i<imgsPresentacion.length;i++){
                ref = storageReference.child("img "+i+"_"+System.currentTimeMillis()+"."+ getImageExt(imgsPresentacion[i]));
                ref.putFile(imgsPresentacion[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DatabaseReference dbPresentacion2;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        dbPresentacion2 = database.getReference("presentacion");
                        dbPresentacion2.child("imagen presentacion "+indice).setValue(taskSnapshot.getDownloadUrl().toString().trim());
                        indice++;
                    }
                });
            }
        } else{
            Toast.makeText(getApplicationContext(), "Porfavor Seleccione al menos una imagen para cargar!!!",Toast.LENGTH_LONG).show();
        }

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
            LayoutInflater inflater = LayoutInflater.from(GestionSuperUsuario.this);
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
