package com.informaciones.facultad.contaduriaalacima.Fragmentos;

/**
 * Created by USUARIO on 17/09/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.informaciones.facultad.contaduriaalacima.R;


public class AcercaDeFragment extends Fragment {

    private View parentView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_acercade, container, false);

        return parentView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
