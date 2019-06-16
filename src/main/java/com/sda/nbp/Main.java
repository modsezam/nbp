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

    public static void main(String[] args) throws IOException {

        double currencyUSD = 0;
        double currencyEUR = 0;
        double currencyGBP = 0;
        double currencyCHF = 0;
        double currencySaleUSD = 0;
        double currencySaleEUR = 0;
        double currencySaleGBP = 0;
        double currencySaleCHF = 0;
        double oldPurchaseUSD = 0;
        double oldPurchaseEUR = 0;
        double oldPurchaseGBP = 0;
        double oldPurchaseCHF = 0;

        double currencyPLN = 100;
        int numberOfMonths = 1;

        LocalDate date = LocalDate.now().minusMonths(numberOfMonths);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String patch = "http://api.nbp.pl/api/exchangerates/tables/a/?format=json";
        Currency[] currency = getCurrencies(patch);
        for (Rates rate : currency[0].getRates()) {
            if (rate.getCode().equals("USD")) currencyUSD = rate.getMid();
            if (rate.getCode().equals("EUR")) currencyEUR = rate.getMid();
            if (rate.getCode().equals("GBP")) currencyGBP = rate.getMid();
            if (rate.getCode().equals("CHF")) currencyCHF = rate.getMid();
        }

        patch = "http://api.nbp.pl/api/exchangerates/tables/c/?format=json";
        Currency[] currency2 = getCurrencies(patch);
        for (Rates rate : currency2[0].getRates()) {
            if (rate.getCode().equals("USD")) currencySaleUSD = rate.getAsk();
            if (rate.getCode().equals("EUR")) currencySaleEUR = rate.getAsk();
            if (rate.getCode().equals("GBP")) currencySaleGBP = rate.getAsk();
            if (rate.getCode().equals("CHF")) currencySaleCHF = rate.getAsk();
        }

        patch = "http://api.nbp.pl/api/exchangerates/tables/c/"+ date.format(formatter) +"/?format=json";
        Currency[] currency3 = getCurrencies(patch);
        for (Rates rate : currency3[0].getRates()) {
            if (rate.getCode().equals("USD")) oldPurchaseUSD = rate.getBid();
            if (rate.getCode().equals("EUR")) oldPurchaseEUR = rate.getBid();
            if (rate.getCode().equals("GBP")) oldPurchaseGBP = rate.getBid();
            if (rate.getCode().equals("CHF")) oldPurchaseCHF = rate.getBid();
        }

        System.out.println("Currency of USD is: " + currencyUSD + ", " + currencyPLN + " PLN is worth "
                + Math.round(currencyPLN * 100/currencySaleUSD)/100.0 + " USD");
        System.out.println("Currency of EUR is: " + currencyEUR + ", " + currencyPLN + " PLN is worth "
                + Math.round(currencyPLN * 100/currencySaleEUR)/100.0 + " EUR");
        System.out.println("Currency of GBP is: " + currencyGBP + ", " + currencyPLN + " PLN is worth "
                + Math.round(currencyPLN * 100/currencySaleGBP)/100.0 + " GBP");
        System.out.println("Currency of CHF is: " + currencyCHF + ", " + currencyPLN + " PLN is worth "
                + Math.round(currencyPLN * 100/currencySaleCHF)/100.0 + " CHF");

        System.out.println("\nProfit from exchange " + currencyPLN + " PLN:");
        System.out.println("Profit from buying USD a " + numberOfMonths + " month ago is: " +
                (Math.round(currencyPLN * 100 / oldPurchaseUSD * currencySaleUSD - currencyPLN * 100) / 100.0) + " PLN");
        System.out.println("Profit from buying EUR a " + numberOfMonths + " month ago is: " +
                (Math.round(currencyPLN * 100 / oldPurchaseEUR * currencySaleEUR - currencyPLN * 100) / 100.0) + " PLN");
        System.out.println("Profit from buying GBP a " + numberOfMonths + " month ago is: " +
                (Math.round(currencyPLN * 100 / oldPurchaseGBP * currencySaleGBP - currencyPLN * 100) / 100.0) + " PLN");
        System.out.println("Profit from buying CHF a " + numberOfMonths + " month ago is: " +
                (Math.round(currencyPLN * 100 / oldPurchaseCHF * currencySaleCHF - currencyPLN * 100) / 100.0) + " PLN");
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
