package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Ishaan Mauli Mishra
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        if (!_input.hasNext("\\*")) {
            throw error("bad input");
        }
        while (_input.hasNextLine()) {
            while (_input.findInLine("\\*") == null) {
                _output.println();
                _input.nextLine();
            }
            String settings =  _input.nextLine();
            Pattern p = Pattern.compile("([^\\s]*[\\s])*[^\\s]*");
            if (!checkMatch(settings, p)) {
                throw error("bad input");
            }
            setUp(machine, settings);
            while (_input.hasNextLine() && !_input.hasNext("\\*")) {
                String msg = _input.nextLine();
                String encoded = machine.convert(msg);
                printMessageLine(encoded);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            Collection<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            Machine machine = new Machine(_alphabet,
                                numRotors, pawls, allRotors);
            return machine;
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String description = _config.next();
            String cycles = " ";
            while (_config.hasNext("[(][^\\s]+[)]")) {
                cycles += _config.next() + " ";
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            if (description.charAt(0) == 'M') {
                return new MovingRotor(name, perm, description.substring(1));
            } else if (description.charAt(0) == 'N') {
                return new FixedRotor(name, perm);
            } else if (description.charAt(0) == 'R') {
                return new Reflector(name, perm);
            } else {
                throw error("bad rotor description");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int posPlugboard = settings.indexOf('(');
        if (posPlugboard != -1) {
            String pCycles = settings.substring(posPlugboard);
            Permutation plugboard = new Permutation(pCycles, _alphabet);
            M.setPlugboard(plugboard);
            settings = settings.substring(0, posPlugboard - 1);
        } else {
            Permutation plugboard = new Permutation("", _alphabet);
            M.setPlugboard(plugboard);
        }
        ArrayList<String> rotorsNsettings = new ArrayList<String>();
        Scanner sc = new Scanner(settings);
        while (sc.hasNext()) {
            rotorsNsettings.add(sc.next());
        }
        String[] rotors = new String[M.numRotors()];
        if (rotorsNsettings.size() < M.numRotors() + 1
                || rotorsNsettings.size() > M.numRotors() + 2) {
            throw error("bad input");
        }
        for (int i = 0; i < rotors.length; i += 1) {
            rotors[i] = rotorsNsettings.get(i);
        }
        M.insertRotors(rotors);
        M.setRotors(rotorsNsettings.get(M.numRotors()));
        if (rotorsNsettings.size() == M.numRotors() + 2) {
            M.setRings(rotorsNsettings.get(M.numRotors() + 1));
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int count = 0;
        for (int i = 0; i < msg.length(); i += 1) {
            _output.print(msg.charAt(i));
            count += 1;
            if (count == 5) {
                _output.print(" ");
                count = 0;
            }
        }
        _output.println();
    }

    /** Returns true if the string s matches pattern p.
     * @param s String to check.
     * @param p Patter to check against. */
    private static boolean checkMatch(String s, Pattern p) {
        Matcher mat = p.matcher(s);
        return mat.matches();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
