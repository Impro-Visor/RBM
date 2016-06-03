/**
 * This Java Class is part of the RBM-provisor Application
 * which, in turn, is part of the Intelligent Music Software
 * project at Harvey Mudd College, under the directorship of Robert Keller.
 *
 * Copyright (C) 2009 Robert Keller and Harvey Mudd College
 *
 * RBM-provisor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * RBM-provisor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RBM-provisor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package analysis;

import java.io.*;
import java.util.*;
import javax.swing.*;
import rbm.*;

/**
 *
 * @author Greg Bickerman
 */
public class IntervalAnalyzer {

    public static void writeAllInfoToFile(File[] toAnalyze, String outfilename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfilename));
            out.write("Filename, Average interval, Maximum Interval, Minimum Interval, Repeated Notes");
            out.newLine();
            for (File f: toAnalyze) {
                float[] values = getIntervalInfo(f);
                out.write(f.getName() + ", " + values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3]);
                out.newLine();
            }

            out.close();
        } catch (IOException ignored) {}
    }

    private static float[] getIntervalInfo(File f) {
        return getIntervalInfo(f.getAbsolutePath());
    }

    private static float[] getIntervalInfo(String filename) {
        float[] values = new float[4];

        int[] beat = new int[18];
        ArrayList<Integer> intervals = new ArrayList<Integer>();

        DataVessel dv = FileParser.parseFile(filename);
        System.arraycopy(dv.getMelody(), 0, beat, 0, beat.length);
        int pitchNum = getPitchNum(beat);
        for (int i=1; i<dv.getNumRows(); ++i) {
            System.arraycopy(dv.getMelody(), i*beat.length, beat, 0, beat.length);
            int currPitchNum = getPitchNum(beat);
            if (currPitchNum < 1) {
                continue;
            } else if (pitchNum < 1) {
                pitchNum = currPitchNum;
                continue;
            } else {
                int interval = Math.abs(currPitchNum-pitchNum);
                intervals.add(interval);
                pitchNum = currPitchNum;
            }

        }

        System.out.println(intervals);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int numRepeats = 0;
        int total = 0;
        for (int i:intervals) {
            if (i<min)
                min = i;
            if (i>max)
                max = i;
            if (i==0)
                numRepeats++;
            total += i;
        }
        float avg = ((float)total)/intervals.size();
        values[0] = avg;
        values[1] = max;
        values[2] = min;
        values[3] = numRepeats;
        System.out.println("Average interval: "+avg);
        System.out.println("Maximum interval: "+max);
        System.out.println("Minimum interval: "+min);
        System.out.println("Repeated Notes:   "+numRepeats);
        return values;
    }

    private static int getPitchNum(int[] beat) {
        if (beat[0] == 1)
            return -1;

        else if (beat[1] == 1)
            return 0;

        else {
            int note = 0;
            while (beat[note+2] != 1) {
                ++note;
            }

            int octave = 0;
            while (beat[octave+14] != 1) {
                ++octave;
            }

            return octave*12+note;
        }
    }

    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int status = chooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            File[] toAnalyze = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ls");
                }
            });
            String newFilename = dir.getAbsolutePath()+File.separatorChar+"IntervalAnalysis.csv";
            writeAllInfoToFile(toAnalyze, newFilename);
        }
    }
}
