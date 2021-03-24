package io.github.cchzc.weather.setting;

import lombok.Data;

@Data
public class Town {
    private int id;
    private Name name;
    private char rid;
    private boolean tide;
}
