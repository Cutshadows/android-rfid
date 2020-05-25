package com.appc72_uhf.app.entities;

public class DatamodelInventories {
    int Id;
    private int IsSelect;
    String Name;
    boolean DetailForDevice;
    int InventoryStatus;
    int CodeCompany;

    public DatamodelInventories(int Id, String Name, boolean DetailForDevice, int inventoryStatus){
        this.Id = Id;
        this.Name = Name;
        this.DetailForDevice = DetailForDevice;
        this.InventoryStatus=inventoryStatus;
    }
    public DatamodelInventories(int Id, String Name, boolean DetailForDevice, int inventoryStatus, int isSelect, int codeCompany){
        this.Id = Id;
        this.Name = Name;
        this.DetailForDevice = DetailForDevice;
        this.InventoryStatus=inventoryStatus;
        this.IsSelect=isSelect;
        this.CodeCompany=codeCompany;
    }

    public int getisSelect() {
        return IsSelect;
    }
    public int getCodeCompany() {
        return CodeCompany;
    }
    public int getInventoryStatus() {
        return InventoryStatus;
    }

    public void setSelect(int select) {
        IsSelect = select;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean getDetailForDevice() {
        return DetailForDevice;
    }

    public void setDetailForDevice(boolean detailForDevice) {
        DetailForDevice = detailForDevice;
    }
}
