package sciwhiz12.aoc.y2020;

import sciwhiz12.aoc.common.IO;
import sciwhiz12.aoc.common.Timings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sciwhiz12.aoc.common.Printer.Builder.outputs;

public class Day4 {
    static final Pattern KEY_VALUE_PATTERN = Pattern.compile("([a-z]+):([\\w#\\d]+)");
    static final List<String> INPUTS = IO.input("2020/day4.txt");

    public static void main(String... args) {

        partOne();

        partTwo();

    }

    public static void partOne() {
        long passportAmount;
        long validPassports;
        AtomicLong fullyValid = new AtomicLong();

        Timings.start();

        final List<Passport> passports = readPassports(INPUTS);
        passportAmount = passports.size();

        validPassports = passports.stream()
                .filter(passport -> passport.birthYear != null)
                .filter(passport -> passport.issueYear != null)
                .filter(passport -> passport.expirationYear != null)
                .filter(passport -> passport.height != null)
                .filter(passport -> passport.hairColor != null)
                .filter(passport -> passport.eyeColor != null)
                .filter(passport -> passport.passportID != null)
                .peek(passport -> {
                    if (passport.countryID != null) {
                        fullyValid.incrementAndGet();
                    }
                })
                .count();

        Timings.stop();

        outputs()
                .add("Total Passports Processed", passportAmount)
                .add("Valid Passports (w/ and w/o CID)", validPassports)
                .add("Fully Valid Passports", fullyValid)
                .print();
    }

    public static void partTwo() {
        long passportAmount;
        long validPassports;
        AtomicLong fullyValid = new AtomicLong();

        Timings.start();

        final Predicate<String> heightValidator = height -> {
            final IntSupplier i = () -> Integer.parseInt(height.substring(0, height.length() - 2));
            if (height.endsWith("cm")) {
                final int value = i.getAsInt();
                return value >= 150 && value <= 193;
            } else if (height.endsWith("in")) {
                final int value = i.getAsInt();
                return value >= 59 && value <= 76;
            }
            return false;
        };
        final List<String> validEyeColors = List.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");
        final Pattern hex_code = Pattern.compile("#[0-9a-f]{6}");
        final List<Passport> passports = readPassports(INPUTS);
        passportAmount = passports.size();

        validPassports = passports.stream()
                .filter(p -> p.birthYear != null && p.birthYear >= 1920 && p.birthYear <= 2002)
                .filter(p -> p.issueYear != null && p.issueYear >= 2010 && p.issueYear <= 2020)
                .filter(p -> p.expirationYear != null && p.expirationYear >= 2020 && p.expirationYear <= 2030)
                .filter(p -> p.height != null && heightValidator.test(p.height))
                .filter(p -> p.hairColor != null && hex_code.matcher(p.hairColor).matches())
                .filter(p -> p.eyeColor != null && validEyeColors.contains(p.eyeColor))
                .filter(p -> p.passportID != null && p.passportID.length() == 9)
                .peek(passport -> {
                    if (passport.countryID != null) {
                        fullyValid.incrementAndGet();
                    }
                })
                .count();

        Timings.stop();

        outputs()
                .add("Total Passports Processed", passportAmount)
                .add("Valid Passports (w/ and w/o CID)", validPassports)
                .add("Fully Valid Passports", fullyValid)
                .print();
    }

    public static List<Passport> readPassports(List<String> input) {
        final ArrayList<Passport> output = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (String str : input) {
            if (str.isEmpty()) {
                output.add(parsePassport(builder.toString()));
                builder = new StringBuilder();
                continue;
            }
            if (!builder.isEmpty())
                builder.append(' ');
            builder.append(str);
        }
        if (!builder.isEmpty()) {
            output.add(parsePassport(builder.toString()));
        }

        return output;
    }

    private static Passport parsePassport(String string) {
        final Matcher matcher = KEY_VALUE_PATTERN.matcher(string);

        @Nullable
        Integer birthYear = null;
        @Nullable
        Integer issueYear = null;
        @Nullable
        Integer expirationYear = null;
        @Nullable
        String height = null;
        @Nullable
        String hairColor = null;
        @Nullable
        String eyeColor = null;
        @Nullable
        String passportID = null;
        @Nullable
        String countryID = null;

        while (matcher.find()) {
            final String key = matcher.group(1);
            final String value = matcher.group(2);

            switch (key) {
                case "byr" -> birthYear = Integer.parseInt(value);
                case "iyr" -> issueYear = Integer.parseInt(value);
                case "eyr" -> expirationYear = Integer.parseInt(value);
                case "hgt" -> height = value;
                case "hcl" -> hairColor = value;
                case "ecl" -> eyeColor = value;
                case "pid" -> passportID = value;
                case "cid" -> countryID = value;
                default -> throw new IllegalStateException("Unknown key " + key + " with value " + value);
            }
        }

        return new Passport(birthYear, issueYear, expirationYear, height, hairColor, eyeColor, passportID, countryID);
    }

    static class Passport {
        @Nullable
        public final Integer birthYear;
        @Nullable
        public final Integer issueYear;
        @Nullable
        public final Integer expirationYear;
        @Nullable
        public final String height;
        @Nullable
        public final String hairColor;
        @Nullable
        public final String eyeColor;
        @Nullable
        public final String passportID;
        @Nullable
        public final String countryID;

        Passport(@Nullable Integer birthYear,
                 @Nullable Integer issueYear,
                 @Nullable Integer expirationYear,
                 @Nullable String height,
                 @Nullable String hairColor,
                 @Nullable String eyeColor,
                 @Nullable String passportID,
                 @Nullable String countryID) {
            this.birthYear = birthYear;
            this.issueYear = issueYear;
            this.expirationYear = expirationYear;
            this.height = height;
            this.hairColor = hairColor;
            this.eyeColor = eyeColor;
            this.passportID = passportID;
            this.countryID = countryID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Passport passport = (Passport) o;
            return Objects.equals(birthYear, passport.birthYear) &&
                    Objects.equals(issueYear, passport.issueYear) &&
                    Objects.equals(expirationYear, passport.expirationYear) &&
                    Objects.equals(height, passport.height) &&
                    Objects.equals(hairColor, passport.hairColor) &&
                    Objects.equals(eyeColor, passport.eyeColor) &&
                    Objects.equals(passportID, passport.passportID) &&
                    Objects.equals(countryID, passport.countryID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(birthYear, issueYear, expirationYear, height, hairColor, eyeColor, passportID, countryID);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Passport.class.getSimpleName() + "[", "]")
                    .add("birthYear='" + birthYear + "'")
                    .add("issueYear='" + issueYear + "'")
                    .add("expirationYear='" + expirationYear + "'")
                    .add("height='" + height + "'")
                    .add("hairColor='" + hairColor + "'")
                    .add("eyeColor='" + eyeColor + "'")
                    .add("passportID='" + passportID + "'")
                    .add("countryID='" + countryID + "'")
                    .toString();
        }
    }
}
