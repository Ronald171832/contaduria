package com.informaciones.facultad.contaduriaalacima.Bloqueados;

/**
 * Created by Ronald Lopez on 08/01/2018.
 */

public class BloqueadoModel {

    public String nombre;
    public String fecha;
    public String msj;
    public String fotoPerfil;

    public BloqueadoModel(String nombre, String fecha, String msj, String fotoPerfil, String id) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.msj = msj;
        this.fotoPerfil = fotoPerfil;
        this.id = id;
    }

    public String id;

    public BloqueadoModel() {
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMsj() {
        return msj;
    }

    public void setMsj(String msj) {
        this.msj = msj;
    }
}
