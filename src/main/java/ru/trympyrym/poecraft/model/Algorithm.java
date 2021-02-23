package ru.trympyrym.poecraft.model;

import ru.trympyrym.poecraft.model.stages.Stage;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {
    public List<Stage> stages = new ArrayList<>();
    public Double price;

    public Algorithm addStage(Stage stage) {
        stages.add(stage);
        return this;
    }
}
