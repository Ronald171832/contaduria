package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.informaciones.facultad.contaduriaalacima.R;
import com.informaciones.facultad.contaduriaalacima.Publicaciones.CrearPublicacion.*;

import java.util.List;

/**
 * Created by Admin on 6/21/2017.
 */

public class PublicacionesListAdapter extends ArrayAdapter<PublicacionesModel> {
    private Activity context;
    private int resource;
    private List<PublicacionesModel> listImage;
    // parche para las imagenes


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
        //ImageView imagen = (ImageView) v.findViewById(R.id.cardImagenPublicacion);
        ViewPager vpImagenes=(ViewPager)v.findViewById(R.id.vpListarImagenesPublicacion);
        TextView tvLike = (TextView) v.findViewById(R.id.cardLikesPublicacion);

        tvTitulo.setText(listImage.get(position).getTitulo());
        tvDescripcion.setText(listImage.get(position).getDescripcion());
        tvLike.setText(listImage.get(position).getLikes()+"");
        ImagePagerAdapter imgPager=new ImagePagerAdapter(listImage.get(position).getImagenes());
        vpImagenes.setAdapter(imgPager);
        //Glide.with(context).load(listImage.get(position).getImagen()).into(imagen);
        return v;
    }


    public class ImagePagerAdapter extends PagerAdapter {

        //private Bitmap[] mImages;
        private Uri[] urlImages;

        public ImagePagerAdapter(Uri[] urlImages){
            //this.mImages=imagenes;
            this.urlImages=urlImages;
        }


        @Override
        public int getCount() {
            return urlImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            //return false;
            //return view == ((ImageView) object);
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            /*Context context = PreguntasAdapter.context.getApplicationContext();
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(8);
            imageView.setPadding(padding, padding, padding, padding);
            //imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageBitmap(mImages[position]);
            //imageView.setImageResource(mImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
            //return super.instantiateItem(container, position);*/
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.slide_images, container, false);
            container.addView(layout);
            ImageView image = (ImageView) layout.findViewById(R.id.imageSlide);

            /*image.setImageURI(urlImages[position]);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
            //image.setImageBitmap(mImages[position]);
            Glide.with(context).load(urlImages[position]).into(image);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            //super.destroyItem(container, position, object);
        }
    }

}
