package ru.trympyrym.poecraft.model.conditions;

import ru.trympyrym.poecraft.model.AffixType;

public class NoAffixCondition implements Condition {
    public NoAffixCondition(AffixType affixType) {
        this.affixType = affixType;
    }

    public AffixType affixType;
}
