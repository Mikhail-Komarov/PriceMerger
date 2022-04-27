package com.pricemerger;

import com.pricemerger.model.Price;
import com.pricemerger.service.PriceMergerService;
import com.pricemerger.service.PriceVisualiser;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PriceMergerSecondTest {
    /**
     * Метод возвращает список имеющихся цен
     *
     * @return список имеющихся цен
     */
    private static ArrayList<Price> getOldPrices() {
        ArrayList<Price> oldPrices = new ArrayList<>();
        oldPrices.add(new Price(1, "1", 1, 1,
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 11, 1, 0, 0), 1));
        return oldPrices;
    }

    /**
     * Метод возвращает список новых цен на основании которых будет производиться обновление
     *
     * @return список новых цен
     */
    private static ArrayList<Price> getNewPrices() {
        ArrayList<Price> newPrices = new ArrayList<>();
        newPrices.add(new Price(2, "2", 2, 2,
                LocalDateTime.of(2022, 3, 1, 0, 0),
                LocalDateTime.of(2022, 10, 15, 0, 0), 2));
        return newPrices;
    }

    /**
     * Метод возвращает ожидаемый результат объединения цен
     *
     * @return список обновленных цен
     */
    private static ArrayList<Price> getExpected() {
        ArrayList<Price> expected = new ArrayList<>();
        expected.add(new Price(1, "1", 1, 1,
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 11, 1, 0, 0), 1));
        expected.add(new Price(2, "2", 2, 2,
                LocalDateTime.of(2022, 3, 1, 0, 0),
                LocalDateTime.of(2022, 10, 15, 0, 0), 2));
        return expected;
    }

    /**
     * Тестовый сценарий, согласно которому к списку имеющихся цен добавляется список новых,
     * при этом код товара, номер цены и номер отдела у имеющихся и новых цен отличается
     */
    @Test
    public void test() {
        PriceMergerService priceMergerService = new PriceMergerService();
        ArrayList<Price> oldPrices = getOldPrices();
        ArrayList<Price> newPrices = getNewPrices();
        List<Price> updatedPrices = priceMergerService.updatePrices(oldPrices, newPrices).stream().sorted().collect(Collectors.toList());
        PriceVisualiser priceVisualiser = new PriceVisualiser();
        System.out.println(priceVisualiser.getVisualisation(oldPrices, PriceVisualiser.Step.OLD));
        System.out.println(priceVisualiser.getVisualisation(newPrices, PriceVisualiser.Step.NEW));
        System.out.println(priceVisualiser.getVisualisation(updatedPrices, PriceVisualiser.Step.RESULT));
        List<Price> expectedResult = getExpected().stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(expectedResult, updatedPrices);
    }
}
