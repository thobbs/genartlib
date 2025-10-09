package genartlib;

import java.util.ArrayDeque;

public class LineSimplification {

  public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  private static double pointToLineDist(Point lineStart, Point lineEnd, Point p) {
    double x1 = lineStart.x;
    double y1 = lineStart.y;
    double x2 = lineEnd.x;
    double y2 = lineEnd.y;
    double x0 = p.x;
    double y0 = p.y;

    double denom = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    if (denom == 0)
      return 0;

    double numerator = Math.abs(
        (1 * ((y2 - y1) * x0)) +
        (-1 * ((x2 - x1) * y0)) +
        (1 * (x2 * y1)) +
        (-1 * (y2 * x1)));

    return numerator / denom;
  }

  /**
   * An implementation of the Ramer-Douglas-Peucker line simplification algorithm.
   * A good value for `min-tolerated-dist` is probably between 0.0001 and 0.01 times
   * the image width, depending on your use case."
   **/
  public static ArrayList<Point> simplify(ArrayList<Point> points, double minToleratedDist) {
    if (points.size() <= 2) {
      return points;
    }

    ArrayDeque<List<Point>> processStack = new ArrayDeque<>();
    processStack.addFirst(points);

    while (!processStack.isEmpty()) {
      List<Point> toProcess = processStack.pop();
      Point startPoint = toProcess.get(0);
      Point endPoint = toProcess.get(toProcess.size() - 1);

      double maxDist = -1;
      int maxIndex = -1;
      for (int i = 0; i < toProcess.size(); i++) {
        p = toProcess.get(i);
        double d = pointToLineDist(startPoint, endPoint, p);
        if (d > maxDist) {
          maxDist = d;
          maxIndex = i;
        }
      }

      if (maxDist < minToleratedDist) {
        ArrayList<Point> result = new ArrayList<>(2);
        result.add(startPoint);
        result.add(endPoint);
        return result;
      }

      ArrayList<Point> simplifiedFirstHalf = simplify(points.subList(0, maxIndex + 1), minToleratedDist);
      ArrayList<Point> simplifiedSecondHalf = simplify(points.subList(maxIndex, points.size()), minToleratedDist);
      simplifiedSecondHalf.remove(0);
      simplifiedFirstHalf.addAll(simplifiedSecondHalf);
      return simplifiedFirstHalf;
    }
  }
}
