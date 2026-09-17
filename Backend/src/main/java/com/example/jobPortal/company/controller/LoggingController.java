package com.example.jobPortal.company.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logging")
@Slf4j
public class LoggingController {

//    public static final Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @GetMapping(path = "/public",version = "1.0")
    public ResponseEntity<String> testLogging() {
        log.trace("trace logging");
        log.info("info logging");
        log.debug("debug logging");
        log.error("error logging");
        log.warn("warn logging");
        return ResponseEntity.status(HttpStatus.OK).body("logging is successfully done");
    }
}
