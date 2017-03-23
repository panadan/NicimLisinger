import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link KeyListener} implementation for barcode readers. This implementation
 * checks for input rate and jitter to distinguish human and scanner
 * input sequences by 'precision'. A barcode input sequence from a scanner is
 * typically terminated with a line break.
 * 
 * @author Me
 */
public abstract class AbstractBarcodeInputListener implements KeyListener {
    public static final int DEFAULT_MIN_PAUSE = 300;// [ms]
    public static final int DEFAULT_MAX_TIME_DELTA = 200;// [ms]
    public static final int DEFAULT_MAX_TIME_JITTER = 50;// [ms]

    public static Integer parseInt(Pattern pattern, int group, String line) {
        final Matcher matcher = pattern.matcher(line);
        if (matcher.matches())
            return Integer.parseInt(matcher.group(group));
        return null;
    }

    private String input;

    private final long minPause;
    private long maxTimeDelta;
    private final long maxTimeJitter;

    private long firstTime;
    private long firstTimeDelta;
    private long lastTimeDelta;
    private long lastTime;

    public AbstractBarcodeInputListener(long maxTimeDelta, long maxTimeJitter) {
        this.input = new String();

        this.minPause = AbstractBarcodeInputListener.DEFAULT_MIN_PAUSE;
        this.maxTimeDelta = maxTimeDelta;
        this.maxTimeJitter = maxTimeJitter;

        this.firstTime = 0;
        this.firstTimeDelta = 0;
        this.lastTimeDelta = 0;
        this.lastTime = 0;
    }

    public AbstractBarcodeInputListener() {
        this(AbstractBarcodeInputListener.DEFAULT_MAX_TIME_DELTA,
                AbstractBarcodeInputListener.DEFAULT_MAX_TIME_JITTER);
    }

    private boolean checkTiming(KeyEvent e) {
        final int inputLength = this.input.length();
        final long time = e.getWhen();
        long timeDelta = time - this.lastTime;
        long absJitter = 0;
        long relJitter = 0;

        boolean inputOK = true;

        switch (inputLength) {
        case 0: // pause check
            inputOK &= (timeDelta > this.minPause);
            this.firstTime = time;
            this.firstTimeDelta = timeDelta = 0;
            break;
        case 1: // delta check
            this.firstTimeDelta = timeDelta;
            inputOK &= (timeDelta < this.maxTimeDelta);
            break;
        default:// jitter check & delta check
            absJitter = Math.abs(timeDelta - this.firstTimeDelta);
            relJitter = Math.abs(timeDelta - this.lastTimeDelta);
            inputOK &= (absJitter < this.maxTimeJitter);
            inputOK &= (relJitter < this.maxTimeJitter);
            inputOK &= (timeDelta < this.maxTimeDelta);
            break;
        }

        this.lastTime = time;
        this.lastTimeDelta = timeDelta;

        return inputOK;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    private void clearInput() {
        this.input = new String();
    }

    private void commitInput(KeyEvent e) {
        final String code = this.input;
        if (!code.isEmpty()) {
            final long avgIntervalTime = e.getWhen() - this.firstTime;
            this.maxTimeDelta = (avgIntervalTime * 15) / 10;
            this.clearInput();
            this.codeRead(code);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (this.checkTiming(e)) {
            final char c = e.getKeyChar();
            switch (c) {
            case '\b':
                this.clearInput();
                break;
            case '\n':
                this.commitInput(e);
                break;
            default:
                this.input += c;
                break;
            }
        } else {
            this.clearInput();
        }
    }

    public abstract void codeRead(String line);
}