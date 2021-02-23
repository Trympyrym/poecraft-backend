package ru.trympyrym.poecraft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.trympyrym.poecraft.calculator.MainCalculator;
import ru.trympyrym.poecraft.model.Affix;
import ru.trympyrym.poecraft.model.AffixType;
import ru.trympyrym.poecraft.model.Algorithm;
import ru.trympyrym.poecraft.model.conditions.AffixMatchCondition;
import ru.trympyrym.poecraft.model.conditions.Condition;
import ru.trympyrym.poecraft.model.conditions.NoAffixCondition;
import ru.trympyrym.poecraft.model.stages.PickBaseStage;
import ru.trympyrym.poecraft.model.stages.SlamStage;
import ru.trympyrym.poecraft.model.stages.SpamStage;
import ru.trympyrym.poecraft.model.stages.Stage;
import ru.trympyrym.poecraft.storage.DataStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class FrontController {

    private final DataStorage dataStorage;
    private final MainCalculator mainCalculator;

    public FrontController(DataStorage dataStorage, MainCalculator mainCalculator) {
        this.dataStorage = dataStorage;
        this.mainCalculator = mainCalculator;
    }

    @GetMapping("/affixes")
    public Set<AffixDto> getAffixes() {
        return dataStorage.getAllAffixes().stream().map(x -> {
            AffixDto affixDto = new AffixDto();
            affixDto.affixType = x.affixType;
            affixDto.id = x.id;
            affixDto.translation = x.text;
            return affixDto;
        }).collect(Collectors.toSet());
    }

    @PostMapping("algorithm")
    public AlgorihtmsDto algorithm(@RequestBody RequestDto requestDto) {
        List<Condition> conditions = new ArrayList<>();
        if ("EMPTY".equals(requestDto.prefixId)) {
            conditions.add(new NoAffixCondition(AffixType.PREFIX));
        } else if (!"ANY".equals(requestDto.prefixId)) {
            conditions.add(new AffixMatchCondition(UUID.fromString(requestDto.prefixId)));
        }

        if ("EMPTY".equals(requestDto.suffixId)) {
            conditions.add(new NoAffixCondition(AffixType.SUFFIX));
        } else if (!"ANY".equals(requestDto.suffixId)) {
            conditions.add(new AffixMatchCondition(UUID.fromString(requestDto.suffixId)));
        }
        return convert(mainCalculator.calculate(conditions));
    }

    private AlgorihtmsDto convert(List<Algorithm> algorithms) {
        AlgorihtmsDto algorihtmsDto = new AlgorihtmsDto();
        algorihtmsDto.usedAffixes = new HashMap<>();
        algorihtmsDto.algorithms = new ArrayList<>();
        algorithms.forEach(algorithm -> {
            AlgorithmDto algorithmDto = new AlgorithmDto();
            algorithmDto.price = algorithm.price;
            algorithmDto.stages = new ArrayList<>();
            algorithm.stages.forEach(stage -> {
                StageDto stageDto = convertStage(stage);
                stage.onMatch.forEach(stageBreak -> {
                    StageBreakDto stageBreakDto = new StageBreakDto();
                    stageBreakDto.nextStageIndex = stageBreak.nextStageIndex;
                    stageBreakDto.conditions = new ArrayList<>();
                    stageBreak.conditions.forEach(condition -> {
                        stageBreakDto.conditions.add(convertCondition(condition, algorihtmsDto.usedAffixes));
                    });
                    stageDto.onMatch.add(stageBreakDto);
                });
                algorithmDto.stages.add(stageDto);
            });
            algorihtmsDto.algorithms.add(algorithmDto);
        });
        algorihtmsDto.algorithms.sort(Comparator.comparingDouble(x->x.price));
        return algorihtmsDto;
    }

    private ConditionDto convertCondition(Condition condition, Map<UUID, AffixDto> usedAffixes) {
        if (condition instanceof NoAffixCondition) {
            ConditionDto result = new ConditionDto();
            result.name = ((NoAffixCondition) condition).affixType == AffixType.PREFIX ?
                    "NoPrefixCondition" : "NoSuffixCondition";
            return result;
        }
        if (condition instanceof AffixMatchCondition) {

            UUID affixId = ((AffixMatchCondition) condition).affixId;
            addUsedAffix(affixId, usedAffixes);

            AffixMatchConditionDto result = new AffixMatchConditionDto();
            result.name = "AffixMatchCondition";
            result.affixId = affixId;
            return result;
        }
        throw new IllegalStateException();
    }

    private void addUsedAffix(UUID affixId, Map<UUID, AffixDto> usedAffixes) {
        Affix affix = dataStorage.getAffixById(affixId);
        AffixDto affixDto = new AffixDto();
        affixDto.id = affixId;
        affixDto.affixType = affix.affixType;
        affixDto.translation = affix.text;
        usedAffixes.put(affixId, affixDto);
    }

    private StageDto convertStage(Stage stage) {
        if (stage instanceof PickBaseStage) {
            StageDto stageDto = new StageDto();
            stageDto.name = stage.getClass().getSimpleName();
            stageDto.stageType = StageDto.StageType.PICK_BASE;
            stageDto.onMatch = new ArrayList<>();
            return stageDto;
        }
        if (stage instanceof SpamStage) {
            StageDto stageDto = new StageDto();
            stageDto.name = stage.getClass().getSimpleName();
            stageDto.stageType = StageDto.StageType.SPAM;
            stageDto.onMatch = new ArrayList<>();
            return stageDto;
        }
        if (stage instanceof SlamStage) {
            SlamStageDto stageDto = new SlamStageDto();
            stageDto.onFailGoto = ((SlamStage) stage).getOnFailGotoIndex();
            stageDto.name = stage.getClass().getSimpleName();
            stageDto.stageType = StageDto.StageType.SLAM;
            stageDto.onMatch = new ArrayList<>();
            return stageDto;
        }
        throw new IllegalStateException();
    }
}
