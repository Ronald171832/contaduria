package com.informaciones.facultad.contaduriaalacima.Chat;

import android.net.Uri;

import java.util.Map;

/**
 * Created by user on 05/09/2017. 05
 */

public class MensajeEnviar extends Mensaje {
    private Map hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora, String enviado_recibido, String uriFoto) {
        super(mensaje, nombre, fotoPerfil, type_mensaje,enviado_recibido,uriFoto);
        this.hora = hora;

    }

    public MensajeEnviar(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Map hora,String enviado_recibido,String uriFoto) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje,enviado_recibido,uriFoto);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
