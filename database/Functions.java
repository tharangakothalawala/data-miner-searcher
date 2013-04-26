
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 * @Purpose	This class is to provide some legacy functions. (custom function library)
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
        } catch (NullPointerException ex) {
            return false;
        }

        return true;
    }

    /*
     * To check an array is emtpy or not
     */
    public static boolean is_array_empty(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return false;
            }
        }

        return true;
    }

    /*
     * This prints all the values in any given array. Useful when debuging
     */
    public static void dumpArray (String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                System.out.println(array[i]);
        }
    }
}
