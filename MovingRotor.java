package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Ishaan Mauli Mishra
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new int[notches.length()];
        for (int i = 0; i < notches.length(); i += 1) {
            char c = notches.charAt(i);
            if (!alphabet().contains(c)) {
                throw error("notch not in alphabet");
            }
            _notches[i] = alphabet().toInt(c);
        }
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length; i += 1) {
            if (_notches[i] == setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    /** Position of my notches. */
    private int[] _notches;
}
