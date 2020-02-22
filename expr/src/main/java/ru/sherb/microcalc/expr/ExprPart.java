package ru.sherb.microcalc.expr;

/**
 * @author maksim
 * @since 08.02.2020
 */
public class ExprPart {

    static final String ID_PREFIX = "$";

    private final int id;
    private final String operator;

    private String[] args;
    private Number answer;

    ExprPart(int id, String op, String... args) {
        assert op != null && !op.isBlank();
        assert args != null && args.length > 0;

        this.id = id;
        this.operator = op;
        this.args = args;
    }

    void answer(Number answer) {
        assert answer != null;
        this.answer = answer;
    }

    Number answer() {
        return this.answer;
    }

    public String operator() {
        return operator;
    }


    public String[] args() {
        return args;
    }

    @Override
    public String toString() {
        String result = '(' + ID_PREFIX + id + ", " + String.join(" ", args) + ' ' + operator + ')';
        if (answer != null) {
            result += " = " + answer;
        }
        return result;
    }

    int id() {
        return id;
    }

    void resolve(int id, Number answer) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(ID_PREFIX) && Integer.parseInt(args[i].substring(1)) == id) {
                args[i] = String.valueOf(answer);
            }
        }
    }

    boolean isMaterialized() {
        if (answer != null) {
            return true;
        }

        for (String token : args) {
            if (token.startsWith(ID_PREFIX)) {
                return false;
            }
        }

        return true;
    }

    boolean hasAnswer() {
        return answer != null;
    }
}
