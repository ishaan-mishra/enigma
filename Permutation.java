package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Ishaan Mauli Mishra
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = "";
        for (int i = 0; i < cycles.length(); i += 1) {
            char c = cycles.charAt(i);
            if (c != ' ' && c != '(' && c != ')'
                    && (_cycles.indexOf(c) != -1
                        || !alphabet().contains(c))) {
                throw error("error in cycles");
            }
            _cycles += c;
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += " (";
        for (int i = 0; i < cycle.length(); i += 1) {
            char c = cycle.charAt(i);
            if (_cycles.indexOf(c) != -1 || !alphabet().contains(c)) {
                throw error("error in cycle");
            }
            _cycles += c;
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        char cp = alphabet().toChar(index);
        char permuted = permute(cp);
        return alphabet().toInt(permuted);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int index = wrap(c);
        char cc = alphabet().toChar(index);
        char inverted = invert(cc);
        return alphabet().toInt(inverted);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int pos = _cycles.indexOf(p);
        if (!alphabet().contains(p)) {
            throw error("Not in alphabet");
        }
        if (pos != -1) {
            if (_cycles.charAt(pos + 1) != ')') {
                return _cycles.charAt(pos + 1);
            } else {
                for (int i = pos - 1; i >= 0; i -= 1) {
                    if (_cycles.charAt(i) == '(') {
                        return _cycles.charAt(i + 1);
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int pos = _cycles.indexOf(c);
        if (!alphabet().contains(c)) {
            throw error("Not in alphabet");
        }
        if (pos != -1) {
            if (_cycles.charAt(pos - 1) != '(') {
                return _cycles.charAt(pos - 1);
            } else {
                for (int i = pos + 1; i < _cycles.length(); i += 1) {
                    if (_cycles.charAt(i) == ')') {
                        return _cycles.charAt(i - 1);
                    }
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int posPar = 0;
        int numChars = 0;
        for (int i = 1; i < _cycles.length(); i += 1) {
            if (_cycles.charAt(i) == '(') {
                posPar = i;
            } else if (_cycles.charAt(i) == ')') {
                if (i - posPar == 2) {
                    return false;
                }
            } else if (_cycles.charAt(i) != ' ') {
                numChars += 1;
            }
        }
        return (numChars == alphabet().size());
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String _cycles;
}
