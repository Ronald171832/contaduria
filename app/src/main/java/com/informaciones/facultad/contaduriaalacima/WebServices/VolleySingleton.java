package com.informaciones.facultad.contaduriaalacima.WebServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Ronald Lopez on 23/07/2017.
 */

public final class VolleySingleton {

    // Atributos
    private static VolleySingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;
    private ImageLoader mImageLoader;
    Cache cache; Network network;

    private VolleySingleton(Context context) {
        VolleySingleton.context = context;
        requestQueue = getRequestQueue();


        // TODO: CODIGO PARA EJECUTAR LA PETICION DE IMAGENES
        mImageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new VolleySingleton(context.getApplicationContext());
        }
        return singleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
// MODIFICACIONES POSIBLES
            cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            network= new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            requestQueue.start();


            ///requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


    /**
     * Añade la petición a la cola
     *
     * @param req petición
     * @param <T> Resultado final de tipo T
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
