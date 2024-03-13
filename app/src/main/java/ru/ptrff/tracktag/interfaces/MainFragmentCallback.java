package ru.ptrff.tracktag.interfaces;

import java.util.List;

import ru.ptrff.tracktag.data.OptionActions;
import ru.ptrff.tracktag.models.Tag;

public interface MainFragmentCallback extends MoreFragmentCallback {
    void onTagsLoaded(List<Tag> tags);
    void focusOnTag(Tag tag);
    void setBottomSheetState(int state);
    void performAction(OptionActions action);
}
