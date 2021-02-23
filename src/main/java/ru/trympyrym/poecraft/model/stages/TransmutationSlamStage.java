package ru.trympyrym.poecraft.model.stages;

import java.util.List;

public class TransmutationSlamStage extends SlamStage {
    public TransmutationSlamStage(List<StageBreak> onMatch, Integer onFailGotoIndex) {
        super(onMatch, onFailGotoIndex);
    }
}
