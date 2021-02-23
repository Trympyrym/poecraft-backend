package ru.trympyrym.poecraft.storage;

import ru.trympyrym.poecraft.model.Affix;
import ru.trympyrym.poecraft.model.AffixType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class AffixesCalculationView {
    private int totalWeight;
    private int totalPrefixesWeight;
    private int totalSuffixesWeight;
    private List<Affix> prefixes = new ArrayList<>();
    private List<Affix> suffixes = new ArrayList<>();

    public AffixesCalculationView(Collection<Affix> affixes) {
        totalWeight = 0;
        totalPrefixesWeight = 0;
        totalSuffixesWeight = 0;
        for (Affix affix: affixes) {
            totalWeight += affix.weight;
            if (affix.affixType == AffixType.PREFIX) {
                totalPrefixesWeight += affix.weight;
                prefixes.add(affix);
            }
            if (affix.affixType == AffixType.SUFFIX) {
                totalSuffixesWeight += affix.weight;
                suffixes.add(affix);
            }
        }
    }

    public Affix getRandomAffix() {
        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);
        if (randomValue < totalPrefixesWeight) {
            return pick(prefixes, randomValue);
        } else {
            return pick(suffixes, randomValue - totalSuffixesWeight);
        }
    }

    public Affix getRandomPrefix() {
        return pick(prefixes, new Random().nextInt(totalPrefixesWeight));
    }

    public Affix getRandomSuffix() {
        return pick(suffixes, new Random().nextInt(totalSuffixesWeight));
    }

    private Affix pick(List<Affix> affixes, int randomValue) {
        int accuulator = 0;
        int currentIndex = -1;
        do {
            currentIndex += 1;
            accuulator += affixes.get(currentIndex).weight;
        } while (accuulator < randomValue);
        return affixes.get(currentIndex);
    }
}
