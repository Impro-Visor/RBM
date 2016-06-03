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
import java.util.List;
import java.util.Iterator;

/**

This class is the entry point for constructing Cluster Analysis objects.
Each instance of JCA object is associated with one or more clusters,
and a List of DataPoint objects. The JCA and DataPoint classes are
the only classes available from other packages.
@see DataPoint

**/
public class JCA {
    private Cluster[] clusters;
    private int miter;
    private List<DataPoint> mDataPoints;
    private double mSWCSS;

    public JCA(int k, int iter, List<DataPoint> dataPoints) {
        clusters = new Cluster[k];
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster("Cluster" + i);
        }
        miter = iter;
        mDataPoints = dataPoints;
    }

    private void calcSWCSS() {
        double temp = 0;
        for (Cluster c : clusters) {
            temp += c.getSumSqr();
        }
        mSWCSS = temp;
    }

    public void startAnalysis() {
        //set Starting centroid positions - Start of Step 1
        setInitialCentroids();
        Iterator<DataPoint> n = mDataPoints.iterator();
        //assign DataPoint to clusters
        loop1:
        while (true) {
            for (Cluster c : clusters)
            {
                c.addDataPoint(n.next());
                if (!n.hasNext())
                    break loop1;
            }
        }

        //calculate E for all the clusters
        calcSWCSS();

        //recalculate Cluster centroids - Start of Step 2
        for (Cluster c : clusters) {
            c.getCentroid().calcCentroid();
        }

        //recalculate E for all the clusters
        calcSWCSS();
        int i = 0;
        boolean convergence = false;
        Cluster[] prevClusters = new Cluster[this.clusters.length];

        // go until we converge or we iterate for too long, whichever comes first
        while(i < this.miter && !convergence) {

            // store the previous state so we can tell if we are making progress
            for(int prevIndex = 0; prevIndex < prevClusters.length; prevIndex++){
                prevClusters[prevIndex] = new Cluster(this.clusters[prevIndex]);
            }


            //enter the loop for cluster 1
            for (Cluster c : clusters) {
                for (Iterator<DataPoint> k = c.getDataPoints().iterator(); k.hasNext(); ) {
                    DataPoint dp = k.next();

                    //pick the first element of the first cluster
                    //get the current Euclidean distance
                    double tempEuDt = dp.getCurrentEuDt();
                    Cluster tempCluster = null;
                    boolean matchFoundFlag = false;

                    //call testEuclidean distance for all clusters
                    for (Cluster d : clusters) {

                        //if testEuclidean < currentEuclidean then
                        if (tempEuDt > dp.testEuclideanDistance(d.getCentroid())) {
                            tempEuDt = dp.testEuclideanDistance(d.getCentroid());
                            tempCluster = d;
                            matchFoundFlag = true;
                        }
                        //if statement - Check whether the Last EuDt is > Present EuDt

                    }
                    //for variable 'd' - Looping between different Clusters for matching a Data Point.
                    //add DataPoint to the cluster and calcSWCSS

                    if (matchFoundFlag) {
		        tempCluster.addDataPoint(dp);
		        k.remove();
                        for (Cluster d : clusters) {
                            d.getCentroid().calcCentroid();
                        }

                        //for variable 'd' - Recalculating centroids for all Clusters

                        calcSWCSS();
                    }

                    //if statement - A Data Point is eligible for transfer between Clusters.
                }
                //for variable 'k' - Looping through all Data Points of the current Cluster.
            }//for variable 'c' - Looping through all the Clusters.

            // check to see whether we have converged yet: if nothing has changed
            boolean changed = false;
            for(int prevIndex = 0; prevIndex < prevClusters.length; prevIndex++){
                if( ! prevClusters[prevIndex].equals(this.clusters[prevIndex]) ){
                    changed = true;
                }
            }

            convergence = !changed;
            i += 1;
        }//for variable 'i' - Number of iterations.

        System.out.println("Clustering took " + i + " iterations to converge");
    } // end of method startAnalysis

    
    public List<DataPoint>[] getClusterOutput() 
    {
        @SuppressWarnings("unchecked")
        List<DataPoint>[] v = new List[clusters.length];
        int i =0;
        for (Cluster c : clusters) {
            v[i] = c.getDataPoints();
            i++;
        }
        return v;
    }


    private void setInitialCentroids() {
        //kn = (round((max-min)/k)*n)+min where n is from 0 to (k-1).
        float[] cvec, maxvec, minvec;
        for (int n = 1; n <= clusters.length; n++) {

            maxvec = getMaxVector();
            minvec = getMinVector();
            cvec = new float[maxvec.length];

            for(int i = 0; i < maxvec.length; i++){
                cvec[i] = ( ((maxvec[i] - minvec[i]) / (clusters.length + 1)) * n ) + minvec[i];
            }

            Centroid c1 = new Centroid(cvec);
            clusters[n - 1].setCentroid(c1);
            c1.setCluster(clusters[n - 1]);
        }
    }


    private float[] getMaxVector(){
        float tempVector[] = new float[mDataPoints.get(0).getVector().length];
        System.arraycopy(mDataPoints.get(0).getVector(), 0, tempVector, 0, tempVector.length);

        for(DataPoint dp : this.mDataPoints){
            for(int i = 0; i < dp.getVector().length; i++){
                if(dp.getVector()[i] > tempVector[i]){
                    tempVector[i] = dp.getVector()[i];
                }
            }
        }

        return tempVector;
    }


    private float[] getMinVector(){
        float tempVector[] = new float[mDataPoints.get(0).getVector().length];
        System.arraycopy(mDataPoints.get(0).getVector(), 0, tempVector, 0, tempVector.length);

        for(DataPoint dp : this.mDataPoints){
            for(int i = 0; i < dp.getVector().length; i++){
                if(dp.getVector()[i] < tempVector[i]){
                    tempVector[i] = dp.getVector()[i];
                }
            }
        }

        return tempVector;
    }


    public int getKValue() {
        return clusters.length;
    }

    public int getIterations() {
        return miter;
    }

    public int getTotalDataPoints() {
        return mDataPoints.size();
    }

    public double getSWCSS() {
        return mSWCSS;
    }

    public Cluster getCluster(int pos) {
        return clusters[pos];
    }
}

