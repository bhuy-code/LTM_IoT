
import javax.swing.*;
import java.awt.*;

public class SensorForm extends JFrame {

    private MQTTConnection mqtt = new MQTTConnection();

    private JTextField txtTemperature;
    private JTextField txtHumidity;
    private JLabel lblStatus;

    public SensorForm() {
        setTitle("SMART FARM - SENSOR CONTROL");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(new Color(25, 35, 52));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("ĐIỀU KHIỂN CẢM BIẾN", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        txtTemperature = new JTextField("30");
        txtHumidity = new JTextField("60");

        panel.add(createRow("Nhiệt độ (°C):", txtTemperature));
        panel.add(createRow("Độ ẩm (%):", txtHumidity));

        JButton btnSend = new JButton("GỬI DỮ LIỆU MQTT");
        btnSend.setBackground(new Color(60, 180, 160));
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(new Font("Arial", Font.BOLD, 14));

        btnSend.addActionListener(e -> sendData());
        panel.add(btnSend);

        lblStatus = new JLabel("Đang chờ gửi dữ liệu...", JLabel.CENTER);
        lblStatus.setForeground(Color.WHITE);
        panel.add(lblStatus);

        add(panel);

        mqtt.connect();
    }

    private JPanel createRow(String name, JTextField field) {
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 10));
        row.setBackground(new Color(25, 35, 52));

        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);

        row.add(label);
        row.add(field);

        return row;
    }

    private void sendData() {
        try {
            double temperature = Double.parseDouble(txtTemperature.getText());
            double humidity = Double.parseDouble(txtHumidity.getText());

            if (temperature < -50 || temperature > 100 ||
                humidity < 0 || humidity > 100) {
                lblStatus.setText("Giá trị cảm biến không hợp lệ!");
                return;
            }

            mqtt.publish("farm/cow1/temperature",
                    String.valueOf(temperature));

            mqtt.publish("farm/cow1/humidity",
                    String.valueOf(humidity));

            lblStatus.setText("Đã gửi dữ liệu thành công!");

        } catch (NumberFormatException e) {
            lblStatus.setText("Vui lòng nhập số hợp lệ!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SensorForm().setVisible(true);
        });
    }
}
