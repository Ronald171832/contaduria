package com.informaciones.facultad.contaduriaalacima.Chat;

import android.net.Uri;

/**
 * Created by user on 05/09/2017. 05
 */

public class MensajeRecibir extends Mensaje {

    private Long hora;

    public MensajeRecibir() {
    }

    public MensajeRecibir(Long hora) {
        this.hora = hora;
    }

    public MensajeRecibir(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Long hora, String uriFoto) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje,uriFoto);
        this.hora = hora;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
