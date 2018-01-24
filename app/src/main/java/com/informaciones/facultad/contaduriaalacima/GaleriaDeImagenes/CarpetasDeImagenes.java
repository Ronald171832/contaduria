package com.informaciones.facultad.contaduriaalacima.GaleriaDeImagenes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.Chat.PasoDeParametros;
import com.informaciones.facultad.contaduriaalacima.R;


public class CarpetasDeImagenes extends AppCompatActivity {
    private DatabaseReference dbCarpetas;
    public RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeria_carpetas_de_imagenes);
        iniciar();
    }

    private void iniciar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Carpetas...");
        progressDialog.show();
        recyclerView = (RecyclerView) findViewById(R.id.rv_carpetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbCarpetas = database.getReference("galeria");
        dbCarpetas.keepSynced(true);
        FirebaseRecyclerAdapter<CarpetaModel, myViewHolader> adapter = new FirebaseRecyclerAdapter<CarpetaModel, myViewHolader>(
                CarpetaModel.class, R.layout.galeria_carpetas_card, CarpetasDeImagenes.myViewHolader.class, dbCarpetas
        ) {
            @Override
            protected void populateViewHolder(CarpetasDeImagenes.myViewHolader viewHolder, CarpetaModel model, int position) {
                viewHolder.carpeta.setText(model.getTitulo());
                viewHolder.fecha.setText(model.getFecha());
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
        TextView carpeta;
        TextView fecha;
        public Context mContext;

        public myViewHolader(View itemView) {
            super(itemView);
            carpeta = (TextView) itemView.findViewById(R.id.cardCarpetasNombre);
            fecha = (TextView) itemView.findViewById(R.id.cardCarpetasFecha);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext = v.getContext();
                    Intent intent = new Intent(v.getContext(), Imagenes.class);
                    PasoDeParametros.FOLDER = fecha.getText().toString().trim();
                    mContext.startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }
    }
}
