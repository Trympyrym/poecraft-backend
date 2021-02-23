package ru.trympyrym.poecraft.calculator;

import org.springframework.stereotype.Service;
import ru.trympyrym.poecraft.model.Affix;
import ru.trympyrym.poecraft.model.AffixType;
import ru.trympyrym.poecraft.model.Algorithm;
import ru.trympyrym.poecraft.model.conditions.AffixMatchCondition;
import ru.trympyrym.poecraft.model.conditions.Condition;
import ru.trympyrym.poecraft.model.conditions.NoAffixCondition;
import ru.trympyrym.poecraft.model.stages.AlterationSpamStage;
import ru.trympyrym.poecraft.model.stages.AugmentationSlamStage;
import ru.trympyrym.poecraft.model.stages.PickBaseStage;
import ru.trympyrym.poecraft.model.stages.StageBreak;
import ru.trympyrym.poecraft.model.stages.TransmutationSlamStage;
import ru.trympyrym.poecraft.storage.DataStorage;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.trympyrym.poecraft.model.AffixType.PREFIX;
import static ru.trympyrym.poecraft.model.AffixType.SUFFIX;

@Service
public class Algorithmizer {

    private final DataStorage dataStorage;

    public Algorithmizer(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public List<Algorithm> calculate(List<Condition> conditions) {

        List<Affix> affixesMustBe = conditions.stream()
                .filter(x -> x instanceof AffixMatchCondition)
                .map(x -> (AffixMatchCondition)x)
                .map(x -> dataStorage.getAffixById(x.affixId))
                .collect(Collectors.toList());

        if (affixesMustBe.isEmpty()) {
            return noCraft();
        }

        Set<AffixType> affixTypesMustNotBe = conditions.stream()
                .filter(x -> x instanceof NoAffixCondition)
                .map(x -> (NoAffixCondition)x)
                .map(x -> x.affixType)
                .collect(Collectors.toSet());

        if (!affixTypesMustNotBe.isEmpty()) {
            return exactlyOneAffixCraft(affixesMustBe.get(0).id);
        }

        if (affixesMustBe.size() == 1) {
            return oneAffixCraft(affixesMustBe.get(0).id);
        }

        return bothAffixCraft(affixesMustBe.get(0).id, affixesMustBe.get(1).id);
    }

    private List<Algorithm> noCraft() {
        return List.of(new Algorithm().addStage(new PickBaseStage(List.of(new StageBreak(Set.of(), null)))));
    }

    private List<Algorithm> exactlyOneAffixCraft(UUID affixId) {

        AffixType affixType = dataStorage.getAffixById(affixId).affixType;

        PickBaseStage pickBaseStage = new PickBaseStage(List.of(new StageBreak(Set.of(), 1)));

        TransmutationSlamStage transmutationSlamStage = new TransmutationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId),
                                new NoAffixCondition(PREFIX.equals(affixType) ? SUFFIX : PREFIX)
                        ),
                        null
                )
        ), 2);

        AlterationSpamStage alterationSpamStage = new AlterationSpamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId),
                                new NoAffixCondition(PREFIX.equals(affixType) ? SUFFIX : PREFIX)
                        ),
                        null
                )
        ));

        return List.of(
                new Algorithm()
                        .addStage(pickBaseStage)
                        .addStage(transmutationSlamStage)
                        .addStage(alterationSpamStage)
        );
    }

    private List<Algorithm> oneAffixCraft(UUID affixId) {

        AffixType affixSlot = dataStorage.getAffixById(affixId).affixType;

        PickBaseStage withAugPickBaseStage = new PickBaseStage(List.of(new StageBreak(Set.of(), 1)));

        TransmutationSlamStage withAugTransmutationSlamStage = new TransmutationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId)
                        ),
                        null
                ),
                new StageBreak(
                        Set.of(
                                new NoAffixCondition(affixSlot)
                                ),
                        3
                )
        ), 2);

        AlterationSpamStage withAugAlterationSpamStage = new AlterationSpamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId)
                        ),
                        null
                ),
                new StageBreak(
                        Set.of(
                                new NoAffixCondition(affixSlot)
                        ),
                        3
                )
        ));

        AugmentationSlamStage withAugAugmentationSlamStage = new AugmentationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId)
                        ),
                        null
                )
        ), 2);

        PickBaseStage noAugPickBaseStage = new PickBaseStage(List.of(new StageBreak(Set.of(), 1)));

        TransmutationSlamStage noAugTransmutationSlamStage = new TransmutationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId)
                        ),
                        null
                )
        ), 2);

        AlterationSpamStage noAugAlterationSpamStage = new AlterationSpamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(affixId)
                        ),
                        null
                )
        ));

        return List.of(
                new Algorithm()
                        .addStage(withAugPickBaseStage)
                        .addStage(withAugTransmutationSlamStage)
                        .addStage(withAugAlterationSpamStage)
                        .addStage(withAugAugmentationSlamStage),
                new Algorithm()
                        .addStage(noAugPickBaseStage)
                        .addStage(noAugTransmutationSlamStage)
                        .addStage(noAugAlterationSpamStage)
        );
    }

    private List<Algorithm> bothAffixCraft(UUID firstAffixId, UUID secondAffixId) {

        AffixType firstAffixType = dataStorage.getAffixById(firstAffixId).affixType;

        UUID prefixId = PREFIX.equals(firstAffixType) ? firstAffixId : secondAffixId;

        UUID suffixId = PREFIX.equals(firstAffixType) ? secondAffixId : firstAffixId;

        PickBaseStage withAugPickBaseStage = new PickBaseStage(List.of(new StageBreak(Set.of(), 1)));

        TransmutationSlamStage withAugTransmutationSlamStage = new TransmutationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new AffixMatchCondition(suffixId)
                        ),
                        null
                ),
                new StageBreak(
                        Set.of(
                                new NoAffixCondition(PREFIX),
                                new AffixMatchCondition(suffixId)
                        ),
                        3
                ),
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new NoAffixCondition(SUFFIX)
                        ),
                        3
                )
        ), 2);

        AlterationSpamStage withAugAlterationSpamStage = new AlterationSpamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new AffixMatchCondition(suffixId)
                        ),
                        null
                ),
                new StageBreak(
                        Set.of(
                                new NoAffixCondition(PREFIX),
                                new AffixMatchCondition(suffixId)
                        ),
                        3
                ),
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new NoAffixCondition(SUFFIX)
                        ),
                        3
                )
        ));

        AugmentationSlamStage withAugAugmentationSlamStage = new AugmentationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new AffixMatchCondition(suffixId)
                        ),
                        null
                )
        ), 2);

        PickBaseStage noAugPickBaseStage = new PickBaseStage(List.of(new StageBreak(Set.of(), 1)));

        TransmutationSlamStage noAugTransmutationSlamStage = new TransmutationSlamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new AffixMatchCondition(suffixId)
                        ),
                        null
                )
        ), 2);

        AlterationSpamStage noAugAlterationSpamStage = new AlterationSpamStage(List.of(
                new StageBreak(
                        Set.of(
                                new AffixMatchCondition(prefixId),
                                new AffixMatchCondition(suffixId)
                        ),
                        null
                )
        ));

        return List.of(
                new Algorithm()
                        .addStage(withAugPickBaseStage)
                        .addStage(withAugTransmutationSlamStage)
                        .addStage(withAugAlterationSpamStage)
                        .addStage(withAugAugmentationSlamStage),
                new Algorithm()
                        .addStage(noAugPickBaseStage)
                        .addStage(noAugTransmutationSlamStage)
                        .addStage(noAugAlterationSpamStage)
        );
    }
}
