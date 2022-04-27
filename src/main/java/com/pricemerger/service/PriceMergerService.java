package com.pricemerger.service;

import com.pricemerger.model.Price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriceMergerService {

    /**
     * Метод возвращает список обновленных цен
     *
     * @param oldPrices список имеющихся цен
     * @param newPrices список новых цен
     * @return список обновленных цен
     */
    public List<Price> updatePrices(ArrayList<Price> oldPrices, ArrayList<Price> newPrices) {
        Map<String, List<Price>> oldSortedPrices = getSortedPricesByMarking(oldPrices);
        Map<String, List<Price>> newSortedPrices = getSortedPricesByMarking(newPrices);
        Map<String, List<Price>> updatedSortedPrices = getUpdatedPrices(oldSortedPrices, newSortedPrices);
        ArrayList<Price> updatedPrices = new ArrayList<>();
        updatedSortedPrices.values().forEach(updatedPrices::addAll);
        return updatedPrices;
    }

    /**
     * Метод возвращает map заполненную на основании списка,
     * где ключ это (код товара + номер цены + номер отдела), а занчение это список цен
     *
     * @param prices список цен
     * @return Map<String, List < Price>>
     */
    public Map<String, List<Price>> getSortedPricesByMarking(List<Price> prices) {
        return prices.stream().collect(Collectors.groupingBy(Price::getMarking));
    }

    /**
     * Метод возвращает map c обновленными ценами
     *
     * @param updatablePrices map с имеющимися ценами
     * @param newPrices       map с новыми ценами
     * @return Map<String, List < Price>> с обновленными ценами
     */
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

    /**
     * Метод возвращает список обновленных цен
     *
     * @param oldPriceList список имеющихся цен
     * @param newPriceList список новых цен
     * @return список обновленных цен
     */
    public List<Price> getMergedPrices(List<Price> oldPriceList, List<Price> newPriceList) {
        List<Price> mergePrices = new ArrayList<>();
        List<Price> removePriseList = new ArrayList<>();
        boolean updateExists = true;
        while (updateExists) {
            if (!removePriseList.isEmpty()) {
                newPriceList.removeAll(removePriseList);
                oldPriceList.removeAll(removePriseList);
                removePriseList.clear();
            }
            updateExists = false;
            for (Price oldPrice : oldPriceList) {
                for (Price newPrice : newPriceList) {
                    Price price = new Price(oldPrice);
                    if (oldPrice.getValue() == newPrice.getValue()
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getEnd())) {
                        price.setEnd(newPrice.getEnd());
                        mergePrices.add(price);
                        removePriseList.add(oldPrice);
                        removePriseList.add(newPrice);
                        updateExists = true;
                    } else if (oldPrice.getValue() == newPrice.getValue()
                            && newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getBegin())) {
                        price.setBegin(newPrice.getBegin());
                        mergePrices.add(price);
                        removePriseList.add(oldPrice);
                        removePriseList.add(newPrice);
                        updateExists = true;
                    } else if (oldPrice.getValue() == newPrice.getValue()
                            && !(newPrice.getEnd().isAfter(oldPrice.getEnd()))
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())) {
                        removePriseList.add(newPrice);
                        updateExists = true;
                    } else if (newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getEnd())) {
                        price.setEnd(newPrice.getBegin());
                        mergePrices.add(price);
                        removePriseList.add(oldPrice);
                        removePriseList.add(oldPrice);
                        updateExists = true;
                    } else if (newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getBegin())) {
                        price.setBegin(newPrice.getEnd());
                        mergePrices.add(price);
                        removePriseList.add(oldPrice);
                        updateExists = true;
                    } else if (newPrice.getEnd().isBefore(oldPrice.getEnd())
                            && newPrice.getEnd().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && newPrice.getBegin().isBefore(oldPrice.getEnd())) {
                        price.setBegin(newPrice.getEnd());
                        mergePrices.add(price);
                        Price duplicatePrice = new Price(oldPrice);
                        duplicatePrice.setEnd(newPrice.getBegin());
                        mergePrices.add(duplicatePrice);
                        removePriseList.add(oldPrice);
                        updateExists = true;
                    } else if (!newPrice.getBegin().isAfter(oldPrice.getBegin())
                            && !newPrice.getEnd().isBefore(oldPrice.getEnd())) {
                        removePriseList.add(oldPrice);
                        updateExists = true;
                    }
                }
            }
        }
        mergePrices.addAll(oldPriceList);
        mergePrices.addAll(newPriceList);
        return mergePrices;
    }
}
