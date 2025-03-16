package com.yasm3.bulbcontrol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bulb {

    // Universal
    private String name; // Max 64 bytes ASCII
    private String host;
    private boolean powered;
    private int brightness; // 1 - 100
    private BulbColorMode colorMode;

    // Only COLOR_MODE (1)
    private int rgb; //

    // Only COLOR_TEMPERATURE_MODE (2)
    private int currentColorTemperature; // Depending model

    // Only HSV_MODE
    private int hue; // 0 - 359
    private int sat; // 0 - 100

    // Actions queue
    private BulbCommander bc;

    public Bulb(String data) {
        updateValues(data);
        bc = new BulbCommander(host);
    }

    public void updateValues(String data) {

        // parse host
        String location = extractValue(data, 5);
        host = extractHost(location);

        // parse name
        name = extractValue(data, 18);

        // parse power state
        String powerState = extractValue(data, 11);
        powered = powerState.equals("on");

        // parse brightness
        brightness = Integer.parseInt(extractValue(data, 12));

        // parse color mode and associated values
        switch (Integer.parseInt(extractValue(data, 13))) {
            case 1:
                colorMode = BulbColorMode.COLOR_MODE;
                rgb = Integer.parseInt(extractValue(data, 15));
                break;
            case 2:
                colorMode = BulbColorMode.COLOR_TEMPERATURE_MODE;
                currentColorTemperature = Integer.parseInt(extractValue(data, 14));
                break;
            case 3:
                colorMode = BulbColorMode.HSV_MODE;
                hue = Integer.parseInt(extractValue(data, 15));
                sat = Integer.parseInt(extractValue(data, 16));
                break;
        }
    }

    private static String extractValue(String input, int line) {
        String[] arrayOfString = input.split("\n");
        if (arrayOfString.length >= line) {
            String out =  arrayOfString[line - 1];
            return out.substring(out.indexOf(":") + 2).trim();
        }
        return "";
    }

    private static String extractHost(String location) {
        String locationRegex = "(?<=yeelight://)(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
        Pattern pattern = Pattern.compile(locationRegex);
        Matcher matcher = pattern.matcher(location);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public void printInfos() {
        System.out.println("Printing Bulb Infos:");
        System.out.println("Name: " + name + "\n" +
                "IP Address: " + host + "\n" +
                "Power: " + (powered ? "on" : "off") + "\n" +
                "Brightness: " + brightness + "\n" +
                "Color Mode: " + colorMode
        );
        switch (colorMode) {
            case COLOR_MODE -> System.out.println("RGB: " + rgb);
            case COLOR_TEMPERATURE_MODE -> System.out.println("Color Temperature: " + currentColorTemperature);
            case HSV_MODE -> System.out.println("HUE: " + hue + "\n" + "SAT: " + sat);
        }
    }

    public String getHost() {
        return host;
    }

    public void setPower(boolean power) {
        BulbCommand c = new BulbCommand(1,
                "set_power",
                new Object[]{(power ? "on" : "off"), "sudden", 200}
        );
        bc.pushCommand(c);
    }

    public void commit() {
        bc.send();
    }

}
