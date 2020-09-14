package com.appc72_uhf.app.domain;

public class Application {
    private String Id;
    private String Description;
    private String ApplicationUserId;
    private String Name;
    private String ApplicationUser = null;
    private String CreateDate;
    private int LocationId;
    Location LocationObject;
    private int InventoryStatus;
    private String CloseDate;
    private String CloseUserId;
    private int CompanyId;
    private String HardwareId;
    private String IsActive;
    private String IsAssigned;
    private String TakingInventory;
    private String MakeLabel;
    private String AccessToken;
    private String DetailForDevice;
    private boolean Validate;
    private int IsSelect;
    private String AssignedResponse;
    private boolean IncludeTID;
    private int DocumentId;
    private String CompanyCodeRFID;
    public Application() {
    }


    public String getCompanyCodeRFID() {
        return CompanyCodeRFID;
    }

    public void setCompanyCodeRFID(String companyCodeRFID) {
        CompanyCodeRFID = companyCodeRFID;
    }

    public Application(
            String id,
            String name,
            String description,
            String applicationUserId,
            String applicationUser,
            String createDate,
            int locationId,
            Location locationObject,
            int inventoryStatus,
            String closeDate,
            String closeUserId,
            int companyId,
            String hardwareId,
            String companyCodeRFID,
            String isActive,
            String isAssigned,
            String takingInventory,
            String makeLabel,
            String accessToken,
            String detailForDevice,
            boolean validate,
            boolean includeTID,
            int isSelect,
            String assignedResponse,
            int documentId
    ) {
        Id = id;
        Name = name;
        Description = description;
        ApplicationUserId = applicationUserId;
        ApplicationUser = applicationUser;
        CreateDate = createDate;
        LocationId = locationId;
        LocationObject = locationObject;
        InventoryStatus = inventoryStatus;
        CloseDate = closeDate;
        CloseUserId = closeUserId;
        CompanyId=companyId;
        HardwareId=hardwareId;
        IsActive=isActive;
        IsAssigned=isAssigned;
        TakingInventory=takingInventory;
        MakeLabel=makeLabel;
        AccessToken= accessToken;
        DetailForDevice=detailForDevice;
        Validate=validate;
        IsSelect=isSelect;
        AssignedResponse=assignedResponse;
        IncludeTID=includeTID;
        DocumentId=documentId;
        CompanyCodeRFID=companyCodeRFID;


    }
    public int getIsSelect() {
        return IsSelect;
    }
    public int getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(int documentId) {
        DocumentId = documentId;
    }
    public void setSelect(int select) {
        IsSelect = select;
    }
    // Getter Methods
    public String getMakeLabel(){
        return MakeLabel;
    }
    public String getAssignedResponse(){
        return AssignedResponse;
    }
    public String getAccessToken(){
        return AccessToken;
    }
    public boolean getIncludeTID(){return IncludeTID;};
    public String getTakingInventory(){
        return TakingInventory;
    }
    public String getIsAssigned(){
        return IsAssigned;
    }
    public String getId() {
        return Id;
    }
    public String getHardwareId() {
        return HardwareId;
    }
    public String getIsActive(){
        return IsActive;
    }
    public Integer getCompanyId(){return CompanyId;}

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }
    public String getDetailForDevice() {
        return DetailForDevice;
    }

    public String getApplicationUserId() {
        return ApplicationUserId;
    }

    public String getApplicationUser() {
        return ApplicationUser;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public int getLocationId() {
        return LocationId;
    }

    public Location getLocation() {
        return LocationObject;
    }

    public Integer getInventoryStatus() {
        return InventoryStatus;
    }

    public String getCloseDate() {
        return CloseDate;
    }

    public String getCloseUserId() {
        return CloseUserId;
    }
    public boolean getValidate() {
        return Validate;
    }

    // Setter Methods

    public void setId( String Id ) {
        this.Id = Id;
    }

    public void setName( String Name ) {
        this.Name = Name;
    }

    public void setDescription( String Description ) {
        this.Description = Description;
    }

    public void setApplicationUserId( String ApplicationUserId ) {
        this.ApplicationUserId = ApplicationUserId;
    }

    public void setApplicationUser( String ApplicationUser ) {
        this.ApplicationUser = ApplicationUser;
    }

    public void setCreateDate( String CreateDate ) {
        this.CreateDate = CreateDate;
    }

    public void setLocationId( int LocationId ) {
        this.LocationId = LocationId;
    }

    public void setLocation( Location LocationObject ) {
        this.LocationObject = LocationObject;
    }

    public void setInventoryStatus( int InventoryStatus ) {
        this.InventoryStatus = InventoryStatus;
    }

    public void setCloseDate( String CloseDate ) {
        this.CloseDate = CloseDate;
    }

    public void setCloseUserId( String CloseUserId ) {
        this.CloseUserId = CloseUserId;
    }

}
