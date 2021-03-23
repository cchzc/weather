package io.github.cchzc.weather;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

/**
 * Get Weather Forecast data from CWB website.
 */
public class WeatherBot {
    private static final Logger logger = Logger.getLogger(WeatherBot.class.getName());
    private static final String icon_prefix = "/V8/assets/img/weather_icons/weathers/svg_icon/";
    /**
     * Get every 3-hour Weather Forecast data for town.
     * @param townCode The code of town.
     * @return Every 3-hour Weather Forecast data.
     */
    public static List<ForecastData> get3HrForecastData(String townCode){
        List<ForecastData> result = new ArrayList<>();
        String temp3 = "https://www.cwb.gov.tw/V8/E/W/Town/MOD/3hr/%s_3hr_m.html";
        Elements tables = getRawData(String.format(temp3, townCode));
        for (Element ele : tables) {
            logger.fine("element's html: \n" + ele.outerHtml());
            ForecastData data = new ForecastData();
            data.setDataType(ForecastData.Type.HOUR);

            //Date
            String datetime = String.format(Locale.ENGLISH,"%d/%s",
                                LocalDate.now().getYear(),ele.selectFirst("th").text());

            LocalDateTime dt = LocalDateTime.parse(datetime,
                    DateTimeFormatter.ofPattern("yyyy/MM/dd (EEE.) HH:mm", Locale.ENGLISH));
            data.setDateTime(dt);

            //weather
            data.setWeather(ele.selectFirst("img").attr("alt"));

            //weather icon
            data.setWeatherIcon(ele.selectFirst("img").attr("src")
                    .replace(icon_prefix,"")
                    .replace("day/","ic_weather_day_")
                    .replace("night/","ic_weather_night_")
                    .replace(".svg", ""));

            //temp
            data.setMaxTemp(Integer.parseInt(ele.select(".tem-C").get(0).text()));
            data.setMinTemp(Integer.parseInt(ele.select(".tem-C").get(0).text()));

            //atemp
            data.setMaxATemp(Integer.parseInt(ele.select(".tem-C").get(1).text()));
            data.setMinATemp(Integer.parseInt(ele.select(".tem-C").get(1).text()));

            //RH
            data.setHumidity(Integer.parseInt(ele.selectFirst("td[headers*='m3_RH']")
                    .text().replace("%", "")));

            //rain
            data.setRain(Integer.parseInt(ele.selectFirst("td[headers*='m3_Po']")
                    .text().replace("%", "")));

            //comfortIndex
            data.setComfortIndex(ForecastData.ComfortIndex.valueOf(
                    ele.selectFirst("td[headers*='m3_CI']").text()
                            .replace(" ","")));

            //wind
            data.setWind(Integer.parseInt(
                    ele.select("span.wind_1").get(1).text()
                            .replace("≤","")));

            //windDirection
            data.setWindDirection(ForecastData.WindDirection.valueOf(
                    ele.selectFirst("td[headers*='m3_WD']").text()
                            .replace(" wind", "")));

            result.add(data);
        }
        return result;
    }

    /**
     * Get 7-day Weather Forecast data for town.
     * @param townCode The code of town.
     * @return 7-day Weather Forecast data.
     */
    public static List<ForecastData> get7DayForecastData(String townCode){
        List<ForecastData> result = new ArrayList<>();
        String temp7 = "https://www.cwb.gov.tw/V8/E/W/Town/MOD/Week/%s_Week_m.html";

        Elements tables = getRawData(String.format(temp7, townCode));
        for (Element ele : tables) {
            logger.fine("element's html: \n" + ele.outerHtml());
            ForecastData data = new ForecastData();
            data.setDataType(ForecastData.Type.WEEK);

            //Date
            LocalDateTime dt = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            String datetime = ele.selectFirst("th").text(); //ex. 03/23 (Tue.) Day
            dt = dt.withMonth(Integer.parseInt(datetime.substring(0,2)))
                    .withDayOfMonth(Integer.parseInt(datetime.substring(3,5)));
            dt = (datetime.substring(13).equals("Day")) ? dt.withHour(8) : dt.withHour(20);
            data.setDateTime(dt);

            //weather
            data.setWeather(ele.selectFirst("img").attr("alt"));

            //weather icon
            data.setWeatherIcon(ele.selectFirst("img").attr("src")
                    .replace(icon_prefix,"")
                    .replace("day/","ic_weather_day_")
                    .replace("night/","ic_weather_night_")
                    .replace(".svg", ""));

            //temp
            data.setMaxTemp(Integer.parseInt(ele.select(".tem-C").get(0).text().substring(3,5)));
            data.setMinTemp(Integer.parseInt(ele.select(".tem-C").get(0).text().substring(0,2)));

            //atemp
            data.setMaxATemp(Integer.parseInt(ele.select(".tem-C").get(1).text().substring(3,5)));
            data.setMinATemp(Integer.parseInt(ele.select(".tem-C").get(1).text().substring(0,2)));

            //RH
            data.setHumidity(Integer.parseInt(ele.selectFirst("td[headers*='m7_RH']")
                    .text().replace("%", "")));

            //rain
            try {
                data.setRain(Integer.parseInt(ele.selectFirst("td[headers*='m7_Po']")
                        .text().replace("%", "")));
            }catch (NumberFormatException ignored){}

            //UVIndex
            String uvStr = ele.selectFirst("td[headers*='m7_UVI']").text().trim();
            if(!uvStr.isEmpty())
                data.setUVIndex(Integer.parseInt(uvStr));

            //wind
            data.setWind(Integer.parseInt(
                    ele.select("span.wind_1").get(1).text()
                            .replace("≤","")));

            //windDirection
            data.setWindDirection(ForecastData.WindDirection.valueOf(
                    ele.selectFirst("td[headers*='m7_WD']").text()
                            .replace(" wind", "")));

            result.add(data);
        }
        return result;
    }

    /**
     * Get Weather Forecast data from website. A element is a html formatted table.
     * A table has a weather forecast.<br/>
     * The element likes:<br/>
     * &lt;table class="table"&gt;<br/>
     * &nbsp;&nbsp;&lt;thead&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;tr&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;th id="m7_D1D"&gt;&lt;span&gt;03/22(一)&lt;/span&gt;&lt;br&gt;白天&lt;/th&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;td headers="m7_D1D" class="text-nowrap"&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;img src="07.svg" alt="陰天"&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/td&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/tr&gt;<br/>
     * &nbsp;&nbsp;&lt;/thead&gt;<br/>
     * &lt;/table&gt;<br/>
     * @param url Weather Forecast url.
     * @return A list of elements which have weather forecast.
     */
    private static Elements getRawData(String url){
        Elements tables = new Elements();
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null,null,null);
            SSLContext.setDefault(ctx);

            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .sslSocketFactory(SSLContext.getDefault().getSocketFactory()).get();
            tables = document.getElementsByClass("table");
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            e.printStackTrace();
        }
        return tables;
    }
}
