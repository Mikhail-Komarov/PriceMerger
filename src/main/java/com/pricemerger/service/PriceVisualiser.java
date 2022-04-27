package com.pricemerger.service;

import com.pricemerger.model.Price;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriceVisualiser {

    private static final int COEFFICIENT = 10;

    public String getVisualisation(List<Price> prices, Step step) {
        StringBuilder result = new StringBuilder();
        result.append("      ***PRICE_MERGE_").append(step).append("***      ").append(System.lineSeparator());
        Map<String, List<Price>> sortedPricesByMarking = prices.stream().collect(Collectors.groupingBy(Price::getMarking));
        sortedPricesByMarking.keySet().forEach(m -> {
            StringBuffer priceString = new StringBuffer(String.join("", Collections.nCopies(36, "_")));
            sortedPricesByMarking.get(m).forEach(p -> priceString.replace(getNumberOfSymbolsBeforeStart(p), getNumberOfSymbolsBeforeEnd(p), String.join("",
                    Collections.nCopies(getNumberOfSymbols(p), String.valueOf(p.getId())))));
            result.append(priceString).append(System.lineSeparator());
        });
        return result.toString();
    }

    private int getNumberOfSymbols(Price price) {
        LocalDateTime begin = price.getBegin();
        LocalDateTime end = price.getEnd();
        int diff = end.getDayOfYear() - begin.getDayOfYear();
        return (int) Math.ceil((double) (diff + 1) / COEFFICIENT);
    }

    private int getNumberOfSymbolsBeforeStart(Price price) {
        LocalDateTime begin = price.getBegin();
        int diff = begin.getDayOfYear();
        return (int) ((double) (diff) / COEFFICIENT);
    }

    private int getNumberOfSymbolsBeforeEnd(Price price) {
        LocalDateTime end = price.getEnd();
        int diff = end.getDayOfYear();
        return (int) ((double) (diff) / COEFFICIENT);
    }

    public enum Step {
        OLD,
        NEW,
        RESULT
    }
}

