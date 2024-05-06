import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Efficient {

   private static final int delta = 30;

   private static int alpha(char c1, char c2) {
      if ((c1 == 'A' && c2 == 'C') || (c1 == 'C' && c2 == 'A')) {
         return 110;
      }
      else if ((c1 == 'A' && c2 == 'G') || (c1 == 'G' && c2 == 'A')) {
         return 48;
      }
      else if ((c1 == 'A' && c2 == 'T') || (c1 == 'T' && c2 == 'A')) {
         return 94;
      }
      else if ((c1 == 'C' && c2 == 'G') || (c1 == 'G' && c2 == 'C')) {
         return 118;
      }
      else if ((c1 == 'C' && c2 == 'T') || (c1 == 'T' && c2 == 'C')) {
         return 48;
      }
      else if ((c1 == 'T' && c2 == 'G') || (c1 == 'G' && c2 == 'T')) {
         return 110;
      }
      else {    // c1 == c2
         return 0;
      }
   }

   public static int minCost(String s1, String s2) {
      int len1 = s1.length();
      int len2 = s2.length();

      int[][] memoMinCost = new int[len1+1][2];

      for (int i = 0; i <= len1; i++) {
         memoMinCost[i][0] = i * delta;
      }

      for (int j = 1; j <= len2; j++) {
         memoMinCost[0][1] = j * delta;
         for (int i = 1; i <= len1; i++) {
            memoMinCost[i][1] = Math.min(memoMinCost[i-1][0] + alpha(s1.charAt(i-1), s2.charAt(j-1)),
                    Math.min(memoMinCost[i-1][1], memoMinCost[i][0]) + delta);
         }
         for (int i = 0; i <= len1; i++) {
            memoMinCost[i][0] = memoMinCost[i][1];
         }
      }

      return memoMinCost[len1][1];
   }

   public static String[] divideAndConquerAlign(String x, String y) {
      if (x.isEmpty()) {
         StringBuilder match = new StringBuilder();
         for (int i = 0; i < y.length(); i++) {
            match.append('_');
         }
         return new String[]{match.toString(), y};
      }
      if (y.length() == 1) {
         return strCharAlign(x, y.charAt(0));
      }

      String yL = y.substring(0, y.length() / 2);
      String yR = y.substring(y.length() / 2);
      String xL;
      String xR;

      int min = Integer.MAX_VALUE;
      int cut = 0;

      for (int i = 0; i <= x.length(); i++) {
         xL = x.substring(0, i);
         xR = x.substring(i);
         if (minCost(xL, yL) + minCost(xR, yR) < min) {
            min = minCost(xL, yL) + minCost(xR, yR);
            cut = i;
         }
      }

      String[] alignL = divideAndConquerAlign(x.substring(0, cut), yL);
      String[] alignR = divideAndConquerAlign(x.substring(cut), yR);

      String[] ans = new String[2];
      ans[0] = alignL[0] + alignR[0];
      ans[1] = alignL[1] + alignR[1];

      return ans;
   }

   public static String[] strCharAlign(String s, char c) {
      String[] ans = new String[2];
      StringBuilder match = new StringBuilder();
      int pos = 0;
      int min = 2 * delta;
      for (int i = 0; i < s.length(); i++) {
         if (alpha(c, s.charAt(i)) < min) {
            min = alpha(c, s.charAt(i));
            pos = i;
         }
      }
      if (min < 2 * delta) {
         for (int i = 0; i < pos; i++) {
            match.append('_');
         }
         match.append(c);
         for (int i = pos+1; i < s.length(); i++) {
            match.append('_');
         }
      }
      else {
         for (int i = 0; i < s.length(); i++) {
            match.append('_');
         }
         match.append(c);
         s += "_";
      }
      ans[0] = s;
      ans[1] = match.toString();
      return ans;
   }

   private static double getTimeInMilliseconds() {
      return System.nanoTime() / 10e6;
   }

   private static double getMemoryInKB() {
      double total = Runtime.getRuntime().totalMemory();
      return (total - Runtime.getRuntime().freeMemory()) / 10e3;
   }

   public static ArrayList<String> inputString(String inputFile) {
      ArrayList<String> strList = new ArrayList<>();
      String base;
      ArrayList<Integer> index = new ArrayList<>();

      try (Scanner scanner = new Scanner(new File(inputFile))) {
         while (scanner.hasNext() && !scanner.hasNextInt()) {
            base = scanner.next();
            while (scanner.hasNextInt()) {
               index.add(scanner.nextInt());
            }
            strList.add(strGenerator(base, index));
            index.clear();
         }
      } catch (IOException e) {
         System.out.println("Input file not found.");
      }

      return strList;
   }

   public static String strGenerator(String base, ArrayList<Integer> index) {
      StringBuilder bs = new StringBuilder(base);
      for (int i = 0; i < index.size(); i++) {
         bs.insert(index.get(i) + 1, bs.toString());
      }
      return bs.toString();
   }

   private static int testCost(String s1, String s2) {
      if (s1.length() != s2.length()) {
         System.out.println("Error: not equal length.");
      }
      int total = 0;
      for (int i = 0; i < s1.length(); i++) {
         if (s1.charAt(i) == '_' || s2.charAt(i) == '_') {
            total += delta;
         }
         else {
            total += alpha(s1.charAt(i), s2.charAt(i));
         }
      }
      return total;
   }

   public static void main(String[] args) {
      String inputFile = args[0];
      String outputFile = args[1];

      ArrayList<String> inputStr = inputString(inputFile);

      double beforeUsedMem = getMemoryInKB();
      double startTime = getTimeInMilliseconds();

      int cost = minCost(inputStr.get(0), inputStr.get(1));
      String[] align = divideAndConquerAlign(inputStr.get(0), inputStr.get(1));
      //System.out.println(align[0]);
      //System.out.println(align[1]);
      //System.out.println("testCost: " + testCost(align[0], align[1]));

      double afterUsedMem = getMemoryInKB();
      double endTime = getTimeInMilliseconds();

      double totalUsage = afterUsedMem - beforeUsedMem;
      double totalTime = endTime - startTime;

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
         writer.write(Integer.toString(cost));
         writer.newLine();

         writer.write(align[0]);
         writer.newLine();
         writer.write(align[1]);
         writer.newLine();

         writer.write(Double.toString(totalTime));
         writer.newLine();
         writer.write(Double.toString(totalUsage));
         writer.newLine();
      } catch (IOException e) {
         System.out.println("Output file not found.");
      }
   }
}
