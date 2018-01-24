package com.informaciones.facultad.contaduriaalacima.GaleriaDeImagenes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.informaciones.facultad.contaduriaalacima.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GaleriaSubirImagenes extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    DatabaseReference dbCategorias;
    DatabaseReference dbPublicacion;
    StorageReference storageReference;
    private List<String> listaCategorias;
    private List<String> listaFechas;
    ArrayAdapter<String> dataAdapter;
    private ProgressDialog progressDialog;
    private ImageView iv_galeria;

    private Uri[] imgsUri;
    private Uri imguri;

    public ViewPager viewImagenes;
    public ImagePagerAdapter adaptadorImgSlide;
    private Spinner spinnerCategorias;
    private boolean estado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeria_crear_carpeta);
        iniciar();
        cargarSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_galeria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_galeria_folder:
                crearFolder();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void crearFolder() {
        final Dialog dialog = new Dialog(GaleriaSubirImagenes.this);
        dialog.setTitle("Nombre del Folder");
        dialog.setContentView(R.layout.ventana_emergente2);
        final EditText nombrePerfil = (EditText) dialog.findViewById(R.id.et_ventana_ingresar);
        final TextView titulo = (TextView) dialog.findViewById(R.id.tv_ventana_titulo);
        titulo.setText("Nombre del Folder");
        Button boton = (Button) dialog.findViewById(R.id.bt_ventana_aceptar);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombrePerfil.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "INSERTE NOMBRE!", Toast.LENGTH_SHORT).show();
                } else {
                    estado = true;
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String nombreImagen = hourdateFormat.format(date);
                    CarpetaModel carpetaModel = new CarpetaModel(nombrePerfil.getText().toString().trim(), nombreImagen);
                    dbCategorias.child(nombreImagen).setValue(carpetaModel);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void cargarSpinner() {
        listaCategorias = new ArrayList<>();
        listaFechas = new ArrayList<>();
        listaCategorias.add("Elija Categoria:");
        spinnerCategorias = (Spinner) findViewById(R.id.sp_Categorias);
        dbCategorias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (estado) {
                    listaCategorias = new ArrayList<>();
                    listaFechas = new ArrayList<>();
                    listaCategorias.add("Elija Categoria:");
                    listaFechas.add("Elija Categoria:");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CarpetaModel folder = snapshot.getValue(CarpetaModel.class);
                        listaCategorias.add(folder.getTitulo());
                        listaFechas.add(folder.getFecha());
                        dataAdapter = new ArrayAdapter<String>(GaleriaSubirImagenes.this, android.R.layout.simple_spinner_item, listaCategorias);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategorias.setAdapter(dataAdapter);
                    }
                    estado = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void iniciar() {
        iv_galeria = (ImageView) findViewById(R.id.iv_galeria);
        viewImagenes = (ViewPager) findViewById(R.id.vpImagenesPublicacion);
        storageReference = FirebaseStorage.getInstance().getReference("galeria");
        spinnerCategorias = (Spinner) findViewById(R.id.sp_Categorias);
        dbCategorias = FirebaseDatabase.getInstance().getReference("galeria");
        estado = true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void elegirImagenesGaleria(View v) {
        AlertDialog.Builder salvarDibujo = new AlertDialog.Builder(this);
        salvarDibujo.setTitle("Abrir Galeria");
        salvarDibujo.setMessage("Elija una opcion:");
        salvarDibujo.setPositiveButton("Subir una imagen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 1);
            }
        });
        salvarDibujo.setNegativeButton("Subir mas de una imagen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(gallery, "Selccione Imagene(s)"), PICK_IMAGE);
            }
        });
        salvarDibujo.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                if (data == null) {
                    Toast.makeText(this, "DEBE SELECCIOAR UNA O MAS IMAGENES ... PRESIONAR 2 SEGUNDOS SOBRE LA(S) IMAGENES QUE SELECCIONE", Toast.LENGTH_LONG).show();
                    return;
                }
                if (data.getClipData() != null) {
                    viewImagenes.setVisibility(View.VISIBLE);
                    iv_galeria.setVisibility(View.GONE);
                    imgsUri = new Uri[data.getClipData().getItemCount()];
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uriImagenActual = data.getClipData().getItemAt(i).getUri();
                        imgsUri[i] = uriImagenActual;
                    }
                    adaptadorImgSlide = new ImagePagerAdapter(imgsUri);
                    viewImagenes.setAdapter(adaptadorImgSlide);
                    return;
                }
                if (data.getData() != null) {
                    imgsUri = new Uri[data.getClipData().getItemCount()];
                    imgsUri[0] = data.getData();
                    adaptadorImgSlide = new ImagePagerAdapter(imgsUri);
                    viewImagenes.setAdapter(adaptadorImgSlide);
                    return;
                }
            } catch (Exception e) {
                String error = e.getMessage().toString();
                System.out.println(error);
            }
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            imguri = data.getData();
            iv_galeria.setVisibility(View.VISIBLE);
            viewImagenes.setVisibility(View.GONE);
            iv_galeria.setImageURI(imguri);
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void subirImagenPublicacion(View v) {
        final String folder = spinnerCategorias.getSelectedItem().toString();
        String f = listaFechas.get(spinnerCategorias.getSelectedItemPosition());
        if (folder.equals("Elija Categoria:")) {
            Toast.makeText(GaleriaSubirImagenes.this, "Elejir una Categoria Por favor!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imguri != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Cargando Imagenes...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            dbPublicacion = FirebaseDatabase.getInstance().getReference("galeria/" + f);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Cargando Publicacion...");
            dialog.show();
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExt(imguri));
            try {
                Uri file = imguri;
                ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String nombreImagen = System.currentTimeMillis() + "";
                        ImagenModel imagenModel = new ImagenModel(nombreImagen, taskSnapshot.getDownloadUrl().toString());
                        dbPublicacion.child(listaFechas.get(spinnerCategorias.getSelectedItemPosition())).child(nombreImagen).setValue(imagenModel);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                if (progress >= 100) {
                                    progressDialog.dismiss();

                                } else {
                                    progressDialog.setMessage("Cargando...." + (int) progress + "%");
                                }
                            }
                        });
            } catch (Exception e) {
                String error = e.getMessage().toString();
            }
            dialog.dismiss();
            return;
        }
        if (imgsUri.length > 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Cargando Imagenes...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            dbPublicacion = FirebaseDatabase.getInstance().getReference("galeria/" + f);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Cargando Publicacion...");
            dialog.show();
            for (int i = 0; i < imgsUri.length; i++) {
                StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExt(imgsUri[i]));
                try {
                    Uri file = imgsUri[i];
                    final int finalI = i;
                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String nombreImagen = System.currentTimeMillis() + "";
                            ImagenModel imagenModel = new ImagenModel(nombreImagen, taskSnapshot.getDownloadUrl().toString());
                            dbPublicacion.child(listaFechas.get(spinnerCategorias.getSelectedItemPosition())).child(nombreImagen).setValue(imagenModel);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    if (progress >= 100) {
                                        progressDialog.dismiss();
                                    } else {
                                        progressDialog.setMessage("Imagen " + finalI + "\nCargando...." + (int) progress + "%");
                                    }
                                }
                            });
                } catch (Exception e) {
                    String error = e.getMessage().toString();
                }

            }
            dialog.dismiss();
        } else {
            Toast.makeText(getApplicationContext(), "Elegir una Imagen por favor!", Toast.LENGTH_SHORT).show();
        }
    }

    double progress;

    // CLASE PARA EL VIEW PAGER DE IMAGENES
    public static class ImagePagerAdapter extends PagerAdapter {
        private Uri[] urlImages;

        public ImagePagerAdapter(Uri[] urlImages) {
            this.urlImages = urlImages;
        }

        @Override
        public int getCount() {
            return urlImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.galeria_slide_images, container, false);
            container.addView(layout);
            ImageView image = (ImageView) layout.findViewById(R.id.imageSlide);
            image.setImageURI(urlImages[position]);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
}


/*


long yourmilliseconds = System.currentTimeMillis();
SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
Date resultdate = new Date(yourmilliseconds);
System.out.println(sdf.format(resultdate));
* */