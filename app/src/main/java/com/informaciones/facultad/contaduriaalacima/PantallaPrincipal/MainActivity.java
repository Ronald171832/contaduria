package com.informaciones.facultad.contaduriaalacima.PantallaPrincipal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.informaciones.facultad.contaduriaalacima.Categorias.Categorias;
import com.informaciones.facultad.contaduriaalacima.Chat.Chat;
import com.informaciones.facultad.contaduriaalacima.Documentos.CrearDocumentos;
import com.informaciones.facultad.contaduriaalacima.Fragmentos.AcercaDeFragment;
import com.informaciones.facultad.contaduriaalacima.Fragmentos.HomeFragment;
import com.informaciones.facultad.contaduriaalacima.R;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ResideMenu resideMenu;
    private Context mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemDocumento;
    private ResideMenuItem itemChat;
    private ResideMenuItem itemPreguntas;
    private ResideMenuItem itemAbout;
    private ResideMenuItem itemContacto;
    private ResideMenuItem itemGestion;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        setUpMenu();
        if (savedInstanceState == null) {
            changeFragment(new HomeFragment());
        }

    }

    private void setUpMenu() {
        resideMenu = new ResideMenu(MainActivity.this);
        resideMenu.setBackground(R.drawable.menu_contaduria);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.7f);
        // create menu items;
        itemHome = new ResideMenuItem(this, R.drawable.menu, "Inicio");
        itemDocumento = new ResideMenuItem(this, R.drawable.file, "Documento");
        itemChat = new ResideMenuItem(this, R.drawable.chat, "Chat");
        itemPreguntas = new ResideMenuItem(this, R.drawable.customerservice, "Categorias");
        itemAbout = new ResideMenuItem(this, R.drawable.information, "Acerca de");
        itemContacto = new ResideMenuItem(this, R.drawable.visitcard, "Contacto");
        itemGestion = new ResideMenuItem(this, R.drawable.super_user, "Gestion");

        itemHome.setOnClickListener(this);
        itemDocumento.setOnClickListener(this);
        itemChat.setOnClickListener(this);
        itemPreguntas.setOnClickListener(this);
        itemAbout.setOnClickListener(this);
        itemContacto.setOnClickListener(this);
        itemGestion.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemPreguntas, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemChat, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemDocumento, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemGestion, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemContacto, ResideMenu.DIRECTION_RIGHT);
        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (view == itemHome) {
            changeFragment(new HomeFragment());
        } else if (view == itemChat) {
            startActivity(new Intent(MainActivity.this, Chat.class));
        } else if (view == itemDocumento) {
            startActivity(new Intent(MainActivity.this, CrearDocumentos.class));
        } else if (view == itemPreguntas) {
            startActivity(new Intent(MainActivity.this, Categorias.class));
        } else if (view == itemAbout) {
            changeFragment(new AcercaDeFragment()); // acerca de...
        } else if (view == itemContacto) {
            // startActivity(new Intent(MainActivity.this, Email_Envios.class));
        } else if (view == itemGestion) {
            verificarIngreso();
        }

        resideMenu.closeMenu();
    }

    private void verificarIngreso() {
        final Dialog login_ventana = new Dialog(MainActivity.this);
        login_ventana.setTitle("Registrar Docente");
        login_ventana.setContentView(R.layout.gestionar_contra);
        final EditText contra = (EditText) login_ventana.findViewById(R.id.et_gestion_contra);
        Button boton = (Button) login_ventana.findViewById(R.id.btn_gestion_contra);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = contra.getText().toString().trim();
                if (c.equals("cp2017")) {
                    startActivity(new Intent(MainActivity.this, GestionDePublicaciones.class));
                } else {
                    login_ventana.cancel();
                }
            }
        });
        login_ventana.show();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //    Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //   Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    boolean b = true;

    public void abrirLaterales(View view) {
        if (b) {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        } else {
            resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
        }
        b = !b;
    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }
}

