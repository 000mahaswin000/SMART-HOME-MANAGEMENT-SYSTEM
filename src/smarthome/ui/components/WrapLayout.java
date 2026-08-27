package smarthome.ui.components;

import java.awt.*;

/**
 * A FlowLayout variant that wraps to a new line and reports a
 * correct preferred height for its container, so it behaves properly
 * inside a JScrollPane (unlike stock FlowLayout, whose preferred size
 * ignores wrapping and causes the scroll pane to clip content).
 * Used for the room card grid and the dashboard stat card grid.
 */
public class WrapLayout extends FlowLayout {

    private static final long serialVersionUID = 1L;

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth == 0) {
                Container parent = target.getParent();
                targetWidth = (parent != null && parent.getWidth() > 0) ? parent.getWidth() : Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
            if (maxWidth <= 0) maxWidth = Integer.MAX_VALUE;

            int x = 0;
            int y = insets.top + vgap;
            int rowHeight = 0;

            for (Component m : target.getComponents()) {
                if (!m.isVisible()) continue;
                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                if (x == 0 || x + d.width <= maxWidth) {
                    if (x > 0) x += hgap;
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                } else {
                    y += rowHeight + vgap;
                    x = d.width;
                    rowHeight = d.height;
                }
            }
            y += rowHeight + vgap;
            return new Dimension(targetWidth, y);
        }
    }
}
