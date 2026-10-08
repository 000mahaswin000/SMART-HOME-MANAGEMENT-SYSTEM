package smarthome.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A vertical BoxLayout panel that implements {@link Scrollable} and
 * tracks the enclosing JScrollPane's viewport width. Without this,
 * JScrollPane lets its view grow to its natural preferred width and
 * only adds a horizontal scrollbar instead of wrapping content or
 * resizing to fit - which breaks both WrapLayout-based grids (they
 * never see a bounded width to wrap against) and any child relying
 * on "fill available width" behaviour. Every tab's scrollable root
 * panel should use this instead of a plain JPanel.
 */
public class ScrollableContent extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    public ScrollableContent() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
