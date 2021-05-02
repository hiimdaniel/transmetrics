package com.daniel.transmetrics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Configuration
public class DateTimeClientConfiguration {

    private static final String ACCESS_KEY_PARAM_NAME = "accesskey";
    private static final String CALENDAR_FORM_PARAM_NAME = "calform";
    private static final String VERBOSE_TIME_PARAM_NAME = "verbosetime";
    private static final String TZ_PARAM_NAME = "tz";
    private static final String HOLIDAY_TYPES_PARAM_NAME = "types";
    private static final String LANGUAGE_PARAM_NAME = "lang";
    private static final String OUTPUT_FORMAT_PARAM_NAME = "out";
    private static final String VERSION_PARAM_NAME = "version";
    private static final String SIGNATURE_PARAM_NAME = "signature";
    private static final String EXPIRES_PARAM_NAME = "expires";
    private static final String HOLIDAYS_ENDPOINT = "holidays";

    @Value("${transmetrics.date-time-api.host}")
    private String dateTimeApiHost;

    @Value("${transmetrics.date-time-api.accesskey}")
    private String accessKey;

    @Value("${transmetrics.date-time-api.secret_key}")
    private String secretKey;

    @Autowired
    private ObjectMapper mapper;

    @Bean
    public DateTimeAPiClient dateTimeAPiClient() {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new OptionalDecoder(new JacksonDecoder(mapper)))
                .logger(new Slf4jLogger(DateTimeAPiClient.class))
                .contract(new Contract.Default())
                .requestInterceptor(new DateTimeInterceptor())
                .decode404()
                .target(DateTimeAPiClient.class, dateTimeApiHost);
    }

    @RequiredArgsConstructor
    public class DateTimeInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate template) {
            String expires = getExpires();
            String signature = calculateSignature(expires);
            template.query(ACCESS_KEY_PARAM_NAME, accessKey);
            template.query(CALENDAR_FORM_PARAM_NAME, "simple");
            template.query(VERBOSE_TIME_PARAM_NAME, "1");
            template.query(TZ_PARAM_NAME, "1");
            template.query(HOLIDAY_TYPES_PARAM_NAME, "all");
            template.query(LANGUAGE_PARAM_NAME, "en");
            template.query(OUTPUT_FORMAT_PARAM_NAME, "js");
            template.query(VERSION_PARAM_NAME, "3");
            template.query(SIGNATURE_PARAM_NAME, signature);
            template.query(EXPIRES_PARAM_NAME, expires);
        }

        private String getExpires() {
            return DateTimeFormatter.ISO_INSTANT.format(Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS));
        }

        private String calculateSignature(String expires) {
            String baseString = accessKey + HOLIDAYS_ENDPOINT + expires;
            HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey);
            byte[] hmacHex = hmacUtils.hmac(baseString);
            return Base64.getEncoder().encodeToString(hmacHex);
        }
    }
}
