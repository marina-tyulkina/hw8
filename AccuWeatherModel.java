package homework7;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.sql.SQLException;


public class AccuWeatherModel implements WeatherModel {
    private static final String PROTOKOL = "http";
    private static final String API_KEY = "Sbvq5WVAAjyqAm6homjwwDvuZSPrYCNe";
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String CURRENT_CONDITIONS_ENDPOINT = "currentconditions";
    private static final String API_V1 = "v1";
    private static final String LOCATIONS_ENDPOINT = "locations";
    private static final String CITIES_ENDPOINT = "cities";
    private static final String AUTOCOMPLETE_ENDPOINT = "autocomplete";
    private static final String FORECASTS = "forecasts";
    private static final String DAILY = "daily";
    private static final String FIVE_DAY = "5day";

    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void getWeather(Period period, String selectedCity) throws IOException {
        String cityKey = detectCityKey(selectedCity);
        if (period == Period.NOW) {
            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(PROTOKOL)
                    .host(BASE_HOST)
                    .addPathSegment(CURRENT_CONDITIONS_ENDPOINT)
                    .addPathSegment(API_V1)
                    .addPathSegment(cityKey)
                    .addQueryParameter("apikey", API_KEY)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .url(httpUrl)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();
            String weatherDate = objectMapper.readTree(responseString).get(0).at("/LocalObservationDateTime").asText();
            String temperature = objectMapper.readTree(responseString).get(0).at("/Temperature/Metric/Value").asText();
            System.out.println("Погода в городе: " + selectedCity);
            System.out.println("На дату: " + weatherDate);
            System.out.println("Температура воздуха: " + temperature);
        }

        if (period == Period.FIVE_DAYS) {
            HttpUrl httpUrlFiveDay = new HttpUrl.Builder()
                    .scheme(PROTOKOL)
                    .host(BASE_HOST)
                    .addPathSegment(FORECASTS)
                    .addPathSegment(API_V1)
                    .addPathSegment(DAILY)
                    .addPathSegment(FIVE_DAY)
                    .addPathSegment(cityKey)
                    .addQueryParameter("apikey", API_KEY)
                    .build();
            Request request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .url(httpUrlFiveDay)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();
            String str = responseString.replaceAll("[{}]", "");
            System.out.println(responseString);//DailyForecasts

        }
    }

    @Override
    public void getExit(Period period) {
        if (period == Period.EXIT) {
            System.exit(0);
        }

    }
    @Override
    public void getSavedWeatherData(Period period) {
        DataBaseRepository dataBaseRepository = new DataBaseRepository();
        try {
            dataBaseRepository.getSavedWeatherData();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public String detectCityKey(String selectedCity) throws IOException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(PROTOKOL)
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS_ENDPOINT)
                .addPathSegment(API_V1)
                .addPathSegment(CITIES_ENDPOINT)
                .addPathSegment(AUTOCOMPLETE_ENDPOINT)
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("q", selectedCity)
                .build();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(httpUrl)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String responseString = response.body().string();

        String cityKey = objectMapper.readTree(responseString).get(0).at("/Key").asText();

        return cityKey;

    }


}
