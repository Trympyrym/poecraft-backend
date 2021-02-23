package ru.trympyrym.poecraft.storage;

import org.springframework.stereotype.Service;
import ru.trympyrym.poecraft.model.Affix;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DataStorage {

    private final Map<UUID, Affix> affixByIdIndex;
    private AffixesCalculationView affixesCalculationView;

    public DataStorage(DataLoader dataLoader) {
        List<Affix> data = dataLoader.getData();
        this.affixByIdIndex = data.stream().collect(Collectors.toMap(x->x.id, x->x));
    }

    public Affix getAffixById(UUID affixId) {
        return affixByIdIndex.get(affixId);
    }

    public synchronized AffixesCalculationView getAffixesCalculationView() {
        if (affixesCalculationView == null) {
            affixesCalculationView = new AffixesCalculationView(affixByIdIndex.values());
        }
        return affixesCalculationView;
    }

    public Set<Affix> getAllAffixes() {
        return new HashSet<>(affixByIdIndex.values());
    }
}
