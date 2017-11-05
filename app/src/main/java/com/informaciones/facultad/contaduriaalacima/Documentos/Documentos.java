package com.informaciones.facultad.contaduriaalacima.Documentos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.ArrayList;
import java.util.List;

public class Documentos extends AppCompatActivity {
    private DatabaseReference dbDocumentos;
    private List<DocumentoModel> listaDocumentos;
    private ListView lv;
    private DocumentoListAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentos_principal);
        iniciar();
        tocarListView();
    }

    private void tocarListView() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int posicion, long l) {
                final String linkDescarga = listaDocumentos.get(posicion).getLinkDeDescarga();
                ImageView documento = view.findViewById(R.id.card_Descarga_Documento);
                documento.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(linkDescarga);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
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
        listaDocumentos = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewDocumentos);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Documentos...");
        progressDialog.show();
        dbDocumentos = FirebaseDatabase.getInstance().getReference("documentos");
        dbDocumentos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DocumentoModel img = snapshot.getValue(DocumentoModel.class);
                    listaDocumentos.add(img);
                }
                adapter = new DocumentoListAdapter(Documentos.this, R.layout.documento_card, listaDocumentos);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
