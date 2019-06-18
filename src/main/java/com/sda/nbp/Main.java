package com.sda.nbp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    private static final double CURRENCY_PLN = 100;
    private static final int NUMBER_OF_MONTHS = 1;

    public static void main(String[] args) {

        double currencyUSD = 0;
        double currencyEUR = 0;
        double currencyGBP = 0;
        double currencyCHF = 0;
        double currencySaleUSD = 0;
        double currencySaleEUR = 0;
        double currencySaleGBP = 0;
        double currencySaleCHF = 0;
        double previousPurchaseUSD = 0;
        double previousPurchaseEUR = 0;
        double previousPurchaseGBP = 0;
        double previousPurchaseCHF = 0;

        LocalDate dateCurrent = LocalDate.now();
        LocalDate dateOfPreviousExchange = LocalDate.now().minusMonths(NUMBER_OF_MONTHS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String patch = "http://api.nbp.pl/api/exchangerates/tables/a/?format=json";
        Currency[] currency;
        try {
            currency = getCurrencies(patch);
        } catch (IOException e) {
            System.out.println("No connection witch the api NBP: " + e);
            System.out.println("Check your connection!");
            return;
        }
        String effectiveDate = currency[0].getEffectiveDate();
        LocalDate dateReadFromJson = LocalDate.parse(effectiveDate);
        for (Rates rate : currency[0].getRates()) {
            if (rate.getCode().equals("USD")) currencyUSD = rate.getMid();
            if (rate.getCode().equals("EUR")) currencyEUR = rate.getMid();
            if (rate.getCode().equals("GBP")) currencyGBP = rate.getMid();
            if (rate.getCode().equals("CHF")) currencyCHF = rate.getMid();
        }

        System.out.println("Data from: " + dateReadFromJson);
        if (!dateReadFromJson.equals(dateCurrent)){
            System.out.println("The date read is different from the current one!");
        }
        System.out.println("The current average USD exchange rate is: " + currencyUSD + ", " + CURRENCY_PLN + " PLN is worth "
                + Math.round(CURRENCY_PLN * 100 / currencyUSD) / 100.0 + " USD");
        System.out.println("The current average EUR exchange rate is: " + currencyEUR + ", " + CURRENCY_PLN + " PLN is worth "
                + Math.round(CURRENCY_PLN * 100 / currencyEUR) / 100.0 + " EUR");
        System.out.println("The current average GBP exchange rate is: " + currencyGBP + ", " + CURRENCY_PLN + " PLN is worth "
                + Math.round(CURRENCY_PLN * 100 / currencyGBP) / 100.0 + " GBP");
        System.out.println("The current average CHF exchange rate is: " + currencyCHF + ", " + CURRENCY_PLN + " PLN is worth "
                + Math.round(CURRENCY_PLN * 100 / currencyCHF) / 100.0 + " CHF");


        patch = "http://api.nbp.pl/api/exchangerates/tables/c/?format=json";
        Currency[] currentSalesValues;
        try {
            currentSalesValues = getCurrencies(patch);
        } catch (IOException e) {
            System.out.println("No connection witch the api NBP: " + e);
            System.out.println("Check your connection!");
            return;
        }
        for (Rates rate : currentSalesValues[0].getRates()) {
            if (rate.getCode().equals("USD")) currencySaleUSD = rate.getAsk();
            if (rate.getCode().equals("EUR")) currencySaleEUR = rate.getAsk();
            if (rate.getCode().equals("GBP")) currencySaleGBP = rate.getAsk();
            if (rate.getCode().equals("CHF")) currencySaleCHF = rate.getAsk();
        }

        System.out.println("\nProfit from exchange " + CURRENCY_PLN + " PLN:");
        patch = "http://api.nbp.pl/api/exchangerates/tables/c/"+ dateOfPreviousExchange.format(formatter) +"/?format=json";
        Currency[] previousPurchaseValues = new Currency[0];
        for (int i = 0; i < 10; i++) {
            try {
                previousPurchaseValues = getCurrencies(patch);
                System.out.println("Exchange in " + dateOfPreviousExchange.minusDays(i) + ":");
                break;
            } catch (IOException e) {
                System.out.println("There is no data in day: " + dateOfPreviousExchange.minusDays(i).format(formatter));
                System.out.println("System choose day before: " + dateOfPreviousExchange.minusDays(i + 1).format(formatter));
                patch = "http://api.nbp.pl/api/exchangerates/tables/c/"+ dateOfPreviousExchange.minusDays(i + 1).format(formatter) +"/?format=json";
            }
            if (i == 9) {
                System.out.println("No data in the given data range!");
                return;
            }
        }
        for (Rates rate : previousPurchaseValues[0].getRates()) {
            if (rate.getCode().equals("USD")) previousPurchaseUSD = rate.getBid();
            if (rate.getCode().equals("EUR")) previousPurchaseEUR = rate.getBid();
            if (rate.getCode().equals("GBP")) previousPurchaseGBP = rate.getBid();
            if (rate.getCode().equals("CHF")) previousPurchaseCHF = rate.getBid();
        }
        System.out.println("Profit from buying USD a " + NUMBER_OF_MONTHS + " month ago is: " +
                (Math.round(CURRENCY_PLN * 100 / previousPurchaseUSD * currencySaleUSD - CURRENCY_PLN * 100) / 100.0) + " PLN");
        System.out.println("Profit from buying EUR a " + NUMBER_OF_MONTHS + " month ago is: " +
                (Math.round(CURRENCY_PLN * 100 / previousPurchaseEUR * currencySaleEUR - CURRENCY_PLN * 100) / 100.0) + " PLN");
        System.out.println("Profit from buying GBP a " + NUMBER_OF_MONTHS + " month ago is: " +
                (Math.round(CURRENCY_PLN * 100 / previousPurchaseGBP * currencySaleGBP - CURRENCY_PLN * 100) / 100.0) + " PLN");
        System.out.println("Profit from buying CHF a " + NUMBER_OF_MONTHS + " month ago is: " +
                (Math.round(CURRENCY_PLN * 100 / previousPurchaseCHF * currencySaleCHF - CURRENCY_PLN * 100) / 100.0) + " PLN");
    }


    private static Currency[] getCurrencies(final String patch) throws IOException {
        URL url = new URL(patch);
        URLConnection connection = url.openConnection();
        InputStream input = connection.getInputStream();

        Scanner scanner = new Scanner(input);
        String json = scanner.nextLine();

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, Currency[].class);
    }

}
