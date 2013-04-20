
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package database;

public class Functions {


    /*
     * This initializes any given array
     */
    public static void initializeArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }

    /*
     * This is equivalent to the PHP in_array function to detect values in any given array
     */
    public static boolean in_array(String[] array, String searchValue) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(searchValue)) {
                return true;
            }
        }

        return false;
    }

    /*
     * This is equivalent to the PHP is_array function to detect any given array is really an array
     */
    public static boolean is_array(String[] array) {
        try {
            String attempToGetTheValueAtArrayIndexTwo = array[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }

        return true;
    }
}
