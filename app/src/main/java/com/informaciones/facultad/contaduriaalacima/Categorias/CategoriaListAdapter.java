package com.informaciones.facultad.contaduriaalacima.Categorias;

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

public class CategoriaListAdapter extends ArrayAdapter<CategoriaModel> {
    private Activity context;
    private int resource;
    private List<CategoriaModel> listCategorias;

    public CategoriaListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<CategoriaModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listCategorias = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(resource, null);
        TextView tvTitulo = (TextView) v.findViewById(R.id.cardTitulo);
        ImageView imagen = (ImageView) v.findViewById(R.id.cardImagenCATEGORIA);
        TextView tvDescripcion = (TextView) v.findViewById(R.id.cardDescripcion);
        TextView tvVisitas = (TextView) v.findViewById(R.id.cardVisitas);

        tvTitulo.setText(listCategorias.get(position).getTitulo());
        tvDescripcion.setText(listCategorias.get(position).getDescripcion());
        tvVisitas.setText("visitas "+listCategorias.get(position).getVisitas()+"");
        Glide.with(context).load(listCategorias.get(position).getImagen()).into(imagen);
        return v;
    }

}
