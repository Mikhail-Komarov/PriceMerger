package com.pricemerger;

import com.pricemerger.model.Price;
import com.pricemerger.service.PriceMergerService;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PriceMergerFourthTest {
    private static ArrayList<Price> getOldPrices() {
        ArrayList<Price> oldPrices = new ArrayList<>();
        oldPrices.add(new Price(1, "1", 1, 1,
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 1, 31, 0, 0), 1));
        oldPrices.add(new Price(2, "1", 1, 1,
                LocalDateTime.of(2022, 2, 1, 0, 0),
                LocalDateTime.of(2022, 2, 28, 0, 0), 2));
        oldPrices.add(new Price(3, "1", 1, 1,
                LocalDateTime.of(2022, 3, 1, 0, 0),
                LocalDateTime.of(2022, 3, 31, 0, 0), 3));
        return oldPrices;
    }

    private static ArrayList<Price> getNewPrices() {
        ArrayList<Price> newPrices = new ArrayList<>();
        newPrices.add(new Price(4, "1", 1, 1,
                LocalDateTime.of(2022, 2, 1, 0, 0),
                LocalDateTime.of(2022, 2, 28, 0, 0), 4));
        return newPrices;
    }

    private static ArrayList<Price> getExpected() {
        ArrayList<Price> expected = new ArrayList<>();
        expected.add(new Price(1, "1", 1, 1,
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 1, 31, 0, 0), 1));
        expected.add(new Price(3, "1", 1, 1,
                LocalDateTime.of(2022, 3, 1, 0, 0),
                LocalDateTime.of(2022, 3, 31, 0, 0), 3));
        expected.add(new Price(4, "1", 1, 1,
                LocalDateTime.of(2022, 2, 1, 0, 0),
                LocalDateTime.of(2022, 2, 28, 0, 0), 4));
        return expected;
    }
    @Test
    public void testCaseOne() {
        PriceMergerService priceMergerService = new PriceMergerService();
        List<Price> updatedPrices = priceMergerService.updatePrices(getOldPrices(), getNewPrices());
        List<String> result = updatedPrices.stream().map(Price::toString).sorted().collect(Collectors.toList());
        List<String> expectedResult = getExpected().stream().map(Price::toString).sorted().collect(Collectors.toList());
        Assert.assertEquals(expectedResult, result);
    }
}

