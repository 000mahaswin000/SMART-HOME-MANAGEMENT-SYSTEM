package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A horizontal bar chart drawn with plain Graphics2D rounded
 * rectangles - no charting library. Each row shows a label on the
 * left, a proportional bar, and a formatted value on the right.
 * Used on the dashboard to break down power consumption per room.
 */
public class BarChart extends JPanel {

    private static final long serialVersionUID = 1L;

    public record Row(String label, double value, String valueText) {
    }

    private List<Row> rows = List.of();
    private static final int ROW_HEIGHT = 26;
    private static final int BAR_HEIGHT = 10;
    private static final int LABEL_WIDTH = 96;
    private static final int VALUE_WIDTH = 70;

    public BarChart() {
        setOpaque(false);
    }

    public void setData(List<Row> rows) {
        this.rows = rows;
        setPreferredSize(new Dimension(getPreferredSize().width, Math.max(1, rows.size()) * ROW_HEIGHT + 6));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rows.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Theme.TEXT_MUTED);
            g2.setFont(Theme.FONT_BODY);
            g2.drawString("No data yet.", 4, 18);
            g2.dispose();
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double max = rows.stream().mapToDouble(Row::value).max().orElse(1.0);
        if (max <= 0) max = 1.0;
        int barAreaWidth = Math.max(40, getWidth() - LABEL_WIDTH - VALUE_WIDTH - 12);

        int rowY = 2;
        for (Row row : rows) {
            g2.setFont(Theme.FONT_SMALL);
            g2.setColor(Theme.TEXT_SECONDARY);
            String label = truncate(row.label(), g2.getFontMetrics(), LABEL_WIDTH - 8);
            g2.drawString(label, 0, rowY + ROW_HEIGHT / 2 + 4);

            int barY = rowY + (ROW_HEIGHT - BAR_HEIGHT) / 2;
            int trackX = LABEL_WIDTH;
            g2.setColor(Theme.BG_RAISED);
            g2.fillRoundRect(trackX, barY, barAreaWidth, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);

            int fillWidth = (int) Math.round((row.value() / max) * barAreaWidth);
            fillWidth = Math.max(row.value() > 0 ? 6 : 0, Math.min(barAreaWidth, fillWidth));
            g2.setColor(Theme.ACCENT);
            if (fillWidth > 0) {
                g2.fillRoundRect(trackX, barY, fillWidth, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
            }

            g2.setFont(Theme.FONT_SMALL_BOLD);
            g2.setColor(Theme.TEXT_PRIMARY);
            String valueText = row.valueText();
            int vw = g2.getFontMetrics().stringWidth(valueText);
            g2.drawString(valueText, trackX + barAreaWidth + 10, rowY + ROW_HEIGHT / 2 + 4);

            rowY += ROW_HEIGHT;
        }
        g2.dispose();
    }

    private static String truncate(String s, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(s) <= maxWidth) return s;
        String ellipsis = "\u2026";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (fm.stringWidth(sb.toString() + c + ellipsis) > maxWidth) break;
            sb.append(c);
        }
        return sb + ellipsis;
    }
}
