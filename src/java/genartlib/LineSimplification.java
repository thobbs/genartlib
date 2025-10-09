package genartlib;

import java.util.List;
import java.util.ArrayList;

public class LineSimplification {

  public static class Point {
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

    double denom = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    if (denom == 0)
      return 0;

    double numerator = Math.abs(
        (1 * ((y2 - y1) * p.x)) +
        (-1 * ((x2 - x1) * p.y)) +
        (1 * (x2 * y1)) +
        (-1 * (y2 * x1)));

    return numerator / denom;
  }

  private static class Segment {
    final int startIndex;
    final int endIndex;

    Segment(int startIndex, int endIndex) {
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }
  }

  /**
   * An implementation of the Ramer-Douglas-Peucker line simplification algorithm.
   * A good value for `min-tolerated-dist` is probably between 0.0001 and 0.01
   * times the image width, depending on your use case."
   */
  public static List<Point> simplify(List<Point> points, double minToleratedDist) {
    if (points.size() <= 2) {
      return points;
    }

    // Use a stack to manage segments to process
    java.util.Stack<Segment> stack = new java.util.Stack<>();
    // Use a set to track which points should be kept
    java.util.Set<Integer> keepIndices = new java.util.HashSet<>();

    // Always keep the first and last points
    keepIndices.add(0);
    keepIndices.add(points.size() - 1);

    // Start with the entire line segment
    stack.push(new Segment(0, points.size() - 1));

    while (!stack.isEmpty()) {
      Segment segment = stack.pop();

      if (segment.endIndex - segment.startIndex <= 1) {
        // No points between start and end, nothing to process
        continue;
      }

      Point startPoint = points.get(segment.startIndex);
      Point endPoint = points.get(segment.endIndex);

      // Find the point with maximum distance from the line
      double maxDist = -1;
      int maxIndex = -1;
      for (int i = segment.startIndex + 1; i < segment.endIndex; i++) {
        Point p = points.get(i);
        double d = pointToLineDist(startPoint, endPoint, p);
        if (d > maxDist) {
          maxDist = d;
          maxIndex = i;
        }
      }

      if (maxDist >= minToleratedDist) {
        // Keep the point with maximum distance
        keepIndices.add(maxIndex);

        // Add the two sub-segments to the stack for further processing
        stack.push(new Segment(segment.startIndex, maxIndex));
        stack.push(new Segment(maxIndex, segment.endIndex));
      }
    }

    // Build the result list with only the kept points
    List<Point> result = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      if (keepIndices.contains(i)) {
        result.add(points.get(i));
      }
    }

    return result;
  }
}
