package ru.ptrff.tracktag.data;

import java.util.Arrays;
import java.util.List;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.models.Option;

public class Options {
    public static final List<Option> guest = Arrays.asList(
            new Option(OptionActions.AUTH, "Авторизация", R.drawable.ic_person),
            new Option(OptionActions.LIST, "пусто", R.drawable.ic_dashboard),
            new Option(OptionActions.LIST, "пусто", R.drawable.ic_dashboard),
            new Option(OptionActions.LIST, "пусто", R.drawable.ic_dashboard)
    );

    public static final List<Option> authorized = Arrays.asList(
            new Option(OptionActions.LIST, "пусто", R.drawable.ic_dashboard),
            new Option(OptionActions.LIST, "пусто", R.drawable.ic_dashboard),
            new Option(OptionActions.LIST, "пусто", R.drawable.ic_dashboard)
    );
}
