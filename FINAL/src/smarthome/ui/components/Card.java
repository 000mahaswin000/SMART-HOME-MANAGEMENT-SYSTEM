package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A rounded-corner surface panel with a subtle border, used as the
 * building block for stat cards, form sections and grouped content
 * throughout the app, replacing the plain rectangular
 * {@code TitledBorder} panels of the original UI.
 */
public class Card extends JPanel {

    private static final long serialVersionUID = 1L;

    private Color background = Theme.BG_SURFACE;
    private Color borderColor = Theme.BORDER_SUBTLE;
    private int radius = Theme.RADIUS;

    public Card() {
        this(new BorderLayout(10, 10));
    }

    public Card(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));
    }

    public Card withBackground(Color color) {
        this.background = color;
        return this;
    }

    public Card withBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    public Card withRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public Card withPadding(int top, int left, int bottom, int right) {
        setBorder(new EmptyBorder(top, left, bottom, right));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
