package com.appc72_uhf.app.domain;

public class Application {
    private int Id;
    private String Description;
    private String ApplicationUserId;
    private String Name;
    private String ApplicationUser = null;
    private String CreateDate;
    private float LocationId;
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

    public Application() {
    }

    public Application(int id, String name, String description, String applicationUserId, String applicationUser, String createDate, float locationId, Location locationObject, int inventoryStatus, String closeDate, String closeUserId, int companyId, String hardwareId, String isActive, String isAssigned, String takingInventory, String makeLabel, String accessToken, String detailForDevice) {
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
    }

    // Getter Methods
    public String getMakeLabel(){
        return MakeLabel;
    }
    public String getAccessToken(){
        return AccessToken;
    }
    public String getTakingInventory(){
        return TakingInventory;
    }
    public String getIsAssigned(){
        return IsAssigned;
    }
    public int getId() {
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

    public float getLocationId() {
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

    // Setter Methods

    public void setId( int Id ) {
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

    public void setLocationId( float LocationId ) {
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
