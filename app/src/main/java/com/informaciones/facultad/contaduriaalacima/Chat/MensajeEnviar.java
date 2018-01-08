package com.informaciones.facultad.contaduriaalacima.Chat;

/**
 * Created by user on 05/09/2017. 05
 */

public class MensajeEnviar extends Mensaje {
    private String hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(String hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, String hora, String enviado_recibido, String uriFoto) {
        super(mensaje, nombre, fotoPerfil, type_mensaje,enviado_recibido,uriFoto);
        this.hora = hora;

    }

    public MensajeEnviar(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, String hora,String enviado_recibido,String uriFoto) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje,enviado_recibido,uriFoto);
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
