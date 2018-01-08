package com.informaciones.facultad.contaduriaalacima.Categorias;

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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.PantallaPrincipal.MainActivity;
import com.informaciones.facultad.contaduriaalacima.Publicaciones.Publicaciones;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;
import java.util.List;

public class Categorias extends AppCompatActivity {

    private DatabaseReference myRef;
    private RecyclerView rvCategorias;
    private Paint p = new Paint();
    public static Context context;
    private DatabaseReference dbVisitas;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    List<CategoriaModel> categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_principal);
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
                        final Dialog dialog = new Dialog(Categorias.this);
                        dialog.setTitle("Editar");
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.ventana_emergente);
                        final EditText nombrePerfil = (EditText) dialog.findViewById(R.id.et_nombre_ventana_emergente);
                        final TextView titulo = (TextView) dialog.findViewById(R.id.textViewTitulo_ventana);
                        Button boton = (Button) dialog.findViewById(R.id.bt_ventana_emergente);
                        titulo.setText("Editar Categoria");
                        int width = (int) (Categorias.this.getResources().getDisplayMetrics().widthPixels * 1);
                        int height = (int) (Categorias.this.getResources().getDisplayMetrics().heightPixels * 0.50);
                        dialog.getWindow().setLayout(width, height);
                        nombrePerfil.setText(categorias.get(position).getTitulo());
                        boton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (nombrePerfil.getText().toString().equals("")) {
                                    Toast.makeText(getApplicationContext(), "INSERTE NOMBRE!", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference currentUserBD = myRef.child(categorias.get(position).getFecha()).child("titulo");
                                    currentUserBD.setValue(nombrePerfil.getText().toString().trim());
                                    dialog.cancel();
                                }
                            }
                        });
                        dialog.show();


                        //   Toast.makeText(Categorias.this, " izquierda posicion:" + position, Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder ventana = new AlertDialog.Builder(Categorias.this);
                        ventana.setTitle("ELIMINAR CATEGORIA");
                        ventana.setCancelable(false);
                        ventana.setMessage("Elija una opcion:");
                        ventana.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference currentUserBD = myRef.child(categorias.get(position).getFecha());
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
                        // Toast.makeText(Categorias.this, " derecha posicion:" + position, Toast.LENGTH_SHORT).show();
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
            itemTouchHelper.attachToRecyclerView(rvCategorias);
        }
    }


    private void iniciar() {
        context = getApplicationContext();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Categorias...");
        progressDialog.show();
        categorias = new ArrayList<>();
        rvCategorias = (RecyclerView) findViewById(R.id.rv_Categorias);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        rvCategorias.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("categorias");
        myRef.keepSynced(true);
        FirebaseRecyclerAdapter<CategoriaModel, myViewHolader> adapter =
                new FirebaseRecyclerAdapter<CategoriaModel, myViewHolader>(CategoriaModel.class, R.layout.categoria_card, myViewHolader.class, myRef) {
                    @Override
                    protected void populateViewHolder(myViewHolader viewHolder, final CategoriaModel model, int position) {
                        viewHolder.tvTitulo.setText(model.getTitulo());
                        CategoriaModel categoriaModel = new CategoriaModel(model.getTitulo(), model.getFecha(), 0);
                        categorias.add(categoriaModel);
                        viewHolder.tvVisitas.setText("visitas " + model.getVisitas() + "");
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String categoria = model.getFecha();
                                dbVisitas = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/visitas");
                                dbVisitas.setValue(model.getVisitas() + 1);
                                Intent intent = new Intent(Categorias.this, Publicaciones.class);
                                intent.putExtra("categoria", categoria);
                                startActivity(intent);
                            }
                        });
                        progressDialog.dismiss();
                    }
                };
        rvCategorias.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        startActivity(new Intent(Categorias.this, MainActivity.class));
    }

    public static class myViewHolader extends RecyclerView.ViewHolder {
        public TextView tvTitulo, tvVisitas, tvFecha;


        public myViewHolader(View itemView) {
            super(itemView);
            //  tvFecha.setPadding(0,0,0,0);
            tvTitulo = (TextView) itemView.findViewById(R.id.cardCategoriaTitulo);
            tvVisitas = (TextView) itemView.findViewById(R.id.cardCategoriaVisitas);
            tvFecha = (TextView) itemView.findViewById(R.id.cardCategoriaFecha);
        }
    }
}
