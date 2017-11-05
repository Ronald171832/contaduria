package com.informaciones.facultad.contaduriaalacima.Publicaciones;

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

public class PublicacionesListAdapter extends ArrayAdapter<PublicacionesModel> {
    private Activity context;
    private int resource;
    private List<PublicacionesModel> listImage;

    public PublicacionesListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<PublicacionesModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listImage = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);
        TextView tvTitulo = (TextView) v.findViewById(R.id.cardTituloPublicacion);
        TextView tvDescripcion = (TextView) v.findViewById(R.id.cardDescripcionPublicacion);
        ImageView imagen = (ImageView) v.findViewById(R.id.cardImagenPublicacion);
        TextView tvLike = (TextView) v.findViewById(R.id.cardLikesPublicacion);

        tvTitulo.setText(listImage.get(position).getTitulo());
        tvDescripcion.setText(listImage.get(position).getDescripcion());
        tvLike.setText(listImage.get(position).getLikes()+"");
        Glide.with(context).load(listImage.get(position).getImagen()).into(imagen);
        return v;
    }

}
