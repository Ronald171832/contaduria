package com.informaciones.facultad.contaduriaalacima;

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

import java.util.List;

/**
 * Created by Admin on 6/21/2017.
 */

public class ImageListAdapter extends ArrayAdapter<ItemLayout> {
    private Activity context;
    private int resource;
    private List<ItemLayout> listImage;

    public ImageListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<ItemLayout> objects) {
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
        TextView tvTitulo = (TextView) v.findViewById(R.id.item_tv_titulo);
        ImageView imagen = (ImageView) v.findViewById(R.id.item_iv_image);
        ImageView favorito = (ImageView) v.findViewById(R.id.item_iv_favorite);
        ImageView compartir = (ImageView) v.findViewById(R.id.item_iv_compartir);
        ImageView hablar = (ImageView) v.findViewById(R.id.item_iv_hablar);
        ImageView comentar = (ImageView) v.findViewById(R.id.item_iv_comentar);
        TextView tvVisitas = (TextView) v.findViewById(R.id.item_tv_visitas);

        tvTitulo.setText(listImage.get(position).getTitulo());
        tvVisitas.setText("visitas "+listImage.get(position).getVisitas()+"");
        Glide.with(context).load(listImage.get(position).getImagen()).into(imagen);
        return v;
    }

}
