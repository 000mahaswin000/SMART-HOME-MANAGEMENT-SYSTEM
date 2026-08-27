package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A rounded, flat, hover-highlighted button used throughout the app
 * in place of the stock JButton square edges. Supports a small set
 * of semantic colour variants so the same component can represent a
 * primary action, a neutral action, or a destructive action just by
 * passing a {@link Variant}.
 */
public class PillButton extends JButton {

    private static final long serialVersionUID = 1L;

    public enum Variant {PRIMARY, NEUTRAL, DANGER, GHOST}

    private final Variant variant;
    private boolean hovering = false;

    public PillButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setFont(Theme.FONT_BODY_BOLD);
        setForeground(foregroundFor(variant));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    public PillButton(String text) {
        this(text, Variant.NEUTRAL);
    }

    private static Color foregroundFor(Variant v) {
        return switch (v) {
            case PRIMARY -> Theme.TEXT_ON_ACCENT;
            case DANGER -> Theme.TEXT_PRIMARY;
            case GHOST -> Theme.TEXT_SECONDARY;
            case NEUTRAL -> Theme.TEXT_PRIMARY;
        };
    }

    private Color backgroundFor() {
        boolean enabled = isEnabled();
        return switch (variant) {
            case PRIMARY -> !enabled ? Theme.BG_RAISED : hovering ? Theme.ACCENT_HOVER : Theme.ACCENT;
            case DANGER -> !enabled ? Theme.BG_RAISED : hovering ? Theme.DANGER_HOVER : Theme.DANGER;
            case NEUTRAL -> hovering && enabled ? Theme.BG_SURFACE_ALT : Theme.BG_RAISED;
            case GHOST -> hovering && enabled ? Theme.BG_SURFACE : new Color(0, 0, 0, 0);
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundFor());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS);
        g2.dispose();
        setForeground(isEnabled() ? foregroundFor(variant) : Theme.TEXT_MUTED);
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 0), Math.max(d.height, 34));
    }
}
