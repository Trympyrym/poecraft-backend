package ru.trympyrym.poecraft.storage;

import ru.trympyrym.poecraft.model.Affix;

import java.util.List;

public class DataLoader {
    private List<Affix> data;

    public DataLoader(List<Affix> data) {
        this.data = data;
    }

    public List<Affix> getData() {
        return data;
    }
}
