package lazy.moofluids.utils;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @author jittagornp
 * thank you
 * http://stackoverflow.com/questions/10530426/how-can-i-find-dominant-color-of-an-image
 * @author lazy
 * changes:
 * - return the actual int color instead of hex
 * - make optional the check for gray
 */
public class ImageDominantColor {

    public static int getColor(BufferedImage image, boolean checkForGray) {
        Map<Integer, Integer> colorMap = new HashMap<>();
        int height = image.getHeight();
        int width = image.getWidth();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);

                Integer counter = colorMap.get(rgb);
                if (counter == null) {
                    counter = 0;
                }

                if (checkForGray) {
                    if (!isGray(getRGBArr(rgb))) {
                        colorMap.put(rgb, ++counter);
                    }
                } else {
                    colorMap.put(rgb, ++counter);
                }
            }
        }

        return getMostCommonColor(colorMap);
    }

    private static int getMostCommonColor(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (Map.Entry<Integer, Integer> obj1, Map.Entry<Integer, Integer> obj2)
                -> ((Comparable) obj1.getValue()).compareTo(obj2.getValue()));

        Map.Entry<Integer, Integer> entry = list.get(list.size() - 1);
        return entry.getKey();
    }

    private static int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red, green, blue};
    }

    private static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance) {
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        }
        return true;
    }
}