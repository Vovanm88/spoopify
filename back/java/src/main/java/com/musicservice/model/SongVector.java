package com.musicservice.model;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
@Data
@NoArgsConstructor
public class SongVector {
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID songId;
    private double[] vectorData;
}