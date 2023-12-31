package com.example.gruppensystem.Utils;

import java.sql.SQLOutput;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
    public static boolean dauerIstRichtigFormatiert(String dauer) {
        String pattern = "^[0-9]+:[0-9]+:[0-9]+:[0-9]+$";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(dauer);
        return matcher.matches();
    }
    public static LocalDateTime rechneAustrittsdatum(String dauer){
        LocalDateTime jetzt = LocalDateTime.now();
        String[] parts = dauer.split(":");
        int days = Integer.parseInt(parts[0]);
        int hours = Integer.parseInt(parts[1]);
        int minutes = Integer.parseInt(parts[2]);
        int seconds = Integer.parseInt(parts[3]);

        return jetzt.plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    public static long dauerZwischen(LocalDateTime dauer1, LocalDateTime dauer2){
        if(dauer1 == null && dauer2 == null){
            return 0;
        }

        if(dauer1 == null){
            return Duration.between(LocalDateTime.now(), dauer2).getSeconds();
        }

        Duration duration = Duration.between(dauer1, dauer2);
        return duration.getSeconds();
    }

    public static String formatierteDauerBisAustritt(LocalDateTime dauer){
        long sec = DateUtils.dauerZwischen(LocalDateTime.now(), dauer);
        Duration d = Duration.ofSeconds(sec);

        long tage = d.toDays();
        long stunden = d.minusDays(tage).toHours();
        long minuten = d.minusDays(tage).minusHours(stunden).toMinutes();
        long sekunden = d.minusDays(tage).minusHours(stunden).minusMinutes(minuten).getSeconds();
        return String.format("%d Tage %02d Stunden %02d Minuten %02d Sekunden", tage, stunden, minuten, sekunden);
    }
}
