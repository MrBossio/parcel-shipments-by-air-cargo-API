package com.redex.application.core.Utils;

import java.time.*;

public class DateAndTime {

    public static LocalTime HHMMStringToLocalTime(String hh){
        String[] split = hh.split(":");
        LocalTime localTime = LocalTime.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), 0);
        return localTime;
    }

    public static ZoneOffset convertToZoneOffset(final ZoneId zoneId) {
        return zoneId.getRules().getOffset(Instant.now());
    }

    public static LocalTime getDifferenceLocalTime(LocalDateTime startTime, LocalDateTime endTime){

        LocalTime totalTime = LocalTime.ofSecondOfDay(
                endTime.toEpochSecond(convertToZoneOffset(ZoneId.of("America/Lima"))) -
                        startTime.toEpochSecond(convertToZoneOffset(ZoneId.of("America/Lima")))
        );
        return totalTime;
    }

    public static Duration getDurationLocalTime(LocalDateTime startTime, LocalDateTime endTime){

        Duration totalTime = Duration.between(startTime, endTime);
        return totalTime;
    }


}
