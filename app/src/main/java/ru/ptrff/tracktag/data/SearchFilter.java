package ru.ptrff.tracktag.data;

public class SearchFilter {
    private static SearchFilter instance;

    private Integer filterBy;
    private Integer sortBy;
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

    public Integer getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(Integer filterBy) {
        this.filterBy = filterBy;
    }

    public Integer getSortBy() {
        return sortBy;
    }

    public void setSortBy(Integer sortBy) {
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
