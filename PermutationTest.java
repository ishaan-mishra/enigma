package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Ishaan Mauli Mishra
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;
     /**
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     */
    private Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

    /**
     * @return an Alphabet with chars as its characters
     */
    private Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    /**
     * @return a default Alphabet with characters ABCD...Z
     */
    private Alphabet getNewAlphabet() {
        return new Alphabet();
    }

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = getNewPermutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        p.invert('F');
    }

    @Test
    public void testSize() {
        Permutation p1 = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(4, p1.size());
        Permutation p2 = getNewPermutation("", getNewAlphabet(""));
        assertEquals(0, p2.size());
    }

    @Test
    public void testPermuteInts() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(0, p.permute(1));
        assertEquals(0, p.permute(5));
        assertEquals(2, p.permute(0));
        assertEquals(2, p.permute(4));
        assertEquals(3, p.permute(2));
        assertEquals(3, p.permute(6));
        assertEquals(1, p.permute(3));
        assertEquals(1, p.permute(7));

        Permutation p1 = getNewPermutation("(ILK) (J)", getNewAlphabet("IJKL"));
        assertEquals(3, p1.permute(0));
        assertEquals(3, p1.permute(4));
        assertEquals(2, p1.permute(3));
        assertEquals(2, p1.permute(7));
        assertEquals(0, p1.permute(2));
        assertEquals(0, p1.permute(6));
        assertEquals(1, p1.permute(1));
        assertEquals(1, p1.permute(5));

        Permutation p2 = getNewPermutation("(ILK)", getNewAlphabet("IJKL"));
        assertEquals(3, p2.permute(0));
        assertEquals(3, p2.permute(4));
        assertEquals(2, p2.permute(3));
        assertEquals(2, p2.permute(7));
        assertEquals(0, p2.permute(2));
        assertEquals(0, p2.permute(6));
        assertEquals(1, p2.permute(1));
        assertEquals(1, p2.permute(5));
    }

    @Test
    public void testPermuteChars() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals('A', p.permute('B'));
        assertEquals('C', p.permute('A'));
        assertEquals('D', p.permute('C'));
        assertEquals('B', p.permute('D'));

        Permutation p1 = getNewPermutation("(ILK) (J)", getNewAlphabet("IJKL"));
        assertEquals('L', p1.permute('I'));
        assertEquals('K', p1.permute('L'));
        assertEquals('I', p1.permute('K'));
        assertEquals('J', p1.permute('J'));

        Permutation p2 = getNewPermutation("(ILK)", getNewAlphabet("IJKL"));
        assertEquals('L', p2.permute('I'));
        assertEquals('K', p2.permute('L'));
        assertEquals('I', p2.permute('K'));
        assertEquals('J', p2.permute('J'));
    }

    @Test
    public void testInvertInts() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(1, p.invert(0));
        assertEquals(0, p.invert(2));
        assertEquals(2, p.invert(3));
        assertEquals(3, p.invert(1));

        Permutation p1 = getNewPermutation("(ILK) (J)", getNewAlphabet("IJKL"));
        assertEquals(0, p1.invert(3));
        assertEquals(3, p1.invert(2));
        assertEquals(2, p1.invert(0));
        assertEquals(1, p1.invert(1));

        Permutation p2 = getNewPermutation("(ILK)", getNewAlphabet("IJKL"));
        assertEquals(0, p2.invert(3));
        assertEquals(3, p2.invert(2));
        assertEquals(2, p2.invert(0));
        assertEquals(1, p2.invert(1));
    }

    @Test
    public void testInvertChars() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('C', p.invert('D'));
        assertEquals('D', p.invert('B'));

        Permutation p1 = getNewPermutation("(ILK) (J)", getNewAlphabet("IJKL"));
        assertEquals('I', p1.invert('L'));
        assertEquals('L', p1.invert('K'));
        assertEquals('K', p1.invert('I'));
        assertEquals('J', p1.invert('J'));

        Permutation p2 = getNewPermutation("(ILK)", getNewAlphabet("IJKL"));
        assertEquals('I', p2.invert('L'));
        assertEquals('L', p2.invert('K'));
        assertEquals('K', p2.invert('I'));
        assertEquals('J', p2.invert('J'));
    }

    @Test
    public void testDerangement() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertTrue(p.derangement());
        Permutation p1 = getNewPermutation("(ILK) (J)", getNewAlphabet("IJKL"));
        assertFalse(p1.derangement());
        Permutation p2 = getNewPermutation("(ILK)", getNewAlphabet("IJKL"));
        assertFalse(p2.derangement());
    }

}
