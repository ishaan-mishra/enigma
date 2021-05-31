package enigma;

import org.junit.Test;

/** Runs Integration Tests from provided files(used for debugging).
 *  @author Ishaan Mauli Mishra
 */
public class IntegrationTest {

    @Test
    public void testTrivial() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/trivial.in"};
        Main.main(arr);
    }

    @Test
    public void testTrivial1() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/trivial1.in"};
        Main.main(arr);
    }

    @Test
    public void testDefault() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/default.in"};
        Main.main(arr);
    }

    @Test
    public void testDefault1() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/default1.in"};
        Main.main(arr);
    }

    @Test (expected = EnigmaException.class)
    public void testTrivialErr() {
        String[] arr = {"testing/error/default.conf",
                        "testing/error/trivialerr.in"};
        Main.main(arr);
    }

    @Test
    public void testFormat1() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/01-format.in"};
        Main.main(arr);
    }

    @Test
    public void testFormat3() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/03-format.in"};
        Main.main(arr);
    }

    @Test
    public void testCarroll4() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/04-carroll.in"};
        Main.main(arr);
    }

    @Test
    public void testCarroll1Ring() {
        String[] arr = {"testing/correct/default.conf",
                        "testing/correct/carroll1-ring.in"};
        Main.main(arr);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(IntegrationTest.class));
    }
}
