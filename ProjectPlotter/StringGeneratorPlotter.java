import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StringGeneratorPlotter {

    private static double[] timeResults = new double[15];
    private static double[] memoryReuslts = new double[15];

    public static void getResults() {
        int numFiles = 15;
        ArrayList<Double> yResults = new ArrayList<>();

        for (int i = 0; i < numFiles; i++) {
            String timeUsage;
            String memUsage;
            File file = (new File(System.getProperty("user.dir"), "/out" + (i + 1) + ".txt"));
            try {
                timeUsage = (String) Files.readAllLines(Path.of(file.getAbsolutePath())).get(3);
                memUsage = (String) Files.readAllLines(Path.of(file.getAbsolutePath())).get(4);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            timeResults[i] = Double.parseDouble(timeUsage);
            memoryReuslts[i] = Double.parseDouble(memUsage);
        }
    }

    public static void main(String[] args) {
        double[] xData = new double[] {
                16.0,
                64.0,
                128.0,
                256.0,
                384.0,
                512.0,
                768.0,
                1024.0,
                1280.0,
                1536.0,
                2048.0,
                2560.0,
                3072.0,
                3584.0,
                3968.0
        };
        getResults();
        XYChart timeChart = QuickChart.getChart("Input Size Versus Time", "M+N", "Time in MS", "Efficient", xData, timeResults);
        XYChart memoryChart = QuickChart.getChart("Input Size Versus Memory", "M+N", "Memory in KB", "Efficient", xData, memoryReuslts);

        new SwingWrapper(timeChart).displayChart();
        new SwingWrapper(memoryChart).displayChart();

        try {
            BitmapEncoder.saveBitmapWithDPI(timeChart, "./plots/Input Size Versus Time In MS", BitmapEncoder.BitmapFormat.PNG, 300);
            BitmapEncoder.saveBitmapWithDPI(memoryChart, "./plots/Input Size Versus Memory in KB", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
