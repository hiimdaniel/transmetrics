package com.daniel.transmetrics.service.exception;

public class HolidayApiException extends RuntimeException {

    public HolidayApiException(String countryCode, Integer year, String error) {
        super("Date-time API returned error for the request. Country: " + countryCode + ", year: " + year+" Error in response: "+error);
    }
}
