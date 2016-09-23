package com.example.diego.mayday_03;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jorge on 23/09/16.
 */
public class ConversationsFragment extends Fragment {

    private SwipeRefreshLayout srlMySwipeMensajes;
    private DataBaseHelper db;
    private ArrayList<ChatMessage> messageArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_all_chats, container, false);

        loadConversations();
        /*
        DbMensaje tblMsn = new DbMensaje(this.getActivity().getApplicationContext());
        ArrayList<Mensaje> lista = tblMsn.getMensajes();

        if(lista.size()==0)
        {
            tblMsn.insert("Miguel Cruz", "Juan Perez", "Hola  me urge comunicarme contigo, saludos");
            tblMsn.insert("Miguel Cruz", "Claudio Lopez", "Nos vemos mañana, gracias por la invitacion");
            tblMsn.insert("Miguel Cruz", "Mario Campos", "Hola  me urge comunicarme contigo, saludos");
            tblMsn.insert("Miguel Cruz", "Fernando Estrada", "Nos vemos mañana, gracias por la invitacion");
            tblMsn.insert("Miguel Cruz", "Carlos Trejo", "Hola  me urge comunicarme contigo, saludos");
            tblMsn.insert("Miguel Cruz", "Cesar Rios", "Nos vemos mañana, gracias por la invitacion");
            lista = tblMsn.getMensajes();
        }
        */
        srlMySwipeMensajes = (SwipeRefreshLayout)view.findViewById(R.id.srlMySwipeMensajes);
        String[] columnasBD = new String[] {"_id", "imagen", "textoSuperior", "textoInferior"};
        MatrixCursor cursor = new MatrixCursor(columnasBD);
        /*
        for(int i = 0; i< lista.size(); i++)
        {
            cursor.addRow(new Object[] {lista.get(i).getId(), R.drawable.usericon, lista.get(i).getDestino(), lista.get(i).getBody()});
        }
        */
        //Añadimos los datos al Adapter y le indicamos donde dibujar cada dato en la fila del Layout
        String[] desdeEstasColumnas = {"imagen", "textoSuperior", "textoInferior"};
        int[] aEstasViews = {R.id.imageView_imagen, R.id.textView_superior, R.id.textView_inferior};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(view.getContext(), R.layout.item_conversation, cursor, desdeEstasColumnas, aEstasViews, 0);
        ListView listado = (ListView)view.findViewById(R.id.miLista);

        /*listado.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public  void onItemClick(AdapterView<?> parent, View view_aux, int position, long id){
                Intent conver = new Intent(getContext(), com.mcruz.maydayentrega.ConversacionActivity.class);
                startActivity(conver);
            }
        });*/

        srlMySwipeMensajes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){

                actualizarListView();

            }
        });

        listado.setAdapter(adapter);

        System.out.println("Tab 1");
        return view;
    }

    private void loadConversations() {
        db = new DataBaseHelper(getContext());
        this.messageArrayList = db.getConversationsLastestMessage();
        ///
        // db = new DataBaseHelper(this.getContext());

    }

    public void actualizarListView(){
        srlMySwipeMensajes.setRefreshing(false);


    }
}
