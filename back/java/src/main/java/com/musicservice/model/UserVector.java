package com.musicservice.model;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
@Data
@NoArgsConstructor
public class UserVector {
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID userId;
    private double[] vectorData;
}