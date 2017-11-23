package com.informaciones.facultad.contaduriaalacima.Notificaciones;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by USUARIO on 09/11/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    //public static final String URL_REGISTER_LOCAL="http://192.168.43.168:8080/Notificacion_Firebase/register.php";
    public static final String URL_REGISTER_REMOTO="http://pruebaws.hol.es/Notificacion_Contaduria/register.php";

    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        String token=FirebaseInstanceId.getInstance().getToken();
        registrarToken(token);
    }

    private void registrarToken(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token",token)
                .build();

        Request request = new Request.Builder()
                .url(URL_REGISTER_REMOTO) //http://192.168.1.71/fcm/register.php  url origen
                .post(body)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
