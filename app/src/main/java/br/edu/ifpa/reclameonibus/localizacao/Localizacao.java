package br.edu.ifpa.reclameonibus.localizacao;

import android.location.LocationManager;

import java.util.Date;

public class Localizacao{

    LocationManager locationManager;

    private Double longitude;

    private Double latitude;

    private Date data;

    private double horario;


    //<editor-fold desc="Getters e setters">
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getHorario() {
        return horario;
    }

    public void setHorario(double horario) {
        this.horario = horario;
    }
    //</editor-fold>
}
