package com.scalesec.vulnado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

  public class Cowsay {
  private static final Logger LOGGER = Logger.getLogger(Cowsay.class.getName());

  public static String run(String input) {
    String sanitizedInput = input.replaceAll("[^a-zA-Z0-9 .,!?'-]", "");
    ProcessBuilder processBuilder = new ProcessBuilder("/usr/games/cowsay", sanitizedInput);
    processBuilder.environment().put("PATH", "/usr/games");

    StringBuilder output = new StringBuilder();

    try {
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line + "\n");
      }
    } catch (Exception e) {
      LOGGER.severe("Error executing cowsay: " + e.getMessage());
    }
    return output.toString();
}
  }