package io.github.cchzc.weather;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherBotTest {
    private WeatherBot weatherBot;
    private static String townCode = "6600200";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    public void testGet3HrForecastData() {
        List<ForecastData> datas = WeatherBot.get3HrForecastData(townCode);
        datas.forEach(System.out::println);
        Assert.assertNotNull(datas);
        ForecastData data = datas.get(0);
        Assert.assertEquals("The DataType should be Type.HOUR",
                ForecastData.Type.HOUR, data.getDataType());
        testForecastData(data);

    }

    @Test
    public void testGet7DayForecastData() {
        List<ForecastData> datas = WeatherBot.get7DayForecastData(townCode);
        datas.forEach(System.out::println);
        Assert.assertNotNull(datas);
        ForecastData data = datas.get(0);
        Assert.assertEquals("The DataType should be Type.WEEK",
                ForecastData.Type.WEEK, data.getDataType());
        testForecastData(data);
    }

    public void testForecastData(ForecastData data){
        Assert.assertFalse(data.getWeather().isEmpty());
        Assert.assertTrue(data.getMinTemp()<= data.getMaxTemp());
        Assert.assertTrue(data.getMinATemp()<= data.getMaxATemp());
        Assert.assertTrue(data.getHumidity() >= -1 &&
                                    data.getHumidity() <= 100);
        Assert.assertTrue(data.getRain() >= -1 && data.getRain() <= 100);
        Assert.assertTrue(data.getWind() >=0 && data.getWind() <=12);
        // data.getWindDirection();
        Assert.assertTrue(data.getUVIndex() >= -1);
        //data.getComfortIndex();
    }
}