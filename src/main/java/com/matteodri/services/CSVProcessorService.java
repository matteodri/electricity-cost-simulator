package com.matteodri.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(CSVProcessorService.class);

    public Stats process(Reader csvFileReader, Rates rates, OptionalInt warningThresholdW) {
        Stats stats = new Stats();
        Battery battery = new Battery.Builder().build();
        BufferedReader br = null;
        String line;
        long lineCount = 0;
        LocalDateTime previousTimestamp = null;
        int peakConsumptionW = 0;
        Duration timeOverThreshold = Duration.ZERO;
        LocalDateTime peakConsumptionTimestamp = null;
        Map<String, Integer> fieldIndexMap = null;

        // accumulator maps
        Map<Rate, Double> overallCostPerRate = new HashMap<>();
        Map<Rate, Double> overallCostPerRateIfHadBattery = new HashMap<>();
        Map<LocalDate, Double> consumptionPerDay = new HashMap<>();
        Map<LocalDate, Double> solarProductionPerDay = new HashMap<>();

        logger.info("Starting CSV processing...");
        long startTime = System.currentTimeMillis();

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
                    double measurementTimeFrameInHours = (double) measurementTimeFrame.toMillis() / 3600_000;

                    String strCurrentConsumptionW = splitLine[fieldIndexMap.get(FIELD_NAME_CURRENT_CONSUMPTION)];
                    int currentConsumptionW = parseIntValue(strCurrentConsumptionW);

                    String strSolarProduction = splitLine[fieldIndexMap.get(FIELD_NAME_SOLAR_PRODUCTION)];
                    int solarProductionW = parseIntValue(strSolarProduction);

                    // a positive energy balance indicates energy is being drawn from the electricity grid,
                    // a negative balance means surplus energy is being pushed to the grid
                    int energyBalanceFromGrid = currentConsumptionW - solarProductionW;
                    boolean isEnergyBeingDrawnFromGrid = energyBalanceFromGrid > 0;

                    boolean canBatteryCoverCurrentConsumption = !isEnergyBeingDrawnFromGrid ||
                        battery.retrievePower(energyBalanceFromGrid * measurementTimeFrameInHours);

                    if (!isEnergyBeingDrawnFromGrid) {
                        battery.storePower(-energyBalanceFromGrid * measurementTimeFrameInHours);
                    }

                    if (warningThresholdW.isPresent() && energyBalanceFromGrid > warningThresholdW
                        .getAsInt()) {
                        timeOverThreshold = timeOverThreshold.plus(measurementTimeFrame);
                    }

                    Rate currentRate = Rate.of(timestamp);

                    double costPerKWh = rates.costOf(currentRate);

                    double cost = isEnergyBeingDrawnFromGrid ?
                                  (double) energyBalanceFromGrid / 1000 * costPerKWh * measurementTimeFrameInHours
                                  : 0;

                    overallCostPerRate.compute(currentRate,
                        (r, overallCost) -> (overallCost == null) ? cost : overallCost + cost);
                    if (!canBatteryCoverCurrentConsumption) {
                        overallCostPerRateIfHadBattery.compute(currentRate,
                            (r, overallCost) -> (overallCost == null) ? cost : overallCost + cost);
                    }
                    consumptionPerDay.compute(timestamp.toLocalDate(),
                        (d, dailyConsumptionSoFar) -> (dailyConsumptionSoFar == null) ?
                            currentConsumptionW : dailyConsumptionSoFar + currentConsumptionW);
                    solarProductionPerDay.compute(timestamp.toLocalDate(),
                        (d, dailySolarProductionSoFar) -> (dailySolarProductionSoFar == null) ?
                            solarProductionW : dailySolarProductionSoFar + solarProductionW);

                    if (energyBalanceFromGrid > peakConsumptionW) {
                        peakConsumptionW = energyBalanceFromGrid;
                        peakConsumptionTimestamp = timestamp;
                    }

                    previousTimestamp = timestamp;
                }
                logger.trace("Read line {}", lineCount);
                lineCount++;
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }

        stats.setEndTime(previousTimestamp);
        stats.setOverallCost(overallCostPerRate.values().stream().reduce(0d, Double::sum));
        stats.setF1Cost(overallCostPerRate.get(Rate.F1));
        stats.setF2Cost(overallCostPerRate.get(Rate.F2));
        stats.setF3Cost(overallCostPerRate.get(Rate.F3));
        stats.setF1CostIfHadBattery(overallCostPerRateIfHadBattery.get(Rate.F1));
        stats.setF2CostIfHadBattery(overallCostPerRateIfHadBattery.get(Rate.F2));
        stats.setF3CostIfHadBattery(overallCostPerRateIfHadBattery.get(Rate.F3));
        stats.setPeakConsumptionW(peakConsumptionW);
        stats.setPeakConsumptionTime(peakConsumptionTimestamp);
        stats.setTimeOverWarningThreshold(timeOverThreshold);
        stats.setDaysWithConsumptionGreaterThanSolarProduction(
            calculateDaysWithConsumptionGreaterThanSolarProduction(consumptionPerDay, solarProductionPerDay));
        stats.setDaysProcessed(Duration.between(stats.getStartTime(), stats.getEndTime()).toDays());
        stats.setProcessedLines(lineCount);

        logger.info("Process took {} ms", (System.currentTimeMillis() - startTime));

        return stats;
    }

    private int parseIntValue(String strValue) {
        int intValue = 0;
        try {
            intValue = Double.valueOf(strValue).intValue();

        } catch (NumberFormatException nfe) {
            logger.warn("Not able to parse {}", strValue);
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

    private long calculateDaysWithConsumptionGreaterThanSolarProduction(Map<LocalDate, Double> consumptionPerDay,
        Map<LocalDate, Double> solarProductionPerDay) {
        return consumptionPerDay.keySet().stream()
            .filter(day -> consumptionPerDay.get(day) > solarProductionPerDay.get(day))
            .count();
    }
}
