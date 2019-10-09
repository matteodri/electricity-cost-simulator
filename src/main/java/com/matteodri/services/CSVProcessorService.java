package com.matteodri.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

    private static final double F1_COST_PER_KWH = 0.129;
    private static final double F2_COST_PER_KWH = 0.0299;
    private static final double F3_COST_PER_KWH = 0.0299;
    public static final int WARNING_THRESHOLD_W = 3500;

    public void process(String csvFilePath) {

        BufferedReader br = null;
        String line;
        long lineCount = 0;
        LocalDateTime previousTimestamp = null;
        double overallCostF1 = 0d;
        double overallCostF2 = 0d;
        double overallCostF3 = 0d;
        double peakConsumptionW = 0d;
        Duration timeOverThreshold = Duration.ZERO;
        LocalDateTime peakConsumptionTimestamp = null;
        Map<String, Integer> fieldIndexMap = null;

        try {

            br = new BufferedReader(new FileReader(csvFilePath));
            while ((line = br.readLine()) != null) {

                String[] splitLine = line.split(CSV_SPLIT_BY);

                if (lineCount == 0) {
                    fieldIndexMap = buildFieldIndexMapFromHeaderLine(splitLine);
                } else if (lineCount == 1) {
                    // first line serves just as reference for starting timestamp
                    String strTimestamp = splitLine[fieldIndexMap.get(FIELD_NAME_TIMESTAMP)];
                    previousTimestamp = LocalDateTime.parse(strTimestamp, formatter);
                    System.out.println("Starting at " + previousTimestamp);
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

                    if (currentConsumptionFromNetworkW > WARNING_THRESHOLD_W) {
                        timeOverThreshold = timeOverThreshold.plus(measurementTimeFrame);
                    }

                    Rate currentRate = Rate.of(timestamp);

                    double costPerKWh;
                    switch (currentRate) {
                        case F1:
                            costPerKWh = F1_COST_PER_KWH;
                            break;
                        case F2:
                            costPerKWh = F2_COST_PER_KWH;
                            break;
                        default:
                            costPerKWh = F3_COST_PER_KWH;
                    }

                    double measurementTimeFrameInHours = (double) measurementTimeFrame.toMillis() / 3600_000;

                    double cost =
                        (double) currentConsumptionFromNetworkW / 1000 * costPerKWh * measurementTimeFrameInHours;

                    switch (currentRate) {
                        case F1:
                            overallCostF1 += cost;
                            break;
                        case F2:
                            overallCostF2 += cost;
                            break;
                        default:
                            overallCostF3 += cost;
                    }

                    if (currentConsumptionFromNetworkW > peakConsumptionW) {
                        peakConsumptionW = currentConsumptionFromNetworkW;
                        peakConsumptionTimestamp = timestamp;
                    }

                    previousTimestamp = timestamp;
                }

                lineCount++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finishing at " + previousTimestamp);
        System.out.println("Overall cost = " + (overallCostF1 + overallCostF2 + overallCostF3));
        System.out.println("Cost F1 = " + overallCostF1 + " F2 = " + overallCostF2 + " F3 = " + overallCostF3);
        System.out.println("Peak consumption = " + peakConsumptionW + " on " + peakConsumptionTimestamp);
        System.out.println("Minutes over " + WARNING_THRESHOLD_W + "W threshold = " + timeOverThreshold.toMinutes());
        System.out.println("Lines processed = " + lineCount);
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
