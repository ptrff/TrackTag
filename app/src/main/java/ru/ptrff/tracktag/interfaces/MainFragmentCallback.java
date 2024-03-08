package ru.ptrff.tracktag.interfaces;

import java.util.List;

import ru.ptrff.tracktag.models.Tag;

public interface MainFragmentCallback {
    void onTagsLoaded(List<Tag> tags);
    void focusOnTag(Tag tag);
    void performAction(int action);
}
