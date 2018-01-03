package com.informaciones.facultad.contaduriaalacima.PantallaPrincipal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.informaciones.facultad.contaduriaalacima.Categorias.Categorias;
import com.informaciones.facultad.contaduriaalacima.Chat.Chat;
import com.informaciones.facultad.contaduriaalacima.Documentos.Documentos;
import com.informaciones.facultad.contaduriaalacima.Email.Contacto;
import com.informaciones.facultad.contaduriaalacima.Fragmentos.AcercaDeFragment;
import com.informaciones.facultad.contaduriaalacima.Fragmentos.HomeFragment;
import com.informaciones.facultad.contaduriaalacima.R;
import com.informaciones.facultad.contaduriaalacima.RegistroDeDatos.Registro_Datos;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ResideMenu resideMenu;
    private Context mContext;
    private ResideMenuItem itemHomeR;
    private ResideMenuItem itemHomeI;
    private ResideMenuItem itemDocumento;
    private ResideMenuItem itemChat;
    private ResideMenuItem itemPreguntas;
    private ResideMenuItem itemAbout;
    private ResideMenuItem itemContacto;
    private ResideMenuItem itemGestion;
    SharedPreferences sharedPreferences;
    private static final int STORAGE_PERMISSION_CODE = 23;

    // TODO IMPLEMENTAR NUEVAMENTE EL PAQUETE DE NOTIFICACIONES Y DE WEBSERVICES CON LAS RESPECTIVAS URLS PARA EL ENVIO ,TOMAR EN CUENTA NOTIF APP DE EJEMPLO Y ADECUAR , LA NOTIFICACION ENVIARLA AL MOMENTO QUE SE GENERE EL ULTIMO ENVIO DE IAMGEN CON LA PRIMER IMAGEN Y EL RESTO DEL CONTENIDO


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cosultarRegistroDeDatos();
        mContext = this;
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("test");
            FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            String error = e.getMessage().toString();
        }
        isAlowedReadPermission();

        setUpMenu();
        if (savedInstanceState == null) {
            changeFragment(new HomeFragment());
        }
    }

    public void isAlowedReadPermission() {
        if (isReadStorageAllowed()) {
            //If permission is already having then showing the toast
            //  Toast.makeText(MainActivity.this, "You already have the permission", Toast.LENGTH_LONG).show();
            //Existing the method with return
            return;
        }

        //If the app has not the permission then asking for the permission
        requestStoragePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Displaying a toast
                    Toast.makeText(this, "Permiso aceptado!", Toast.LENGTH_LONG).show();
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Permiso denegado!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    private void cosultarRegistroDeDatos() {
        sharedPreferences = getSharedPreferences("nombre", MODE_PRIVATE);
        boolean registrarDatos = sharedPreferences.getBoolean("registrarDatos", true);
        if (registrarDatos) {
            startActivity(new Intent(MainActivity.this, Registro_Datos.class));
        }
    }

    private void setUpMenu() {
        resideMenu = new ResideMenu(this);
        //resideMenu.setBackgroundColor(Color.BLUE);
        resideMenu.setBackground(R.drawable.menu_contaduria);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.5f);
        // create menu items;
        itemHomeR = new ResideMenuItem(this, R.drawable.img_opciones_menu, "Inicio");
        itemHomeI = new ResideMenuItem(this, R.drawable.img_opciones_menu, "Inicio");
        itemDocumento = new ResideMenuItem(this, R.drawable.file, "Documento");
        itemChat = new ResideMenuItem(this, R.drawable.img_chat_menu, "Chat");
        itemPreguntas = new ResideMenuItem(this, R.drawable.img_categ_menu, "Categorias");
        itemAbout = new ResideMenuItem(this, R.drawable.img_acercade_menu, "Acerca de");
        itemContacto = new ResideMenuItem(this, R.drawable.img_mail_menu, "Contacto");
        itemGestion = new ResideMenuItem(this, R.drawable.super_user, "Gestion");

        itemHomeR.setOnClickListener(this);
        itemHomeI.setOnClickListener(this);
        itemDocumento.setOnClickListener(this);
        itemChat.setOnClickListener(this);
        itemPreguntas.setOnClickListener(this);
        itemAbout.setOnClickListener(this);
        itemContacto.setOnClickListener(this);
        itemGestion.setOnClickListener(this);

        resideMenu.addMenuItem(itemHomeR, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemHomeI, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemPreguntas, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemChat, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemDocumento, ResideMenu.DIRECTION_LEFT);
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
        if (view == itemHomeR) {
            changeFragment(new HomeFragment());
        } else if (view == itemHomeI) {
            changeFragment(new HomeFragment());
        } else if (view == itemChat) {
            startActivity(new Intent(MainActivity.this, Chat.class));
        } else if (view == itemDocumento) {
            startActivity(new Intent(MainActivity.this, Documentos.class));
        } else if (view == itemPreguntas) {
            startActivity(new Intent(MainActivity.this, Categorias.class));
        } else if (view == itemAbout) {
            changeFragment(new AcercaDeFragment());
        } else if (view == itemContacto) {
            startActivity(new Intent(MainActivity.this, Contacto.class));
        } else if (view == itemGestion) {
            verificarIngreso();
        }
        resideMenu.closeMenu();
    }

    private void verificarIngreso() {
        startActivity(new Intent(MainActivity.this, GestionDePublicaciones.class));
/*
        final Dialog login_ventana = new Dialog(MainActivity.this);
        login_ventana.setTitle("Ingresar Contraseña");
        login_ventana.setContentView(R.layout.gestionar_contra);
        final EditText contra = (EditText) login_ventana.findViewById(R.id.et_gestion_contra);
        Button boton = (Button) login_ventana.findViewById(R.id.btn_gestion_contra);
        int width = (int) (MainActivity.this.getResources().getDisplayMetrics().widthPixels * 0.9);
        // set height for dialog
        int height = (int) (MainActivity.this.getResources().getDisplayMetrics().heightPixels * 0.35);
        login_ventana.getWindow().setLayout(width, height);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = contra.getText().toString().trim();
                if (c.equals("cp2017")) {
                    startActivity(new Intent(MainActivity.this, GestionDePublicaciones.class));
                }
                login_ventana.cancel();
            }
        });
        login_ventana.show();*/
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ventana = new AlertDialog.Builder(MainActivity.this);
        ventana.setTitle("Salir");
        ventana.setMessage("Elija una opcion:");
        ventana.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        ventana.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        ventana.show();
    }
}

