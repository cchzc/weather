package io.github.cchzc.weather;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ForecastData {
    private Type dataType = Type.WEEK;
    private LocalDateTime dateTime;
    /**
     * Maximum temperature(°C).
     */
    private int maxTemp;
    /**
     * Minimum temperature(°C).
     */
    private int minTemp;
    /**
     * Maximum apparent temperature(°C).
     */
    private int maxATemp;
    /**
     * Minimum apparent temperature(°C).
     */
    private int minATemp;
    /**
     * Probability of Precipitation(%).<br/>
     * The value ranges from -1 to 100.<br/>
     * It means no precipitation forecast if the value is -1.<br/>
     * -1 is default.
     */
    private int rain = -1;
    /**
     * Relative Humidity(%).<br/>
     * The value ranges from -1 to 100.<br/>
     * It means no relative humidity forecast if the value is -1.<b/>
     * -1 is default.
     */
    private int humidity = -1;
    /**
     * Weather description.
     */
    private String weather;
    /**
     * The icon name of weather.<br/>
     * Format: ic_weather_[day|night]_\d{2}<br/>
     */
    private String weatherIcon;
    /**
     * The force of wind(Beaufort Scale).<br/>
     * The value ranges from 0 to 12.
     * 0 is default.
     */
    private int wind = 0;
    /**
     * Wind direction.
     */
    private WindDirection windDirection;
    /**
     * Comfort index.<br/>
     * NONE is default.
     */
    private ComfortIndex comfortIndex = ComfortIndex.NONE;
    /**
     * UV index.<br/>
     * The value is greater than or equal to -1.<br/>
     * -1   No data(Default)<br/>
     * 0-2  Low<br/>
     * 3-5  Moderate<br/>
     * 6-7  High<br/>
     * 8-10 Very High<br/>
     * 11+  extreme<br/>
     */
    private int UVIndex = -1;

    public enum Type {WEEK, HOUR}
    public enum ComfortIndex {
        NONE(""),ExtremelyCold("Extremely Code"), Cold("Cold"),
            Cool("Cool"), Comfortable("Confortable"), Hot("Hot"),
            ExtremelyHot("Extremely Hot");
        private String description="";
        private ComfortIndex(String description){
            this.description = description;
        }
        public String description(){
            return  description;
        }
    }
    public enum WindDirection {North ,Northeast, East, Southeast, South, Southwest, West, Northwest}
}
