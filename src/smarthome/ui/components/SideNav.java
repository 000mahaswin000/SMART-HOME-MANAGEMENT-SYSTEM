package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * A vertical icon+label navigation rail replacing the stock
 * JTabbedPane's row of small text tabs. Each entry is a glyph plus
 * a label; the active entry is highlighted with a rounded fill and
 * a left accent bar. Optionally shows a small numeric badge (used
 * for unread alert count).
 */
public class SideNav extends JPanel {

    private static final long serialVersionUID = 1L;

    private final List<Entry> entries = new ArrayList<>();
    private int selectedIndex = 0;
    private IntConsumer onSelect;

    public SideNav() {
        setOpaque(true);
        setBackground(Theme.BG_DEEPEST);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 8, 10, 8));
    }

    public void addItem(String glyph, String label) {
        Entry entry = new Entry(glyph, label, entries.size());
        entries.add(entry);
        add(entry);
        add(Box.createVerticalStrut(3));
        revalidate();
        repaint();
    }

    public void setOnSelect(IntConsumer onSelect) {
        this.onSelect = onSelect;
    }

    public void setBadge(int index, int count) {
        if (index >= 0 && index < entries.size()) {
            entries.get(index).setBadge(count);
        }
    }

    public void select(int index) {
        if (index < 0 || index >= entries.size() || index == selectedIndex) return;
        entries.get(selectedIndex).setSelected(false);
        selectedIndex = index;
        entries.get(selectedIndex).setSelected(true);
        if (onSelect != null) onSelect.accept(index);
    }

    private class Entry extends JPanel {
        private static final long serialVersionUID = 1L;
        private boolean selected;
        private boolean hovering;
        private int badgeCount = 0;
        private final JLabel glyphLabel;
        private final JLabel textLabel;
        private final JLabel badgeLabel;

        Entry(String glyph, String text, int index) {
            super(new BorderLayout(10, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(9, 12, 9, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            glyphLabel = new JLabel(glyph);
            glyphLabel.setFont(Theme.FONT_BODY.deriveFont(15f));
            glyphLabel.setForeground(Theme.TEXT_SECONDARY);
            glyphLabel.setPreferredSize(new Dimension(20, 20));
            glyphLabel.setHorizontalAlignment(SwingConstants.CENTER);

            textLabel = new JLabel(text);
            textLabel.setFont(Theme.FONT_BODY_BOLD);
            textLabel.setForeground(Theme.TEXT_SECONDARY);

            badgeLabel = new JLabel("");
            badgeLabel.setFont(Theme.FONT_SMALL_BOLD);
            badgeLabel.setForeground(Theme.TEXT_ON_ACCENT);
            badgeLabel.setOpaque(false);
            badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            badgeLabel.setVisible(false);

            JPanel left = new JPanel(new BorderLayout(10, 0));
            left.setOpaque(false);
            left.add(glyphLabel, BorderLayout.WEST);
            left.add(textLabel, BorderLayout.CENTER);

            add(left, BorderLayout.CENTER);
            add(badgeWrap(), BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    select(index);
                }

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

        private JPanel badgeWrap() {
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(badgeLabel, BorderLayout.CENTER);
            return wrap;
        }

        void setBadge(int count) {
            this.badgeCount = count;
            badgeLabel.setText(count > 99 ? "99+" : String.valueOf(count));
            badgeLabel.setVisible(count > 0);
            badgeLabel.setPreferredSize(count > 0 ? new Dimension(count > 9 ? 28 : 20, 18) : new Dimension(0, 0));
            revalidate();
            repaint();
        }

        void setSelected(boolean selected) {
            this.selected = selected;
            glyphLabel.setForeground(selected ? Theme.ACCENT : Theme.TEXT_SECONDARY);
            textLabel.setForeground(selected ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected) {
                g2.setColor(Theme.BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS);
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(0, 4, 3, getHeight() - 8, 3, 3);
            } else if (hovering) {
                g2.setColor(Theme.BG_BASE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS);
            }
            if (badgeCount > 0) {
                g2.setColor(Theme.DANGER);
                int bw = badgeLabel.getWidth() > 0 ? badgeLabel.getWidth() : 20;
                int bh = 18;
                int bx = getWidth() - bw - 12;
                int by = (getHeight() - bh) / 2;
                g2.fillRoundRect(bx, by, bw, bh, bh, bh);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
