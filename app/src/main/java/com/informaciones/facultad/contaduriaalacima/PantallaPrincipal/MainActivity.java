package com.informaciones.facultad.contaduriaalacima.PantallaPrincipal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.informaciones.facultad.contaduriaalacima.Categorias.Categorias;
import com.informaciones.facultad.contaduriaalacima.Chat.Chat;
import com.informaciones.facultad.contaduriaalacima.Chat.PasoDeParametros;
import com.informaciones.facultad.contaduriaalacima.Documentos.Documentos;
import com.informaciones.facultad.contaduriaalacima.Email.Contacto;
import com.informaciones.facultad.contaduriaalacima.Fragmentos.AcercaDeFragment;
import com.informaciones.facultad.contaduriaalacima.Fragmentos.HomeFragment;
import com.informaciones.facultad.contaduriaalacima.Informacion.Informacion;
import com.informaciones.facultad.contaduriaalacima.R;
import com.informaciones.facultad.contaduriaalacima.RegistroDeDatos.Registro_Datos;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private ResideMenuItem itemFacebook;
    private ResideMenuItem itemInformacion;
    private ResideMenuItem itemConfiguraciones;
    SharedPreferences sharedPreferences;
    private static final int STORAGE_PERMISSION_CODE = 23;
    FrameLayout frPrincipal;

    ///carrusel de imagenes
    private ImageSwitcher imageSwitcher;
    private Timer timer = null;
    private int[] gallery = {R.drawable.p1, R.drawable.p2, R.drawable.p3,
            R.drawable.p4, R.drawable.p5, R.drawable.p6};
    private int position;
    private static final Integer DURATION = 4000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frPrincipal = (FrameLayout) findViewById(R.id.frPrincipal);
        cosultarRegistroDeDatos();
        if (getIntent().getStringExtra("saludo") != null) { // viene desde registro de datos
            Snackbar.make(frPrincipal, "Bienvenido  " + getIntent().getStringExtra("saludo"), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
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
        iniciarCarrusel();
    }

    private void iniciarCarrusel() {
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                return imageView;
            }
        });
        Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        imageSwitcher.setInAnimation(fadeIn);
        imageSwitcher.setOutAnimation(fadeOut);
        ///rotar las imagenes
        timer = new Timer();
    }

    public void startSlider() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                // avoid exception:
                // "Only the original thread that created a view hierarchy can touch its views"
                runOnUiThread(new Runnable() {
                    public void run() {
                        imageSwitcher.setImageResource(gallery[position]);
                        position++;
                        if (position == gallery.length) {
                            position = 0;
                        }
                    }
                });
            }

        }, 0, DURATION);
    }

    // Stops the slider when the Activity is going into the background
    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            startSlider();
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

    private void cosultarRegistroDeDatos() { // primera vez que si inicia la app, manda a registro de usuario
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
        itemPreguntas = new ResideMenuItem(this, R.drawable.img_categ_menu, "Publicaciones");
        itemAbout = new ResideMenuItem(this, R.drawable.desarroladores, "Desarrolladores");
        itemContacto = new ResideMenuItem(this, R.drawable.img_mail_menu, "Contacto");
        itemGestion = new ResideMenuItem(this, R.drawable.super_user, "Gestion");
        itemFacebook = new ResideMenuItem(this, R.drawable.facebook, "Facebook");
        itemInformacion = new ResideMenuItem(this, R.drawable.img_acercade_menu, "Informacion");
        itemConfiguraciones = new ResideMenuItem(this, R.drawable.configuraciones, "Ajustes");

        itemHomeR.setOnClickListener(this);
        itemHomeI.setOnClickListener(this);
        itemDocumento.setOnClickListener(this);
        itemInformacion.setOnClickListener(this);
        itemChat.setOnClickListener(this);
        itemPreguntas.setOnClickListener(this);
        itemFacebook.setOnClickListener(this);
        itemAbout.setOnClickListener(this);
        itemContacto.setOnClickListener(this);
        // TODO: 22/01/2018 comentar para subir a play store
        itemConfiguraciones.setOnClickListener(this);
        itemGestion.setOnClickListener(this);

        resideMenu.addMenuItem(itemHomeR, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemPreguntas, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemDocumento, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemInformacion, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemChat, ResideMenu.DIRECTION_RIGHT);

        resideMenu.addMenuItem(itemHomeI, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFacebook, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemContacto, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_LEFT);
        // resideMenu.addMenuItem(itemConfiguraciones, ResideMenu.DIRECTION_LEFT);
        // TODO: 19/01/2018 COMENTAR PARA SER ESTUDIANTE
        resideMenu.addMenuItem(itemGestion, ResideMenu.DIRECTION_LEFT);
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
            imageSwitcher.setVisibility(View.VISIBLE);
            changeFragment(new HomeFragment());
        } else if (view == itemHomeI) {
            imageSwitcher.setVisibility(View.VISIBLE);
            changeFragment(new HomeFragment());
        } else if (view == itemChat) {
            elegirChat();
        } else if (view == itemDocumento) {
            startActivity(new Intent(MainActivity.this, Documentos.class));
        } else if (view == itemPreguntas) {
            startActivity(new Intent(MainActivity.this, Categorias.class));
        } else if (view == itemAbout) {
            imageSwitcher.setVisibility(View.GONE);
            changeFragment(new AcercaDeFragment());
        } else if (view == itemContacto) {
            startActivity(new Intent(MainActivity.this, Contacto.class));
        } else if (view == itemInformacion) {
            startActivity(new Intent(MainActivity.this, Informacion.class));
        } else if (view == itemConfiguraciones) {
            startActivity(new Intent(MainActivity.this, Registro_Datos.class));
        } else if (view == itemGestion) {
            startActivity(new Intent(MainActivity.this, GestionDePublicaciones.class));
            //verificarIngreso();
        } else if (view == itemFacebook) {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURL(this);
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        }
        resideMenu.closeMenu();
    }

    private void elegirChat() {
        String TIPO = sharedPreferences.getString("tipoUsuario", "");
        if (TIPO.equals("est")) {
            PasoDeParametros.TIPO_CHAT = "est";
            startActivity(new Intent(MainActivity.this, Chat.class));
        } else {
            final ArrayList<String> listItems = new ArrayList<>();
            listItems.add("Estudiante");
            listItems.add("Administrativo");
            listItems.add("Docente");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Elija una Opción:")
                    .setCancelable(false)
                    .setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listItems),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                       /* //Toast.makeText(Cliente.this, pos + "  " + listaClientes.get(pos).getNombre(), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Cliente.this, listItems.get(item), Toast.LENGTH_LONG).show();*/
                                    switch (item) {
                                        case 0:
                                            PasoDeParametros.TIPO_CHAT = "est";
                                            startActivity(new Intent(MainActivity.this, Chat.class));
                                            break;
                                        case 1:
                                            PasoDeParametros.TIPO_CHAT = "adm";
                                            startActivity(new Intent(MainActivity.this, Chat.class));
                                            break;
                                        case 2:
                                            PasoDeParametros.TIPO_CHAT = "doc";
                                            startActivity(new Intent(MainActivity.this, Chat.class));
                                            break;
                                    }
                                }
                            }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
        }
    }

    public static String FACEBOOK_URL = "https://www.facebook.com/DireccionDeCarreraContaduriaPublica";
    public static String FACEBOOK_PAGE_ID = "1669735299923836";

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //versiones nuevas de facebook
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //versiones antiguas de fb
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }


    private void verificarIngreso() {
        startActivity(new Intent(MainActivity.this, GestionDePublicaciones.class));
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
        ventana.setTitle("Desar Salir de Contaduria a la cima ???");
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

