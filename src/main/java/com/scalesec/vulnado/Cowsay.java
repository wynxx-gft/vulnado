package com.scalesec.vulnado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

    public class Cowsay {
  private static final Logger LOGGER = Logger.getLogger(Cowsay.class.getName());
  private static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9 .,!?'\-]+$");

  private Cowsay() {
    // Private constructor to hide the implicit public one
  }

  public static String run(String input) {
    if (input == null || !SAFE_INPUT_PATTERN.matcher(input).matches()) {
      LOGGER.warning("Invalid input provided to cowsay");
      return "Invalid input";
    }

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("/usr/games/cowsay", input);

    StringBuilder output = new StringBuilder();

    try {
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));