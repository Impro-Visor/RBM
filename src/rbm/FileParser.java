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

/**
 * FileParser
 *
 * @author Sam Bosley
 *
 * Contains code for reading data from file and writing data to file
 *
 */
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class FileParser {

    /**
     *
     * @param filename - the file to parse
     * @return a DataVessel containing data from the parsed file
     */
    public static DataVessel parseFile(String filename) {
        if (getExtension(filename).equals(".lick")) {
            return parseLickFile(filename);
        } else if (getExtension(filename).equals(".ls")) {
            return leadsheet.LeadSheetHandler.parseLeadSheetLick(filename);
        }
        else
        {
            throw new LeadSheetParseException();
        }
    }

    /**
     *
     * @param file - the file to parse
     * @return a DataVessel containing data from the parsed file
     */
    public static DataVessel parseFile(File file) {
        return parseFile(file.getAbsolutePath());
    }

    /**
     * 
     * @param filename - the name of the .lick file to parse lick data from
     * @return a DataVessel containing the data from the .lick file
     */
    public static DataVessel parseLickFile(String filename) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            //get sizes and initialize array
            int numRows = Integer.parseInt(in.readLine());
            int numCols = Integer.parseInt(in.readLine());

            int[] melody = new int[numRows * numCols];
            int[] chords = new int[numRows * Params.NUM_CHORD_COLUMNS];

            for (int row = 0; row < numRows; ++row) {
                String[] currLine = in.readLine().split(" ");
                for (int col = 0; col < currLine.length; ++col) {
                    melody[row * numCols + col] = Integer.parseInt(currLine[col]);
                }
            }
            for (int row = 0; row < numRows; ++row) {
                String[] currLine = in.readLine().split(" ");
                for (int col = 0; col < currLine.length; ++col) {
                    chords[row * Params.NUM_CHORD_COLUMNS + col] = Integer.parseInt(currLine[col]);
                }
            }
            in.close();
            return new DataVessel(melody, chords, numRows, numCols);
        } catch (IOException e) {
            System.err.println("Could not read file " + filename);
            return null;
        }
    }

    /**
     *
     * @param file - the .lick file to parse lick data from
     * @return a DataVessel containing the data from the .lick file
     */
    public static DataVessel parseLickFile(File file) {
        return parseLickFile(file.getAbsolutePath());
    }

    /**
     *
     * parseAllFiles
     *
     * @param files - an array of files to read melodies and their chords from
     * returns - an array of DataVessels, each one of which contains all melody
     * and chord data from the specified file.
     *
     */
    public static DataVessel[] parseAllFiles(File[] files) {
        DataVessel[] licks = new DataVessel[files.length];
        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            licks[fileIndex] = parseFile(files[fileIndex]);
        }
        return licks;
    }

    /**
     *
     * @param filename - the name of the .lick file to parse melody data from
     * @return - an int[] of melody data from the .lick file
     */
    public static int[] parseMelodyFromLickFile(String filename) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            int melRows = Integer.parseInt(in.readLine());
            int melCols = Integer.parseInt(in.readLine());

            int[] output = new int[melRows * melCols];

            for (int row = 0; row < melRows; row++) {
                String[] currLine = in.readLine().split(" ");
                for (int col = 0; col < melCols; col++) {
                    output[row * melCols + col] = Integer.parseInt(currLine[col]);
                }
            }

            in.close();
            return output;

        } catch (IOException e) {
            System.err.println("Could not read file: " + filename);
            return null;
        }
    }

    /**
     *
     * @param file - the .lick file to parse melody data from
     * @return - an int[] of melody data from the .lick file
     */
    public static int[] parseMelodyFromLickFile(File file) {
        return parseMelodyFromLickFile(file.getAbsolutePath());
    }

    /**
     *
     * @param filename - the .lick file to parse chords from
     * @return an array of chord data parsed from the file (skipping over the melody bits)
     */
    public static int[] parseChordFromLickFile(String filename) {
        try {
            //get array sizes and construct array
            BufferedReader in = new BufferedReader(new FileReader(filename));
            int numRows = Integer.parseInt(in.readLine());
            int numCols = Params.NUM_CHORD_COLUMNS;
            int[] chords = new int[numRows * numCols];

            //skip next line (don't care about number of melody columns),
            //as well as all melody lines
            for (int i = 0; i < numRows + 1; ++i) {
                in.readLine();
            }

            //parse chord array
            for (int i = 0; i < numRows; ++i) {
                String[] currLine = in.readLine().split(" ");
                for (int j = 0; j < currLine.length; ++j) {
                    chords[i * numCols + j] = Integer.parseInt(currLine[j]);
                }
            }
            return chords;

        } catch (IOException e) {
            System.err.println("Could not read file: " + filename);
            return null;
        }
    }

    /**
     *
     * @param file - the .lick file to parse chords from
     * @return an array of chord data parsed from the file (skipping over the melody bits)
     */
    public static int[] parseChordFromLickFile(File file) {
        return parseChordFromLickFile(file.getAbsolutePath());
    }

    /**
     *
     * @param files - array of files to parse into windows of inputs
     * @param windowLength - the length of each window (i.e. number of rows per window)
     * @param stepSize - the number of rows to move the window forward by each step
     * @return - an array of DataVessels. Each DataVessel in the array contains
     * one window's worth of data.
     */
    public static DataVessel[] parseDataVesselsToWindows(DataVessel[] dataVessels, int windowLength, int stepSize) {
        ArrayList<DataVessel> allWindows = new ArrayList<DataVessel>();

        for (DataVessel dv:dataVessels) {
            int[] currMelody = dv.getMelody();
            int[] currChords = dv.getChords();
            int melRows = dv.getNumRows();
            int melCols = dv.getNumCols();
            int numChordRows = currChords.length / Params.NUM_CHORD_COLUMNS;

            if (numChordRows != melRows) {
                int[] temp = new int[melRows * Params.NUM_CHORD_COLUMNS];
                for (int i = 0; i < temp.length / currChords.length; i++) {
                    System.arraycopy(currChords, 0, temp, i*currChords.length, currChords.length);
                }
                System.arraycopy(currChords, 0, temp,
                        (temp.length / currChords.length)*currChords.length, temp.length % currChords.length);
                currChords = temp;
            }

            int numWindows = ((melRows - windowLength) / stepSize) + 1;

            for (int i = 0; i < numWindows; i++) {
                int[] currWindowMelody = new int[windowLength * melCols];
                int[] currWindowChords = new int[windowLength * Params.NUM_CHORD_COLUMNS];

                System.arraycopy(currMelody, i * stepSize * melCols,
                        currWindowMelody, 0, windowLength * melCols);

                System.arraycopy(currChords, i * stepSize * Params.NUM_CHORD_COLUMNS,
                        currWindowChords, 0, windowLength * Params.NUM_CHORD_COLUMNS);

                DataVessel newWindow = new DataVessel(currWindowMelody, currWindowChords, windowLength, melCols);
                allWindows.add(newWindow);
            }

        }
        DataVessel[] output = new DataVessel[allWindows.size()];
        return allWindows.toArray(output);
    }


    /**
     * parseAllFilesToMelodies
     *
     * @param files
     * @return - an array of DataVessels with null chord arrays
     */
    /*
    public static DataVessel[] parseAllFilesToMelodies(File[] files) {
        DataVessel[] melodies = new DataVessel[files.length];
        for (int i = 0; i < files.length; i++) {
            melodies[i] = parseFileToMelody(files[i]);
        }
        return melodies;
    }//*/

    /**
     * parseFileToMelody
     * @param file
     * @return - a single DataVessel with a null chord array
     * 
     */
    /*
    public static DataVessel parseFileToMelody(File file) {
        int[] melody = null;
        int numRows = 0;
        int numCols = 0;
        if (getExtension(file).equals(".lick")) {
            melody = parseMelodyFromLickFile(file);
            numRows = getNumRowsFromLickFile(file);
            numCols = melody.length/numRows;
        } else if (getExtension(file).equals(".ls")) {
            melody = leadsheet.LeadSheetHandler.parseLeadSheetMelody(file);
            numRows = melody.length/Params.NUM_LEADSHEET_MELODY_COLUMNS;
            numCols = Params.NUM_LEADSHEET_MELODY_COLUMNS;
        }
        return new DataVessel(melody, null, numRows, numCols);
        
    }//*/

    //Gets the number of rows from a .lick file
    /*private static int getNumRowsFromLickFile(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            int numRows = Integer.parseInt(in.readLine());
            in.close();
            return numRows;
        } catch (IOException e) {
            System.err.println("Error reading file "+file.getAbsolutePath());
            return 0;
        }
    }//*/

    /*
    public static DataVessel[] parseAllFilesToWindowedMelodies(File[] files, int windowLength, int stepSize) {
        ArrayList<DataVessel> allWindows = new ArrayList<DataVessel>();
        String extension = getExtension(files[0]);
        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            int[] currMelody = null;
            int melRows = 0;
            int melCols = 0;

            if (extension.equals(".lick")) {
                currMelody = parseMelodyFromLickFile(files[fileIndex]);
                melRows = getNumRowsFromLickFile(files[fileIndex]);
                melCols = currMelody.length/melRows;
            } else if (extension.equals(".ls")) {
                currMelody = leadsheet.LeadSheetHandler.parseLeadSheetMelody(files[fileIndex]);
                melRows = currMelody.length/Params.NUM_LEADSHEET_MELODY_COLUMNS;
                melCols = currMelody.length/melRows;
            }

            int numWindows = ((melRows - windowLength)/stepSize) + 1;

            for (int i = 0; i < numWindows; i++) {
                int[] currWindowMelody = new int[windowLength*melCols];
                System.arraycopy(currMelody, i*stepSize*melCols,
                        currWindowMelody, 0, windowLength*melCols);

                DataVessel newWindow = new DataVessel(currWindowMelody, null, windowLength, melCols);
                allWindows.add(newWindow);
            }
        }
        DataVessel[] output = new DataVessel[allWindows.size()];
        return allWindows.toArray(output);

    }//*/


    /**
     * writeArrayToStream
     * @param data - integer array to be written to file
     * @param rows - number of rows to break array into
     * @param outstream - the BufferedWriter (i.e. open file writer) to write the array to
     *
     * Writes an integer array to an open BufferedWriter
     *
     */
    
    private static void writeArrayToStream(int[] data, int rows, BufferedWriter out) {
        if (data.length > 0) {
            try {
                int cols = data.length / rows;
                for (int i = 0; i < rows; ++i) {
                    out.write(data[i * cols] + "");
                    for (int j = 1; j < (cols); ++j) {
                        out.write(" " + data[i * cols + j]);
                    }
                    out.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error writing to file");
            }
        }
    }

    /**
     *
     * @param output - the DataVessel to write to file
     * @param filename - the name of the file to write to
     *
     * Writes a DataVessel to file
     */
    public static void writeDataVesselToFile(DataVessel output, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            int melodyRows = output.getNumRows();
            int melodyCols = output.getNumCols();
            //write arrays sizes at top of file
            out.write(melodyRows + "");
            out.newLine();
            out.write(melodyCols + "");
            out.newLine();

            //write melody and chord arrays to file
            int[] melody = output.getMelody();
            int[] chords = output.getChords();
            if (melody != null) {
                writeArrayToStream(output.getMelody(), melodyRows, out);
            }
            if (chords != null) {
                writeArrayToStream(output.getChords(), melodyRows, out);
            }
            out.close();
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename);
        }
    }

    /**
     *
     * @param output - the DataVessel to write to file
     * @param file - the file to write to
     *
     * Writes a DataVessel to file
     */
    public static void writeDataVesselToFile(DataVessel output, File file) {
        writeDataVesselToFile(output, file.getAbsolutePath());
    }

    /**
     *
     * @param output - the DataVessel to write in leadsheet format
     * @param rowsPerBeat - the number of rows per beat in the DataVessel (i.e. resolution)
     * @param filename - the name of the file to write out to
     */
    public static void writeDataVesselToLeadsheet(DataVessel output, int rowsPerBeat, String filename) {
        leadsheet.LeadSheetHandler.writeLeadSheet(output, rowsPerBeat, filename);
    }

    /**
     *
     * @param output - the DataVessel to write in leadsheet format
     * @param rowsPerBeat - the number of rows per beat in the DataVessel (i.e. resolution)
     * @param file - the file to write out to
     */
    public static void writeDataVesselToLeadsheet(DataVessel output, int rowsPerBeat, File file) {
        writeDataVesselToLeadsheet(output, rowsPerBeat, file.getAbsolutePath());
    }

    /**
     *
     * @param obj - the object to be written to a compressed file.
     * Usually a MusicBrain object
     * for the purposes of our code.
     * @param filename - the name of the file to write to
     */
    public static void writeObject(Serializable obj, String filename) {
        try {
            ObjectOutputStream objout = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)));
            objout.writeObject(obj);
            objout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param obj - the object to be written to file. Usually a MusicBrain object
     * for the purposes of our code.
     * @param file - the file to write to
     */
    public static void writeObject(Serializable obj, File file) {
        writeObject(obj, file.getAbsolutePath());
    }

    /**
     *
     * @param outImg - the BufferedImage to write to file
     * @param filename - the name of the file to write to
     */
    public static void writeImage(BufferedImage outImg, String filename){
        filename += ".png";
        try {
            File outFile = new File(filename);
            // the second argument, "png", is the file format, not the extension
            ImageIO.write(outImg, "png", outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end of method writeImage


    /**
     *
     * @param filename - the name of the file to read the compressed object from.
     * @return - the object read from the file. Usually a MusicBrain object (null if
     * something goes wrong).
     */
    public static Object readObject(String filename) {
        try {
            ObjectInputStream objin = new ObjectInputStream(new GZIPInputStream(new FileInputStream(filename)));
            Object obj = objin.readObject();
            objin.close();
            return obj;
        } catch (Exception e) {
            // don't attept to recover the object
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param filename - the file to read the compressed object from.
     * @return - the object read from the file. Usually a MusicBrain object (null if
     * something goes wrong).
     */
    public static Object readObject(File file) {
        return readObject(file.getAbsolutePath());
    }

    /**
     *
     * @param filename
     * @return - the extension of the specified file
     */
    public static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index != -1)
            return filename.substring(index);
        else return "";
    }

    /**
     *
     * @param file
     * @return - the extension of the specified file
     */
    public static String getExtension(File file) {
        return getExtension(file.getAbsolutePath());
    }

    /**
     *
     * @param melodyArrays the melodies to be transposed.
     * @param numMelodyCols the number of columns in a melody.
     * @return - a 2D int array containing all 12 transpositions of the given melody array
     */
    public static int[][] transposeMelodyArray(int[] melody, int numMelodyCols) {
        final int NUM_PITCHES = 12;
        final int NUM_OCTAVES = 4;
        final int OCTAVE_OFFSET = 14;
        int numMelodyRows = melody.length/numMelodyCols;
        int[][] newMelodyArray = new int[12][];

            for (int transpos = 0; transpos < NUM_PITCHES; transpos++) {

                int[] newMelody = new int[melody.length];

                for (int row = 0; row < numMelodyRows; row++) {
                    int rowIndex = row*numMelodyCols;

                    if (melody[rowIndex] == 1) { //if original note is sustained
                        newMelody[rowIndex] = 1; //transposed note is also sustained
                        System.arraycopy(newMelody, rowIndex-NUM_OCTAVES, newMelody, (rowIndex+OCTAVE_OFFSET), NUM_OCTAVES); //and octave is sustained as well
                    }
                    else if (melody[rowIndex+1] == 1) { //if original note is a rest
                        newMelody[rowIndex+1] = 1;      //transposed note is also a rest
                        newMelody[rowIndex+OCTAVE_OFFSET+1] = 1;     //and default octave (1) is selected
                    }
                    else {  //original note is an actual pitch

                        boolean raiseOctave = false; //note whether transposition goes into new octave

                        //transpose note bit
                        for (int noteBit = 0; noteBit<NUM_PITCHES; ++noteBit) {
                            if (melody[rowIndex+2+noteBit] == 1) {
                                if (noteBit + transpos < NUM_PITCHES) { //same octave
                                    newMelody[rowIndex+2+noteBit+transpos] = 1;
                                }
                                else { //higher octave
                                    newMelody[rowIndex+2+noteBit+transpos-12] = 1;
                                    raiseOctave = true;
                                }
                                break;
                            }
                        }

                        //transpose octave bit
                        int octaveIndex = rowIndex+OCTAVE_OFFSET;
                        for (int octaveBit = 0; octaveBit<NUM_OCTAVES; ++octaveBit) {
                            if (melody[octaveIndex+octaveBit] == 1) {
                                
                                //if the note went up an octave, and there is a higher octave to transpose into...
                                if (octaveBit < NUM_OCTAVES-1 && raiseOctave)
                                    ++octaveBit;
                                newMelody[octaveIndex+octaveBit] = 1;
                                break;
                            }
                        }
                    }
                }
                newMelodyArray[transpos] = newMelody;
            }

        return newMelodyArray;
    } // end of method transposeMelodyArrays

    /**
     *
     * @param chordArray - the chords to be transposed.
     * @return - a 2D int array containing all 12 transpositions of the given chord array
     */
    public static int[][] transposeChordArray(int[] chordArray) {
        int numRows = chordArray.length/Params.NUM_CHORD_COLUMNS;
        int[][] newChordArray = new int[12][];

        for (int transpos = 0; transpos < 12; transpos++) {

            int[] newChord = new int[numRows * Params.NUM_CHORD_COLUMNS];

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < Params.NUM_CHORD_COLUMNS; col++) {
                    if (chordArray[row * Params.NUM_CHORD_COLUMNS + col] == 1) {
                        newChord[row * Params.NUM_CHORD_COLUMNS + ((col + transpos) % Params.NUM_CHORD_COLUMNS)] = 1;
                    }
                }
            }
            newChordArray[transpos] = newChord;
        }
        return newChordArray;
    }

    /**
     *
     * @param data - an array of DataVessels to transpose into all keys
     * @return - a larger array of DataVessels containing all 12 transpositions
     * of each of the original data
     */
    public static DataVessel[] transposeDataVessels(DataVessel[] data) {
        ArrayList<DataVessel> output = new ArrayList<DataVessel>();
        boolean chordsPresent;
        int[][] transposedMelody;
        int[][] transposedChords = null;
        int numRows;
        int numCols;

        DataVessel temp;

        for (DataVessel dv : data) {
            numRows = dv.getNumRows();
            numCols = dv.getNumCols();
            chordsPresent = (dv.getChords()!=null);

            transposedMelody = transposeMelodyArray(dv.getMelody(), numCols);
            if (chordsPresent)
                transposedChords = transposeChordArray(dv.getChords());

            for(int i = 0; i < transposedMelody.length; i++){
                if (chordsPresent)
                    temp = new DataVessel(transposedMelody[i], transposedChords[i], numRows, numCols);
                else
                    temp = new DataVessel(transposedMelody[i], null, numRows, numCols);
                output.add(temp);
            }
        }

        return output.toArray(new DataVessel[0]);
    }
}
