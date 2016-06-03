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

import cluster.*;
import java.awt.Color;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;


/*
 * A Receptive field is an image depicting the connections between a hidden layer's
 * neuron and the visible layer.  The idea is that, if we can see which visible
 * neurons are affected, we can tell which "features" that hidden neuron is
 * looking for.
 *
 * This class can output tiled representations of these connections between the
 * hidden layer and the visible one.  It can also cluster these fields based on
 * their similarities.
 *
 * @author swirepe
 */
public class ReceptiveFieldImageMaker {

    public static void makeReceptiveField(float[][] weights, int numRows, int numCols, boolean clustering, boolean thresholding, String filename) {

        // the rule of thumb for k-means is to make # clusters = the square root of half the number of inputs.
        int groups = (int)Math.sqrt(weights[0].length / 2);

        ArrayList<BufferedImage> imgList = new ArrayList<BufferedImage>();
        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();

        for (int detector = 0; detector < weights[0].length; detector++) {
            float[] transposed = new float[weights.length];

            // feature detectors are columns in the weight matrix
            for (int i = 0; i < weights.length; i++) {
                transposed[i] = weights[i][detector];
            }

            normalize(transposed);
            dataPoints.add(new DataPoint(transposed, detector));
        } // end of layer for

        
        if(clustering){
            JCA jca = new JCA(groups, 1000, dataPoints);
            jca.startAnalysis();


            // for making bar graphs
            final int barGraphWidth = 500;
            BufferedImage img, bar;
            int[] heights = new int[groups];
            ArrayList<BufferedImage> bars = new ArrayList<BufferedImage>();

            for (int clustIndex = 0; clustIndex < groups; clustIndex++) {
                Cluster cluster = jca.getCluster(clustIndex);

                @SuppressWarnings("unchecked")
                ArrayList<DataPoint> tempDP = (ArrayList) cluster.getDataPoints();

                // make the bar graph
                heights[clustIndex] = cluster.getNumDataPoints();
                System.out.println("Cluster number " + clustIndex + " size: " + heights[clustIndex]);
                bar = makeGrayRectangle(barGraphWidth / (groups+ 1), heights[clustIndex], .5f);
                bar = colorize(bar, (12+clustIndex));
                bars.add(bar);

                // colorize each point in the cluster
                for (DataPoint dp : tempDP) {
                    img = dataPointToImage(dp, numRows, numCols);

                    if(thresholding){
                        img = threshold(img, Params.THRESHOLD);
                    }

                    img = colorize(img, clustIndex);
                    imgList.add(img);
                }
            }

            // find the median:
            Arrays.sort(heights);
            double median = 0;
            
            if(heights.length % 2 == 0){
                median = (heights[ heights.length / 2] + heights[(heights.length/2) - 1]) / 2;
            } else {
                median = heights[heights.length / 2];
            }

            // if every group was about the same size, that would be better
            double suggestedSize = median*groups;
     
            bars.add( makeGrayRectangle(barGraphWidth / (groups + 1), (int)(median), 1.0f) );
            makeBarGraph(bars, suggestedSize, filename);

        }else{
            for(DataPoint dp: dataPoints){

                BufferedImage img = dataPointToImage(dp, numRows, numCols);

                if(thresholding){
                    img = threshold(img, Params.THRESHOLD);
                }
                
                imgList.add(img);
            }
        } // end of clustering else



        BufferedImage mon = montage(imgList);

        mon = scale(mon, mon.getTileWidth() * 4, mon.getHeight() * 4);
        FileParser.writeImage(mon, filename);
    } // end of method makeReceptiveField

    
    /**
     *  Colorizes the image in (hopefully) and interesting way, as to
     *  differentiate between different clusters
     * @param img
     * @param clusterNumber
     * @return
     */
    private static BufferedImage colorize(BufferedImage img, int clusterNumber) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        int rgb;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                rgb = img.getRGB(x, y);
                rgb *= -1;

                // get the individual color values out of the single int
                int blue = rgb % 256;
                rgb /= 256;
                int green = rgb % 256;
                rgb /= 256;
                int red = rgb % 256;

                // color them a bit, with six different patterns
                // I tried to keep similar color separate, as to avoid confusion
                // between clusters.  all values should change by different
                // amounts in each one.
                if (clusterNumber % 6 == 0) {
                    red += 12 * clusterNumber + 10;
                    red = red > 255 ? 255 : red;

                } else if (clusterNumber % 6 == 1) {
                    green += 6 * clusterNumber + 10;
                    green = green > 255 ? 255 : green;
                    blue -= 8 * clusterNumber + 10;
                    blue = blue < 0 ? 0 : blue;
                   
                } else if (clusterNumber % 6 == 2) {
                    blue += 8 * clusterNumber + 10;
                    blue = blue > 255 ? 255 : blue;

                } else if (clusterNumber % 6 == 3){
                    red += 12 * clusterNumber + 10;
                    red = red > 255 ? 255 : red;
                    green += 6 * clusterNumber + 10;
                    green = green > 255 ? 255 : green;
                    
                } else if (clusterNumber % 6 == 4){
                    red += 12 * clusterNumber + 10;
                    red = red > 255 ? 255 : red;
                    blue += 8 * clusterNumber + 10;
                    blue = blue > 255 ? 255 : blue;

                } else if (clusterNumber % 6 == 5){
                    green += 6 * clusterNumber + 10;
                    green = green > 255 ? 255 : green;
                }

                // put them back together and paint the new pixel this color
                rgb = (red * 256 * 256) + (green * 256) + blue;
                newImg.setRGB(x, y, rgb);
            }
        }

        return newImg;
    } // end of method colorize


    // gray rectangles are good for being colorized, and good for making bar graphs
    private static BufferedImage makeGrayRectangle(int width, int height, float gray){
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                output.setRGB(x, y, grayFloatToRGB(gray));
            }
        }

        return output;
    }

    
    /**
     * tiles a list of images on to the same image
     *
     * @param imgList
     * @param filename
     */
    public static BufferedImage montage(ArrayList<BufferedImage> imgList) {

        final int borderSize = 3;

        int width = imgList.get(0).getWidth();
        int canvasWidth = (int) ((width + borderSize) * Math.sqrt(imgList.size())) + width + borderSize;
        int height = imgList.get(0).getHeight();
        int canvasHeight = (int) ((height + borderSize) * Math.sqrt(imgList.size())) + height + borderSize;
        BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        BufferedImage img;
        
        int col = 0;
        int row = 0;

        while (imgList.size() != 0) {
            img = imgList.remove(0);

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    canvas.setRGB(x + col, y + row, img.getRGB(x, y));
                }
            }

            col += width + borderSize;

            if (col >= (canvasWidth - (width + borderSize))) {
                col = 0;
                row += height + borderSize;
            }
        }

        return canvas;
    } // end of method montage



    public static void makeBarGraph(ArrayList<BufferedImage> bars, double suggestedSize, String filename){
        int maxHeight = 0;
        for(BufferedImage bar: bars){
            if(bar.getHeight() > maxHeight){
                maxHeight = bar.getHeight();
            }
        }
        // for a border
        maxHeight += 20;

        // they should all be the same width
        int width = bars.get(0).getWidth();
        int maxWidth = (width * bars.size());
        BufferedImage graph = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
        BufferedImage bar;

        for(int col = 0; col < bars.size(); col++){
            bar = bars.get(col);
            for(int x = 0; x < width; x++){
                for(int y = maxHeight - 1; y > maxHeight - bar.getHeight() - 1; y--){
                    // in retrospect, i could have written this with just color and height
                    graph.setRGB( (col*width) + x ,  y , bar.getRGB(x, y - (maxHeight - bar.getHeight()) ));
                }
            }
        }

        // no matter how big or small the maximum, we want a 500x500 square
        // scale before we add labels, because there may not actually be room for them before
        graph = scale(graph, maxWidth, 500);

        // add labels
        Graphics g = graph.getGraphics();

        g.drawString("Suggested size of layer for similar inputs: " + suggestedSize, 2,12);

        for(int col = 0; col < bars.size(); col++){
            bar = bars.get(col);
            g.drawString("" + bar.getHeight(), (int)(col*width), graph.getHeight() - 4);
        }

        // label the median(the white one on the end)
        g.setColor(Color.black);
        g.drawString("" + bars.get( bars.size() - 1).getHeight(), maxWidth - width, graph.getHeight() - 4);
        g.drawString("Median", maxWidth - width, graph.getHeight() - 14 );


        // add "graph" to the filename and write it to disk
        int sep = filename.lastIndexOf( File.separatorChar );
        String pre = filename.substring(0, sep) + File.separatorChar;
        String post = filename.substring(sep + 1);
        FileParser.writeImage(graph, pre + "graph_" + post );

    } // end of method makeBarGraph


    /**
     *  Normalizes a vector in place
     * @param vector
     */
    private static void normalize(float[] vector) {
        float max = vector[0];
        float min = vector[0];
        for (int i = 0; i < vector.length; i++) {
            if (max < vector[i]) {
                max = vector[i];
            }

            if (min > vector[i]) {
                min = vector[i];
            }

        }

        for (int i = 0; i < vector.length; i++) {
            vector[i] -= min;
            vector[i] /= (max - min);
        }
    } // end of method normalize

    /**
     *  Turns a data point into a single square greyscale image.  If the image
     *  is representative of the first layer of hidden nodes, it is shaped like
     *  our other visualizations.  This is for ease of interpretation: that first
     *  layer should directly match features in the input, and these should be
     *  visible.
     *
     *  Otherwise, it is a square.  Inner layers track "meta-features," which
     *  may not mean anything to humans.
     * 
     * @param dp - A datapoint to be converted
     * @return img - A BufferedImage
     */
    private static BufferedImage dataPointToImage(DataPoint dp, int numRows, int numCols) {

        float[] vector = dp.getVector();
        
        // -1 for rows and cols means that this is an inner layer.  we should
        // just make this a square
        if(numRows == -1 || numCols == -1){
            numRows = (int)(Math.sqrt(vector.length));
            numCols = numRows;
        }

        BufferedImage img = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                img.setRGB(col, row, grayFloatToRGB(vector[(row * numCols) + col]));
            }
        }

        return img;
    } // end of method dataPointToImage

    /**
     *
     * @param img the image to be scaled
     * @param width the new width of the image
     * @param height the new height of the image
     * @return
     */
    public static BufferedImage scale(BufferedImage img, int width, int height) {
        BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bdest.createGraphics();

        AffineTransform at = AffineTransform.getScaleInstance((double) width / img.getWidth(),
                (double) height / img.getHeight());

        g.drawRenderedImage(img, at);
        return bdest;
    }

    public static BufferedImage imageToGrayScale(BufferedImage colorImage) {
        BufferedImage grayImage = new BufferedImage(colorImage.getWidth(),
                colorImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayImage.createGraphics();
        g.drawRenderedImage(colorImage, null);

        return grayImage;
    }


    /**
     * Brings the value of each pixel in an image up or down, based on a threshold point.
     *
     * @param img The image to be thresholded.
     * @param thresh The point at which a connection can be called "on."
     *   This can be found in the Params file, but is passed in for convenience.
     * @return
     */
    public static BufferedImage threshold(BufferedImage img, double thresh) {
        final double max = floatsToRGB(1f, 1f, 1f);
        double temp = 0;

        BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                temp = Math.abs(img.getRGB(x, y)) / max;

                if(temp < thresh){
                    output.setRGB(x, y, 0);
                }else{
                    output.setRGB(x, y, (int)floatsToRGB(.5f,.5f,.5f));
                }
            }
        }
        return output;
    }


    /**
     * Takes in three floats with values 0-1 and returns one int that encompasses them all
     *
     * 
     */
    public static int floatsToRGB(float red, float green, float blue) {

        red *= 255;
        green *= 255;
        blue *= 255;

        red = (float) Math.ceil(red);
        green = (float) Math.ceil(green);
        blue = (float) Math.ceil(blue);

        red *= 256 * 256;
        green *= 256;
        blue *= 1;

        return (int) (red + green + blue);
    }

    public static int grayFloatToRGB(float gray) {
        return floatsToRGB(gray, gray, gray);
    }
} // end of class ReceptiveFieldImageMaker

