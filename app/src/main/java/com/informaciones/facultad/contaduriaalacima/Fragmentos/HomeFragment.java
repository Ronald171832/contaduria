package com.informaciones.facultad.contaduriaalacima.Fragmentos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informaciones.facultad.contaduriaalacima.R;
import com.special.ResideMenu.ResideMenu;


public class HomeFragment extends Fragment {

    private View parentView;
    private ResideMenu resideMenu;
    private DatabaseReference dbPublicaciones;
    private ImageView portada;
    private TextView estado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragement_pantalla_principa, container, false);
        portada = (ImageView) parentView.findViewById(R.id.iv_portada);
        estado = (TextView) parentView.findViewById(R.id.tv_perfil_estado);
        cargarImagen();
        cargarEstado();
        //setUpViews();
        return parentView;
    }

    private void cargarEstado() {
        System.out.println("***************************/////////////////////////////////");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbPublicaciones = database.getReference("estado");
        dbPublicaciones.keepSynced(true);
        dbPublicaciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String perfilModel = snapshot.getValue(String.class);
                    if (perfilModel.equals("")) {
                        estado.setVisibility(View.GONE);
                    } else {
                        estado.setVisibility(View.VISIBLE );
                        estado.setText(perfilModel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void cargarImagen() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbPublicaciones = database.getReference("perfil");
        dbPublicaciones.keepSynced(true);
        dbPublicaciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String perfilModel = snapshot.getValue(String.class);
                    Glide.with(HomeFragment.this).load(perfilModel).into(portada);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
