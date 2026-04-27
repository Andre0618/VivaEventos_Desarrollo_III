package com.vivaeventos.eventservice.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class EventFilterRequest {

    private String category;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo;

    // Getters y Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDateTime dateFrom) { this.dateFrom = dateFrom; }

    public LocalDateTime getDateTo() { return dateTo; }
    public void setDateTo(LocalDateTime dateTo) { this.dateTo = dateTo; }
}