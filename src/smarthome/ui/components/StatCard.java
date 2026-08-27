package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A dashboard summary tile: a glyph in a tinted circle, a large
 * value, a caption underneath, and an optional secondary line for
 * extra context (e.g. "3 of 10 rooms"). Replaces the plain
 * bordered-panel-with-two-labels stat tiles from the original
 * dashboard.
 */
public class StatCard extends Card {

    private static final long serialVersionUID = 1L;

    private final JLabel valueLabel = new JLabel("0");
    private final JLabel captionLabel;
    private final JLabel glyphLabel;
    private final Color accent;

    public StatCard(String glyph, String caption, Color accent) {
        super(new BorderLayout(0, 2));
        this.accent = accent;
        withPadding(14, 16, 14, 16);

        glyphLabel = new JLabel(glyph);
        glyphLabel.setOpaque(true);
        glyphLabel.setBackground(tint(accent));
        glyphLabel.setForeground(accent);
        glyphLabel.setFont(Theme.FONT_HEADING.deriveFont(15f));
        glyphLabel.setHorizontalAlignment(SwingConstants.CENTER);
        glyphLabel.setPreferredSize(new Dimension(34, 34));
        RoundedIcon iconWrap = new RoundedIcon(glyphLabel, accent);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(iconWrap, BorderLayout.WEST);

        valueLabel.setFont(Theme.FONT_STAT_VALUE);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        captionLabel = new JLabel(caption);
        captionLabel.setFont(Theme.FONT_SMALL);
        captionLabel.setForeground(Theme.TEXT_SECONDARY);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        captionLabel.setAlignmentX(LEFT_ALIGNMENT);
        textStack.add(Box.createVerticalStrut(10));
        textStack.add(valueLabel);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(captionLabel);

        add(top, BorderLayout.NORTH);
        add(textStack, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
        valueLabel.setFont(value != null && value.length() > 7
                ? Theme.FONT_STAT_VALUE.deriveFont(19f)
                : Theme.FONT_STAT_VALUE);
    }

    public void setValueColor(Color color) {
        valueLabel.setForeground(color);
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    private static Color tint(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 40);
    }

    /** Small helper: paints its child inside a filled rounded circle rather than a square background. */
    private static class RoundedIcon extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color color;

        RoundedIcon(JComponent child, Color color) {
            super(new BorderLayout());
            this.color = color;
            setOpaque(false);
            add(child, BorderLayout.CENTER);
            child.setOpaque(false);
            setPreferredSize(new Dimension(34, 34));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(tint(color));
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
