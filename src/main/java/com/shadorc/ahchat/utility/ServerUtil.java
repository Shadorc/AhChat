package com.shadorc.ahchat.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ServerUtil {

    private static final String GET_IP_URL = "http://checkip.amazonaws.com";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss ");

    public static String getIp() throws IOException {
        final URL url = new URL(GET_IP_URL);
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return in.readLine();
        }
    }

    public static String getFormattedTime() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public static String toReadableUnit(final long bytes) {
        final int unit = 1000;
        if (bytes < unit) {
            return bytes + " B";
        }
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final char pre = ("kMGTPE").charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
