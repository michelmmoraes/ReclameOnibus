package br.edu.ifpa.reclameonibus.auxiliares;

import br.edu.ifpa.reclameonibus.componentes.Onibus;

public class RespostaOnibus {

    private Onibus onibus;
    private boolean estaNoOnibus;

    public Onibus getOnibus() {
        return onibus;
    }

    public void setOnibus(Onibus onibus) {
        this.onibus = onibus;
    }

    public boolean isEstaNoOnibus() {
        return estaNoOnibus;
    }

    public void setEstaNoOnibus(boolean estaNoOnibus) {
        this.estaNoOnibus = estaNoOnibus;
    }
}
