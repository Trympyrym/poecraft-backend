package ru.trympyrym.poecraft.model.stages;

import ru.trympyrym.poecraft.model.conditions.Condition;

import java.util.Set;

public class StageBreak {
    public Set<Condition> conditions;
    public Integer nextStageIndex;

    public StageBreak(Set<Condition> conditions, Integer nextStageIndex) {
        this.conditions = conditions;
        this.nextStageIndex = nextStageIndex;
    }
}
