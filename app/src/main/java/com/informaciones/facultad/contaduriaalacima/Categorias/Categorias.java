package com.informaciones.facultad.contaduriaalacima.Categorias;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
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
import com.informaciones.facultad.contaduriaalacima.PantallaPrincipal.MainActivity;
import com.informaciones.facultad.contaduriaalacima.R;

public class Categorias extends AppCompatActivity {

    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_principal);
        iniciar();
        tocarRecycler();
        tocarItem();
        context=getApplicationContext();
    }

    private void tocarItem() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }


    private void tocarRecycler() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(Categorias.this, " izquierda posicion:" + position, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Categorias.this, " derecha posicion:" + position, Toast.LENGTH_SHORT).show();
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

                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.chat);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.cargando);
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

    private void iniciar() {
        recyclerView = (RecyclerView) findViewById(R.id.my_Recylerivew_);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("categorias");
        myRef.keepSynced(true);
        FirebaseRecyclerAdapter<CategoriaModel, myViewHolader> adapter = new FirebaseRecyclerAdapter<CategoriaModel, myViewHolader>(
                CategoriaModel.class, R.layout.categoria_card, myViewHolader.class, myRef
        ) {
            @Override
            protected void populateViewHolder(myViewHolader viewHolder, CategoriaModel model, int position) {
                viewHolder.tvTitulo.setText(model.getTitulo());
                viewHolder.tvDescripcion.setText(model.getDescripcion());
                viewHolder.tvVisitas.setText("visitas " + model.getVisitas() + "");
                Glide.with(Categorias.this).load(model.getImagen()).into(viewHolder.imagen);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"asdasdadsasdasd",Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        //recyclerView.
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Categorias.this, MainActivity.class));
    }

    public static class myViewHolader extends RecyclerView.ViewHolder  {
        public TextView tvTitulo, tvVisitas, tvDescripcion;
        public ImageView imagen;
        private DatabaseReference dbVisitas;


        public myViewHolader(View itemView) {
            super(itemView);
            tvTitulo = (TextView) itemView.findViewById(R.id.cardTitulo);
            imagen = (ImageView) itemView.findViewById(R.id.cardImagenCATEGORIA);
            tvDescripcion = (TextView) itemView.findViewById(R.id.cardDescripcion);
            tvVisitas = (TextView) itemView.findViewById(R.id.cardVisitas);
            tvVisitas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    String categoria = tvTitulo.getText().toString();
                    dbVisitas = FirebaseDatabase.getInstance().getReference("categorias/" + categoria + "/visitas");


                    /*dbPrueba = FirebaseDatabase.getInstance().getReference("auxiliares/" + nombre + "/contra");
                    dbPrueba.setValue(contras.get(getPosition())+1+"");
                    contras.set(getPosition(),contras.get(getPosition())+1);*/
                    /*dbVisitas.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int visitas=Integer.parseInt(dataSnapshot.getValue().toString());
                            visitas++; // aunmentar un like
                            dbVisitas.setValue(visitas);
                            //Toast.makeText(context,,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //String x=dbVisitas.toString();
                    //dbVisitas.
                    //Toast.makeText(,x,Toast.LENGTH_LONG).show();
                    //dbVisitas.setValue(4);*/
                }
            });
        }


    }


    /*private DatabaseReference dbCategorias;
    private List<CategoriaModel> listaCategorias;
    private ListView lv;
    private CategoriaListAdapter adapter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_principal);
        iniciar();
        tocarListView();
    }

    private void tocarListView() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int posicion, long l) {
                String categoria=listaCategorias.get(posicion).getTitulo();
                Intent intent=new Intent(Categorias.this,Publicaciones.class);
                intent.putExtra("categoria",categoria);
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(Categorias.this,"ASASASA",Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void iniciar() {
        listaCategorias = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewCategorias);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Categorias...");
        progressDialog.show();
        dbCategorias = FirebaseDatabase.getInstance().getReference("categorias");
        dbCategorias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CategoriaModel img = snapshot.getValue(CategoriaModel.class);
                    listaCategorias.add(img);
                }
                adapter = new CategoriaListAdapter(Categorias.this, R.layout.categoria_card, listaCategorias);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }*/
}
