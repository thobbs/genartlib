package genartlib;

public class PolyUtils {

    public static boolean polygonContainsPoint(int[] xPoints, int[] yPoints, int testX, int testY)
    {
        int numVerts = xPoints.length;
        boolean c = false;
        int j = numVerts - 1;
        for (int i = 0; i < numVerts; i++)
        {
            double deltaX = xPoints[j] - xPoints[i];
            double ySpread = testY - yPoints[i];
            double deltaY = yPoints[j] - yPoints[i];
            if (((yPoints[i] > testY) != (yPoints[j] > testY)) &&
                (testX < ((((xPoints[j] - xPoints[i]) * (testY - yPoints[i])) / (yPoints[j] - yPoints[i]))
                          + xPoints[i])))
            {
                c = !c;
            }

            j = i;
        }
        return c;
    }
}
