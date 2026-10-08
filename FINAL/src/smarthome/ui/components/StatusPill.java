package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A small rounded, coloured badge showing a short status word (ON,
 * OFF, LOCKED, HIGH, CRITICAL, ...). Used inside table cell
 * renderers and standalone status displays so state reads as a
 * colour + shape at a glance rather than requiring the user to read
 * plain black text.
 */
public class StatusPill extends JLabel {

    private static final long serialVersionUID = 1L;

    private Color dot;
    private Color fg;
    private Color bg;

    public StatusPill(String text, Color dot, Color fg, Color bg) {
        super(text);
        applyColors(dot, fg, bg);
        setFont(Theme.FONT_SMALL_BOLD);
        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    public void applyColors(Color dot, Color fg, Color bg) {
        this.dot = dot;
        this.fg = fg;
        this.bg = bg;
        setForeground(fg);
    }

    public void setText2(String text, Color dot, Color fg, Color bg) {
        setText(text);
        applyColors(dot, fg, bg);
    }

    /** Convenience factory for boolean on/off style pills. */
    public static StatusPill onOff(boolean on) {
        return on
                ? new StatusPill("ON", Theme.SUCCESS, Theme.SUCCESS, Theme.SUCCESS_DIM)
                : new StatusPill("OFF", Theme.TEXT_MUTED, Theme.TEXT_SECONDARY, Theme.BG_RAISED);
    }

    /** Convenience factory for Alert.Severity-style pills, driven by {@link Theme#severityColor}. */
    public static StatusPill severity(String severityName) {
        Color c = Theme.severityColor(severityName);
        Color bg = new Color(c.getRed(), c.getGreen(), c.getBlue(), 34);
        return new StatusPill(severityName, c, c, bg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 20));
    }
}
