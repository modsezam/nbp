package com.sda.nbp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        double currencyUSD = 0.0;
        double currencyEUR = 0;
        double currencyGBP = 0;
        double currencyCHF = 0;

        String patch = "http://api.nbp.pl/api/exchangerates/tables/a/?format=json";
        URL url = new URL(patch);
        URLConnection connection = url.openConnection();
        InputStream input = connection.getInputStream();

        Scanner scanner = new Scanner(input);
        String json = scanner.nextLine();

        Gson gson = new GsonBuilder().create();
        Currency[] currency = gson.fromJson(json, Currency[].class);

        for (Rates rate : currency[0].getRates()) {
            if (rate.getCode().equals("USD")) currencyUSD = rate.getMid();
            if (rate.getCode().equals("EUR")) currencyEUR = rate.getMid();
            if (rate.getCode().equals("GBP")) currencyGBP = rate.getMid();
            if (rate.getCode().equals("CHF")) currencyCHF = rate.getMid();
        }

        System.out.println("Currency of USD is: " + currencyUSD + ", 100 PLN is worth "
                + Math.round(10000/currencyUSD)/100.0 + " USD");
        System.out.println("Currency of EUR is: " + currencyEUR + ", 100 PLN is worth "
                + Math.round(10000/currencyEUR)/100.0 + " EUR");
        System.out.println("Currency of GBP is: " + currencyGBP + ", 100 PLN is worth "
                + Math.round(10000/currencyGBP)/100.0 + " GBP");
        System.out.println("Currency of CHF is: " + currencyCHF + ", 100 PLN is worth "
                + Math.round(10000/currencyCHF)/100.0 + " CHF");


    }


}
