package ru.trympyrym.poecraft.model.conditions;

import java.util.UUID;

public class AffixMatchCondition implements Condition {
    public AffixMatchCondition(UUID affixId) {
        this.affixId = affixId;
    }

    public UUID affixId;
}
