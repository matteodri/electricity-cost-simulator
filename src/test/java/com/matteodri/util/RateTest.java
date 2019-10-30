package com.matteodri.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class RateTest {

    @Test
    public void ofModay() {
        testWeekday(2019, 10, 21);
    }

    @Test
    public void ofTuesday() {
        testWeekday(2019, 10, 22);
    }

    @Test
    public void ofWednesday() {
        testWeekday(2019, 10, 23);
    }

    @Test
    public void ofThursday() {
        testWeekday(2019, 10, 24);
    }

    @Test
    public void ofFriday() {
        testWeekday(2019, 10, 25);
    }

    @Test
    public void ofSaturday() {
        testSaturday(2019, 10, 26);
    }

    @Test
    public void ofSunday() {
        testSunday(2019, 10, 27);
    }

    @Test
    public void ofAllSaints() {
        testNationalHoliday(2019, 11, 1);
    }

    @Test
    public void ofChristmas() {
        testNationalHoliday(2019, 12, 25);
    }

    @Test
    public void ofNewYearsDay() {
        testNationalHoliday(2020, 1, 1);
    }

    @Test
    public void ofEaster2019() {
        testNationalHoliday(2019, 4, 21);
    }

    @Test
    public void ofEaster2020() {
        testNationalHoliday(2020, 4, 12);
    }

    @Test
    public void ofEasterMonday2025() {
        testNationalHoliday(2025, 4, 21);
    }

    private void testWeekday(int year, int month, int day) {
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 0, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 6, 59)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 7, 00)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 7, 59)));
        assertEquals(Rate.F1, Rate.of(LocalDateTime.of(year, month, day, 8, 0)));
        assertEquals(Rate.F1, Rate.of(LocalDateTime.of(year, month, day, 18, 59)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 19, 0)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 22, 59)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 23, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 23, 59)));
    }

    private void testSaturday(int year, int month, int day) {
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 0, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 6, 59)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 7, 00)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 7, 59)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 8, 0)));
        assertEquals(Rate.F2, Rate.of(LocalDateTime.of(year, month, day, 22, 59)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 23, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 23, 59)));
    }

    private void testSunday(int year, int month, int day) {
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 0, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 6, 59)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 7, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 7, 59)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 8, 1)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 19, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 23, 0)));
        assertEquals(Rate.F3, Rate.of(LocalDateTime.of(year, month, day, 23, 59)));
    }

    private void testNationalHoliday(int year, int month, int day) {
        testSunday(year, month, day);
    }


}