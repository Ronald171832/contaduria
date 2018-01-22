package com.informaciones.facultad.contaduriaalacima.Fragmentos;

/**
 * Created by USUARIO on 17/09/2017.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.informaciones.facultad.contaduriaalacima.R;


public class AcercaDeFragment extends Fragment {

    private View parentView;
    private ImageView ro, ma;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_acercade, container, false);
        ro = (ImageView) parentView.findViewById(R.id.iv_roni);
        ma = (ImageView) parentView.findViewById(R.id.iv_manu);
        ro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://api.whatsapp.com/send?phone=59177302270");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
        ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://api.whatsapp.com/send?phone=59179936062");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
        return parentView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
