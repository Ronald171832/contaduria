package com.informaciones.facultad.contaduriaalacima.Documentos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearDocumentos extends AppCompatActivity {
    DatabaseReference dbCategoria;
    private EditText et_titulo, et_descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentos_crear);
        iniciar();
    }

    private void iniciar() {
        dbCategoria = FirebaseDatabase.getInstance().getReference("documentos");
        et_titulo = (EditText) findViewById(R.id.et_tituloDocumento);
        et_descripcion = (EditText) findViewById(R.id.et_descripcionDocumento);
    }
    public void subirImagenDocumento(View v) {
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String fechaHora=hourdateFormat.format(date);

        final DocumentoModel documento = new DocumentoModel(et_titulo.getText().toString().trim(), et_descripcion.getText().toString().trim(), "0",fechaHora);
        dbCategoria.child(fechaHora).setValue(documento);
        Toast.makeText(getApplicationContext(), "Documento cargado Correctamente!", Toast.LENGTH_SHORT).show();
        et_titulo.setText("");
        et_descripcion.setText("");


    }

    public void listarDocumentos(View v) {
        Intent i = new Intent(CrearDocumentos.this, Documentos.class);
        startActivity(i);
    }


}
