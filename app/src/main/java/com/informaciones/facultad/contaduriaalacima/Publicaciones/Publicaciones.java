package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Publicaciones extends AppCompatActivity {

    private DatabaseReference dbPublicaciones;
    private List<PublicacionesModel> listaPublicaciones;
    private ListView lv;
    private PublicacionesListAdapter adapter;
    private ProgressDialog progressDialog;
    TextToSpeech talk;
    String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicaciones_principal);
        iniciar();
        tocarListView();
    }


    private void iniciar() {
        listaPublicaciones = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewPublicaciones);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Publicaciones...");
        progressDialog.show();
        categoria = getIntent().getStringExtra("categoria");
        dbPublicaciones = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/publicaciones");
        dbPublicaciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(listaPublicaciones.size()<=0){
                    listaPublicaciones = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PublicacionesModel publicacionesModel=new PublicacionesModel();
                        try{
                            Map<String,Object> img =(HashMap) snapshot.getValue();
                            publicacionesModel.setTitulo((String) img.get("titulo"));
                            publicacionesModel.setDescripcion((String) img.get("descripcion"));
                            publicacionesModel.setLikes(Integer.parseInt(img.get("likes").toString()));
                            Map<String,Object> g=(HashMap)img.get("imagenes");
                            Uri[] imagenes=new Uri[g.size()];
                            for (int i=0;i<g.size();i++) {
                                String dir=(String)g.get("imagen "+String.valueOf(i));
                                imagenes[i]=Uri.parse(dir);
                            }
                            publicacionesModel.setImagenes(imagenes);
                            listaPublicaciones.add(publicacionesModel);
                        } catch (Exception e){
                            String error=e.getMessage().toString();
                        }

                    }
                    adapter = new PublicacionesListAdapter(Publicaciones.this, R.layout.publicacion_card, listaPublicaciones);
                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void tocarListView() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                ImageView comentario =(ImageView) view.findViewById(R.id.cardComentarPublicacion);
                ImageView hablar =(ImageView) view.findViewById(R.id.cardHablarPublicacion);
                ImageView compartir =(ImageView) view.findViewById(R.id.cardCompartirPublicacion);
                ImageView like =(ImageView) view.findViewById(R.id.cardLikePublicaciones);

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     /*   String titulo = imgList.get(i).getTitulo();
                        databasevISITAS = FirebaseDatabase.getInstance().getReference("publicaciones/" + titulo);
                        int visitas = imgList.get(i).getVisitas() + 1;
                        databasevISITAS.child("visitas").setValue(visitas);*/
                    }
                });

                compartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  Bitmap icon = BitmapFactory.decodeResource(getResources(), R.id.item_iv_image);
//Se guarda la imagen en la SDCARD
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                       // icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "tmp" + File.separator + "peter.jpg");
                        try {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                        }
                        //compartir imagen
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                        share.putExtra(Intent.EXTRA_TEXT, "Mi imagen");
                        startActivity(Intent.createChooser(share, "Compartir imagen"));
                     /*   Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, imgList.get(i).getTitulo());
                        startActivity(Intent.createChooser(sendIntent, "COMPARTIR APLICACIÓN:"));*/
                    }
                });
                hablar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        talk = new TextToSpeech(Publicaciones.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    Locale locSpanish = new Locale("es_ES");
                                    talk.setLanguage(locSpanish);
                                    talk.speak(listaPublicaciones.get(i).getTitulo(), TextToSpeech.QUEUE_FLUSH, null);
                                    talk.setPitch(8.8f);
                                } else {
                                    Toast.makeText(Publicaciones.this, "ERROR...", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                comentario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirVentanaComentario(i);
                    }
                });
            }
        });
    }

    ArrayAdapter ad;
    List<String> item;
    ListView lvComentarios;
    DatabaseReference dbComentar;

    public void abrirVentanaComentario(final int i) {
        final String titulo = listaPublicaciones.get(i).getTitulo();
        dbComentar = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/publicaciones/" + titulo + "/comentario");
        final Dialog dialog = new Dialog(Publicaciones.this);
        dialog.setContentView(R.layout.publicaciones_comentario);
        final EditText msj = (EditText) dialog.findViewById(R.id.et_comentario);
        lvComentarios = (ListView) dialog.findViewById(R.id.lv_comentarios);
        final Button boton = (Button) dialog.findViewById(R.id.btnComentarioEnviar);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbComentar.push().setValue(msj.getText().toString().trim());
                dialog.cancel();
            }
        });
        dbComentar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    item.add((String) snapshot.getValue());
                }
                ad = new ArrayAdapter<String>(Publicaciones.this, R.layout.publicaciones_comentario_estilo, item);
                lvComentarios.setAdapter(ad);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height =(int)(display.getHeight()*0.8);
        dialog.getWindow().setLayout(width, height);
        dialog.show();
    }




    public class PublicacionesListAdapter extends ArrayAdapter<PublicacionesModel> {
        private Activity context;
        private int resource;
        private List<PublicacionesModel> listImage;
        // parche para las imagenes


        public PublicacionesListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<PublicacionesModel> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            listImage = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            //convertView=inflater.inflate(resource, parent);
            View v = inflater.inflate(resource, null);
            TextView tvTitulo = (TextView) v.findViewById(R.id.cardTituloPublicacion);
            TextView tvDescripcion = (TextView) v.findViewById(R.id.cardDescripcionPublicacion);
            //ImageView imagen = (ImageView) v.findViewById(R.id.cardImagenPublicacion);
            ViewPager vpImagenes=(ViewPager)v.findViewById(R.id.vpListarImagenesPublicacion);
            TextView tvLike = (TextView) v.findViewById(R.id.cardLikesPublicacion);
            ImageView hablar =(ImageView) v.findViewById(R.id.cardHablarPublicacion);
            ImageView compartir =(ImageView) v.findViewById(R.id.cardCompartirPublicacion);
            ImageView like =(ImageView) v.findViewById(R.id.cardLikePublicaciones);
            ImageView comentar=(ImageView) v.findViewById(R.id.cardComentarPublicacion);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        /*String titulo = imgList.get(i).getTitulo();
                        databasevISITAS = FirebaseDatabase.getInstance().getReference("publicaciones/" + titulo);
                        int visitas = imgList.get(i).getVisitas() + 1;
                        databasevISITAS.child("visitas").setValue(visitas);*/
                }
            });
            compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Bitmap icon = BitmapFactory.decodeResource(getResources(), R.id.item_iv_image);
                    //Se guarda la imagen en la SDCARD
                    /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "tmp" + File.separator + "peter.jpg");
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                    }
                    //compartir imagen
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    share.putExtra(Intent.EXTRA_TEXT, "Mi imagen");
                    startActivity(Intent.createChooser(share, "Compartir imagen"));*/
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, listaPublicaciones.get(position).getTitulo());
                    startActivity(Intent.createChooser(sendIntent, "COMPARTIR APLICACIÓN:"));
                }
            });
            hablar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    talk = new TextToSpeech(Publicaciones.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                Locale locSpanish = new Locale("es_ES");
                                talk.setLanguage(locSpanish);
                                talk.speak(listaPublicaciones.get(position).getTitulo(), TextToSpeech.QUEUE_FLUSH, null);
                                talk.setPitch(8.8f);
                            } else {
                                Toast.makeText(Publicaciones.this, "ERROR...", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            comentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(Publicaciones.this,"AAAA",Toast.LENGTH_LONG).show();
                    abrirVentanaComentario(position);
                }
            });


            tvTitulo.setText(listImage.get(position).getTitulo());
            tvDescripcion.setText(listImage.get(position).getDescripcion());
            tvLike.setText(listImage.get(position).getLikes()+"");
            ImagePagerAdapter imgPager=new ImagePagerAdapter(listImage.get(position).getImagenes());
            vpImagenes.setAdapter(imgPager);
            //Glide.with(context).load(listImage.get(position).getImagen()).into(imagen);
            return v;
        }


        public class ImagePagerAdapter extends PagerAdapter {

            private Uri[] urlImages;

            public ImagePagerAdapter(Uri[] urlImages){
                this.urlImages=urlImages;
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
                LayoutInflater inflater = LayoutInflater.from(context);
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.slide_images, container, false);
                container.addView(layout);
                ImageView image = (ImageView) layout.findViewById(R.id.imageSlide);

            /*image.setImageURI(urlImages[position]);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
                //image.setImageBitmap(mImages[position]);
                Glide.with(context).load(urlImages[position]).into(image);
                return layout;

            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
                //super.destroyItem(container, position, object);
            }
        }

    }


}
