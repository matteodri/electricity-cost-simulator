package com.matteodri.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CSVProcessorService}.
 *
 * @author Matteo Dri 30 Oct 2019
 */
class CSVProcessorServiceTest {

    private static final double F1_RATE = 0.1;
    private static final double F2_RATE = 0.1;
    private static final double F3_RATE = 0.1;
    private static final double DELTA = 0.00001;

    private CSVProcessorService target = new CSVProcessorService();

    @Test
    @DisplayName("Test processing of empty reader")
    public void processEmptyReader() {
        Reader reader = new StringReader("");

        Stats stats = target.process(reader, new Rates(F1_RATE, F2_RATE, F3_RATE), OptionalInt.empty(),
            OptionalDouble.empty(), OptionalInt.empty());

        assertNull(stats.getStartTime());
        assertNull(stats.getEndTime());
        assertEquals(0d, stats.getOverallCost().doubleValue());
        assertEquals(0d, stats.getF1Cost().doubleValue());
        assertEquals(0d, stats.getF2Cost().doubleValue());
        assertEquals(0d, stats.getF3Cost().doubleValue());
        assertEquals(0d, stats.getF1CostIfHadBattery().doubleValue());
        assertEquals(0d, stats.getF2CostIfHadBattery().doubleValue());
        assertEquals(0d, stats.getF3CostIfHadBattery().doubleValue());
        assertEquals(0, stats.getPeakConsumptionW().intValue());
        assertNull(stats.getPeakConsumptionTime());
        assertEquals(0, stats.getPeakProductionW().intValue());
        assertNull(stats.getPeakProductionTime());
        assertEquals(Duration.ZERO, stats.getTimeOverWarningThreshold());
        assertEquals(Duration.ZERO, stats.getTimeDrawingEnergyFromGridIfHadBattery());
        assertEquals(0, stats.getDaysWithConsumptionGreaterThanSolarProduction());
        assertEquals(0, stats.getSolarProductionLostDueToClippingKWh());
        assertEquals(0, stats.getPercentOfTimeWithProductionOverClippingThreshold());
        assertEquals(0, stats.getDaysProcessed());
        assertEquals(0, stats.getProcessedLines());
    }

    @Test
    @DisplayName("Test processing of a file with two data lines in F1 (first data line is not considered in cost)")
    public void processTwoLinesReader() {
        Reader reader = new StringReader(
            "timestamp,curr_property,curr_solar_generating\n"
                + "2019-04-17 12:54:13,0,0\n"
                + "2019-04-17 12:55:13,600,0");

        Stats stats = target.process(reader, new Rates(F1_RATE, F2_RATE, F3_RATE), OptionalInt.empty(),
            OptionalDouble.empty(), OptionalInt.empty());

        assertEquals(LocalDateTime.of(2019, 4, 17, 12, 54, 13), stats.getStartTime());
        assertEquals(LocalDateTime.of(2019, 4, 17, 12, 55, 13), stats.getEndTime());
        assertEquals(0.001d, stats.getOverallCost().doubleValue(), DELTA);
        assertEquals(0.001d, stats.getF1Cost().doubleValue(), DELTA);
        assertEquals(0d, stats.getF2Cost().doubleValue());
        assertEquals(0d, stats.getF3Cost().doubleValue());
        assertEquals(0.001d, stats.getF1CostIfHadBattery().doubleValue(), DELTA);
        assertEquals(0d, stats.getF2CostIfHadBattery().doubleValue());
        assertEquals(0d, stats.getF3CostIfHadBattery().doubleValue());
        assertEquals(600, stats.getPeakConsumptionW().intValue());
        assertEquals(LocalDateTime.of(2019, 4, 17, 12, 55, 13), stats.getPeakConsumptionTime());
        assertEquals(0, stats.getPeakProductionW().intValue());
        assertEquals(Duration.ZERO, stats.getTimeOverWarningThreshold());
        assertEquals(Duration.ofMinutes(1), stats.getTimeDrawingEnergyFromGridIfHadBattery());
        assertEquals(1, stats.getDaysWithConsumptionGreaterThanSolarProduction());
        assertEquals(0, stats.getDaysProcessed());
        assertEquals(3, stats.getProcessedLines());
    }

    @Test
    @DisplayName("Test processing of a file with three data lines (first data line is not considered) with production over clipping threshold")
    public void processProductionWithClipping() {
        Reader reader = new StringReader(
            "timestamp,curr_property,curr_solar_generating\n"
                + "2021-11-13 12:00:00,0,0\n"
                + "2021-11-13 13:00:00,0,5000\n"
                + "2021-11-13 14:00:00,1500,1000");

        Stats stats = target.process(reader, new Rates(F1_RATE, F2_RATE, F3_RATE), OptionalInt.empty(),
            OptionalDouble.empty(), OptionalInt.of(3_000));

        assertEquals(500, stats.getPeakConsumptionW().intValue());
        assertEquals(LocalDateTime.of(2021, 11, 13, 14, 0, 0), stats.getPeakConsumptionTime());
        assertEquals(5000, stats.getPeakProductionW().intValue());
        assertEquals(LocalDateTime.of(2021, 11, 13, 13, 0, 0), stats.getPeakProductionTime());
        assertEquals(2, stats.getSolarProductionLostDueToClippingKWh());
        assertEquals(50, stats.getPercentOfTimeWithProductionOverClippingThreshold());
        assertEquals(0, stats.getDaysProcessed());
        assertEquals(4, stats.getProcessedLines());
    }
}
