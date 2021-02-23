package ru.trympyrym.poecraft.controller;

import java.util.List;

public class StageDto {
    public String name;
    public StageType stageType;
    public List<StageBreakDto> onMatch;

    public enum StageType {
        SLAM, SPAM, PICK_BASE
    }
}
