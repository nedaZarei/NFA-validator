import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NDFAvalidator {
    private HashMap<Integer, String[]> alphabet;
    private HashMap<Integer, String[]> states;
    private String startState;
    //in hashmaps keys are machine(testcase) ids
    private HashMap<Integer, String[]> finalStates;
    private HashMap<Integer, ArrayList<Transition>> transitions;
    private HashMap<Integer, ArrayList<String>> testcases;

    private int number_of_machines;

    public NDFAvalidator(String inputFile) {
        alphabet = new HashMap<>();
        states = new HashMap<>();
        finalStates = new HashMap<>();
        transitions = new HashMap<>();
        testcases = new HashMap<>();
        readInputFile(inputFile);
    }

    public int getNumber_of_machines() {
        return number_of_machines;
    }

    private void readInputFile(String inputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            number_of_machines = Integer.parseInt(reader.readLine());
            for (int i = 0; i < number_of_machines; i++) {
                /* file format for each machine:
                # number of operators
                # operators
                # number of states
                # states
                # number of transitions
                # transitions : start operator result
                # the starting state
                # number of final states
                # final states
                # number of test cases
                # test cases
                */
                String alphabet_num = reader.readLine().trim();
                String alphabetLine = reader.readLine();
                String state_num = reader.readLine().trim();
                String statesLine = reader.readLine();
                int transition_num = Integer.parseInt(reader.readLine().trim());
                String transitionLine;

                ArrayList<Transition> transitions_for_this_machine = new ArrayList<>();
                for (int j = 0; j < transition_num; j++) {
                    transitionLine = reader.readLine();
                    String[] transitionArray = transitionLine.split("\\s");
                    if (transitionArray.length == 3) {
                        transitions_for_this_machine.add(new Transition(transitionArray[0], transitionArray[1], transitionArray[2]));
                    }
                }
                transitions.put(i + 1, transitions_for_this_machine);

                startState = reader.readLine();
                String finalState_num = reader.readLine().trim();
                String finalStatesLine = reader.readLine();

                if (alphabetLine != null && statesLine != null && startState != null && finalStatesLine != null) {
                    String[] alphabetArray = alphabetLine.split("\\s");
                    String[] statesArray = statesLine.split("\\s");
                    String[] finalStatesArray = finalStatesLine.split("\\s");

                    for (int m = 0; m < Integer.parseInt(alphabet_num); m++) {
                        alphabet.put(i + 1, alphabetArray);
                    }
                    for (int m = 0; m < Integer.parseInt(state_num); m++) {
                        states.put(i + 1, statesArray);
                    }
                    for (int m = 0; m < Integer.parseInt(finalState_num); m++) {
                        finalStates.put(i + 1, finalStatesArray);
                    }
                }

                int testcase_num = Integer.parseInt(reader.readLine().trim());
                String testCaseLine;
                ArrayList<String> testcases_for_this_machine = new ArrayList<>();
                for (int k = 0; k < testcase_num; k++) {
                    testCaseLine = reader.readLine();
                    testcases_for_this_machine.add(testCaseLine);
                }
                testcases.put(i + 1, testcases_for_this_machine);
                if (i != number_of_machines - 1) reader.readLine(); //reads enter at the end of each machine
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validateString(int machineNumber, String input) {
        //using a Set<String> to represent the set of possible current states.
        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);

        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            //for each input symbol, we iterate through all possible current states and consider
            // all transitions for that state and symbol.
            // the resulting set of states (nextStates)
            // represents all possible states the NDFA could be in after processing the current symbol.
            Set<String> nextStates = new HashSet<>();

            for (String currentState : currentStates) {
                for (Transition transition : transitions.get(machineNumber)) {
                    if (transition.getStartState().equals(currentState) && transition.getSymbol().equals(symbol)) {
                        nextStates.add(transition.getEndState());
                    }
                }
            }

            currentStates = nextStates;
        }

        for (String currentState : currentStates) {
            if (Arrays.asList(finalStates.get(machineNumber)).contains(currentState)) {
                return true;
            }
        }
        return false;
    }

    private static class Transition {
        private String startState;
        private String symbol;
        private String endState;

        public Transition(String startState, String symbol, String endState) {
            this.startState = startState;
            this.symbol = symbol;
            this.endState = endState;
        }

        public String getStartState() {
            return startState;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getEndState() {
            return endState;
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("enter the absolute path of your NDFA input file:");
        String inputFile = input.nextLine();

        NDFAvalidator validator = new NDFAvalidator(inputFile);

        for (int i = 0; i < validator.getNumber_of_machines(); i++) {
            System.out.println("machine " + (i + 1));
            for (String inputString : validator.testcases.get(i + 1)) {
                boolean isValid = validator.validateString(i + 1, inputString);
                System.out.println(inputString + " -> " + (isValid ? "yes" : "no"));
            }
            System.out.println("-------------------------------");
        }
    }
}
