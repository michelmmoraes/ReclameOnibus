package br.edu.ifpa.reclameonibus.telas.outro;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.CaixaDialogo;
import br.edu.ifpa.reclameonibus.reclamacao.Problema;

public class TelaOutro extends ActionBarActivity {
    MyCustomAdapter dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_outro);
        //Generate list View from ArrayList
        displayListView();
        //checkButtonClick();
    }

    private void displayListView() {

        //Array list of countries
        ArrayList<Problema> listaProblemas = new ArrayList<Problema>();
        Problema problema = new Problema("ID: 2015122610");
        listaProblemas.add(problema);
        problema = new Problema("ID: 2015122612");
        listaProblemas.add(problema);
        problema = new Problema("ID: 2015122618");
        listaProblemas.add(problema);
        problema = new Problema("ID: 2015122629");
        listaProblemas.add(problema);
        problema = new Problema("ID: 2015122688");
        listaProblemas.add(problema);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.viagens_info, listaProblemas);
        ListView listView = (ListView) findViewById(R.id.listaViagens);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Problema problema = (Problema) parent.getItemAtPosition(position);
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Problema> {

        private ArrayList<Problema> listaProblemas;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Problema> listaProblemas) {
            super(context, textViewResourceId, listaProblemas);
            this.listaProblemas = new ArrayList<Problema>();
            this.listaProblemas.addAll(listaProblemas);
        }

        private class ViewHolder {
            TextView textoDescricaoViagem;
            RatingBar ratingBar;
            Button botaoAvaliarViagem;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.viagens_info, null);

                holder = new ViewHolder();
                holder.textoDescricaoViagem = (TextView) convertView.findViewById(R.id.textoDescricaoViagem);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                holder.botaoAvaliarViagem = (Button) convertView.findViewById(R.id.botaoAvaliarViagem);
                convertView.setTag(holder);

                /*holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Problema problema = (Problema) cb.getTag();
                    }
                });*/
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Problema problema = listaProblemas.get(position);
            holder.textoDescricaoViagem.setText(problema.getNomeProblema());

            return convertView;

        }

    }

    public void avaliarViagem(View v) {
        CaixaDialogo caixaDialogo=new CaixaDialogo();
        caixaDialogo.exibir(this,"Avaliação feita!",
                "A avaliação foi enviada com sucesso!",
                R.drawable.onibus_verde,this);
    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.botaoReclamar);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Problema> listaProblemas = dataAdapter.listaProblemas;
                for (int i = 0; i < listaProblemas.size(); i++) {
                    Problema problema = listaProblemas.get(i);

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_outro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
