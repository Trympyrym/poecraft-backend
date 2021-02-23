package ru.trympyrym.poecraft.calculator;

import org.springframework.stereotype.Service;
import ru.trympyrym.poecraft.model.Algorithm;
import ru.trympyrym.poecraft.model.conditions.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class MainCalculator {

    private final Algorithmizer algorithmizer;
    private final Pricer pricer;
    private final ExecutorService executorService = Executors.newFixedThreadPool(6);

    public MainCalculator(Algorithmizer algorithmizer, Pricer pricer) {
        this.algorithmizer = algorithmizer;
        this.pricer = pricer;
    }

    public List<Algorithm> calculate(List<Condition> conditions) {
        List<Algorithm> algorithms = algorithmizer.calculate(conditions);
        List<Future<?>> futures = new ArrayList<>();
        SynchronizedMinPrice minPrice= new SynchronizedMinPrice();
        for (Algorithm algorithm: algorithms) {
            Runnable task = ()-> pricer.addPrice(algorithm, minPrice);
            futures.add(executorService.submit(task));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return algorithms;
    }
}
