package com.matteodri;

import static java.lang.System.exit;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.OptionalInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.matteodri.services.CSVProcessorService;
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
                exit(1);
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

            Stats stats = csvProcessorService.process(csvFileReader, rates, warningThresholdW);

            printStats(stats);
        }

        exit(0);
    }

    private void exitWithUsagePrintout() {
        System.out.println(" Usage: java -jar electricity-cost-simulator-0.1.0.jar "
            + "<csv file> <f1 cost> <f2 cost> <f3 cost>");
        System.out.println("    or: java -jar electricity-cost-simulator-0.1.0.jar "
            + "<csv file> <f1 cost> <f2 cost> <f3 cost> <warning threshold>");
        System.out.println(" Costs are money per kWh. The warning threshold is a value in Watt, "
            + "the amount of time during which consumption from the network exceeded the threshold will be returned");
        exit(1);
    }

    private void printStats(Stats stats) {
        System.out.println("\nRESULTS");
        System.out.println("Dataset starts at " + stats.getStartTime());
        System.out.println("Dataset ends at " + stats.getEndTime());
        System.out.println("Overall cost = " + stats.getOverallCost());
        System.out.println("Cost F1 = " + stats.getF1Cost() + " F2 = " + stats.getF2Cost()
            + " F3 = " + stats.getF3Cost());
        System.out.println("Cost if had a battery F1 = " + stats.getF1CostIfHadBattery() + " F2 = " + stats.getF2CostIfHadBattery()
            + " F3 = " + stats.getF3CostIfHadBattery());
        System.out.println("Peak consumption = " + stats.getPeakConsumptionW()
            + "W on " + stats.getPeakConsumptionTime());
        System.out.println("Minutes over threshold = " + stats.getTimeOverWarningThreshold().toMinutes());
        System.out.println("Days with consumption greater than solar production = "
            + stats.getDaysWithConsumptionGreaterThanSolarProduction());
        System.out.println("Days processed " + stats.getDaysProcessed());
        System.out.println("Lines processed = " + stats.getProcessedLines());
    }
}
