package com.appc72_uhf.app.entities;

public class DataModelProductDetails {
    int Id;
    String EPC;
    String Code;
    String Name;
    String Found;
    int ProductMasterId;

    public DataModelProductDetails(int id, String EPC, String code, String name, String found, int productMasterId) {
        Id = id;
        this.EPC = EPC;
        Code = code;
        Name = name;
        Found = found;
        ProductMasterId = productMasterId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFound() {
        return Found;
    }

    public void setFound(String found) {
        Found = found;
    }

    public int getProductMasterId() {
        return ProductMasterId;
    }

    public void setProductMasterId(int productMasterId) {
        ProductMasterId = productMasterId;
    }
}
