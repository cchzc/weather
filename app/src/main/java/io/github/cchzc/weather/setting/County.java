package io.github.cchzc.weather.setting;

import java.util.List;

import lombok.Data;

@Data
public class County {
    private int id;
    private char area;
    private int tid;
    private Name name;
    private List<Town> town;
}
