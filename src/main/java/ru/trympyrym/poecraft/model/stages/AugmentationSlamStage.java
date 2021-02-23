package ru.trympyrym.poecraft.model.stages;

import java.util.List;

public class AugmentationSlamStage extends SlamStage {
    public AugmentationSlamStage(List<StageBreak> onMatch, Integer onFailGotoIndex) {
        super(onMatch, onFailGotoIndex);
    }
}
