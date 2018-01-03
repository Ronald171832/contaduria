package com.informaciones.facultad.contaduriaalacima.Documentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;
import java.util.List;

public class Documentos extends AppCompatActivity {
    public RecyclerView recyclerView;
    public static DatabaseReference dbDescargas;
    private ProgressDialog progressDialog;
    private Paint p = new Paint();
    public static Context context;
    SharedPreferences sharedPreferences;
    List<DocumentoModel> documentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentos_principal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciar();
        tocarRecycler();
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


    private void tocarRecycler() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        Boolean aBoolean = sharedPreferences.getBoolean("superUsuario", false);
        if (aBoolean) {
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition();

                    if (direction == ItemTouchHelper.LEFT) {
                        final Dialog dialog = new Dialog(Documentos.this);
                        dialog.setTitle("Editar");
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.ventana_emergente);
                        final EditText nombrePerfil = (EditText) dialog.findViewById(R.id.et_nombre_ventana_emergente);
                        final TextView titulo = (TextView) dialog.findViewById(R.id.textViewTitulo_ventana);
                        Button boton = (Button) dialog.findViewById(R.id.bt_ventana_emergente);
                        titulo.setText("Editar Docuemnto");
                        int width = (int) (Documentos.this.getResources().getDisplayMetrics().widthPixels * 1);
                        int height = (int) (Documentos.this.getResources().getDisplayMetrics().heightPixels * 0.50);
                        dialog.getWindow().setLayout(width, height);
                        nombrePerfil.setText(documentos.get(position).getTitulo());
                        boton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (nombrePerfil.getText().toString().equals("")) {
                                    Toast.makeText(getApplicationContext(), "INSERTE NOMBRE!", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference currentUserBD = dbDescargas.child(documentos.get(position).getFecha()).child("titulo");
                                    currentUserBD.setValue(nombrePerfil.getText().toString().trim());
                                    dialog.cancel();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }
                        });
                        dialog.show();


                        //   Toast.makeText(documentos.this, " izquierda posicion:" + position, Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder ventana = new AlertDialog.Builder(Documentos.this);
                        ventana.setTitle("ELIMINAR DOCUMENTO");
                        ventana.setCancelable(false);
                        ventana.setMessage("Elija una opcion:");
                        ventana.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference currentUserBD = dbDescargas.child(documentos.get(position).getFecha());
                                currentUserBD.removeValue();
                            }
                        });
                        ventana.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        ventana.show();
                        // Toast.makeText(documentos.this, " derecha posicion:" + position, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    Bitmap icon;
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        View itemView = viewHolder.itemView;
                        float height = (float) itemView.getBottom() - (float) itemView.getTop();
                        float width = height / 3;

                        if (dX > 0) {
                            p.setColor(Color.parseColor("#D32F2F"));
                            RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                            c.drawRect(background, p);
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                            RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                            c.drawBitmap(icon, null, icon_dest, p);
                        } else {
                            p.setColor(Color.parseColor("#388E3C"));
                            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                            c.drawRect(background, p);
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
                            RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                            c.drawBitmap(icon, null, icon_dest, p);
                        }
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    private void iniciar() {
        documentos = new ArrayList<>();
        context = getApplicationContext();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Documentos...");
        progressDialog.show();
        recyclerView = (RecyclerView) findViewById(R.id.rv_documentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbDescargas = database.getReference("documentos");
        dbDescargas.keepSynced(true);
        FirebaseRecyclerAdapter<DocumentoModel, Documentos.myViewHolader> adapter = new FirebaseRecyclerAdapter<DocumentoModel, Documentos.myViewHolader>(
                DocumentoModel.class, R.layout.documento_card, Documentos.myViewHolader.class, dbDescargas
        ) {
            @Override
            protected void populateViewHolder(Documentos.myViewHolader viewHolder, DocumentoModel model, int position) {
                DocumentoModel documentoModel = new DocumentoModel(model.getTitulo(), "", 0, model.getFecha());
                documentos.add(documentoModel);

                viewHolder.titulo.setText(model.getTitulo());
                viewHolder.fecha.setText(model.getFecha());
                viewHolder.descargas.setText(model.getDescargas() + "");
                viewHolder.link.setText(model.getLinkDeDescarga());
                progressDialog.dismiss();
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
        TextView titulo, link, descargas, fecha;
        ImageView imagen;
        public Context mContext;

        public myViewHolader(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getPosition();
                }
            });
            titulo = (TextView) itemView.findViewById(R.id.cardTituloDescarga);
            fecha = (TextView) itemView.findViewById(R.id.cardFechaDescarga);
            link = (TextView) itemView.findViewById(R.id.cardLinkDescarga);
            descargas = (TextView) itemView.findViewById(R.id.cardCantidadDescargas);
            imagen = (ImageView) itemView.findViewById(R.id.card_img_Descarga);
            imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String linkDescarga = link.getText().toString().trim();
                        mContext = itemView.getContext();
                        Uri uri = Uri.parse(linkDescarga);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                        int d = Integer.parseInt(descargas.getText().toString()) + 1;
                        dbDescargas.child(fecha.getText().toString()).child("descargas").setValue(d);
                    } catch (Exception e) {
                        Toast.makeText(itemView.getContext(), "Enlace roto :(", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    }
}
