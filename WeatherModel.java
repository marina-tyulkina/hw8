package homework7;

import java.io.IOException;

public interface WeatherModel {
    void getWeather(Period period, String selectedCity) throws IOException;

    void getExit(Period period);

    void getSavedWeatherData(Period period);
}
