package com.informaciones.facultad.contaduriaalacima.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.informaciones.facultad.contaduriaalacima.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 04/09/2017. 04
 */

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView nombre;
    private TextView mensaje;
    private TextView hora;
    private CircleImageView fotoMensajePerfil;
    private ImageView fotoMensaje;
    private LinearLayout enviado,recibido;
    private LinearLayout mensajeImage;

    public HolderMensaje(View itemView) {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombreMensaje);
        mensaje = (TextView) itemView.findViewById(R.id.mensajeMensaje);
        hora = (TextView) itemView.findViewById(R.id.horaMensaje);
        fotoMensajePerfil = (CircleImageView) itemView.findViewById(R.id.fotoPerfilMensaje);
        fotoMensaje = (ImageView) itemView.findViewById(R.id.mensajeFoto);
        enviado=(LinearLayout)itemView.findViewById(R.id.l_enviado);
        recibido=(LinearLayout)itemView.findViewById(R.id.l_recibido);
        mensajeImage=(LinearLayout)itemView.findViewById(R.id.l_mensaje);
    }

    public LinearLayout getMensajeImage() {
        return mensajeImage;
    }

    public void setMensajeImage(LinearLayout mensajeImage) {
        this.mensajeImage = mensajeImage;
    }

    public LinearLayout getEnviado() {
        return enviado;
    }

    public void setEnviado(LinearLayout enviado) {
        this.enviado = enviado;
    }

    public LinearLayout getRecibido() {
        return recibido;
    }

    public void setRecibido(LinearLayout recibido) {
        this.recibido = recibido;
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public CircleImageView getFotoMensajePerfil() {
        return fotoMensajePerfil;
    }

    public void setFotoMensajePerfil(CircleImageView fotoMensajePerfil) {
        this.fotoMensajePerfil = fotoMensajePerfil;
    }

    public ImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(ImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }
}
