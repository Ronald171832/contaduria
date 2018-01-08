package com.informaciones.facultad.contaduriaalacima.Bloqueados;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.R;

public class Bloqueados extends AppCompatActivity {
    public RecyclerView recyclerView;
    public static DatabaseReference dbDescargas;
    private ProgressDialog progressDialog;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloqueados_principal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciar();
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

    private void iniciar() {
        context = getApplicationContext();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Bloqueados...");
        progressDialog.show();
        recyclerView = (RecyclerView) findViewById(R.id.rv_bloqueados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbDescargas = database.getReference("bloqueados");
        dbDescargas.keepSynced(true);
        FirebaseRecyclerAdapter<BloqueadoModel, Bloqueados.myViewHolader> adapter = new FirebaseRecyclerAdapter<BloqueadoModel, Bloqueados.myViewHolader>(
                BloqueadoModel.class, R.layout.bloqueado_card, Bloqueados.myViewHolader.class, dbDescargas
        ) {
            @Override
            protected void populateViewHolder(Bloqueados.myViewHolader viewHolder, final BloqueadoModel model, int position) {
                viewHolder.nombre.setText(model.getNombre());
                viewHolder.fecha.setText(model.getFecha());
                viewHolder.msj.setText(model.getMsj());
                Glide.with(Bloqueados.this).load(model.getFotoPerfil()).into(viewHolder.imagen);
                progressDialog.dismiss();
                viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder cliente = new AlertDialog.Builder(Bloqueados.this);
                        cliente.setTitle("Desbloquear a "+model.getNombre()+"?");
                        cliente.setMessage("Esta seguro?");
                        cliente.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference dbBloqueados = database.getReference("bloqueados").child(model.getId());
                                dbBloqueados.removeValue();
                            }
                        });
                        cliente.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        cliente.show();
                        Toast.makeText(Bloqueados.this, model.getId() + "", Toast.LENGTH_LONG).show();
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(recyclerView.getAdapter() != null){
                    if(recyclerView.getAdapter().getItemCount() == 0) {
                        recyclerView.setBackgroundResource(R.drawable.cargando);
                        progressDialog.cancel();
                    }
                }
            }
        },2000);*/
    }


    public static class myViewHolader extends RecyclerView.ViewHolder {
        TextView nombre, fecha, msj;
        ImageView imagen;
        //public Context mContext;

        public myViewHolader(final View itemView) {
            super(itemView);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getPosition();
                }
            });*/
            nombre = (TextView) itemView.findViewById(R.id.bloqueado_nombreMensaje);
            fecha = (TextView) itemView.findViewById(R.id.bloqueado_horaMensaje);
            msj = (TextView) itemView.findViewById(R.id.bloqueado_mensajeMensaje);
            imagen = (ImageView) itemView.findViewById(R.id.bloqueado_fotoPerfilMensaje);
        }


    }
}
