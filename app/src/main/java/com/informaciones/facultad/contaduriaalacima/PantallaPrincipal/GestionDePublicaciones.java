package com.informaciones.facultad.contaduriaalacima.PantallaPrincipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.informaciones.facultad.contaduriaalacima.Categorias.CrearCategorias;
import com.informaciones.facultad.contaduriaalacima.Documentos.CrearDocumentos;
import com.informaciones.facultad.contaduriaalacima.Publicaciones.CrearPublicacion;
import com.informaciones.facultad.contaduriaalacima.R;

public class GestionDePublicaciones extends AppCompatActivity {
    Button categoria,publicaciones,documento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_de_publicaciones);
        iniciar();
    }

    private void iniciar() {
        categoria = (Button) findViewById(R.id.bt_ges_cate);
        publicaciones = (Button) findViewById(R.id.bt_ges_publi);
        documento = (Button) findViewById(R.id.bt_ges_docu);
        Animation animationizq = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);;
        Animation   animationder = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animationder.setDuration(1000);
        animationizq.setDuration(1000);
        categoria.setAnimation(animationizq);
        publicaciones.setAnimation(animationder);
        documento.setAnimation(animationizq);

    }

    public void documentos(View view) {
        startActivity(new Intent(this, CrearDocumentos.class));
    }

    public void publicaciones(View view) {
        startActivity(new Intent(this, CrearPublicacion.class));
    }

    public void categorias(View view) {
        startActivity(new Intent(this, CrearCategorias.class));
    }
}
