package com.pricemerger.service;

import com.pricemerger.model.Price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriceMergerService {
    public List<Price> updatePrices(ArrayList<Price> oldPrices, ArrayList<Price> newPrices) {
        ArrayList<Price> updatedPrices = new ArrayList<>();
        Map<String, List<Price>> oldSortedPrices = getSortedPricesByMarking(oldPrices);
        Map<String, List<Price>> newSortedPrices = getSortedPricesByMarking(newPrices);
        Map<String, List<Price>> updatedSortedPrices = getUpdatedPrices(oldSortedPrices, newSortedPrices);
        for (String key : updatedSortedPrices.keySet()) {
            updatedPrices.addAll(updatedSortedPrices.get(key));
        }
        return updatedPrices;
    }

    public Map<String, List<Price>> getSortedPricesByMarking(List<Price> prices) {
        Map<String, List<Price>> sortedPrices = new HashMap<>();
        for (Price price : prices) {
            if (sortedPrices.containsKey(price.getMarking())) {
                sortedPrices.get(price.getMarking()).add(price);
            } else {
                sortedPrices.put(price.getMarking(), Stream.of(price).collect(Collectors.toList()));
            }
        }
        return sortedPrices;
    }

    public Map<String, List<Price>> getUpdatedPrices(Map<String, List<Price>> updatablePrices, Map<String, List<Price>> newPrices) {
        Map<String, List<Price>> updatedPrices = new HashMap<>(updatablePrices);
        for (String prefix : newPrices.keySet()) {
            if (updatedPrices.containsKey(prefix)) {
                List<Price> oldPriceList = updatedPrices.get(prefix);
                List<Price> newPriceList = newPrices.get(prefix);
                updatedPrices.put(prefix, getMergedPrices(oldPriceList, newPriceList));
            } else {
                updatedPrices.put(prefix, newPrices.get(prefix));
            }
        }
        return updatedPrices;
    }

    public List<Price> getMergedPrices(List<Price> oldPriceList, List<Price> newPriceList) {
        List<Price> mergePrices = new ArrayList<>();
        List<Price> newPricesToRemove = new ArrayList<>();
        List<Price> oldPricesToRemove = new ArrayList<>();
        List<Price> pricesToAdding = new ArrayList<>();
        boolean updateIsExist = true;
        while (updateIsExist) {
            if (!newPricesToRemove.isEmpty()) {
                newPriceList.removeAll(newPricesToRemove);
                newPricesToRemove.clear();
            }
            if (!pricesToAdding.isEmpty()) {
                oldPriceList.addAll(pricesToAdding);
                pricesToAdding.clear();
            }
            if (!oldPricesToRemove.isEmpty()) {
                oldPriceList.removeAll(oldPricesToRemove);
                oldPricesToRemove.clear();
            }
            updateIsExist = false;
            for (Price oldPrice : oldPriceList) {
                for (Price newPrice : newPriceList) {
                    if (oldPrice.getValue() == newPrice.getValue()
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getEnd())) {
                        oldPrice.setEnd(newPrice.getEnd());
                        newPricesToRemove.add(newPrice);
                        updateIsExist = true;
                    } else if (oldPrice.getValue() == newPrice.getValue()
                            && newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getBegin())) {
                        oldPrice.setBegin(newPrice.getBegin());
                        newPricesToRemove.add(newPrice);
                        updateIsExist = true;
                    } else if (oldPrice.getValue() == newPrice.getValue()
                            && !(newPrice.getEnd().isAfter(oldPrice.getEnd()))
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())) {
                        newPricesToRemove.add(newPrice);
                        updateIsExist = true;
                    } else if (oldPrice.getValue() != newPrice.getValue()
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getEnd())) {
                        oldPrice.setEnd(newPrice.getBegin());
                        updateIsExist = true;
                    } else if (oldPrice.getValue() != newPrice.getValue()
                            && newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getBegin())) {
                        oldPrice.setBegin(newPrice.getEnd());
                        updateIsExist = true;
                    } else if (oldPrice.getValue() != newPrice.getValue()
                            && newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())) {
                        Price price = getPriceDuplicate(oldPrice);
                        price.setBegin(newPrice.getEnd());
                        pricesToAdding.add(price);
                        oldPrice.setEnd(newPrice.getBegin());
                        updateIsExist = true;
                    } else if (oldPrice.getValue() != newPrice.getValue()
                            && !newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && !newPrice.getEnd().isBefore(oldPrice.getEnd())) {
                        oldPricesToRemove.add(oldPrice);
                        updateIsExist = true;
                    }
                }
            }
        }
        mergePrices.addAll(oldPriceList);
        mergePrices.addAll(newPriceList);
        return mergePrices;
    }

    private Price getPriceDuplicate(Price price) {
        Price duplicate = new Price();
        duplicate.setBegin(price.getBegin());
        duplicate.setEnd(price.getEnd());
        duplicate.setDepart(price.getDepart());
        duplicate.setNumber(price.getNumber());
        duplicate.setProductCode(price.getProductCode());
        duplicate.setValue(price.getValue());
        duplicate.setId(price.getId());
        duplicate.setMarking(price.getMarking());
        return duplicate;
    }
}
