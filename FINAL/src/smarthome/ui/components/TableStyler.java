package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Applies a consistent dark, zebra-striped visual style to any
 * JTable in the application: no grid lines, alternating row shading,
 * padded cells, a flat header, and comfortable row height. Every
 * panel calls {@link #style(JTable)} once after constructing its
 * table instead of repeating this boilerplate per-panel.
 */
public final class TableStyler {

    private TableStyler() {
    }

    public static void style(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Theme.BG_SURFACE);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setSelectionBackground(Theme.ACCENT_DIM);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_BODY);
        table.setFillsViewportHeight(true);
        table.setFocusTraversalKeysEnabled(false);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(Theme.BG_RAISED);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setFont(Theme.FONT_SMALL_BOLD);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setDefaultRenderer(new HeaderRenderer(header.getDefaultRenderer()));

        table.setDefaultRenderer(Object.class, new StripedRenderer());
    }

    /** Adds left padding and a bottom border under the header, keeping its default text rendering. */
    private static class HeaderRenderer implements TableCellRenderer {
        private final TableCellRenderer delegate;

        HeaderRenderer(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel label) {
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_SUBTLE),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return c;
        }
    }

    /** Zebra-striped body cell renderer with left padding, matching the header padding. */
    private static class StripedRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT);
                setForeground(Theme.TEXT_PRIMARY);
            }
            return c;
        }
    }
}
