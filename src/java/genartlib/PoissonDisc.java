package genartlib;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/** An implementation of Robert Bridson's "Fast Poisson Disk Sampling" algorithm */
public class PoissonDisc {
  private final double margin;
  private final double leftX;
  private final double rightX;
  private final double topY;
  private final double botY;

  private final double width;
  private final double height;
  private final double cellWidth;
  private final int numRows;
  private final int numCols;

  private final Point[][] grid;

  public PoissonDisc(double margin, double leftX, double rightX, double topY, double botY) {
    this.margin = margin;
    this.leftX = leftX;
    this.rightX = rightX;
    this.topY = topY;
    this.botY = botY;

    this.cellWidth = margin / Math.sqrt(2);

    this.width = rightX - leftX;
    this.height = botY - topY;
    this.numRows = (int) Math.ceil(height / cellWidth);
    this.numCols = (int) Math.ceil(width / cellWidth);

    this.grid = new Point[numRows][numCols];
  }

  public List<Point> generate(int maxAttempts, Random rand) {
    ArrayList<Point> points = new ArrayList<>();

    double x = leftX + rand.nextDouble() * width;
    double y = topY + rand.nextDouble() * height;
    Point initial = new Point(x, y);
    ArrayList<Point> activeList = new ArrayList<>();
    activeList.add(initial);
    points.add(initial);
    while (!activeList.isEmpty()) {
      Point p = activeList.remove(activeList.size() - 1);

      boolean foundNewPoint = false;
      for (int k = 0; k < maxAttempts; k++) {

        double theta = rand.nextDouble() * Math.PI * 2.0;
        double mag = margin + rand.nextDouble() * margin;
        x = p.x + mag * Math.cos(theta);
        y = p.y + mag * Math.sin(theta);

        if (x < leftX || x >= rightX || y < topY || y >= botY) {
          continue;
        }

        int row = (int) ((y - this.topY) / cellWidth);
        int col = (int) ((x - this.leftX) / cellWidth);
        boolean haveCollision = false;

        int minRow = Math.max(0, (int) (((y - margin) - this.topY) / cellWidth));
        int maxRow = Math.min(numRows - 1, (int) (((y + margin) - this.topY) / cellWidth));
        int minCol = Math.max(0, (int) (((x - margin) - this.leftX) / cellWidth));
        int maxCol = Math.min(numCols - 1, (int) (((x + margin) - this.leftX) / cellWidth));

        outerloop:
        for (int i = minRow; i <= maxRow; i++) {
          for (int j = minCol; j <= maxCol; j++) {
            Point p2 = this.grid[i][j];
            if (p2 == null || p2 == p) {
              continue;
            }

            double d = Math.sqrt(Math.pow(p2.x - x, 2) + Math.pow(p2.y - y, 2));
            if (d < margin) {
              haveCollision = true;
              break outerloop;
            }
          }
        }

        if (!haveCollision) {
          Point newPoint = new Point(x, y);
          activeList.add(newPoint);
          points.add(newPoint);
          grid[row][col] = newPoint;
          foundNewPoint = true;
        }
      }

      if (foundNewPoint) {
        activeList.add(p);
      }
    }

    return points;
  }

  public static class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
