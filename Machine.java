package enigma;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Ishaan Mauli Mishra
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        if (pawls > numRotors) {
            throw error("number of pawls can't be more than number of rotors");
        }
        _allRotors = allRotors;
        if (numRotors > allRotors.size()) {
            throw error("numRotors > allRotors.size()");
        }
        _rotors = new ArrayList<Rotor>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new ArrayList<Rotor>();
        for (String name : rotors) {
            Iterator<Rotor> allRotors = _allRotors.iterator();
            while (allRotors.hasNext()) {
                Rotor r = allRotors.next();
                if (r.name().equals(name)) {
                    _rotors.add(r);
                }
            }
        }
        if (!(_rotors.get(0) instanceof Reflector)) {
            throw error("leftmost rotor is not a reflector");
        }
        for (int  i = 1; i < _rotors.size(); i += 1) {
            if (i < numRotors() - numPawls()
                    && !(_rotors.get(i) instanceof FixedRotor)) {
                throw error("rotor at posn %c must be FixedRotor", i + 1);
            } else if (i >= numRotors() - numPawls()
                    && !(_rotors.get(i) instanceof MovingRotor)) {
                throw error("rotor at posn %c must be MovingRotor", i + 1);
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _rotors.size(); i += 1) {
            _rotors.get(i).set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Advances the machine. */
    void advance() {
        boolean moved = false;
        for (int i = 1; i < _rotors.size() - 1; i += 1) {
            if (_rotors.get(i + 1).atNotch() && _rotors.get(i).rotates()) {
                if (!moved) {
                    _rotors.get(i).advance();
                }
                _rotors.get(i + 1).advance();
                moved = true;
            } else {
                moved = false;
            }
        }
        if (!moved) {
            _rotors.get(_rotors.size() - 1).advance();
        }
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advance();
        int p = _plugboard.permute(c);
        for (int i = _rotors.size() - 1; i >= 0; i -= 1) {
            p = _rotors.get(i).convertForward(p);
        }
        for (int i = 1; i < _rotors.size(); i += 1) {
            p = _rotors.get(i).convertBackward(p);
        }
        return _plugboard.permute(p);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String converted = "";
        for (int i = 0; i < msg.length(); i += 1) {
            char c = msg.charAt(i);
            if (c == ' ') {
                continue;
            }
            if (!_alphabet.contains(c)) {
                throw error("char %d at posn %c not in alphabet", c, i);
            }
            int convertedInt = convert(_alphabet.toInt(c));
            converted += _alphabet.toChar(convertedInt);
        }
        return converted;
    }

    /** Sets the Ringsettlung for all rotors.
     * @param rings String of Ringstellung settings. */
    void setRings(String rings) {
        for (int i = 1; i < _rotors.size(); i += 1) {
            _rotors.get(i).shift(_alphabet.toInt(rings.charAt(i - 1)));
        }
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots. */
    private int _numRotors;

    /** Number of pawls, i.e., number of moving rotors. */
    private int _pawls;

    /** Collection of all available rotors. */
    private Collection<Rotor> _allRotors;

    /** Rotors in my rotor slots in order. */
    private ArrayList<Rotor> _rotors;

    /** Plugboard connected to me. */
    private Permutation _plugboard;
}
