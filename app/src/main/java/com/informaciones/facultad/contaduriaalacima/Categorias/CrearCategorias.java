package com.informaciones.facultad.contaduriaalacima.Categorias;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informaciones.facultad.contaduriaalacima.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CrearCategorias extends AppCompatActivity {

    DatabaseReference dbCategoria;
    StorageReference storageReference;
    private EditText et_titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_crear);
        iniciar();
    }

    private void iniciar() {
        dbCategoria = FirebaseDatabase.getInstance().getReference("categorias");
        storageReference = FirebaseStorage.getInstance().getReference("categorias");

        et_titulo = (EditText) findViewById(R.id.et_tituloCategoria);
    }


    @SuppressWarnings("VisibleForTests")
    public void subirImagen(View v) {
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String fechaHora = hourdateFormat.format(date);
        final CategoriaModel categorias = new CategoriaModel(et_titulo.getText().toString().trim(),fechaHora, 0);
        dbCategoria.child(fechaHora).setValue(categorias);
    }

    public void listarCategorias(View v) {
        Intent i = new Intent(CrearCategorias.this, Categorias.class);
        startActivity(i);
    }
}
