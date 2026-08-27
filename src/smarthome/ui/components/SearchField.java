package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * A rounded search field with a leading magnifier glyph, used to
 * filter the large tables (Devices, Sensors, Automation, Schedules,
 * Alerts, Logs). Callers register a listener via
 * {@link #onChange(Runnable)} which fires on every keystroke; the
 * panel re-applies its own filter against {@link #getQuery()}.
 */
public class SearchField extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField field = new JTextField();

    public SearchField(String placeholder) {
        super(new BorderLayout(6, 0));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STRONG, 1, true),
                new EmptyBorder(6, 12, 6, 12)));

        JLabel glyph = new JLabel("\u2315");
        glyph.setForeground(Theme.TEXT_MUTED);
        glyph.setFont(Theme.FONT_BODY);

        field.setBorder(BorderFactory.createEmptyBorder());
        field.setBackground(new Color(0, 0, 0, 0));
        field.setOpaque(false);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setFont(Theme.FONT_BODY);
        field.setCaretColor(Theme.ACCENT);
        installPlaceholder(placeholder);

        add(glyph, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
        setPreferredSize(new Dimension(220, 34));
    }

    private void installPlaceholder(String placeholder) {
        field.putClientProperty("JTextField.placeholderText", placeholder);
    }

    public String getQuery() {
        return field.getText() == null ? "" : field.getText().trim().toLowerCase();
    }

    /** Programmatically sets the field's text (e.g. seeded from the global search bar), firing onChange listeners. */
    public void setQueryText(String text) {
        field.setText(text);
    }

    public void onChange(Runnable callback) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                callback.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.BG_SURFACE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.RADIUS, Theme.RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}
