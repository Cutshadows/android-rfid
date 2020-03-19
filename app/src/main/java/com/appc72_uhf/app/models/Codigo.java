package com.appc72_uhf.app.models;

public class Codigo {
    private String InventoryId;
    private String TId;
    private String IdRFID;
    private String IdHardware;

    public String getInventoryId() {
        return InventoryId;
    }

    public void setInventoryId(String inventoryId) {
        InventoryId = inventoryId;
    }

    public String getTId() {
        return TId;
    }

    public void setTId(String TId) {
        this.TId = TId;
    }

    public String getIdRFID() {
        return IdRFID;
    }

    public void setIdRFID(String idRFID) {
        IdRFID = idRFID;
    }

    public String getIdHardware() {
        return IdHardware;
    }

    public void setIdHardware(String idHardware) {
        IdHardware = idHardware;
    }
}