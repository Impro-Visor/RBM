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


package rbm;
import java.util.*;
import java.io.Serializable;
/**
 *
 * @author Sam Bosley
 */
public class RhythmCoercer implements Serializable {

    static final long serialVersionUID = 4357368253735438048L;

    private List<RhythmOccurrence> acceptableRhythms;
    private boolean usingHardcodedRhythms;

    public RhythmCoercer(boolean useHardcodedRhythms) {
        acceptableRhythms = new ArrayList<RhythmOccurrence>();
        this.usingHardcodedRhythms = useHardcodedRhythms;
        if (useHardcodedRhythms) {
            for (int i = 0; i < HARDCODED_RHYTHMS.length; i++) {
                acceptableRhythms.add(new RhythmOccurrence(HARDCODED_RHYTHMS[i]));
            }
        }
    }

    private static final int SLOTS_PER_BEAT = leadsheet.Constants.BEAT/leadsheet.Constants.RESOLUTION_SCALAR;
    //Note: This array assumes a resolution of 12 slots per beat
    private static final int[][] HARDCODED_RHYTHMS =
    {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //quarter
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, //2 8th
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0}, //4 16th
        {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0}, //3 8th triplet
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0}, //1 8th 2 16th
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0}, //2 16th 1 8th
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, //Dotted 8th 1 16th
        {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //1 16th dotted 8th
        {1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0}, //1 8th and turn
        {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0}, //turn and 1 8th
        {1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0}, //1 16th 18th 1 16th
        {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}, //2 turns
    };


    public void learnAcceptableRhythms(DataVessel[] data) {
        if (this.usingHardcodedRhythms) {
            System.err.println("This rhythm coercer uses hardcoded rhythms. \n" +
                    "No need to train on a data set.");
            return;
        }
        for (int i = 0; i < data.length; i++) {

            int melRows = data[i].getNumRows();
            int melCols = data[i].getNumCols();
            int numBeats = melRows / SLOTS_PER_BEAT;

            int[] melody = data[i].getMelody();

            for (int beat = 0; beat < numBeats; beat++) {
                int[] currRhythm = new int[SLOTS_PER_BEAT];
                for (int j = 0; j < currRhythm.length; j++) {
                    currRhythm[j] = melody[melCols * (beat * SLOTS_PER_BEAT + j)];
                }
                currRhythm[0] = 1;
                RhythmOccurrence temp = new RhythmOccurrence(currRhythm);
                if (acceptableRhythms.contains(temp)) {
                    acceptableRhythms.get(acceptableRhythms.indexOf(temp)).incrementOccurrences();
                } else {
                    acceptableRhythms.add(new RhythmOccurrence(currRhythm));
                }
            }
        }
    }

    public void printAcceptableRhythms() {
        for (RhythmOccurrence curr : acceptableRhythms) {
            System.out.println(curr);
        }
    }

    /**
     *
     * @param rhythm - an int[] of length 12 representing the series of attack
     * bits in one beat of melody
     * @return - a rhythm from the list of acceptable rhythms that is closest
     * to the given rhythm based on Levenshtein Distance. If more than one
     * equidistant choices exist, then one is chosen randomly.
     */
    public int[] getBestCoercion(int[] rhythm) {

        boolean resetIndexZero = false;
        if (rhythm[0] == 0) {
            rhythm[0] = 1;
            resetIndexZero = true;
        }

        int[] distances = getDistances(rhythm);
        int[] sortedDistances = createSortedCopy(distances);
        int minDistance = sortedDistances[0];

        if (minDistance == 0) {
            if (resetIndexZero) {
                rhythm[0] = 0;
            }
            return rhythm;
        }
        else {
            ArrayList<RhythmOccurrence> possibleCoercions = new ArrayList<RhythmOccurrence>();
            for (int i = 0; i < distances.length; i++) {
                if (Math.abs(distances[i]-minDistance) <=2) {
                    possibleCoercions.add(acceptableRhythms.get(i));
                }
            }

            int[] toReturn = possibleCoercions.get(possibleCoercions.size() - 1).rhythm;
            if (possibleCoercions.size() > 1) {
                float[] occurrences = new float[possibleCoercions.size()];
                for (int i = 0; i < possibleCoercions.size(); i++) {
                    occurrences[i] = (possibleCoercions.get(i).occurrences) /
                            computeLevenshteinDistance(possibleCoercions.get(i).rhythm, rhythm);
                }

                normalizeProbabilities(occurrences);

                Random rand = new Random();
                float prob = rand.nextFloat();
                for (int i = 0; i < occurrences.length; i++) {
                    if (prob < occurrences[i]) {
                        toReturn = possibleCoercions.get(i).rhythm;
                        break;
                    }
                }
            }

            if (resetIndexZero) {
                toReturn[0] = 0;
            }
            return toReturn;
        }

    }

    private static void normalizeProbabilities(float[] probabilities) {
        float sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
        }
        //Normalize probabilities by dividing by sum
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = probabilities[i]/sum;
        }
        float lastValue = 0;
        //Create distribution - distance between each value in the array will
        //correspond to that bit's likelihood of being picked.
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] += lastValue;
            lastValue = probabilities[i];
        }
    }

    /**
     *
     * @param data - the data vessel to be changed. This function will modify
     * the melody bits by coercing the attack bits of each beat to a recognized
     * rhythm.
     */
    public void coerceDataVesselRhythms(DataVessel data) {
        int melRows = data.getNumRows();
        int melCols = data.getNumCols();
        int numBeats = melRows/SLOTS_PER_BEAT;

        int[] melody = data.getMelody();

        for(int beat = 0; beat < numBeats; beat++) {
            int[] currRhythm = new int[SLOTS_PER_BEAT];
            for (int i = 0; i < currRhythm.length; i++) {
                currRhythm[i] = melody[melCols*(beat*SLOTS_PER_BEAT + i)];
            }
            int[] coercedRhythm = getBestCoercion(currRhythm);
            for (int i = 0; i < currRhythm.length; i++) {
                melody[melCols*(beat*SLOTS_PER_BEAT + i)] = coercedRhythm[i];
            }
        }
    }



    //Creates a deep copy of an int[]
    private static int[] createDeepCopy(int[] arr) {
        int[] newArr = new int[arr.length];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        return newArr;
    }

    //Creates a sorted deep copy of an int[]
    private static int[] createSortedCopy(int[] arr) {
        int[] sortedArr = createDeepCopy(arr);
        Arrays.sort(sortedArr);
        return sortedArr;
    }

    //Computes the Levenshtein distance between the given rhythm and all acceptable rhythms
    private int[] getDistances(int[] rhythm) {
        int[] distances = new int[acceptableRhythms.size()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = computeLevenshteinDistance(rhythm, acceptableRhythms.get(i).rhythm);
        }
        return distances;
    }


    //Functions for computing Levenshtein distance found at
    //http://en.wikibooks.org/wiki/Algorithm_implementation/Strings/Levenshtein_distance
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private static int computeLevenshteinDistance(int[] arr1, int[] arr2) {
        int[][] distance = new int[arr1.length + 1][arr2.length + 1];

        for (int i = 0; i <= arr1.length; i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= arr2.length; j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= arr1.length; i++) {
            for (int j = 1; j <= arr2.length; j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((arr1[i - 1] == arr2[j - 1]) ? 0
                        : 1));
            }
        }

        return distance[arr1.length][arr2.length];
    }

    class RhythmOccurrence implements Serializable {
        private int occurrences;
        private int[] rhythm;

        public RhythmOccurrence(int[] rhythm) {
            this.rhythm = rhythm;
            this.occurrences = 1;
        }

        public void incrementOccurrences() {
            occurrences++;
        }

        @Override
        public boolean equals(Object obj) {
            if (!((obj instanceof RhythmOccurrence) || (obj instanceof int[]))) return false;
            int[] toCompare;
            if (obj instanceof RhythmOccurrence) {
                toCompare = ((RhythmOccurrence)obj).rhythm;
            } else {
                toCompare = (int[])obj;
            }

            if (this.rhythm.length != toCompare.length) return false;
            for (int i = 0; i < this.rhythm.length; i++) {
                if (rhythm[i] != toCompare[i]) return false;
            }
            return true;
        }

        @Override
        public String toString() {
            String str = "Rhythm:";
            for (int i = 0; i < rhythm.length; i++) {
                str += " "+rhythm[i]+",";
            }
            str += (" Occurrences: "+occurrences);
            return str;
        }
    }

}
