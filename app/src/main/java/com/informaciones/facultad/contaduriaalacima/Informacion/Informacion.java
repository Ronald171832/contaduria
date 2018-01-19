package com.informaciones.facultad.contaduriaalacima.Informacion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.ImgenCompleta.ImagenCompleta;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Informacion extends AppCompatActivity {
    private DatabaseReference dbCarpetas;
    public RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    LinearLayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    List<Boolean> booleans;

    //busqueda
    private ClienteAdapter adapter;
    private ArrayList<InformacionModel> listaInformaciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_pantalla_principal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_informacion, menu);
        MenuItem search = menu.findItem(R.id.search_informacion);
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
                    //verificarContenido(newText);
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


    private void iniciar() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        final Boolean aBoolean = sharedPreferences.getBoolean("superUsuario", false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Informacion...");
        progressDialog.show();
        recyclerView = (RecyclerView) findViewById(R.id.rvInformacion);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbCarpetas = database.getReference("info");
        dbCarpetas.keepSynced(true);
        booleans = new ArrayList<>();



        listaInformaciones = new ArrayList<>();
        adapter = new ClienteAdapter(this, listaInformaciones);
        recyclerView.setAdapter(adapter);

        dbCarpetas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaInformaciones.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InformacionModel publicacionesModel = new InformacionModel();
                    try {
                        Map<String, Object> mapPublicacion = (HashMap) snapshot.getValue();
                        publicacionesModel.setFecha((String) mapPublicacion.get("fecha"));
                        publicacionesModel.setDescripcion((String) mapPublicacion.get("descripcion"));
                        publicacionesModel.setImagen((String) mapPublicacion.get("imagen"));
                        publicacionesModel.setTitulo((String) mapPublicacion.get("titulo"));
                        listaInformaciones.add(publicacionesModel);
                    } catch (Exception e) {
                        String error = e.getMessage().toString();
                    }
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        
       /* FirebaseRecyclerAdapter<InformacionModel, Informacion.myViewHolader> adapter = new FirebaseRecyclerAdapter<InformacionModel, Informacion.myViewHolader>(
                InformacionModel.class, R.layout.informacion_card, Informacion.myViewHolader.class, dbCarpetas
        ) {
            @Override
            protected void populateViewHolder(final Informacion.myViewHolader viewHolder, final InformacionModel model, final int position) {
                booleans.add(false);
                Glide.with(Informacion.this).load(model.getImagen()).into(viewHolder.imagen);
                if (aBoolean) {
                    viewHolder.imagen.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            tocarItem(model.getTitulo(), model.getDescripcion(), model.getFecha());
                            return false;
                        }
                    });
                }
                viewHolder.titulo.setText(model.getTitulo());
                viewHolder.descripcion.setText(model.getDescripcion());
                viewHolder.flecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean b = !booleans.get(position);
                        booleans.add(position, b);
                        if (b) {
                            viewHolder.flecha.setBackgroundResource(R.drawable.flecha_derecha);
                            viewHolder.linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.flecha.setBackgroundResource(R.drawable.flecha_abajo);
                            viewHolder.linearLayout.setVisibility(View.GONE);
                        }
                    }
                });
                progressDialog.dismiss();
            }
        };*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView.getAdapter() != null) {
                    if (recyclerView.getAdapter().getItemCount() == 0) {
                        recyclerView.setBackgroundResource(R.drawable.ico_conta);
                        progressDialog.cancel();
                    }
                }
            }
        }, 8000);
    }

    private void tocarItem(final String titulo, final String descripcion, final String fecha) {
        final ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Editar Datos");
        listItems.add("Eliminar");
        new AlertDialog.Builder(Informacion.this)
                .setTitle("Elija una Opción:")
                .setCancelable(false)
                .setAdapter(new ArrayAdapter<String>(Informacion.this, android.R.layout.simple_list_item_1, listItems),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        editarItem(fecha, titulo, descripcion);
                                        break;
                                    case 1:
                                        eliminarItem(fecha, titulo);
                                        break;
                                }
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();
    }

    private void editarItem(final String fecha, final String titulo, final String descripcion) {
        final ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Editar Titulo");
        listItems.add("Editar Descripcion");
       // listItems.add("Editar Imagen");
        new AlertDialog.Builder(Informacion.this)
                .setTitle("Elija una Opción:")
                .setCancelable(false)
                .setAdapter(new ArrayAdapter<String>(Informacion.this, android.R.layout.simple_list_item_1, listItems),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        editarVentana(titulo, fecha, "titulo");
                                        break;
                                    case 1:
                                        editarVentana(descripcion, fecha, "descripcion");
                                        break;
                                    case 2:
                                        //  elegirEstado(fecha);
                                        break;
                                }
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();
    }

    private void editarVentana(String nombre, final String fecha, final String opcion) {
        final Dialog ventana_emergente = new Dialog(Informacion.this);
        ventana_emergente.setTitle("Editar" + opcion);
        ventana_emergente.setContentView(R.layout.ventana_emergente2);
        final EditText editText = (EditText) ventana_emergente.findViewById(R.id.et_ventana_ingresar);
        Button boton = (Button) ventana_emergente.findViewById(R.id.bt_ventana_aceptar);
        final TextView textView = (TextView) ventana_emergente.findViewById(R.id.tv_ventana_titulo);
        textView.setText("Editar " + opcion);
        editText.setText(nombre);
        int width = (int) (Informacion.this.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (Informacion.this.getResources().getDisplayMetrics().heightPixels * 0.5);
        ventana_emergente.getWindow().setLayout(width, height);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    editText.setError("No dejar vacio el campo...");
                    editText.requestFocus();
                } else {
                    DatabaseReference dbBloqueados = FirebaseDatabase.getInstance().getReference("info/" + fecha + "/" + opcion);
                    dbBloqueados.setValue(editText.getText().toString());
                    ventana_emergente.dismiss();
                }
            }
        });
        ventana_emergente.show();
    }

    private void eliminarItem(final String fecha, String titulo) {
        AlertDialog.Builder cliente = new AlertDialog.Builder(Informacion.this);
        cliente.setTitle("Eliminar " + titulo);
        cliente.setMessage("Esta Seguro?");
        cliente.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference dbBloqueados = FirebaseDatabase.getInstance().getReference("info/" + fecha);
                dbBloqueados.removeValue();
            }
        });
        cliente.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        cliente.show();
    }

    public static class myViewHolader extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        ImageView imagen, flecha;
        LinearLayout linearLayout;
        public Context mContext;

        public myViewHolader(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.cardInformacionTitulo);
            descripcion = (TextView) itemView.findViewById(R.id.cardInformacionDescripcion);
            imagen = (ImageView) itemView.findViewById(R.id.cardInformacionImagen);
            flecha = (ImageView) itemView.findViewById(R.id.cardInformacionImagenFlecha);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.cardInformacionLinerLayout);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext = v.getContext();
                    Intent intent = new Intent(v.getContext(), ImagenesFolder.class);
                    intent.putExtra("folder", fecha.getText().toString().trim());
                    mContext.startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });*/
        }
    }




    // CLASE ADAPTER PARA LAS PUBLICACIONES
    public class ClienteAdapter extends RecyclerView.Adapter<myViewHolader> implements Filterable {
        private ArrayList<InformacionModel> publicacionesList;
        private ArrayList<InformacionModel> publicacionesFilterList;
        private Context context;
        SharedPreferences sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        final Boolean aBoolean = sharedPreferences.getBoolean("superUsuario", false);

        public ClienteAdapter(Context context, ArrayList<InformacionModel> arrayList) {
            publicacionesList = arrayList;
            publicacionesFilterList = arrayList;
            this.context = context;
        }


        @Override
        public myViewHolader onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.informacion_card, viewGroup, false);
            myViewHolader vh = new myViewHolader(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final myViewHolader viewHolder, final int i) {
            booleans.add(false);
            Glide.with(Informacion.this).load(publicacionesFilterList.get(i).getImagen()).into(viewHolder.imagen);
            if (aBoolean) {
                viewHolder.imagen.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        tocarItem(publicacionesFilterList.get(i).getTitulo(), publicacionesFilterList.get(i).getDescripcion(), publicacionesFilterList.get(i).getFecha());
                        return false;
                    }
                });
            }
            viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Informacion.this,ImagenCompleta.class);
                    intent.putExtra("url",publicacionesFilterList.get(i).getImagen());
                    startActivity(intent);
                }
            });
            viewHolder.titulo.setText(publicacionesFilterList.get(i).getTitulo());
            viewHolder.descripcion.setText(publicacionesFilterList.get(i).getDescripcion());
            viewHolder.flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean b = !booleans.get(i);
                    booleans.add(i, b);
                    if (b) {
                        viewHolder.flecha.setBackgroundResource(R.drawable.flecha_derecha);
                        viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.flecha.setBackgroundResource(R.drawable.flecha_abajo);
                        viewHolder.linearLayout.setVisibility(View.GONE);
                    }
                }
            });
            progressDialog.dismiss();
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
                    ArrayList<InformacionModel> filteredList;
                    String charString = charSequence.toString();

                    if (charString.isEmpty()) {
                        filteredList = new ArrayList<>();
                        publicacionesFilterList = publicacionesList;
                    } else {

                        filteredList = new ArrayList<>();

                        for (InformacionModel publicacion : publicacionesList) {

                            if (publicacion.getTitulo().toLowerCase().startsWith(charString.toLowerCase())||
                                    publicacion.getDescripcion().toLowerCase().startsWith(charString.toLowerCase())) {

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
                    publicacionesFilterList = (ArrayList<InformacionModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }



}
