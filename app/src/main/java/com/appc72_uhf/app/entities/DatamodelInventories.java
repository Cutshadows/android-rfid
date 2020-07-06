package com.appc72_uhf.app.entities;

public class DatamodelInventories {
    String Id;
    private int IsSelect;
    String Name;
    boolean DetailForDevice;
    int InventoryStatus;
    int CodeCompany;
    boolean IncludeTID;
    int LocationId, TypeInventory;
    int DocumentId;

    public DatamodelInventories(String Id, String Name, boolean DetailForDevice, int inventoryStatus){
        this.Id = Id;
        this.Name = Name;
        this.DetailForDevice = DetailForDevice;
        this.InventoryStatus=inventoryStatus;
    }

    public DatamodelInventories(String Id, String Name, boolean DetailForDevice, int inventoryStatus, boolean includeTID, int isSelect, int typeInventory, int codeCompany){
        this.Id = Id;
        this.Name = Name;
        this.DetailForDevice = DetailForDevice;
        this.InventoryStatus=inventoryStatus;
        this.IsSelect=isSelect;
        this.CodeCompany=codeCompany;
        this.IncludeTID=includeTID;
        this.TypeInventory=typeInventory;
    }

    public int isTypeInventory(){return TypeInventory;};
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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
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
    public boolean getIncludeTID(){
        return IncludeTID;
    }

    public void setDetailForDevice(boolean detailForDevice) {
        DetailForDevice = detailForDevice;
    }
}
