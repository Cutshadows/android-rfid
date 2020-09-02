package com.appc72_uhf.app.entities;

public class DataModelVirtualDocument {
    private int Id;
    private int AssociatedDocNumber;
    private int Status;
    private String CreatedDate ;
    private String ReadDate;
    private int ProductMasterId;
    private int ProductVirtualId ;
    private int DocumentId ;
    private String ProductMaster ;
    private String Document;
    private int TypeDocumentVirtual;
    private int Cost;
    private String WasMoved;
    private String LabelAssociated;
    private int ProductId;
    private int Product;
    private int CountrMaster;
    private String Def1, Def2;


    public String getDef1() {
        return Def1;
    }

    public void setDef1(String def1) {
        Def1 = def1;
    }

    public String getDef2() {
        return Def2;
    }

    public void setDef2(String def2) {
        Def2 = def2;
    }

    public DataModelVirtualDocument(int id, int documentId, int countMaster, String def1, String def2 ) {
        Id=id;
        DocumentId=documentId;
        CountrMaster=countMaster;
        Def1=def1;
        Def2=def2;
    }
    public DataModelVirtualDocument(int id, int associatedDocNumber, int status, String createdDate, String readDate, int productMasterId, int productVirtualId, int documentId, String productMaster, String document, int typeDocumentVirtual, int cost, String wasMoved, String labelAssociated, int productId, int product) {
        Id = id;
        AssociatedDocNumber = associatedDocNumber;
        Status = status;
        CreatedDate = createdDate;
        ReadDate = readDate;
        ProductMasterId = productMasterId;
        ProductVirtualId = productVirtualId;
        DocumentId = documentId;
        ProductMaster = productMaster;
        Document = document;
        TypeDocumentVirtual = typeDocumentVirtual;
        Cost = cost;
        WasMoved = wasMoved;
        LabelAssociated = labelAssociated;
        ProductId = productId;
        Product = product;
    }

    public int getCountrMaster() {
        return CountrMaster;
    }

    public void setCountrMaster(int countrMaster) {
        CountrMaster = countrMaster;
    }
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getAssociatedDocNumber() {
        return AssociatedDocNumber;
    }

    public void setAssociatedDocNumber(int associatedDocNumber) {
        AssociatedDocNumber = associatedDocNumber;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getReadDate() {
        return ReadDate;
    }

    public void setReadDate(String readDate) {
        ReadDate = readDate;
    }

    public int getProductMasterId() {
        return ProductMasterId;
    }

    public void setProductMasterId(int productMasterId) {
        ProductMasterId = productMasterId;
    }

    public int getProductVirtualId() {
        return ProductVirtualId;
    }

    public void setProductVirtualId(int productVirtualId) {
        ProductVirtualId = productVirtualId;
    }

    public int getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(int documentId) {
        DocumentId = documentId;
    }

    public String getProductMaster() {
        return ProductMaster;
    }

    public void setProductMaster(String productMaster) {
        ProductMaster = productMaster;
    }

    public String getDocument() {
        return Document;
    }

    public void setDocument(String document) {
        Document = document;
    }

    public int getTypeDocumentVirtual() {
        return TypeDocumentVirtual;
    }

    public void setTypeDocumentVirtual(int typeDocumentVirtual) {
        TypeDocumentVirtual = typeDocumentVirtual;
    }

    public int getCost() {
        return Cost;
    }

    public void setCost(int cost) {
        Cost = cost;
    }

    public String getWasMoved() {
        return WasMoved;
    }

    public void setWasMoved(String wasMoved) {
        WasMoved = wasMoved;
    }

    public String getLabelAssociated() {
        return LabelAssociated;
    }

    public void setLabelAssociated(String labelAssociated) {
        LabelAssociated = labelAssociated;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int productId) {
        ProductId = productId;
    }

    public int getProduct() {
        return Product;
    }

    public void setProduct(int product) {
        Product = product;
    }
}
