package com.matteodri.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public enum Rate {
    F1, F2, F3;

    /**
     * Returns a {@link Rate} based on the timestamp according to Italian energy hourly bands.
     * These are defined as:
     * - F1 (peak times): Monday to Friday from 8.00 till 19.00. Excluding national holidays.
     * - F2 (intermediate times): Monday to Friday from 7.00 till 8.00 and from 19.00 till 23.00;
     *                            Saturday from 7.00 till 23.00. Excluding national holidays.
     * - F3 (off peak): Monday to Saturday from 23.00 till 7.00. All day on Sunday and national holidays.
     *
     * @param timestamp timestamp for which the rate should be determined
     * @return the corresponding rate
     */
    public static Rate of(LocalDateTime timestamp) {
        int year = timestamp.getYear();
        List<LocalDate> nationalHolidays = italianNationalHolidays(year);

        // National holidays are all in F3
        if (nationalHolidays.contains(timestamp.toLocalDate())){
            return F3;
        }

        if (!timestamp.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !timestamp.getDayOfWeek().equals(DayOfWeek.SUNDAY)
            && timestamp.getHour() >= 8 && timestamp.getHour() < 19){
            return F1;

        } else if (timestamp.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                   || (timestamp.getHour() >= 23 || timestamp.getHour() < 7)){
            return F3;

        } else {
            return F2;
        }
    }

    private static List<LocalDate> italianNationalHolidays(int year) {
        LocalDate easter = easterDate(year);
        List<LocalDate> holidays = List.of(
            LocalDate.of(year, 1, 1), // Capodanno
            LocalDate.of(year, 1, 6),  // Epifania
            easter,  // Pasqua
            easter.plusDays(1),  // Lunedì dell'angelo
            LocalDate.of(year, 4, 25),  // Festa della liberazione
            LocalDate.of(year, 5, 1),  // Festa del lavoro
            LocalDate.of(year, 6, 2),  // Festa della repubblica
            LocalDate.of(year, 8, 15),  // Assunzione di Maria
            LocalDate.of(year, 11, 1),  // Tutti i santi
            LocalDate.of(year, 12, 8),  // Immacolata concezione
            LocalDate.of(year, 12, 25),  // Natale
            LocalDate.of(year, 12, 26)  // Santo Stefano
        );
        return holidays;
    }

    private static LocalDate easterDate(int year) {
        int golden = year % 19;
        int century = year / 100;
        int c = year % 100;
        int d = century / 4;
        int e = century % 4;
        int f = (century + 8) / 25;
        int g = (century - f + 1) / 3;
        int h = (19 * golden + century - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (golden + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}
