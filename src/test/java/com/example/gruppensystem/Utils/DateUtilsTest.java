package com.example.gruppensystem.Utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void dauerIstRichtigFormatiert() {
        assertTrue(DateUtils.dauerIstRichtigFormatiert("22:12:02:00"));
        assertFalse(DateUtils.dauerIstRichtigFormatiert("22:12:02"));
        assertTrue(DateUtils.dauerIstRichtigFormatiert("00:12:02:00"));
    }

    @Test
    void rechneAustrittsdatum() {
        LocalDateTime ad = DateUtils.rechneAustrittsdatum("00:12:00:00");
        assertEquals((LocalDateTime.now().getHour() + 12) % 24, ad.getHour());
    }

    @Test
    void dauerSindVerschieden() {
        LocalDateTime d1 = LocalDateTime.now();
        LocalDateTime ad = DateUtils.rechneAustrittsdatum("00:00:00:11");
        assertEquals(0, DateUtils.dauerZwischen(null, null));
        assertEquals(11, DateUtils.dauerZwischen(d1, ad));
    }
}