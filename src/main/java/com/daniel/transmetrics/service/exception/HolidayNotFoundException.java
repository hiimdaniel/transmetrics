package com.daniel.transmetrics.service.exception;

public class HolidayNotFoundException extends RuntimeException {

    public HolidayNotFoundException(String countryCode, Integer year) {
        super("Date-time API could not return holidays for the requested country " + countryCode + " for year " + year);
    }
}
