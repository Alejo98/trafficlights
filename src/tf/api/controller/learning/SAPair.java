package tf.api.controller.learning;

/**
 * The State-Action pair for Q Learning
 * @author hanli
 *
 */
public class SAPair {

    private int state;
    private Action action;

    public SAPair(int s, Action a) {
        state = s;
        action = a;
    }

    @Override
    public boolean equals(Object obj) {
        assert action != null;
        if (obj != null && obj instanceof SAPair) {
            SAPair oth = (SAPair) obj;
            assert oth.action != null;
            return oth.state == this.state && oth.action.equals(this.action);
        }
        return false;
    }

    @Override
    public int hashCode() {
        assert action != null;
        return state + action.ordinal() * 100000000;
    }
}
