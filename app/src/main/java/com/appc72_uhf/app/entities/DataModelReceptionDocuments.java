package com.appc72_uhf.app.entities;

public class DataModelReceptionDocuments {
    String EPC;
    int PortReader;
    int count;
    String Tid;
    String Location;
    public DataModelReceptionDocuments(String EPC, int portReader, int count, String tid, String location) {
        this.EPC = EPC;
        this.PortReader = portReader;
        this.count = count;
        this.Tid = tid;
        this.Location = location;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public int getPortReader() {
        return PortReader;
    }

    public void setPortReader(int portReader) {
        PortReader = portReader;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTid() {
        return Tid;
    }

    public void setTid(String tid) {
        Tid = tid;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }


}
