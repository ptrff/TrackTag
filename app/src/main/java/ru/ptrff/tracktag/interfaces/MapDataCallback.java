package ru.ptrff.tracktag.interfaces;

import java.util.List;

import ru.ptrff.tracktag.models.Tag;

public interface MapDataCallback {
    void onTagsLoaded(List<Tag> tags);
    void focusOnTag(Tag tag);
}
