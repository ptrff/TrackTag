package ru.ptrff.tracktag.data;

import java.util.Arrays;
import java.util.List;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.models.Option;

public class Options {
    public static final List<Option> guest = Arrays.asList(
            new Option(OptionActions.AUTH, "Авторизация", R.drawable.ic_person),
            new Option(OptionActions.SUBS, "Подписки", R.drawable.ic_subscription),
            new Option(OptionActions.PREF, "Настройки", R.drawable.ic_settings),
            new Option(OptionActions.ABOUT, "О программе", R.drawable.ic_about)
    );

    public static final List<Option> user = Arrays.asList(
            new Option(OptionActions.SUBS, "Подписки", R.drawable.ic_subscription),
            new Option(OptionActions.PREF, "Настройки", R.drawable.ic_settings),
            new Option(OptionActions.ABOUT, "О программе", R.drawable.ic_about)
    );
}
