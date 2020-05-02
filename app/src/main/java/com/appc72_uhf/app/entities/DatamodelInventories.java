package com.appc72_uhf.app.entities;

import androidx.annotation.Nullable;

public class DatamodelInventories {
    int Id;
    private int IsSelect;
    String Name;
    boolean DetailForDevice;
    public DatamodelInventories(int Id, String Name, boolean DetailForDevice, @Nullable int isSelect){
        this.Id = Id;
        this.Name = Name;
        this.DetailForDevice = DetailForDevice;
        this.IsSelect=isSelect;
    }

    public int getisSelect() {
        return IsSelect;
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
