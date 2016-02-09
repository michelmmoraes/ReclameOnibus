package br.edu.ifpa.reclameonibus.auxiliares;

import br.edu.ifpa.reclameonibus.componentes.Parada;

public class RespostaParada {

    private Parada parada;
    private boolean estaNaParada;

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
        this.parada = parada;
    }

    public boolean isEstaNaParada() {
        return estaNaParada;
    }

    public void setEstaNaParada(boolean estaNaParada) {
        this.estaNaParada = estaNaParada;
    }
}
