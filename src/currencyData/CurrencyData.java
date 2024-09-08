package currencyData;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyData {
    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            String currency = "";
            String targetCurrency = "";
            do {
                System.out.println("=======");
                System.out.println("Введите валюту, которую хотите преобразовать (Напишите No, чтобы выйти):");
                currency = input.nextLine();

                if (currency.equalsIgnoreCase("No")) break;

                System.out.println("Введите валюту, в которую хотите конвертировать:");
                targetCurrency = input.nextLine();

                JSONObject rates = getCurrencyData(currency);
                if (rates != null) {
                    System.out.println("Курсы валют для " + targetCurrency + ": " + rates.toJSONString());
                    if (rates.containsKey(targetCurrency.toUpperCase())) {
                        double rate = (double) rates.get(targetCurrency.toUpperCase());
                        System.out.println("Введите сумму конвертации");
                        double amount = input.nextDouble();
                        double convertedAmount = amount * rate;
                        System.out.println(amount + " " + currency.toUpperCase() + " = " + convertedAmount + " " + targetCurrency.toUpperCase());
                    } else {
                        System.out.println("Курс для " + targetCurrency + " не найден.");
                    }
                }
            } while (!currency.equalsIgnoreCase("No"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getCurrencyData(String valuta) {
        String apiKey = "599ffc949e505dbc6e3d2887";
        String urlString = "https://v6.exchangerate-api.com/v6/" +
                apiKey + "/latest/" + valuta;

        try {
            HttpURLConnection apiConnect = fetchApi(urlString);

            if (apiConnect.getResponseCode() != 200) {
                System.out.println("Ошибка: API не найдено.");
                return null;
            }
            String jsonResponse = readApiResponse(apiConnect);
            JSONParser parser = new JSONParser();
            JSONObject resultJSON = (JSONObject) parser.parse(jsonResponse);

            JSONObject conversionRates = (JSONObject) resultJSON.get("conversion_rates");

            if (conversionRates == null || conversionRates.isEmpty()) {
                System.out.println("Данные пусты");
                return null;
            }
            return conversionRates;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApi(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection connection) {
        try {
            StringBuilder result = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                result.append(scanner.nextLine());
            }
            scanner.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}