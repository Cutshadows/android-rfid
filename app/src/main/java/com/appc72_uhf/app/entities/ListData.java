package com.appc72_uhf.app.entities;

public class ListData {

    private String id;
    private String name;
    private String status;
    private String RFID, TID;

    public ListData() {
    }

    public ListData(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    /*public ListData(String rfid, String tid) {
        this.RFID=rfid;
        this.TID = tid;

    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
