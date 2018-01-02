package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Publicaciones extends AppCompatActivity {

    private DatabaseReference dbPublicaciones;
    private ArrayList<PublicacionesModel> listaPublicaciones;
    private RecyclerView rvPublicaciones;
    private PublicacionesAdapter adapter;
    private ProgressDialog progressDialog;
    TextToSpeech talk;
    String categoria;
    SharedPreferences sharedPreferences;

    static String TITULO_COMENTARIO;
    static int width;
    static int height;
    static DatabaseReference dbComentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicaciones_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbPublicaciones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publicaciones, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }


    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    verificarContenido(newText);
                }

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verificarContenido(String newText) {
        if (adapter.publicacionesFilterList.isEmpty() && newText.length() > 0) {
            rvPublicaciones.setVisibility(View.GONE);

        } else {
            rvPublicaciones.setVisibility(View.VISIBLE);
        }
    }

    private void iniciar() {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight() / 2;
        listaPublicaciones = new ArrayList<>();
        rvPublicaciones = (RecyclerView) findViewById(R.id.rvPublicaciones);
        rvPublicaciones.setHasFixedSize(true);
        rvPublicaciones.setLayoutManager(new LinearLayoutManager(this));
        //lv = (ListView) findViewById(R.id.listViewPublicaciones);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Publicaciones...");
        progressDialog.show();
        categoria = getIntent().getStringExtra("categoria");
        adapter = new PublicacionesAdapter(this, listaPublicaciones);
        rvPublicaciones.setAdapter(adapter);

        dbPublicaciones = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/publicaciones");
      /*  FirebaseRecyclerAdapter<PublicacionesModel, ViewHolderPublicacion> adapter = new FirebaseRecyclerAdapter<PublicacionesModel, ViewHolderPublicacion>(
                PublicacionesModel.class, R.layout.publicacion_card, Publicaciones.ViewHolderPublicacion.class, dbPublicaciones
        ) {
            @Override
            protected void populateViewHolder(ViewHolderPublicacion viewHolder, PublicacionesModel model, int position) {
                PublicacionesModel publicacionesModel = new PublicacionesModel();
                try {
                    Map<String, Object> mapPublicacion = (Map<String, Object>) model;
                    publicacionesModel.setTitulo((String) mapPublicacion.get("titulo"));
                    publicacionesModel.setFecha((String) mapPublicacion.get("fecha"));
                    publicacionesModel.setDescripcion((String) mapPublicacion.get("descripcion"));
                    publicacionesModel.setLikes(Integer.parseInt(mapPublicacion.get("likes").toString()));
                    Map<String, Object> mapImg = (HashMap) mapPublicacion.get("imagenes");
                    Uri[] imagenes = new Uri[mapImg.size()];
                    for (int i = 0; i < mapImg.size(); i++) {
                        String dir = (String) mapImg.get("imagen " + String.valueOf(i));
                        imagenes[i] = Uri.parse(dir);
                    }
                    publicacionesModel.setImagenes(imagenes);
                    listaPublicaciones.add(publicacionesModel);
                } catch (Exception e) {
                    String error = e.getMessage().toString();
                }
                progressDialog.dismiss();
            }


        };
        rvPublicaciones.setAdapter(adapter);*/
        dbPublicaciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                listaPublicaciones.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PublicacionesModel publicacionesModel = new PublicacionesModel();
                    try {
                        Map<String, Object> mapPublicacion = (HashMap) snapshot.getValue();
                        publicacionesModel.setTitulo((String) mapPublicacion.get("titulo"));
                        publicacionesModel.setFecha((String) mapPublicacion.get("fecha"));
                        publicacionesModel.setDescripcion((String) mapPublicacion.get("descripcion"));
                        publicacionesModel.setLikes(Integer.parseInt(mapPublicacion.get("likes").toString()));
                        Map<String, Object> mapImg = (HashMap) mapPublicacion.get("imagenes");
                        Uri[] imagenes = new Uri[mapImg.size()];
                        for (int i = 0; i < mapImg.size(); i++) {
                            String dir = (String) mapImg.get("imagen " + String.valueOf(i));
                            imagenes[i] = Uri.parse(dir);
                        }
                        publicacionesModel.setImagenes(imagenes);
                        listaPublicaciones.add(publicacionesModel);
                    } catch (Exception e) {
                        String error = e.getMessage().toString();
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }


    // TODO ARREGLAR AQUI LA CLASS ADAPTER PARA EL RECYCLER  Y AGREGAR EL FILTERLIST CON PRIORIDAD SOBRE TODO AL FILTERLIST PARA TOMAR EN CUENTA PARA LA PUBLICACION DE ITEMS
    public class PublicacionesAdapter extends RecyclerView.Adapter<ViewHolderPublicacion> implements Filterable {
        private ArrayList<PublicacionesModel> publicacionesList;
        private ArrayList<PublicacionesModel> publicacionesFilterList;
        private Context context;

        public PublicacionesAdapter(Context context, ArrayList<PublicacionesModel> arrayList) {
            publicacionesList = arrayList;
            publicacionesFilterList = arrayList;
            this.context = context;
        }


        @Override
        public ViewHolderPublicacion onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.publicacion_card, viewGroup, false);
            ViewHolderPublicacion vh = new ViewHolderPublicacion(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolderPublicacion viewHolder, final int i) {

            viewHolder.tvTitulo.setText(publicacionesFilterList.get(i).getTitulo());
            // System.out.println(publicacionesFilterList.get(i).getFecha()+"***********************************************");
            viewHolder.tvFecha.setText(publicacionesFilterList.get(i).getFecha());
            viewHolder.tvCantImagenes.setText(1 + "/" + publicacionesFilterList.get(i).getImagenes().length);
            viewHolder.tvDescripcion.setText(publicacionesFilterList.get(i).getDescripcion());
            ImagePagerAdapter imgPager = new ImagePagerAdapter(publicacionesFilterList.get(i).getImagenes());
            viewHolder.vpImagenes.setAdapter(imgPager);

           /* viewHolder.compartir.setOnClickListener(new View.OnClickListener() {
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
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, publicacionesFilterList.get(i).getTitulo());
                    startActivity(Intent.createChooser(sendIntent, "COMPARTIR APLICACIÓN:"));
                }
            });*/
            viewHolder.hablar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    talk = new TextToSpeech(Publicaciones.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                Locale locSpanish = new Locale("es_ES");
                                talk.setLanguage(locSpanish);
                                talk.speak(publicacionesFilterList.get(i).getTitulo(), TextToSpeech.QUEUE_FLUSH, null);
                                talk.setPitch(8.8f);
                            } else {
                                Toast.makeText(Publicaciones.this, "ERROR...", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return publicacionesFilterList.size();
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();

                    if (charString.isEmpty()) {

                        publicacionesFilterList = publicacionesList;
                    } else {

                        ArrayList<PublicacionesModel> filteredList = new ArrayList<>();

                        for (PublicacionesModel publicacion : publicacionesList) {

                            if (publicacion.getTitulo().toLowerCase().startsWith(charString.toLowerCase())) {

                                filteredList.add(publicacion);
                            }
                        }

                        publicacionesFilterList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = publicacionesFilterList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    publicacionesFilterList = (ArrayList<PublicacionesModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }


        public class ImagePagerAdapter extends PagerAdapter {

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


    public class ViewHolderPublicacion extends RecyclerView.ViewHolder {
        public Context mContext;
        private TextView tvTitulo, tvDescripcion, tvCantImagenes, tvFecha;
        private ViewPager vpImagenes;
        private ImageView hablar, compartir, comentar;

        public ViewHolderPublicacion(final View v) {
            super(v);
            mContext = itemView.getContext();
            tvTitulo = (TextView) v.findViewById(R.id.cardTituloPublicacion);
            tvFecha = (TextView) v.findViewById(R.id.cardFechaPublicacion);
            tvDescripcion = (TextView) v.findViewById(R.id.cardDescripcionPublicacion);
            tvCantImagenes = (TextView) v.findViewById(R.id.cardCantImagenesPublicacion);
            vpImagenes = (ViewPager) v.findViewById(R.id.vpListarImagenesPublicacion);
            hablar = (ImageView) v.findViewById(R.id.cardHablarPublicacion);
            compartir = (ImageView) v.findViewById(R.id.cardCompartirPublicacion);
            comentar = (ImageView) v.findViewById(R.id.cardComentarPublicacion);
            comentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TITULO_COMENTARIO = tvFecha.getText().toString().trim();
                    comentar(mContext);
                }
            });
        }

        FirebaseRecyclerAdapter adapter2;

        public void comentar(final Context context) {

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.publicaciones_comentario);
            final EditText msj = (EditText) dialog.findViewById(R.id.et_comentario);
            final RecyclerView recyclerViewComentario = (RecyclerView) dialog.findViewById(R.id.rv_publicaciones_comentario);
            listaComentarios=new ArrayList<ComentarioModel>();
            recyclerViewComentario.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewComentario.setHasFixedSize(true);
            final Button boton = (Button) dialog.findViewById(R.id.btnComentarioEnviar);
            String ruta = "categorias/" + categoria + "/publicaciones/" + TITULO_COMENTARIO + "/comentario";
            System.out.println(ruta + "  ************************************");
            dbComentar = FirebaseDatabase.getInstance().getReference(ruta);
            dbComentar.keepSynced(true);
           /* dbComentar.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    listaComentarios.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ComentarioModel comentarioModel = new ComentarioModel();
                        try {
                            Map<String, Object> mapPublicacion = (HashMap) snapshot.getValue();
                            comentarioModel.setNombre((String) mapPublicacion.get("nombre"));
                            comentarioModel.setfecha((String) mapPublicacion.get("fecha"));
                            comentarioModel.setMsj((String) mapPublicacion.get("msj"));
                            comentarioModel.setFotoPerfil((String) mapPublicacion.get("fotoPerfil"));
                            listaComentarios.add(comentarioModel);
                        } catch (Exception e) {
                            String error = e.getMessage().toString();
                        }
                    }
                    adapter2.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });*/
            adapter2 = new FirebaseRecyclerAdapter<ComentarioModel, myViewHoladerComentario>(
                    ComentarioModel.class, R.layout.publicacion_comentario_card, Publicaciones.myViewHoladerComentario.class, dbComentar
            ) {
                @Override
                protected void populateViewHolder(myViewHoladerComentario viewHolder, ComentarioModel model, int position) {
                    viewHolder.nombre.setText(model.getNombre());
                    viewHolder.fecha.setText(model.getfecha());
                    viewHolder.msj.setText(model.getMsj());
                    if (model.getFotoPerfil().equals("")) {
                        //viewHolder.fotoPerfil.setVisibility(View.GONE);
                        viewHolder.fotoPerfil.setBackgroundResource(R.drawable.ico);
                    } else {
                        Glide.with(context).load(model.getFotoPerfil()).into(viewHolder.fotoPerfil);
                    }
                }
            };
            recyclerViewComentario.setAdapter(adapter2);
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String fechaHora = hourdateFormat.format(date);
                    sharedPreferences = context.getSharedPreferences("nombre", MODE_PRIVATE);
                    String url_fotoPerfil = sharedPreferences.getString("fotoPerfil", "");
                    String nombre_perfil = sharedPreferences.getString("nombre", "");
                    ComentarioModel comentarioModel = new ComentarioModel(nombre_perfil, fechaHora, msj.getText().toString().trim(), url_fotoPerfil);
                    dbComentar.child(fechaHora).setValue(comentarioModel);
                    msj.setText("");
                    recyclerViewComentario.scrollToPosition(adapter2.getItemCount() - 1);
                    //    dialog.cancel();
                }
            });

            dialog.getWindow().setLayout(width, height);
            dialog.show();
        }
    }
    private ArrayList<ComentarioModel> listaComentarios;

    public class myViewHoladerComentario extends RecyclerView.ViewHolder {
        TextView nombre, fecha, msj;
        CircleImageView fotoPerfil;

        public myViewHoladerComentario(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.tv_comentarioNombre);
            fecha = (TextView) itemView.findViewById(R.id.tv_comentarioFecha);
            msj = (TextView) itemView.findViewById(R.id.tv_comentarioMensaje);
            fotoPerfil = (CircleImageView) itemView.findViewById(R.id.iv_comentario);
        }
    }
}
