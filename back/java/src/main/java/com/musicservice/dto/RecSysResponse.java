package com.musicservice.dto;

import java.util.List;

public class RecSysResponse {
    private List<String> songIds;
    private List<Double> scores;
    
    // Конструкторы
    public RecSysResponse() {
    }
    
    public RecSysResponse(List<String> songIds, List<Double> scores) {
        this.songIds = songIds;
        this.scores = scores;
    }
    
    // Геттеры и сеттеры
    public List<String> getSongIds() {
        return songIds;
    }
    
    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }
    
    public List<Double> getScores() {
        return scores;
    }
    
    public void setScores(List<Double> scores) {
        this.scores = scores;
    }
}