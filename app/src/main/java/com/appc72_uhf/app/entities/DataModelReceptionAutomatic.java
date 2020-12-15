package com.appc72_uhf.app.entities;

public class DataModelReceptionAutomatic {
    int Location;
    String Comentarios;
    String Fecha;
    int Counter;



    public DataModelReceptionAutomatic(int location, String comentarios, String fecha, int counter) {
        Location = location;
        Comentarios = comentarios;
        Fecha = fecha;
        Counter=counter;
    }

    public int getLocation() {
        return Location;
    }

    public void setLocation(int location) {
        Location = location;
    }


    public String getComentarios() {
        return Comentarios;
    }

    public void setComentarios(String comentarios) {
        Comentarios = comentarios;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }
    public int getCounter() {
        return Counter;
    }

    public void setCounter(int counter) {
        Counter = counter;
    }
}
