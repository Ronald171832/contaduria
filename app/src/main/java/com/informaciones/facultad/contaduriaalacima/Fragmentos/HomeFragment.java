package com.informaciones.facultad.contaduriaalacima.Fragmentos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.informaciones.facultad.contaduriaalacima.R;
import com.special.ResideMenu.ResideMenu;


public class HomeFragment extends Fragment {

    private View parentView;
    private ResideMenu resideMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragement_pantalla_principa, container, false);
        //setUpViews();
        return parentView;
    }

}
