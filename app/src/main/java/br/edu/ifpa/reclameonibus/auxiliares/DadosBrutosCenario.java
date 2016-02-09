package br.edu.ifpa.reclameonibus.auxiliares;

import org.json.JSONArray;

public class DadosBrutosCenario {
    JSONArray arrayTodasParadas, arrayTodasLinhas, posicoesOnibus;

    public JSONArray getPosicoesOnibus() {
        return posicoesOnibus;
    }

    public void setPosicoesOnibus(JSONArray posicoesOnibus) {
        this.posicoesOnibus = posicoesOnibus;
    }

    public JSONArray getArrayTodasParadas() {
        return arrayTodasParadas;
    }

    public void setArrayTodasParadas(JSONArray arrayTodasParadas) {
        this.arrayTodasParadas = arrayTodasParadas;
    }

    public JSONArray getArrayTodasLinhas() {
        return arrayTodasLinhas;
    }

    public void setArrayTodasLinhas(JSONArray arrayTodasLinhas) {
        this.arrayTodasLinhas = arrayTodasLinhas;
    }
}
