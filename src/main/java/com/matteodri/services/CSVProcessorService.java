package com.matteodri.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import org.springframework.stereotype.Service;

import com.matteodri.util.Rate;

/**
 * Service responsible for processing the CSV file with consumption data.
 *
 * @author Matteo Dri 06 Oct 2019
 */
@Service
public class CSVProcessorService {

    private static final String CSV_SPLIT_BY = ",";
    private static final String FIELD_NAME_TIMESTAMP = "timestamp";
    private static final String FIELD_NAME_CURRENT_CONSUMPTION = "curr_property";
    private static final String FIELD_NAME_SOLAR_PRODUCTION = "curr_solar_generating";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Stats process(Reader csvFileReader, Rates rates, OptionalInt warningThresholdW) {
        Stats stats = new Stats();
        BufferedReader br = null;
        String line;
        long lineCount = 0;
        LocalDateTime previousTimestamp = null;
        Map<Rate,Double> overallCostPerRate = new HashMap<>();
        int peakConsumptionW = 0;
        Duration timeOverThreshold = Duration.ZERO;
        LocalDateTime peakConsumptionTimestamp = null;
        Map<String, Integer> fieldIndexMap = null;

        try {

            br = new BufferedReader(csvFileReader);
            while ((line = br.readLine()) != null) {

                String[] splitLine = line.split(CSV_SPLIT_BY);

                if (lineCount == 0) {
                    fieldIndexMap = buildFieldIndexMapFromHeaderLine(splitLine);
                } else if (lineCount == 1) {
                    // first line serves just as reference for starting timestamp
                    String strTimestamp = splitLine[fieldIndexMap.get(FIELD_NAME_TIMESTAMP)];
                    previousTimestamp = LocalDateTime.parse(strTimestamp, formatter);
                    stats.setStartTime(previousTimestamp);
                } else {
                    String strTimestamp = splitLine[fieldIndexMap.get(FIELD_NAME_TIMESTAMP)];
                    LocalDateTime timestamp = LocalDateTime.parse(strTimestamp, formatter);
                    Duration measurementTimeFrame = Duration.between(previousTimestamp, timestamp);

                    String strCurrentConsumptionW = splitLine[fieldIndexMap.get(FIELD_NAME_CURRENT_CONSUMPTION)];
                    int currentConsumptionW = parseIntValue(strCurrentConsumptionW);

                    String strSolarproduction = splitLine[fieldIndexMap.get(FIELD_NAME_SOLAR_PRODUCTION)];
                    int solarproductionW = parseIntValue(strSolarproduction);

                    int currentConsumptionFromNetworkW = currentConsumptionW - solarproductionW > 0 ?
                        currentConsumptionW - solarproductionW : 0;

                    if (warningThresholdW.isPresent() && currentConsumptionFromNetworkW > warningThresholdW.getAsInt()) {
                        timeOverThreshold = timeOverThreshold.plus(measurementTimeFrame);
                    }

                    Rate currentRate = Rate.of(timestamp);

                    double costPerKWh = rates.costOf(currentRate);

                    double measurementTimeFrameInHours = (double) measurementTimeFrame.toMillis() / 3600_000;

                    double cost =
                        (double) currentConsumptionFromNetworkW / 1000 * costPerKWh * measurementTimeFrameInHours;

                    overallCostPerRate.compute(currentRate,
                                               (r, overallCost) -> (overallCost == null) ? cost : overallCost + cost);

                    if (currentConsumptionFromNetworkW > peakConsumptionW) {
                        peakConsumptionW = currentConsumptionFromNetworkW;
                        peakConsumptionTimestamp = timestamp;
                    }

                    previousTimestamp = timestamp;
                }

                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stats.setEndTime(previousTimestamp);
        stats.setOverallCost(overallCostPerRate.values().stream().reduce(0d, Double::sum));
        stats.setF1Cost(overallCostPerRate.get(Rate.F1));
        stats.setF2Cost(overallCostPerRate.get(Rate.F2));
        stats.setF3Cost(overallCostPerRate.get(Rate.F3));
        stats.setPeakConsumptionW(peakConsumptionW);
        stats.setPeakConsumptionTime(peakConsumptionTimestamp);
        stats.setTimeOverWarningThreshold(timeOverThreshold);
        stats.setProcessedLines(lineCount);

        return stats;
    }

    private int parseIntValue(String strValue) {
        int intValue = 0;
        try {
            intValue = Double.valueOf(strValue).intValue();

        } catch (NumberFormatException nfe) {
            System.out.println("Not able to parse " + strValue);
        }
        return intValue;
    }

    private Map<String, Integer> buildFieldIndexMapFromHeaderLine(String[] splitHeaderLine) {
        Map<String, Integer> fieldIndexMap = new HashMap<>();
        for (int index = 0; index < splitHeaderLine.length; index++) {
            fieldIndexMap.put(splitHeaderLine[index], index);
        }
        return fieldIndexMap;
    }
}
