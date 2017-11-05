package com.informaciones.facultad.contaduriaalacima.Documentos;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.informaciones.facultad.contaduriaalacima.R;

import java.util.List;

/**
 * Created by Admin on 6/21/2017.
 */

public class DocumentoListAdapter extends ArrayAdapter<DocumentoModel> {
    private Activity context;
    private int resource;
    private List<DocumentoModel> listDocumentos;

    public DocumentoListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DocumentoModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listDocumentos = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(resource, null);
        TextView tvTitulo = (TextView) v.findViewById(R.id.cardTituloDocumento);
        ImageView imagen = (ImageView) v.findViewById(R.id.cardImagenDocumento);
        TextView tvLinkDeDescarga = (TextView) v.findViewById(R.id.cardDescripcionDocumento);
        TextView tvDescargas = (TextView) v.findViewById(R.id.cardDescargas);

        tvTitulo.setText(listDocumentos.get(position).getTitulo());
        tvLinkDeDescarga.setText(listDocumentos.get(position).getLinkDeDescarga());
        tvDescargas.setText("Descargas "+listDocumentos.get(position).getDescargas());
        Glide.with(context).load(listDocumentos.get(position).getImagen()).into(imagen);
        return v;
    }

}
