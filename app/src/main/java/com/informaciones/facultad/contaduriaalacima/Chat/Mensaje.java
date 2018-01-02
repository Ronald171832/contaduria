package com.informaciones.facultad.contaduriaalacima.Chat;

/**
 * Created by user on 04/09/2017. 04
 */

public class Mensaje {

    private String mensaje;
    private String urlFoto; // firebase
    private String nombre;
    private String fotoPerfil;
    private String type_mensaje;
    private String enviado_recibido;
    // CAMPO ADICIONAL
    private String URIFotoImagenURI;// content provider


    public Mensaje() {
    }

    public Mensaje(String mensaje, String nombre, String fotoPerfil, String type_mensaje,String enviado_recibido,String uriFotoImagen) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.type_mensaje = type_mensaje;
        this.enviado_recibido=enviado_recibido;
        this.URIFotoImagenURI =uriFotoImagen;
    }

    public Mensaje(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje,String enviado_recibido,String uriFotoImagen) {
        this.mensaje = mensaje;
        this.urlFoto = urlFoto;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.type_mensaje = type_mensaje;
        this.enviado_recibido=enviado_recibido;
        this.URIFotoImagenURI =uriFotoImagen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getType_mensaje() {
        return type_mensaje;
    }

    public void setType_mensaje(String type_mensaje) {
        this.type_mensaje = type_mensaje;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getEnviado_recibido() {
        return enviado_recibido;
    }

    public void setEnviado_recibido(String enviado_recibido) {
        this.enviado_recibido = enviado_recibido;
    }

    public String getUriFotoImagen() {
        return URIFotoImagenURI;
    }

    public void setUriFotoImagen(String uriFotoImagen) {
        this.URIFotoImagenURI = uriFotoImagen;
    }
}
