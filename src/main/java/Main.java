
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("SMART FARM - IOT SYSTEM");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 24, 38));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("SMART FARM");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("HE THONG GIAM SAT IOT QUA MQTT");
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnDashboard = new JButton("MO DASHBOARD");
        JButton btnSensor = new JButton("DIEU KHIEN CAM BIEN");

        btnDashboard.setMaximumSize(new Dimension(300, 45));
        btnSensor.setMaximumSize(new Dimension(300, 45));

        btnDashboard.addActionListener(e ->
            new MainForm().setVisible(true));

        btnSensor.addActionListener(e ->
            new SensorForm().setVisible(true));

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(35));
        panel.add(btnDashboard);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnSensor);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new Main().setVisible(true));
    }
}
