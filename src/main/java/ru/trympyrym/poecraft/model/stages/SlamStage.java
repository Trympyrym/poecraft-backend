package ru.trympyrym.poecraft.model.stages;

import java.util.List;

public abstract class SlamStage extends Stage {

    protected int onFailGotoIndex;

    public SlamStage(List<StageBreak> onMatch, Integer onFailGotoIndex) {
        super(onMatch);
        this.onFailGotoIndex = onFailGotoIndex;
    }

    public Integer getOnFailGotoIndex() {
        return onFailGotoIndex;
    }
}
