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
        private String DocumentName; //CreatedDate,
        private int LocationOriginId;
        private String LocationOriginName;
        private int DestinationLocationId = 0;
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
        private int CounterEnabled;

    public DatamodelDocumentsMakeLabel(){

    }
    public DatamodelDocumentsMakeLabel(int documentId,
                                       int deviceId,
                                       String fechaAsignacion,
                                       boolean allowLabeling,
                                       int associatedDocumentId,
                                       String associatedDocNumber,
                                       String locationOriginName,
                                       String client,
                                       int status,
                                       boolean hasVirtualItems,
                                       JSONArray documentDetailsVirtual,
                                       int selected
    ) {
        DocumentId = documentId;
        DeviceId = deviceId;
        LocationOriginName=locationOriginName;
        FechaAsignacion = fechaAsignacion;
        AllowLabeling = allowLabeling;
        AssociatedDocumentId = associatedDocumentId;
        AssociatedDocNumber = associatedDocNumber;
        Client = client;
        Status = status;
        HasVirtualItems = hasVirtualItems;
        DocumentDetailsVirtual = documentDetailsVirtual;
    }



    public DatamodelDocumentsMakeLabel(String documentName, String locationOriginName, int documentId, int counterEnabled) {
        DocumentName=documentName;
        LocationOriginName=locationOriginName;
        DocumentId=documentId;
        CounterEnabled=counterEnabled;

    }
    //                                        , JSONObject documentDetailsVirtual
    public int getCounterEnabled() {
        return CounterEnabled;
    }

    public void setCounterEnabled(int counterEnabled) {
        CounterEnabled = counterEnabled;
    }
    public int getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(int companyId) {
        CompanyId = companyId;
    }

    public DatamodelDocumentsMakeLabel(
            String documentName,
            int documentId,
            int deviceId,
            String fechaAsignacion,
            boolean allowLabeling,
            int associatedDocumentId,
            String associatedDocNumber,
            String locationOriginName,
            int destinationLocationId,
            String client,
            int status,
            boolean hasVirtualItems,
            JSONArray documentDetailsVirtual,
            int companyId
    ) {
        DocumentId = documentId;
        DeviceId = deviceId;
        LocationOriginName=locationOriginName;
        FechaAsignacion=fechaAsignacion;
        DocumentName=documentName;
        HasVirtualItems=hasVirtualItems;
        AssociatedDocNumber=associatedDocNumber;
        AllowLabeling=allowLabeling;
        AssociatedDocumentId=associatedDocumentId;
        DestinationLocationId=destinationLocationId;
        Client=client;
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

   // public String getCreatedDate() {
    //    return CreatedDate;
   // }

    //public void setCreatedDate(String createdDate) {
     //   CreatedDate = createdDate;
    //}

    public int getLocationOriginId() {
        return LocationOriginId;
    }

    public void setLocationOriginId(int locationOriginId) {
        LocationOriginId = locationOriginId;
    }

    public int getDestinationLocationId() {
        return DestinationLocationId;
    }

    public void setDestinationLocationId(int destinationLocationId) {
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
