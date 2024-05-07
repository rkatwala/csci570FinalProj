import org.apache.commons.lang3.math.NumberUtils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringGeneratorPlotter {

    private static final int numFiles = 15;
    private static final double[] timeResults = new double[15];
    private static final double[] memoryReuslts = new double[15];

    public static void getResults() {
        for (int i = 0; i < numFiles; i++) {
            String timeUsage;
            String memUsage;
            File output = new File(System.getProperty("user.dir"), "/out" + (i + 1) + ".txt");
            try {
                timeUsage = Files.readAllLines(Path.of(output.getAbsolutePath())).get(3);
                memUsage = Files.readAllLines(Path.of(output.getAbsolutePath())).get(4);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            timeResults[i] = Double.parseDouble(timeUsage);
            memoryReuslts[i] = Double.parseDouble(memUsage);
        }
    }

    public static double[] getProblemSizes() {
        double[] sizes = new double[numFiles];

        List<String> Lines;

        for (int i = 0; i < numFiles; i++) {
            int baseLen1, baseLen2 = 0;
            int step1 = 0, step2 = 0;
            int flag = 1;
            File input = new File(System.getProperty("user.dir"), "/in" + (i + 1) + ".txt");
            try {
                Lines = Files.readAllLines(Path.of(input.getAbsolutePath()));
                baseLen1 = Lines.get(0).length();
                for (int j = 1; j < Lines.size(); j++) {
                    if (NumberUtils.isParsable(Lines.get(j))) {
                        if (flag == 1) {
                            step1++;
                        }
                        else {
                            step2++;
                        }
                    }
                    else {
                        baseLen2 = Lines.get(j).length();
                        flag = 2;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sizes[i] = (double) baseLen1 * Math.pow(2, step1) + baseLen2 * Math.pow(2, step2);
        }

        return sizes;
    }

    public static void main(String[] args) {
        double[] xData = getProblemSizes();
        //System.out.println(Arrays.toString(xData));
        getResults();
        XYChart timeChart = QuickChart.getChart("Input Size Versus Time",
                "M+N", "Time in MS", "Efficient", xData, timeResults);
        XYChart memoryChart = QuickChart.getChart("Input Size Versus Memory",
                "M+N", "Memory in KB", "Efficient", xData, memoryReuslts);

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
