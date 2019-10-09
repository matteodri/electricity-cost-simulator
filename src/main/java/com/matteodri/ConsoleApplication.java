package com.matteodri;

import static java.lang.System.exit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.matteodri.services.CSVProcessorService;

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

        if (args.length == 0) {
            System.out.println("Path of CSV file required as parameter");
            exit(1);
        } else {
            csvProcessorService.process(args[0]);
        }

        exit(0);
    }
}
