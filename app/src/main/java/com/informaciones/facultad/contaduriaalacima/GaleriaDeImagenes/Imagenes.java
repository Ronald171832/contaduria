package com.informaciones.facultad.contaduriaalacima.GaleriaDeImagenes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.Chat.PasoDeParametros;
import com.informaciones.facultad.contaduriaalacima.ImgenCompleta.ImagenCompleta;
import com.informaciones.facultad.contaduriaalacima.R;

public class Imagenes extends AppCompatActivity {
    static String FOLDER;
    private DatabaseReference dbImagenes;
    public RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private ProgressDialog progressDialog;
    Display display;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeria_imagenes_folder);
        iniciar();
    }

    private void iniciar() {
        display = getWindowManager().getDefaultDisplay();
        height = display.getHeight();
        FOLDER = PasoDeParametros.FOLDER;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Imagenes...");
        progressDialog.show();
        recyclerView = (RecyclerView) findViewById(R.id.rv_imagenes);
        //layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbImagenes = database.getReference("galeria/" + FOLDER + "/" + FOLDER);
        dbImagenes.keepSynced(true);

        FirebaseRecyclerAdapter<ImagenModel, myViewHolader> adapter = new FirebaseRecyclerAdapter<ImagenModel, myViewHolader>(
                ImagenModel.class, R.layout.galeria_imagen_card, Imagenes.myViewHolader.class, dbImagenes
        ) {
            @Override
            protected void populateViewHolder(final Imagenes.myViewHolader viewHolder, final ImagenModel model, int position) {
                viewHolder.titulo.setText(model.getTitulo());
                Glide.with(Imagenes.this).load(model.getLink()).into(viewHolder.imagen);
                viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*AlertDialog.Builder salvarDibujo = new AlertDialog.Builder(Imagenes.this);
                        salvarDibujo.setTitle("Imagen");
                        salvarDibujo.setMessage("Elija una opcion:");
                        salvarDibujo.setPositiveButton("Guardar en Galeria", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                viewHolder.imagen.buildDrawingCache();
                                Bitmap bmap = viewHolder.imagen.getDrawingCache();
                                //guardar imagen
                                Save savefile = new Save();
                                savefile.SaveImage(Imagenes.this, bmap);
                            }
                        });
                        salvarDibujo.setNegativeButton("Ver Imagen Completa", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Imagenes.this, ImagenCompleta.class);
                                PasoDeParametros.URL_IMAGEN = model.getLink();
                                startActivity(intent);
                            }
                        });
                        salvarDibujo.show();*/
                        Intent intent = new Intent(Imagenes.this, ImagenCompleta.class);
                        PasoDeParametros.URL_IMAGEN = model.getLink();
                        startActivity(intent);
                    }
                });
                progressDialog.dismiss();
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView.getAdapter() != null) {
                    if (recyclerView.getAdapter().getItemCount() == 0) {
                        recyclerView.setBackgroundResource(R.drawable.ico);
                        progressDialog.cancel();
                    }
                }
            }
        }, 8000);
    }

    public static class myViewHolader extends RecyclerView.ViewHolder {
        TextView titulo;
        ImageView imagen;
        public Context mContext;

        public myViewHolader(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            titulo = (TextView) itemView.findViewById(R.id.cardTituloImagen);
            imagen = (ImageView) itemView.findViewById(R.id.cardImagenImagen);
        }


    }
}
