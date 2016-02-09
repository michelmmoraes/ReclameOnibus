package br.edu.ifpa.reclameonibus.auxiliares;

import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.componentes.Parada;

public class RespostaCenario {

    public static final int PARADA = 111, ONIBUS = 222, OUTRO = 333;
    public static int CENARIO_DETECTADO = OUTRO;
    private int idCenario;
    private Parada parada;
    private Onibus onibus;

    public int getIdCenario() {
        return idCenario;
    }

    public void setIdCenario(int idCenario) {
        this.idCenario = idCenario;
    }

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
        this.parada = parada;
    }

    public Onibus getOnibus() {
        return onibus;
    }

    public void setOnibus(Onibus onibus) {
        this.onibus = onibus;
    }
}
