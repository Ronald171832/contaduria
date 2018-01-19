package com.informaciones.facultad.contaduriaalacima.Publicaciones;

/**
 * Created by Ronald Lopez on 02/11/2017.
 */

public class ComentarioModel {

    public String nombre;
    public String fecha;
    public String msj;
    public String fotoPerfil;

    public ComentarioModel(String nombre, String fecha, String msj, String fotoPerfil, String idUsuario) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.msj = msj;
        this.fotoPerfil = fotoPerfil;
        this.idUsuario = idUsuario;
    }

    public String idUsuario;

    public ComentarioModel() {
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }




    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getfecha() {
        return fecha;
    }

    public void setfecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMsj() {
        return msj;
    }

    public void setMsj(String msj) {
        this.msj = msj;
    }
}
