package ru.ptrff.tracktag.models;

import androidx.annotation.Nullable;

public class Option {
    private final int action;
    private final String label;
    private final int icon;

    public Option(int action, String label, int icon) {
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

    public int getAction() {
        return action;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
