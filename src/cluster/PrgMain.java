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


package cluster;

/**
 * This is a main class for testing the functionality of the cluster package.
 *
 *
 * @author Peter Swire
 */
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: shyam.s
 * Date: Apr 18, 2004
 * Time: 4:26:06 PM
 */



public class PrgMain {
    public static void main (String args[]){
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        dataPoints.add(new DataPoint(new float[]{22,21},1));
        dataPoints.add(new DataPoint(new float[]{2,70}, 2));
        dataPoints.add(new DataPoint(new float[]{23,71},3));
        dataPoints.add(new DataPoint(new float[]{4,21}, 4));
        dataPoints.add(new DataPoint(new float[]{22,21},5));

        JCA jca = new JCA(2,1000,dataPoints);
        jca.startAnalysis();

        System.out.println(jca.getCluster(0));
        System.out.println(jca.getCluster(1));

        for (List<DataPoint> tempV : jca.getClusterOutput()){
            //System.out.println(tempV);
            for (DataPoint dpTemp : tempV){
                //System.out.println(dpTemp);
            }
        }
    }
}