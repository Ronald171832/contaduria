package com.informaciones.facultad.contaduriaalacima.Categorias;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.Publicaciones.Publicaciones;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;
import java.util.List;

public class Categorias extends AppCompatActivity {
    private DatabaseReference dbCategorias;
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
    }
}
