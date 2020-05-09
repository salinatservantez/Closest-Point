/**
 * Description: This program finds the closest pair of points in a set Q of points using divide, conquer, and combine.
 *
 * @author Salina Servantez
 * @edu.uwp.cs.340.section001
 *  * @edu.uwp.cs.340.assignment4-ClosestPoints
 *  * @bugs none
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ClosestPoints {

    public static class Point{
        private double x;
        private double y;

        /**
         * Constructor for a 2d point
         * @param x
         * @param y
         */
        private Point(double x, double y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString(){
            return "(" + x + ")" + "'" + "(" + y + ")";
        }
    }

    /**
     * Main Method that prompts user for a file to be read, putting x and y as doubles
     * @param args
     * @throws Exception
     */
    public static void main(String [] args) throws Exception{
        // User input for the filename
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the file to be read: ");
        String filename = input.next();
        Scanner fileIn = new Scanner(new File(filename));
        ArrayList<Point> xPoints = new ArrayList<>();
        ArrayList<Point> yPoints = new ArrayList<>();

        // Read in each point from the file
        while(fileIn.hasNextLine()){
            // Read in as a string
            String line = fileIn.nextLine();
            // Split the string into the x and y value
            String[] numbers = line.split(" ");
            double x = Double.parseDouble(numbers[0]);
            double y = Double.parseDouble(numbers[1]);
            // Create the point and add it to both lists
            Point p = new Point(x, y);
            xPoints.add(p);
            yPoints.add(p);

        }

        // Sort each list into ascending order by both x values and y values
        sortX(xPoints);
        sortY(yPoints);

        // If there is only one point
        if(xPoints.size() <= 1) {
            System.out.println("Not enough points to calculate distance");
        }
        // If there is less than 3 points use the brute force method
        else if (xPoints.size() <= 3){
            // Find the closest points
            Point[] pPair = bruteForce(xPoints);
            // Print out the points and the distance between them
            System.out.println("Closest Point");
            System.out.println(pPair[0]);
            System.out.println(pPair[1]);
            System.out.println("Distance between them is: " + dist(pPair[0], pPair[1]));

        } else {
            // Find the closest points using the recursive method
            Point[] pPair = closest(xPoints, yPoints);
            System.out.println("Closest Point");
            System.out.println(pPair[0]);
            System.out.println(pPair[1]);
            System.out.println("Distance between them is: " + dist(pPair[0], pPair[1]));
        }


    }

    /**
     * the bruteForce method looks at all the possible pairs of points.  Since there are Θ (n2) pairs, the brute algorithm is  Θ (n2) .
     * Here we describe a divide and conquer algorithm that is  Θ (n lg n)If X contains 3 or fewer elements, it calculates the distance
     * between all pairs and return the pair that has the smallest distance.
     * @param points
     * @return
     */
    public static Point [] bruteForce(ArrayList<Point> points){
        Point[] pPair = new Point[2];
        // Default the smallest distance to the max value so the first iteration has something to compare to
        double smallDist = Double.MAX_VALUE;
        // Grab the first point
        for(Point p: points){
            // Compare to a second point
            for(Point p2: points){
                if(!(p.x == p2.x)){
                    //If the new distance is smaller make it the new smallest
                    double distance = dist(p, p2);
                    if(smallDist > distance){
                        pPair[0] = p;
                        pPair[1] = p2;
                        smallDist = distance;
                    }
                }
            }
        }

        return pPair;
    }

    /**
     * Find the distance between 2 points
     * @param p1
     * @param p2
     * @return
     */
    public static double dist(Point p1, Point p2){
        return Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    }

    /**
     * Sort an array of points in ascending x order
     * @param points
     */
    public static void sortX(ArrayList<Point> points){
        points.sort((Point p1, Point p2) -> {
            if(p1.x < p2.x)
                return -1;
            else if (p1.x > p2.x)
                return 1;
            else
                return 0;

        });

    }

    /**
     * Sort an array into ascending y order
     * @param points
     */
    public static void sortY(ArrayList<Point> points) {
        points.sort((Point p1, Point p2) -> {
            if (p1.y < p2.y)
                return -1;
            else if (p1.y > p2.y)
                return 1;
            else
                return 0;

        });
    }

    /**
     * This method splits points into arraylists and recursively calls the method, finds closest point to eachother even along the  line
     * @param xPoints
     * @param yPoints
     * @return
     */
    public static Point[] closest(ArrayList<Point> xPoints, ArrayList<Point> yPoints){
        // If there are less than 3 points brute force the answer
        if(xPoints.size() <= 3){
            return bruteForce(xPoints);
        }

        // Find the midpoint and split x into two arraylists
        int midIndex = xPoints.size()/2;
        Point midPoint = xPoints.get(xPoints.size()/2);
        ArrayList<Point> xlPoints = new ArrayList<>(xPoints.subList(0,midIndex));
        ArrayList<Point> xrPoints = new ArrayList<>(xPoints.subList(midIndex, xPoints.size()));

        ArrayList<Point> ylPoints = new ArrayList<>();
        ArrayList<Point> yrPoints = new ArrayList<>();

        // Split the y arraylist into two arraylists
        for(Point p : yPoints){
            if(p.x < midPoint.x)
                ylPoints.add(p);
            else
                yrPoints.add(p);
        }

        // Recursively call the method
        Point[] pPair1 = closest(xlPoints, ylPoints);
        Point[] pPair2 = closest(xrPoints, yrPoints);
        Point[] pClose;

        // See what the smaller distance between each half is and make it the smallest distance set of points
        double dist = min(dist(pPair1[0], pPair1[1]), dist(pPair2[0], pPair2[1]));
        if (dist(pPair1[0], pPair1[1]) == dist){
            pClose = pPair1;
        } else {
            pClose = pPair2;
        }

        // Add all points that are less than the distance from the midpoint to an arraylist
        ArrayList<Point> linePoints = new ArrayList<>();
        for (Point p: yPoints){
            if (Math.abs(p.x - midPoint.x) < dist)
                linePoints.add(p);
        }

        // Compare points on each side of the line
        for(int i = 0; i < linePoints.size(); ++i)
            for (int j = i+1; j < linePoints.size() && (j - i) <= 5; ++j)
                if (dist(linePoints.get(i),linePoints.get(j)) < dist) {
                    dist = dist(linePoints.get(j), linePoints.get(i));
                    pClose[0] = linePoints.get(j);
                    pClose[1] = linePoints.get(i);
                }
        return pClose;
    }

    /**
     * Find the min between 2 numbers
     * @param x
     * @param y
     * @return
     */
    public static double min(double x, double y){
        if(x<y)
            return x;
        else
            return y;
    }


}
