package com.matteodri.services;

import java.util.Map;

import com.matteodri.util.Rate;

/**
 * Class that stores cost associated to each billing rate. Values are in € per kW.
 *
 * @author Matteo Dri 12 Oct 2019
 */
public class Rates {

    private Map<Rate, Double> costToRateMap;

    public Rates(double f1Rate, double f2Rate, double f3Rate) {
        costToRateMap = Map.of(Rate.F1, f1Rate, Rate.F2, f2Rate, Rate.F3, f3Rate);
    }

    public double costOf(Rate rate) {
        return costToRateMap.get(rate);
    }
}
