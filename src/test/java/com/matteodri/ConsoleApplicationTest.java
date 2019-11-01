package com.matteodri;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Reader;
import java.time.Duration;
import java.util.OptionalInt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matteodri.services.CSVProcessorService;
import com.matteodri.services.ExitWrapperService;
import com.matteodri.services.Rates;
import com.matteodri.services.Stats;
import com.matteodri.util.Rate;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class ConsoleApplicationTest {

    private static final double DELTA = 0.00001;

    @Mock
    private ExitWrapperService exitWrapperService;

    @Mock
    private CSVProcessorService csvProcessorService;

    @InjectMocks
    private ConsoleApplication target = new ConsoleApplication();

    @Test
    @DisplayName("No arguments passed")
    public void noArgumentsPassed() throws Exception {
        target.run();

        verify(exitWrapperService).exit(1);
    }

    @Test
    @DisplayName("Not enough arguments passed")
    public void notEnoughArgumentsPassed() throws Exception {
        target.run("1", "2", "3");

        verify(exitWrapperService).exit(1);
    }

    @Test
    @DisplayName("Input file does not exist")
    public void fileDoesNotExist() throws Exception {
        when(csvProcessorService.process(anyObject(), anyObject(), any(OptionalInt.class))).thenReturn(buildStats());
        target.run("./thisDoesntExist.csv", "0.1", "0.1", "0.1");

        verify(exitWrapperService).exit(1);
    }

    @Test
    @DisplayName("Correct input")
    public void correctInput() throws Exception {
        when(csvProcessorService.process(anyObject(), anyObject(), any(OptionalInt.class))).thenReturn(buildStats());
        target.run("src/test/resources/test-file.csv", "0.1", "0.2", "0.3", "4000");

        ArgumentCaptor<Rates> ratesCaptor = ArgumentCaptor.forClass(Rates.class);
        ArgumentCaptor<OptionalInt> thresholdCaptor = ArgumentCaptor.forClass(OptionalInt.class);

        verify(csvProcessorService).process(any(Reader.class), ratesCaptor.capture(), thresholdCaptor.capture());

        assertEquals(0.1, ratesCaptor.getValue().costOf(Rate.F1), DELTA);
        assertEquals(0.2, ratesCaptor.getValue().costOf(Rate.F2), DELTA);
        assertEquals(0.3, ratesCaptor.getValue().costOf(Rate.F3), DELTA);

        assertEquals(4000, thresholdCaptor.getValue().getAsInt());
    }

    private Stats buildStats() {
        Stats stats = new Stats();
        stats.setTimeOverWarningThreshold(Duration.ZERO);

        return stats;
    }
}