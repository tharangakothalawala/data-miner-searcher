
/**
 *
 * @author Tharanga
 */
import database.*;
import java.util.*;

public class TestClass {

    Database db = new Database();

    public void TestClass() { }

    public void testLoad() {
        Map[] resultsets = db.sqlSelectExtended("CALENDAR", "*", "clndr_id = 10", null, null, null, null);

        //Map<String, String> rs = resultsets[0];
        //System.out.println("DATA: "+ rs.get("clndr_type"));

        for (int i = 0; i < 1; i++) {
            Map<String, String> resultset = resultsets[i];

            System.out.println("== ROW: "+ i +" ========================");
            for (Map.Entry<String, String> entry : resultset.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println("['" + key + "'] = " + value);
            }
        }
    }

    public static void main(String[] args) {
        TestClass tc = new TestClass();
        tc.testLoad();
    }
}