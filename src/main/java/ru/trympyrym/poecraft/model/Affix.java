package ru.trympyrym.poecraft.model;

import java.util.UUID;

public class Affix {
    public UUID id;
    public AffixType affixType;
    public int weight;
    public String text;

    public Affix(UUID id, AffixType affixType, int weight, String text) {
        this.id = id;
        this.affixType = affixType;
        this.weight = weight;
        this.text = text;
    }
}
