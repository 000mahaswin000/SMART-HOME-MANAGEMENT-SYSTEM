package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A small donut chart drawn with plain Graphics2D arcs - no charting
 * library. Takes a list of (label, value, color) segments and shows
 * the total in the centre. Used on the dashboard for the
 * active-vs-inactive device/sensor breakdown.
 */
public class DonutChart extends JPanel {

    private static final long serialVersionUID = 1L;

    public record Segment(String label, double value, Color color) {
    }

    private List<Segment> segments = List.of();
    private String centerLabel = "";

    public DonutChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(140, 140));
    }

    public void setData(List<Segment> segments, String centerLabel) {
        this.segments = segments;
        this.centerLabel = centerLabel;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 8;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int thickness = Math.max(10, size / 7);

        double total = segments.stream().mapToDouble(Segment::value).sum();
        if (total <= 0) {
            g2.setColor(Theme.BG_RAISED);
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.drawArc(x, y, size, size, 0, 360);
        } else {
            double startAngle = 90;
            for (Segment seg : segments) {
                if (seg.value() <= 0) continue;
                double sweep = -(seg.value() / total) * 360.0;
                g2.setColor(seg.color());
                g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                g2.drawArc(x, y, size, size, (int) Math.round(startAngle), (int) Math.round(sweep));
                startAngle += sweep;
            }
        }
        g2.dispose();

        if (centerLabel != null && !centerLabel.isEmpty()) {
            Graphics2D gt = (Graphics2D) g.create();
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gt.setFont(Theme.FONT_HEADING);
            gt.setColor(Theme.TEXT_PRIMARY);
            FontMetrics fm = gt.getFontMetrics();
            int tw = fm.stringWidth(centerLabel);
            gt.drawString(centerLabel, getWidth() / 2 - tw / 2, getHeight() / 2 + fm.getAscent() / 2 - 3);
            gt.dispose();
        }
    }
}
