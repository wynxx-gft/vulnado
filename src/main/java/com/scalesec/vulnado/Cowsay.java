package com.scalesec.vulnado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

    public class Cowsay {
    private static final Logger LOGGER = Logger.getLogger(Cowsay.class.getName());
    private static final Pattern ALLOWED_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9 .,!?'-]*$");

    private Cowsay() {
        // Private constructor to hide implicit public one
    }

    public static String run(String input) {
        if (input == null || !ALLOWED_INPUT_PATTERN.matcher(input).matches()) {
            LOGGER.warning("Invalid input provided to cowsay");
            return "Invalid input";
        }
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.environment().put("PATH", "/usr/games");
        processBuilder.command("/usr/games/cowsay", input);

        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(processBuilder.start().getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
