package ru.trympyrym.poecraft;

import org.springframework.stereotype.Service;

import java.util.Map;

import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.ALTERATION_PRICE;
import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.AUGMENTATION_PRICE;
import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.SECOND_MOD_PROBABILITY;
import static ru.trympyrym.poecraft.CalculationConfig.ConfigKey.TRANSMUTATION_PRICE;

@Service
public class CalculationConfig {
    public enum ConfigKey {
        SECOND_MOD_PROBABILITY,

        TRANSMUTATION_PRICE,
        ALTERATION_PRICE,
        AUGMENTATION_PRICE,
    }

    private Map<ConfigKey, Double> config = Map.of(
            SECOND_MOD_PROBABILITY, 0.6,
            TRANSMUTATION_PRICE, 1.0/22.0,
            ALTERATION_PRICE, 1.0/3.0,
            AUGMENTATION_PRICE, 1.0/10.0
    );

    public Double get(ConfigKey key) {
        return config.get(key);
    }
}
