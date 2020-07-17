package com.appc72_uhf.app.entities;

import org.json.JSONArray;

public class DatamodelDocumentsMakeLabel {
        private int DocumentId;
        private int DeviceId;
        private String FechaAsignacion;
        private String AsignadoPor;
        private boolean AllowLabeling;
        private int AssociatedDocumentId;
        private String AssociatedDocNumber;
        private int DocumentTypeId;
        private String Description = null;
        private String CreatedDate, DocumentName;
        private int LocationOriginId;
        private String LocationOriginName;
        private String DestinationLocationId = null;
        private String Aux1 = null;
        private String Aux2 = null;
        private String Aux3 = null;
        private String Client = null;
        private int Status;
        private boolean HasVirtualItems;
        private String ReaderId = null;
        JSONArray DocumentDetailsVirtual = new JSONArray();
        public int Selected;
        private int CompanyId;

    public DatamodelDocumentsMakeLabel(){

    }
    public DatamodelDocumentsMakeLabel(int documentId,
                                       int deviceId,
                                       int locationOriginId,
                                       String fechaAsignacion,
                                       String asignadoPor,
                                       boolean allowLabeling,
                                       int associatedDocumentId,
                                       String associatedDocNumber,
                                       int documentTypeId,
                                       String description,
                                       String createdDate,
                                       String locationOriginName,
                                       String destinationLocationId,
                                       String aux1,
                                       String aux2,
                                       String aux3,
                                       String client,
                                       int status,
                                       boolean hasVirtualItems,
                                       String readerId,
                                       JSONArray documentDetailsVirtual,
                                       int selected
    ) {
        DocumentId = documentId;
        DeviceId = deviceId;
        LocationOriginName=locationOriginName;
        FechaAsignacion = fechaAsignacion;
        AsignadoPor = asignadoPor;
        AllowLabeling = allowLabeling;
        AssociatedDocumentId = associatedDocumentId;
        AssociatedDocNumber = associatedDocNumber;
        DocumentTypeId = documentTypeId;
        Description = description;
        CreatedDate = createdDate;
        LocationOriginId = locationOriginId;
        DestinationLocationId = destinationLocationId;
        Aux1 = aux1;
        Aux2 = aux2;
        Aux3 = aux3;
        Client = client;
        Status = status;
        HasVirtualItems = hasVirtualItems;
        ReaderId = readerId;
        DocumentDetailsVirtual = documentDetailsVirtual;
    }

    public DatamodelDocumentsMakeLabel(String documentName) {
        DocumentName=documentName;
    }
    //                                        , JSONObject documentDetailsVirtual

    public int getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(int companyId) {
        CompanyId = companyId;
    }

    public DatamodelDocumentsMakeLabel(int documentId, int deviceId, String locationOriginName, String documentName, int status, boolean hasVirtualItems, JSONArray documentDetailsVirtual, int companyId) {
        DocumentId = documentId;
        DeviceId = deviceId;
        LocationOriginName=locationOriginName;
        DocumentName=documentName;
        HasVirtualItems=hasVirtualItems;
        Status=status;
        DocumentDetailsVirtual=documentDetailsVirtual;
        CompanyId=companyId;
    }

    public int getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(int documentId) {
        DocumentId = documentId;
    }

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public String getFechaAsignacion() {
        return FechaAsignacion;
    }
    public String getLocationOriginName() {
        return LocationOriginName;
    }
    public String getDocumentName() {
        return DocumentName;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        FechaAsignacion = fechaAsignacion;
    }

    public String getAsignadoPor() {
        return AsignadoPor;
    }

    public void setAsignadoPor(String asignadoPor) {
        AsignadoPor = asignadoPor;
    }

    public boolean isAllowLabeling() {
        return AllowLabeling;
    }

    public void setAllowLabeling(boolean allowLabeling) {
        AllowLabeling = allowLabeling;
    }

    public int getAssociatedDocumentId() {
        return AssociatedDocumentId;
    }

    public void setAssociatedDocumentId(int associatedDocumentId) {
        AssociatedDocumentId = associatedDocumentId;
    }

    public String getAssociatedDocNumber() {
        return AssociatedDocNumber;
    }

    public void setAssociatedDocNumber(String associatedDocNumber) {
        AssociatedDocNumber = associatedDocNumber;
    }

    public int getDocumentTypeId() {
        return DocumentTypeId;
    }

    public void setDocumentTypeId(int documentTypeId) {
        DocumentTypeId = documentTypeId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getLocationOriginId() {
        return LocationOriginId;
    }

    public void setLocationOriginId(int locationOriginId) {
        LocationOriginId = locationOriginId;
    }

    public String getDestinationLocationId() {
        return DestinationLocationId;
    }

    public void setDestinationLocationId(String destinationLocationId) {
        DestinationLocationId = destinationLocationId;
    }

    public String getAux1() {
        return Aux1;
    }

    public void setAux1(String aux1) {
        Aux1 = aux1;
    }

    public String getAux2() {
        return Aux2;
    }

    public void setAux2(String aux2) {
        Aux2 = aux2;
    }

    public String getAux3() {
        return Aux3;
    }

    public void setAux3(String aux3) {
        Aux3 = aux3;
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public int getStatus() {
        return Status;
    }
    public int getIsSelected() {
        return Selected;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public boolean isHasVirtualItems() {
        return HasVirtualItems;
    }

    public void setHasVirtualItems(boolean hasVirtualItems) {
        HasVirtualItems = hasVirtualItems;
    }

    public String getReaderId() {
        return ReaderId;
    }

    public void setReaderId(String readerId) {
        ReaderId = readerId;
    }

    public JSONArray getDocumentDetailsVirtual() {
        return DocumentDetailsVirtual;
    }

    public void setDocumentDetailsVirtual(JSONArray documentDetailsVirtual) {
        DocumentDetailsVirtual = documentDetailsVirtual;
    }
}
