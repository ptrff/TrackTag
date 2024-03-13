package ru.ptrff.tracktag.data;

public class SearchFilter {
    private static SearchFilter instance;

    private String filterBy;
    private String sortBy;
    private Boolean withImage;
    private Boolean withoutImage;
    private Boolean byGuests;
    private Boolean byUsers;
    private Boolean withNoLikes;

    public static SearchFilter getInstance(){
        if(instance == null){
            instance = new SearchFilter();
        }
        return instance;
    }

    public static void removeInstance(){
        instance = null;
    }

    public String getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(String filterBy) {
        this.filterBy = filterBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getWithImage() {
        return withImage;
    }

    public void setWithImage(Boolean withImage) {
        this.withImage = withImage;
    }

    public Boolean getWithoutImage() {
        return withoutImage;
    }

    public void setWithoutImage(Boolean withoutImage) {
        this.withoutImage = withoutImage;
    }

    public Boolean getByGuests() {
        return byGuests;
    }

    public void setByGuests(Boolean byGuests) {
        this.byGuests = byGuests;
    }

    public Boolean getByUsers() {
        return byUsers;
    }

    public void setByUsers(Boolean byUsers) {
        this.byUsers = byUsers;
    }

    public Boolean getWithNoLikes() {
        return withNoLikes;
    }

    public void setWithNoLikes(Boolean withNoLikes) {
        this.withNoLikes = withNoLikes;
    }
}
