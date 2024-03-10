package ru.ptrff.tracktag.models;

import androidx.annotation.Nullable;

import ru.ptrff.tracktag.data.OptionActions;

public class Option {
    private final OptionActions action;
    private final String label;
    private final int icon;

    public Option(OptionActions action, String label, int icon) {
        this.action = action;
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public int getIcon() {
        return icon;
    }

    public OptionActions getAction() {
        return action;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
