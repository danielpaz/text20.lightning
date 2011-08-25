package de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator;

import static net.jcores.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class LogEvaluator {

    /** singleton instance */
    private static LogEvaluator main;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("LogConverter started...");
        System.out.println();

        // run through all files
        for (File file : new File("evaluation/results/text detector evaluation").listFiles()) {

            // build path
            String path = file.getAbsolutePath();

            // check if file exists
            if (!(new File(path + "/DetectorEvaluation.txt").exists())) continue;

            LogEvaluator.getInstance().evaluateLog(path + "/DetectorEvaluation.txt", true);
        }
    }

    /**
     * @return instance
     */
    private static LogEvaluator getInstance() {
        if (main == null) main = new LogEvaluator();
        return main;
    }

    /**
     * @param path
     * @param bigSteps
     */
    @SuppressWarnings("boxing")
    public void evaluateLog(String path, boolean bigSteps) {

        try {
            // indicate file
            System.out.println("next file: " + new File(path).getAbsolutePath());
            System.out.println();

            $(path.replace("DetectorEvaluation.txt", "Evaluated.txt")).file().delete();
            $(path.replace("DetectorEvaluation.txt", "EvaluatedKeys.log")).file().delete();
            $(path.replace("DetectorEvaluation.txt", "EvaluatedKeys.log")).file().append("- headings -\r\n");
            $(path.replace("DetectorEvaluation.txt", "EvaluatedKeys.log")).file().append("coverage, height, width, sensitivity, line, hits, overall, percentage");

            // initialize reader
            BufferedReader lineReader = new BufferedReader(new FileReader(path));
            String[] line = null;
            String key;
            ArrayList<Integer> cov = new ArrayList<Integer>();
            ArrayList<Integer> hei = new ArrayList<Integer>();
            ArrayList<Integer> wid = new ArrayList<Integer>();
            ArrayList<Double> sen = new ArrayList<Double>();
            ArrayList<Integer> lin = new ArrayList<Integer>();
            int coverage;
            int height;
            int width;
            double sensitivity;
            int lineSize;
            int hit;
            HashMap<String, long[]> values = new HashMap<String, long[]>();

            int D = 0;
            
            // read
            String[] lines = $(path).file().text().split("\n").array(String.class);
            for(String _line: lines) {
                // read line
                line = _line.split(",");
                
                // parse variables
                coverage = Integer.parseInt(line[4]);
                height = Integer.parseInt(line[5]);
                width = Integer.parseInt(line[6]);
                sensitivity = Double.parseDouble(line[7]);
                lineSize = Integer.parseInt(line[8]);
                hit = Integer.parseInt(line[10]);
                
                if(!cov.contains(coverage)) cov.add(coverage);
                if(!hei.contains(height))hei.add(height);
                if(!wid.contains(width))wid.add(width);
                if(!sen.contains(sensitivity))sen.add(sensitivity);
                if(!lin.contains(lineSize))lin.add(lineSize);

                // add values
                key = $(coverage, height, width, sensitivity, lineSize).string().join(",");
                if (!values.containsKey(key)) {
                    values.put(key, new long[2]);
                }
                
                D++;
                
                values.get(key)[0] = values.get(key)[0] + hit;
                values.get(key)[1]++;
            }

            System.out.println("LBALAODASLDKJASLDKHASLK");
            System.out.println(D);
            System.out.println();
            System.out.println(values.keySet().size());
            // write evaluation
            for (String curKey : values.keySet()) {
                $(path.replace("DetectorEvaluation.txt", "Evaluated.txt")).file().append(curKey + "," + values.get(curKey)[0] + "," + values.get(curKey)[1] + "," + (((double) values.get(curKey)[0] / values.get(curKey)[1]) * 100) + "\r\n");
            }

            System.out.println("cov " + cov.size());
            for(int tmp: cov) System.out.print(tmp+ " ");
            System.out.println();
            System.out.println("hei " + hei.size());
            for(int tmp: hei) System.out.print(tmp+ " ");
            System.out.println();
            System.out.println("wid " + wid.size());
            for(int tmp: wid) System.out.print(tmp+ " ");
            System.out.println();
            System.out.println("sen " + sen.size());
            for(double tmp: sen) System.out.print(tmp+ " ");
            System.out.println();
            System.out.println("lin " + lin.size());
            for(int tmp: lin) System.out.print(tmp+ " ");
            System.out.println();
            
            // inidicate finish
            System.out.println();
            System.out.println("Done.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
