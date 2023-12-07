import common.Paths;
import common.Timings;
import common.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

public class Day1 {
    public static void main(String[] args) {
        Timings.start();

        int totalFuel = 0;
        int totalComplexFuel = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(Version.AoC2019, "day1.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int mass = Integer.parseInt(line);
                totalFuel += calculateFuel(mass);
                totalComplexFuel += calculateComplexFuel(mass);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Timings.stop();

        System.out.println("Total amount of fuel for modules: " + totalFuel);
        System.out.println("Total amount of fuel for modules + fuel: " + totalComplexFuel);
    }

    public static int calculateFuel(int mass) {
        return Math.floorDiv(mass, 3) - 2;
    }

    public static int calculateComplexFuel(int mass) {
        int finalFuel = 0;
        int previousStep = 0;
        int currentStep = mass;
        do {
            finalFuel += previousStep;
            previousStep = currentStep = calculateFuel(currentStep);
        } while (currentStep > 0);
        return finalFuel;
    }
}
