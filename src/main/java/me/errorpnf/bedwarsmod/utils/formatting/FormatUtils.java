package me.errorpnf.bedwarsmod.utils.formatting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {
    public FormatUtils() {
    }

    public static String format(String string) {
        return string.replaceAll("&", "§");
    }

    public static String unformat(String formattedString) {
        Pattern FORMAT_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]");
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(formattedString);
        return matcher.replaceAll("");
    }

    public static String formatCommas(Number number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(number);
    }

    public static double roundToTwoDecimalPlacesForStats(int numerator, int denominator) {
        if (numerator != 0 && denominator != 0) {
            double result = (double) numerator / denominator;
            return Math.round(result * 100.0) / 100.0;
        } else if (numerator != 0) {
            return numerator;
        } else if (denominator != 0) {
            return 0.0;
        } else {
            return 0.0;
        }
    }

    public static double formatDecimal(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0; // Handle NaN and Infinite values
        }
        if (value == 0) {
            return 0.0;
        }
        BigDecimal bd = new BigDecimal(value).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String removeResetCode(String input) {
        return input.replaceAll("§r", "");
    }
}
