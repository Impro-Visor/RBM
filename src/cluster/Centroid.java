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
 *
 * @author Peter Swire
 */

/**
 * This class represents the Centroid for a Cluster. The initial centroid is calculated
 * using a equation which divides the sample space for each dimension into equal parts
 * depending upon the value of k.
 * @author Original author: Shyam Sivaraman
 * @version 1.0
 * @see Cluster
 */

public class Centroid {
    private float[] mVector;
    private Cluster mCluster;

    public Centroid(float[] vec) {
        this.mVector = vec;
    }

    // copy constructor
    public Centroid(Centroid copy){
        int length = copy.getMVector().length;
        this.mVector = new float[length];
        System.arraycopy(copy.getMVector(), 0 , this.mVector, 0, length);

        this.mCluster = copy.getCluster(); // a shallow copy
    }

    public void calcCentroid() { //only called by CAInstance
        int numDP = mCluster.getNumDataPoints();
        float[] tempVector = new float[this.mVector.length];

        //calculating the new Centroid
        for (DataPoint dp : mCluster.getDataPoints()) {
            for(int i = 0; i < dp.getVector().length; i++){
                tempVector[i] += dp.getVector()[i];
            }
        }


        for(int i = 0; i < tempVector.length; i++){
            this.mVector[i] = tempVector[i] / numDP;
        }

        //calculating the new Euclidean Distance for each Data Point
        for (DataPoint dp : mCluster.getDataPoints()) {
            dp.calcEuclideanDistance();
        }
        //calculate the new Sum of Squares for the Cluster
        mCluster.calcSumOfSquares();
    }

    public void setCluster(Cluster c) {
        mCluster = c;
    }

    public float[] getMVector(){
        return this.mVector;
    }

    public Cluster getCluster() {
        return mCluster;
    }

}  // end of class Centroid
