package com.matteodri;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.matteodri.services.CSVProcessorService;
import com.matteodri.services.ExitWrapperService;
import com.matteodri.services.Rates;
import com.matteodri.services.Stats;

/**
 * Base Spring Boot application class.
 *
 * @author Matteo Dri 03 Oct 2019
 */
@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {

    @Autowired
    private CSVProcessorService csvProcessorService;

    @Autowired
    private ExitWrapperService exitWrapperService;

    public static void main(String[] args) throws Exception {

        SpringApplication app = new SpringApplication(ConsoleApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 4) {
            System.out.println("Expecting more input parameters.");
            exitWithUsagePrintout();
        } else {
            FileReader csvFileReader = null;
            try {
                csvFileReader = new FileReader(args[0]);

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                exitWrapperService.exit(1);
            }

            Rates rates = null;
            try {
                double f1CostPerkWh = Double.parseDouble(args[1]);
                double f2CostPerkWh = Double.parseDouble(args[2]);
                double f3CostPerkWh = Double.parseDouble(args[3]);
                rates = new Rates(f1CostPerkWh, f2CostPerkWh, f3CostPerkWh);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid cost argument.");
                exitWithUsagePrintout();
            }

            OptionalInt warningThresholdW = OptionalInt.empty();
            if (args.length > 4) {
                try {
                    warningThresholdW = OptionalInt.of(Integer.parseInt(args[4]));
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid warning threshold argument.");
                    exitWithUsagePrintout();
                }
            }

            OptionalDouble solarMultiplier = OptionalDouble.empty();
            if (args.length > 5) {
                try {
                    solarMultiplier = OptionalDouble.of(Double.parseDouble(args[5]));
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid solar multiplier argument.");
                    exitWithUsagePrintout();
                }
            }

            Stats stats = csvProcessorService.process(csvFileReader, rates, warningThresholdW, solarMultiplier);

            printStats(stats);
        }
    }

    private void exitWithUsagePrintout() {
        System.out.println(" Usage: java -jar electricity-cost-simulator-<ver>.jar "
            + "<csv file> <f1 cost> <f2 cost> <f3 cost>");
        System.out.println("    or: java -jar electricity-cost-simulator-<ver>.jar "
            + "<csv file> <f1 cost> <f2 cost> <f3 cost> <warning threshold>");
        System.out.println("    or: java -jar electricity-cost-simulator-<ver>.jar "
            + "<csv file> <f1 cost> <f2 cost> <f3 cost> <solar multiplier>");
        exitWrapperService.exit(1);
    }

    private void printStats(Stats stats) {
        System.out.println("\nRESULTS");
        System.out.println("Dataset starts at " + stats.getStartTime());
        System.out.println("Dataset ends at " + stats.getEndTime());
        System.out.println("Overall cost = " + stats.getOverallCost());
        System.out.println("Cost F1 = " + stats.getF1Cost() + " F2 = " + stats.getF2Cost()
            + " F3 = " + stats.getF3Cost());
        System.out.println(
            "Cost if had a battery F1 = " + stats.getF1CostIfHadBattery() + " F2 = " + stats.getF2CostIfHadBattery()
                + " F3 = " + stats.getF3CostIfHadBattery());
        System.out.println("Peak consumption = " + stats.getPeakConsumptionW()
            + "W on " + stats.getPeakConsumptionTime());
        System.out.println("Time over warning threshold = " + formatDuration(stats.getTimeOverWarningThreshold()));
        System.out.println("Time drawing energy from grid if had a battery = "
            + formatDuration(stats.getTimeDrawingEnergyFromGridIfHadBattery()));
        System.out.println("Days with consumption greater than solar production = "
            + stats.getDaysWithConsumptionGreaterThanSolarProduction());
        System.out.println("Days processed " + stats.getDaysProcessed());
        System.out.println("Lines processed = " + stats.getProcessedLines());
    }

    private String formatDuration(Duration duration) {
        if (duration == null) {
            return "";
        }
        return String.format("%sd %sh %sm %ss",
            duration.toDaysPart(),
            duration.toHoursPart(),
            duration.toMinutesPart(),
            duration.toSecondsPart());
    }

}
