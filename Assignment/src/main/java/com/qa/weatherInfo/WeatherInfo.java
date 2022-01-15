package com.qa.weatherInfo;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
public class WeatherInfo {

	private String cityName;
    private List<String> condition;
    private float windSpeed;
    private float windGust;
    private float tempDegrees;
    private float tempFahrenheit;
    private int humidity;

}
