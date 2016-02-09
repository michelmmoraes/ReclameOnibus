package br.edu.ifpa.reclameonibus.telas.parada;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.CaixaDialogo;
import br.edu.ifpa.reclameonibus.reclamacao.Problema;

public class TelaCondicoesParada extends ActionBarActivity {
    TextView tvInfoParada, tvDetalhes;
    MyCustomAdapter dataAdapter = null;
    Button botaoReclamar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_condicoes_parada);
        botaoReclamar = (Button) findViewById(R.id.botaoReclamar);
        botaoReclamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reclamar(v);
            }
        });
        tvInfoParada = (TextView) findViewById(R.id.textoInfoParada);
        tvDetalhes = (TextView) findViewById(R.id.textoDetalhes);
        Intent intent = getIntent();
        tvInfoParada.setText("Parada: " + intent.getStringExtra("codigoparada") + " (" +
                intent.getStringExtra("nomeparada") + ")");
        tvDetalhes.setText("Data: " + DateFormat.getDateInstance()
                .format(new Date()) + " - Horário: "
                + intent.getStringExtra("infotempoatual"));
        //Generate list View from ArrayList
        displayListView();
        //checkButtonClick();
    }

    private void displayListView() {

        //Array list of countries
        ArrayList<Problema> listaProblemas = new ArrayList<Problema>();
        Problema problema = new Problema("Lixo");
        listaProblemas.add(problema);
        problema = new Problema("Falta de iluminação pública");
        listaProblemas.add(problema);
        problema = new Problema("Frequentes assaltos");
        listaProblemas.add(problema);
        problema = new Problema("Falta de abrigo");
        listaProblemas.add(problema);
        problema = new Problema("Vegetação ao redor");
        listaProblemas.add(problema);
        problema = new Problema("Buracos na rua");
        listaProblemas.add(problema);
        problema = new Problema("Sem condições de acessibilidade");
        listaProblemas.add(problema);


        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.problemas_info, listaProblemas);
        ListView listView = (ListView) findViewById(R.id.listaCondicoesParada);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
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
            TextView textView;
            CheckBox checkBox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.problemas_info, null);

                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.code);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Problema problema = (Problema) cb.getTag();
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Problema problema = listaProblemas.get(position);
            holder.checkBox.setText(problema.getNomeProblema());
            holder.textView.setText("");

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.botaoReclamar);
        myButton.setOnClickListener(new OnClickListener() {

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

    public void reclamar(View v) {
        CaixaDialogo caixaDialogo = new CaixaDialogo();
        caixaDialogo.exibir(this, "Parada em más condições!",
                "Reclamação enviada com sucesso!",
                R.drawable.condicoes_parada, this);
    }
}