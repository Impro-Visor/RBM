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


package leadsheet;
import javax.swing.*;
import java.io.File;
/**
 *
 * @author Sam Bosley
 */
public class TestLSParsing {
    public static void main(String[] args) {
    JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
                return f.getName().endsWith(".ls");
            }

        public String getDescription() {
            return "Lick files";
        }

     });

     int status = fc.showOpenDialog(null);
     if (status == JFileChooser.APPROVE_OPTION) {
         File selected = fc.getSelectedFile();
         int[] melody = LeadSheetHandler.parseLeadSheetMelody(selected);
         int[] chords = LeadSheetHandler.parseLeadSheetChords(selected);
         for (int i = 0; i < melody.length; i++) {
             if (i % 50 == 0) System.out.println();
             System.out.print(melody[i]+" ");
         }
         System.out.println();
         for (int i = 0; i < chords.length; i++) {
             if (i % 12 == 0) System.out.println();
             System.out.print(chords[i]+" ");
         }

     }


    }


}
