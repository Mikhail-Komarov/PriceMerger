package com.pricemerger.service;

import com.pricemerger.model.Price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriceMergerService {
    public List<Price> updatePrices(ArrayList<Price> oldPrices, ArrayList<Price> newPrices) {
        Map<String, List<Price>> oldSortedPrices = getSortedPricesByMarking(oldPrices);
        Map<String, List<Price>> newSortedPrices = getSortedPricesByMarking(newPrices);
        Map<String, List<Price>> updatedSortedPrices = getUpdatedPrices(oldSortedPrices, newSortedPrices);
        ArrayList<Price> updatedPrices = new ArrayList<>();
        updatedSortedPrices.values().forEach(updatedPrices::addAll);
        return updatedPrices;
    }

    public Map<String, List<Price>> getSortedPricesByMarking(List<Price> prices) {
        return prices.stream().collect(Collectors.groupingBy(Price::getMarking));
    }

    public Map<String, List<Price>> getUpdatedPrices(Map<String, List<Price>> updatablePrices,
                                                     Map<String, List<Price>> newPrices) {
        Map<String, List<Price>> updatedPrices = new HashMap<>(updatablePrices);
        for (String marking : newPrices.keySet()) {
            if (updatedPrices.containsKey(marking)) {
                List<Price> oldPriceList = updatedPrices.get(marking);
                List<Price> newPriceList = newPrices.get(marking);
                updatedPrices.put(marking, getMergedPrices(oldPriceList, newPriceList));
            } else {
                updatedPrices.put(marking, newPrices.get(marking));
            }
        }
        return updatedPrices;
    }

    public List<Price> getMergedPrices(List<Price> oldPriceList, List<Price> newPriceList) {
        List<Price> mergePrices = new ArrayList<>();
        List<Price> removePriseList = new ArrayList<>();
        boolean updateIsExist = true;
        while (updateIsExist) {
            if (!removePriseList.isEmpty()) {
                newPriceList.removeAll(removePriseList);
                oldPriceList.removeAll(removePriseList);
                removePriseList.clear();
            }
            updateIsExist = false;
            for (Price oldPrice : oldPriceList) {
                for (Price newPrice : newPriceList) {
                    if (oldPrice.getValue() == newPrice.getValue()) {
                        oldPrice.setEnd(newPrice.getEnd());
                        oldPrice.setBegin(newPrice.getBegin());
                        mergePrices.add(oldPrice);
                        removePriseList.add(newPrice);
                        removePriseList.add(oldPrice);
                        updateIsExist = true;
                    } else if (newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getEnd())) {
                        oldPrice.setEnd(newPrice.getBegin());
                        mergePrices.add(oldPrice);
                        removePriseList.add(oldPrice);
                        updateIsExist = true;
                    } else if (newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getBegin())) {
                        oldPrice.setBegin(newPrice.getEnd());
                        mergePrices.add(oldPrice);
                        removePriseList.add(oldPrice);
                        updateIsExist = true;
                    } else if (newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())) {
                        Price price = new Price(oldPrice);
                        price.setBegin(newPrice.getEnd());
                        mergePrices.add(price);
                        oldPrice.setEnd(newPrice.getBegin());
                        mergePrices.add(oldPrice);
                        removePriseList.add(oldPrice);
                        updateIsExist = true;
                    } else if (!newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && !newPrice.getEnd().isBefore(oldPrice.getEnd())) {
                        removePriseList.add(oldPrice);
                        updateIsExist = true;
                    }
                }
            }
        }
        mergePrices.addAll(oldPriceList);
        mergePrices.addAll(newPriceList);
        return mergePrices;
    }
}
