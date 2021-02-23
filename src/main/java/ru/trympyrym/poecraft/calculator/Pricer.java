package ru.trympyrym.poecraft.calculator;

import org.springframework.stereotype.Service;
import ru.trympyrym.poecraft.CalculationConfig;
import ru.trympyrym.poecraft.model.Affix;
import ru.trympyrym.poecraft.model.Algorithm;
import ru.trympyrym.poecraft.model.Item;
import ru.trympyrym.poecraft.model.conditions.AffixMatchCondition;
import ru.trympyrym.poecraft.model.conditions.Condition;
import ru.trympyrym.poecraft.model.conditions.NoAffixCondition;
import ru.trympyrym.poecraft.model.stages.AlterationSpamStage;
import ru.trympyrym.poecraft.model.stages.AugmentationSlamStage;
import ru.trympyrym.poecraft.model.stages.SlamStage;
import ru.trympyrym.poecraft.model.stages.SpamStage;
import ru.trympyrym.poecraft.model.stages.Stage;
import ru.trympyrym.poecraft.model.stages.StageBreak;
import ru.trympyrym.poecraft.model.stages.TransmutationSlamStage;
import ru.trympyrym.poecraft.storage.AffixesCalculationView;
import ru.trympyrym.poecraft.storage.DataStorage;

import java.util.List;
import java.util.Random;

import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.ALTERATION_PRICE;
import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.AUGMENTATION_PRICE;
import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.SECOND_MOD_PROBABILITY;
import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.TRANSMUTATION_PRICE;
import static ru.trympyrym.poecraft.model.AffixType.PREFIX;
import static ru.trympyrym.poecraft.model.AffixType.SUFFIX;

@Service
public class Pricer {

    private static final int RUN_COUNT = 100;

    private final DataStorage dataStorage;
    private final CalculationConfig config;

    public Pricer(DataStorage dataStorage, CalculationConfig config) {
        this.dataStorage = dataStorage;
        this.config = config;
    }

    public void addPrice(Algorithm algorithm, SynchronizedMinPrice synchronizedMinPrice) {
        double price = 0.0;
        for (int i = 0; i < RUN_COUNT; i++) {
            price = price + getPriceOneRun(algorithm);
            Double averagePriceLimit = synchronizedMinPrice.getValue();
            if (averagePriceLimit == null) {
                continue;
            }
            if (price / RUN_COUNT > 3 * averagePriceLimit) {
                algorithm.price = Double.POSITIVE_INFINITY;
                return;
            }
        }
        algorithm.price = price / RUN_COUNT;
        synchronizedMinPrice.registerNewValue(algorithm.price);
    }

    public Double getPriceOneRun(Algorithm algorithm) {
        Integer currentStageIndex = 0;
        Item item = new Item();
        Double totalPrice = 0.0;
        AffixesCalculationView affixesCalculationView = dataStorage.getAffixesCalculationView();
        while (currentStageIndex != null) {
            Stage currentStage = algorithm.stages.get(currentStageIndex);

            // price adding
            if (currentStage instanceof TransmutationSlamStage) {
                item = rollBlueItem(affixesCalculationView);
                totalPrice += config.get(TRANSMUTATION_PRICE);
            } else if (currentStage instanceof AlterationSpamStage) {
                item = rollBlueItem(affixesCalculationView);
                totalPrice += config.get(ALTERATION_PRICE);
            } else if (currentStage instanceof AugmentationSlamStage) {
                addAffix(item, affixesCalculationView);
                totalPrice += config.get(AUGMENTATION_PRICE);
            }

            // next stage calculation
            StageBreak match = checkMatch(item, currentStage.onMatch);
            if (match == null && currentStage instanceof SpamStage) {
                continue;
            }
            if (match == null && currentStage instanceof SlamStage) {
                SlamStage slamStage = (SlamStage) currentStage;
                currentStageIndex = slamStage.getOnFailGotoIndex();
                continue;
            }
            currentStageIndex = currentStage.onMatch.get(0).nextStageIndex;
        }
        return totalPrice;
    }

    private StageBreak checkMatch(Item item, List<StageBreak> onMatch) {
        for (StageBreak stageBreak : onMatch) {
            boolean match = true;
            for (Condition condition : stageBreak.conditions) {
                match = match && match(item, condition);
            }
            if (match) {
                return stageBreak;
            }
        }
        return null;
    }

    private boolean match(Item item, Condition condition) {
        if (condition instanceof NoAffixCondition) {
            NoAffixCondition noAffixCondition = (NoAffixCondition) condition;
            if (noAffixCondition.affixType == PREFIX && item.prefix != null) {
                return false;
            }
            if (noAffixCondition.affixType == SUFFIX && item.suffix != null) {
                return false;
            }
        }
        if (condition instanceof AffixMatchCondition) {
            AffixMatchCondition affixMatchCondition = (AffixMatchCondition) condition;
            if ((item.prefix == null || affixMatchCondition.affixId != item.prefix.id) &&
                    (item.suffix == null || affixMatchCondition.affixId != item.suffix.id)) {
                return false;
            }
        }
        return true;
    }

    private void addAffix(Item item, AffixesCalculationView affixesCalculationView) {
        if (item.prefix != null) {
            item.suffix = affixesCalculationView.getRandomSuffix();
        } else if (item.suffix != null) {
            item.prefix = affixesCalculationView.getRandomPrefix();
        }
    }

    private Item rollBlueItem(AffixesCalculationView affixesCalculationView) {
        Item item = new Item();
        Affix affix = affixesCalculationView.getRandomAffix();
        boolean rollSecondAffix = new Random().nextDouble() < config.get(SECOND_MOD_PROBABILITY);
        if (affix.affixType == PREFIX) {
            item.prefix = affix;
            if (rollSecondAffix) {
                item.suffix = affixesCalculationView.getRandomSuffix();
            }
        } else {
            item.suffix = affix;
            if (rollSecondAffix) {
                item.prefix = affixesCalculationView.getRandomPrefix();
            }
        }
        return item;
    }
}
