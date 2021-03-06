package com.adventofcode.javatomten;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Advent of Code 2016-12-10.
 */
public class Day10 {

    class Receiver {
        int value;

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }

    class Bot extends Receiver {
        private final int id;
        private final String lowReceiver;
        private final String highReceiver;
        private final int lowId;
        private final int highId;
        int value = -1;

        public Bot(int id, String lowReceiver, int lowId, String highReceiver, int highId) {
            this.id = id;
            this.lowReceiver = lowReceiver;
            this.lowId = lowId;
            this.highReceiver = highReceiver;
            this.highId = highId;
        }

        public void setValue(int value) {
            if (this.value == -1) {
                this.value = value;
            } else if (this.value >= 0) {
                Receiver l;
                Receiver h;
                if (lowReceiver.equals("output")) {
                    l = outnet.get(lowId);
                }
                else {
                    l = botnet.get(lowId);
                }
                if (highReceiver.equals("output")) {
                    h = outnet.get(highId);
                }
                else {
                    h = botnet.get(highId);
                }
                if (this.value < value) {
                    if (this.value == 17 && value == 61)
                        System.out.println("Bot: " + id);
                    l.setValue(this.value);
                    h.setValue(value);
                } else {
                    if (value == 17 && this.value == 61)
                        System.out.println("Bot: " + id);
                    l.setValue(value);
                    h.setValue(this.value);
                }
                this.value = -1;
            }
        }

        @Override
        public String toString() {
            return "Bot[" + id + "]: " + lowReceiver + " " + lowId + "-" + highReceiver + " " + highId;
        }
    }

    class Output extends Receiver {
        int id;

        public Output(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Output[" + id + "]: " + value;
        }

    }

    class Input {
        private final int botId;
        private final int value;

        Input(int botId, int value) {
            this.botId = botId;
            this.value = value;
        }

        public void applyValue() {
            Bot bot = botnet.get(botId);
            bot.setValue(value);
        }

        @Override
        public String toString() {
            return "Input:" + value + "=> Bot [" + botId + "]";
        }
    }

    Pattern botPattern = Pattern.compile("bot ([0-9]+) gives low to (bot|output) ([0-9]+) and high to (bot|output) ([0-9]+)");
    Pattern valuePattern = Pattern.compile("value ([0-9]+) goes to bot ([0-9]+)");
    private Map<Integer, Bot> botnet = new HashMap<>();
    private Map<Integer, Output> outnet = new HashMap<>();
    private List<Input> values = new ArrayList<>();


    public static void main(String... args) throws IOException {
        Day10 day = new Day10();
        List<String> lines = day.parseArgs(args);
        day.parse(lines);
    }

    private List<String> parseArgs(String[] args) throws IOException {
        Path path = FileSystems.getDefault().getPath(args[0]);
        return Files.readAllLines(path);
    }

    private void parse(List<String> data) {
        data.forEach(this::parseLine);
        values.forEach(Input::applyValue);
    }

    private void parseLine(String data) {
        Matcher botMatcher = botPattern.matcher(data);
        Matcher valueMatcher = valuePattern.matcher(data);
        if (botMatcher.find()) {
            final int botId = Integer.parseInt(botMatcher.group(1));
            final String lowReceiver = botMatcher.group(2);
            final int lowId = Integer.parseInt(botMatcher.group(3));
            final String highReceiver = botMatcher.group(4);
            final int highId = Integer.parseInt(botMatcher.group(5));
            if (lowReceiver.equals("output")) {
                outnet.put(lowId, new Output(lowId));
            }
            if (highReceiver.equals("output")) {
                outnet.put(highId, new Output(highId));
            }
            Bot b = new Bot(botId, lowReceiver, lowId, highReceiver, highId);
            botnet.put(botId, b);
        } else if (valueMatcher.find()) {
            final int value = Integer.parseInt(valueMatcher.group(1));
            final int botId = Integer.parseInt(valueMatcher.group(2));

            values.add(new Input(botId, value));
        }

    }


}
