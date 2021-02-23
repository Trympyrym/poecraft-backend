package ru.trympyrym.poecraft.model.stages;

import java.util.List;

public abstract class SpamStage extends Stage {
    public SpamStage(List<StageBreak> onMatch) {
        super(onMatch);
    }
}
