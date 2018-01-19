package com.informaciones.facultad.contaduriaalacima.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informaciones.facultad.contaduriaalacima.Bloqueados.BloqueadoModel;
import com.informaciones.facultad.contaduriaalacima.ImgenCompleta.ImagenCompleta;
import com.informaciones.facultad.contaduriaalacima.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ronald Lopez  04/09/2017. 04
 */

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;
    SharedPreferences sharedPreferences;
    //private List<String> bloqueados = new ArrayList<>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbBloqueados;


    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(MensajeRecibir m) {
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }


    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.chat_diseno_mensajes, parent, false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(final HolderMensaje holder, final int position) {
        sharedPreferences = c.getSharedPreferences("nombre", MODE_PRIVATE);
        final String id = sharedPreferences.getString("id", "");
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //  Toast.makeText(c.getApplicationContext(), "OK!", Toast.LENGTH_LONG).show();
                final ArrayList<String> listItems = new ArrayList<>();
                listItems.add("Bloquear Usuario: " + holder.getNombre().getText().toString());
                listItems.add("Eliminar Mensaje: " + holder.getMensaje().getText().toString());
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Elija una Opción:")
                        .setCancelable(false)
                        .setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, listItems),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        switch (item) {
                                            case 0: // bloquear usuario
                                                Date date = new Date();
                                                DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                String fechaHora = hourdateFormat.format(date);
                                                String codigo = listMensaje.get(position).getEnviado_recibido();
                                                dbBloqueados = database.getReference("bloqueados").child(codigo);
                                                BloqueadoModel bloqueadoModel = new BloqueadoModel(listMensaje.get(position).getNombre(), fechaHora,
                                                        listMensaje.get(position).getMensaje(), listMensaje.get(position).getFotoPerfil(), codigo);
                                                dbBloqueados.setValue(bloqueadoModel);
                                                /*DatabaseReference reference = database.getReference("chat");
                                                reference.orderByChild("enviado_recibido").equalTo(listMensaje.get(position).getEnviado_recibido()).addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                        dataSnapshot.getRef().removeValue();
                                                    }

                                                    @Override
                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
*/
                                                break;
                                            case 1: // eliminar mensaje
                                                dbBloqueados = database.getReference("chat").child(holder.getHora().getText().toString());
                                                dbBloqueados.removeValue();
                                                listMensaje.remove(position);
                                                break;
                                        }
                                    }
                                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();
                return false;
            }
        });
        if (listMensaje.get(position).getEnviado_recibido().equals(id)) {
            holder.getEnviado().setVisibility(View.VISIBLE);
            holder.getMensajeImage().setBackgroundResource(R.color.colorEnviar);
            holder.getRecibido().setVisibility(View.GONE);
        } else {
            holder.getEnviado().setVisibility(View.GONE);
            holder.getMensajeImage().setBackgroundResource(R.color.colorRecibir);
            holder.getRecibido().setVisibility(View.VISIBLE);
        }
        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        if (listMensaje.get(position).getType_mensaje().equals("2")) {
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensaje());
        } else if (listMensaje.get(position).getType_mensaje().equals("1")) {
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }
        if (listMensaje.get(position).getFotoPerfil().isEmpty()) {
            holder.getFotoMensajePerfil().setImageResource(R.drawable.user);
        } else {
            Glide.with(c).load(listMensaje.get(position).getFotoPerfil()).into(holder.getFotoMensajePerfil());
        }
       /* Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");//a pm o am
        holder.getHora().setText(sdf.format(d));*/
        holder.getHora().setText(listMensaje.get(position).getHora());
        holder.getFotoMensajePerfil().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent visorImg = new Intent(Intent.ACTION_VIEW);
                    Uri uriFotoActual = Uri.parse(listMensaje.get(position).getFotoPerfil());
                    if (uriFotoActual != null) {
                        visorImg.setDataAndType(uriFotoActual, "image/*");
                        c.startActivity(visorImg);
                    }
                } catch (Exception e) {
                    Intent intent = new Intent(c, ImagenCompleta.class);
                    intent.putExtra("url", listMensaje.get(position).getFotoPerfil());
                    c.startActivity(intent);
                }
            }
        });
        holder.getFotoMensaje().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent visorImg = new Intent(Intent.ACTION_VIEW);
                    Uri uriFotoActual = Uri.parse(listMensaje.get(position).getUriFotoImagen());
                    if (uriFotoActual != null) {
                        visorImg.setDataAndType(uriFotoActual, "image/*");
                        c.startActivity(visorImg);
                    }
                } catch (Exception e) {
                    Intent intent = new Intent(c, ImagenCompleta.class);
                    intent.putExtra("url", listMensaje.get(position).getUriFotoImagen());
                    c.startActivity(intent);
                }


            }
        });
    }


    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

}
