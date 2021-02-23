package ru.trympyrym.poecraft.model.stages;

import java.util.List;

public abstract class Stage {
    public List<StageBreak> onMatch;

    public Stage(List<StageBreak> onMatch) {
        this.onMatch = onMatch;
    }
}
