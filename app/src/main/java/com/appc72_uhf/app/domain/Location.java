package com.appc72_uhf.app.domain;

public class Location {
    private float LocationId;
    private String Name;
    private String Description;
    private boolean IsActive;
    private String Aux1 = null;
    private String Aux2 = null;
    private String Aux3 = null;
    private String UserRoles = null;
    private float CompanyId;
    private String Company = null;
    private String CreatedDate;
    private String Readers = null;

    public Location() {
    }

    public Location(float locationId, String name, String description, boolean isActive, String aux1, String aux2, String aux3, String userRoles, float companyId, String company, String createdDate, String readers) {
        LocationId = locationId;
        Name = name;
        Description = description;
        IsActive = isActive;
        Aux1 = aux1;
        Aux2 = aux2;
        Aux3 = aux3;
        UserRoles = userRoles;
        CompanyId = companyId;
        Company = company;
        CreatedDate = createdDate;
        Readers = readers;
    }

    // Getter Methods

    public float getLocationId() {
        return LocationId;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public String getAux1() {
        return Aux1;
    }

    public String getAux2() {
        return Aux2;
    }

    public String getAux3() {
        return Aux3;
    }

    public String getUserRoles() {
        return UserRoles;
    }

    public float getCompanyId() {
        return CompanyId;
    }

    public String getCompany() {
        return Company;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public String getReaders() {
        return Readers;
    }

    // Setter Methods

    public void setLocationId( float LocationId ) {
        this.LocationId = LocationId;
    }

    public void setName( String Name ) {
        this.Name = Name;
    }

    public void setDescription( String Description ) {
        this.Description = Description;
    }

    public void setIsActive( boolean IsActive ) {
        this.IsActive = IsActive;
    }

    public void setAux1( String Aux1 ) {
        this.Aux1 = Aux1;
    }

    public void setAux2( String Aux2 ) {
        this.Aux2 = Aux2;
    }

    public void setAux3( String Aux3 ) {
        this.Aux3 = Aux3;
    }

    public void setUserRoles( String UserRoles ) {
        this.UserRoles = UserRoles;
    }

    public void setCompanyId( float CompanyId ) {
        this.CompanyId = CompanyId;
    }

    public void setCompany( String Company ) {
        this.Company = Company;
    }

    public void setCreatedDate( String CreatedDate ) {
        this.CreatedDate = CreatedDate;
    }

    public void setReaders( String Readers ) {
        this.Readers = Readers;
    }
}