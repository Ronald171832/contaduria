package com.informaciones.facultad.contaduriaalacima.Chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.informaciones.facultad.contaduriaalacima.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 04/09/2017. 04
 */

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;
    SharedPreferences sharedPreferences;


    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(MensajeRecibir m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.chat_diseno_mensajes,parent,false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(final HolderMensaje holder, final int position) {
        sharedPreferences = c.getSharedPreferences("nombre", MODE_PRIVATE);
        String id=sharedPreferences.getString("id","");
        if (listMensaje.get(position).getEnviado_recibido().equals(id)){
            holder.getEnviado().setVisibility(View.VISIBLE);
            holder.getMensajeImage().setBackgroundResource(R.color.colorEnviar);
            holder.getRecibido().setVisibility(View.GONE);
        } else {
            holder.getEnviado().setVisibility(View.GONE);
            holder.getMensajeImage().setBackgroundResource(R.color.colorRecibir);
            holder.getRecibido().setVisibility(View.VISIBLE);
        }
        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        if(listMensaje.get(position).getType_mensaje().equals("2")){
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensaje());
        }else if(listMensaje.get(position).getType_mensaje().equals("1")){
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }
        if(listMensaje.get(position).getFotoPerfil().isEmpty()){
            holder.getFotoMensajePerfil().setImageResource(R.drawable.user);
        }else{
            Glide.with(c).load(listMensaje.get(position).getFotoPerfil()).into(holder.getFotoMensajePerfil());
        }
        Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");//a pm o am
        holder.getHora().setText(sdf.format(d));

        holder.getFotoMensaje().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent visorImg=new Intent(Intent.ACTION_VIEW);
                Uri uriFotoActual=Uri.parse(listMensaje.get(position).getUriFotoImagen());
                if (uriFotoActual!=null){
                    visorImg.setDataAndType(uriFotoActual,"image/*");
                    c.startActivity(visorImg);
                }


            }
        });
    }





    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

}
