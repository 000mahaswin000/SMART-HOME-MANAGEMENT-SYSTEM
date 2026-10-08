package smarthome.ui.components;

import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;
import java.awt.*;

/**
 * A page/section title label: bold heading plus a muted subtitle
 * underneath, used at the top of every tab in place of the single
 * plain JLabel the original UI used.
 */
public class SectionTitle extends JPanel {

    private static final long serialVersionUID = 1L;

    public SectionTitle(String title, String subtitle) {
        super();
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(titleLabel);

        if (subtitle != null && !subtitle.isBlank()) {
            add(Box.createVerticalStrut(3));
            JLabel subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(Theme.FONT_SUBTITLE);
            subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
            subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
            add(subtitleLabel);
        }
    }

    /**
     * Shows a transient, non-blocking notification banner ("toast")
     * anchored to the top-right of the given root pane's layered
     * pane, auto-dismissing after the given duration. Used for
     * lightweight confirmations (e.g. "Device added") so the user
     * isn't interrupted by a modal dialog for routine successes.
     */
    public static void showToast(JRootPane rootPane, String message, Color accent, int durationMs) {
        if (rootPane == null) return;
        JLayeredPane layered = rootPane.getLayeredPane();

        JPanel toast = new JPanel(new BorderLayout(8, 0));
        toast.setOpaque(true);
        toast.setBackground(Theme.BG_RAISED);
        toast.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(10, 14, 10, 16)));

        JLabel label = new JLabel(message);
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setFont(Theme.FONT_BODY);
        toast.add(label, BorderLayout.CENTER);

        Dimension pref = toast.getPreferredSize();
        int width = Math.max(220, Math.min(420, pref.width + 10));
        int height = pref.height;
        int x = layered.getWidth() - width - 24;
        int y = 46;
        toast.setBounds(Math.max(8, x), y, width, height);

        layered.add(toast, JLayeredPane.POPUP_LAYER);
        layered.revalidate();
        layered.repaint();

        Timer timer = new Timer(durationMs, e -> {
            layered.remove(toast);
            layered.revalidate();
            layered.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
