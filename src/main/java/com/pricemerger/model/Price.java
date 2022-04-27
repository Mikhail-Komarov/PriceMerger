package com.pricemerger.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Price implements Comparable<Price> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");

    private long id;
    private String productCode;
    private int number;
    private int depart;
    private LocalDateTime begin;
    private LocalDateTime end;
    private long value;
    private String marking;

    public Price(long id, String productCode, int number, int depart, LocalDateTime begin, LocalDateTime end, long value) {
        this.id = id;
        this.productCode = productCode;
        this.number = number;
        this.depart = depart;
        this.begin = begin;
        this.end = end;
        this.value = value;
        this.marking = productCode + number + depart;
    }

    public Price(Price duplicate) {
        this.id = duplicate.id;
        this.productCode = duplicate.productCode;
        this.number = duplicate.number;
        this.depart = duplicate.depart;
        this.begin = duplicate.begin;
        this.end = duplicate.end;
        this.value = duplicate.value;
        this.marking = duplicate.productCode + duplicate.number + duplicate.depart;
    }

    public Price() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDepart() {
        return depart;
    }

    public void setDepart(int depart) {
        this.depart = depart;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getMarking() {
        return marking;
    }

    public void setMarking(String marking) {
        this.marking = marking;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", number=" + number +
                ", depart=" + depart +
                ", begin=" + begin.format(formatter) +
                ", end=" + end.format(formatter) +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Price price = (Price) obj;
        return id == price.id
                && productCode.equals(price.productCode)
                && number == price.number
                && depart == price.depart
                && begin.equals(price.begin)
                && end.equals(price.end)
                && value == price.value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result * ((productCode == null) ? 0 : productCode.hashCode()) + number +
                depart + ((begin == null) ? 0 : begin.hashCode()) + ((end == null) ? 0 : end.hashCode()) * (int) value;
        return result;
    }

    @Override
    public int compareTo(Price o) {
        return Comparator.comparing(Price::getMarking)
                .thenComparing(Price::getBegin)
                .compare(this, o);
    }
}
