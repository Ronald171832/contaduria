package com.informaciones.facultad.contaduriaalacima.Chat;

/**
 * Created by user on 05/09/2017. 05
 */

public class MensajeRecibir extends Mensaje {

    private String hora;

    public MensajeRecibir() {
    }

    public MensajeRecibir(String hora) {
        this.hora = hora;
    }

    public MensajeRecibir(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, String hora, String uriFoto) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje,uriFoto);
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
