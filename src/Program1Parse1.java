import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Tianyu Wu, Yifan Yu, Zixi Wang
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to statement string of body of
     *          instruction at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens,
            Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION") : ""
                + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";

        String eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("INSTRUCTION"),
                "THERE IS A WRONG GRAMMER: " + eachCheck);
        String nameOfIns = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(nameOfIns),
                "THERE IS A WRONG GRAMMER");

        Reporter.assertElseFatalError(!(nameOfIns.equals("move")
                || nameOfIns.equals("turnleft") || nameOfIns.equals("turnright")
                || nameOfIns.equals("infect") || nameOfIns.equals("skip")),
                "THERE IS A WRONG GRAMMER");

        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("IS"),
                "THERE IS A WRONG GRAMMER");
        body.parseBlock(tokens);
        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("END"),
                "THERE IS A WRONG GRAMMER");
        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals(nameOfIns),
                "THERE IS A WRONG GRAMMER");

        return nameOfIns;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0 : ""
                + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";

        String eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("PROGRAM"),
                "THERE IS A WRONG GRAMMER");
        String progName = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(progName),
                "THERE IS A WRONG GRAMMER");
        this.setName(progName);
        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("IS"),
                "THERE IS A WRONG GRAMMER");

        Statement tempBody = this.newBody();
        Map<String, Statement> ctxt = this.newContext();
        while (tokens.front().equals("INSTRUCTION")) {
            tempBody = this.newBody();
            String ctxtName = parseInstruction(tokens, tempBody);
            if (ctxt.hasKey(ctxtName) && !ctxtName.equals("move")
                    && !ctxtName.equals("turnleft")
                    && !ctxtName.equals("turnright")
                    && !ctxtName.equals("infect") && !ctxtName.equals("skip")) {
                Reporter.assertElseFatalError(false,
                        "THERE IS A WRONG GRAMMER");
            }
            ctxt.add(ctxtName, tempBody);
        }
        this.swapContext(ctxt);
        Statement body = this.newBody();
        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("BEGIN"),
                "THERE IS A WRONG GRAMMER");
        body.parseBlock(tokens);
        this.swapBody(body);
        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals("END"),
                "THERE IS A WRONG GRAMMER");
        eachCheck = tokens.dequeue();
        Reporter.assertElseFatalError(eachCheck.equals(progName),
                "THERE IS A WRONG GRAMMER");
        Reporter.assertElseFatalError(
                tokens.front().equals(Tokenizer.END_OF_INPUT),
                "THERE IS A WRONG GRAMMER");
    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
