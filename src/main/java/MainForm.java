
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainForm extends JFrame {

    private MQTTConnection mqtt = new MQTTConnection();

    private JLabel lblTemperature;
    private JLabel lblHumidity;
    private JLabel lblStatus;
    private JLabel lblConnection;
    private JLabel lblWarning;

    private JTextArea history;
    private ChartPanel chart;

    private List<Double> temperatures = new ArrayList<>();
    private List<Double> humidities = new ArrayList<>();

    private final Color BACKGROUND = new Color(18, 24, 38);
    private final Color PANEL = new Color(30, 41, 59);
    private final Color ORANGE = new Color(255, 145, 77);
    private final Color BLUE = new Color(75, 160, 255);

    public MainForm() {
        setTitle("SMART FARM - MQTT DASHBOARD");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND);

        createHeader();
        createDashboard();
        createHistory();

        connectMQTT();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL);
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("SMART FARM DASHBOARD");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        lblConnection = new JLabel("● DISCONNECTED");
        lblConnection.setForeground(Color.RED);
        lblConnection.setFont(new Font("Arial", Font.BOLD, 14));

        header.add(title, BorderLayout.WEST);
        header.add(lblConnection, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createDashboard() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.setBackground(BACKGROUND);

        lblTemperature = createCard(cards, "NHIỆT ĐỘ", "-- °C", ORANGE);
        lblHumidity = createCard(cards, "ĐỘ ẨM", "-- %", BLUE);
        lblStatus = createCard(cards, "TRẠNG THÁI", "ĐANG CHỜ", Color.GREEN);

        chart = new ChartPanel();

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(PANEL);
        chartContainer.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel chartTitle = new JLabel("BIỂU ĐỒ GIÁM SÁT REALTIME");
        chartTitle.setForeground(Color.WHITE);
        chartTitle.setFont(new Font("Arial", Font.BOLD, 16));

        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chart, BorderLayout.CENTER);

        lblWarning = new JLabel("Hệ thống đang chờ dữ liệu cảm biến...");
        lblWarning.setForeground(Color.WHITE);
        lblWarning.setFont(new Font("Arial", Font.BOLD, 14));

        JButton btnSensor = new JButton("ĐIỀU KHIỂN CẢM BIẾN");
        btnSensor.addActionListener(e ->
            new SensorForm().setVisible(true)
        );

        JButton btnClear = new JButton("XÓA LỊCH SỬ");
        btnClear.addActionListener(e -> history.setText(""));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BACKGROUND);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(BACKGROUND);
        buttons.add(btnSensor);
        buttons.add(btnClear);

        bottom.add(lblWarning, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        main.add(cards, BorderLayout.NORTH);
        main.add(chartContainer, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
    }

    private JLabel createCard(
        JPanel parent, String title, String value, Color color
    ) {
        JPanel card = new JPanel(new BorderLayout(10, 15));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel name = new JLabel(title);
        name.setForeground(Color.LIGHT_GRAY);
        name.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel data = new JLabel(value);
        data.setForeground(color);
        data.setFont(new Font("Arial", Font.BOLD, 27));

        card.add(name, BorderLayout.NORTH);
        card.add(data, BorderLayout.CENTER);

        parent.add(card);

        return data;
    }

    private void createHistory() {
        history = new JTextArea(8, 30);
        history.setEditable(false);
        history.setBackground(PANEL);
        history.setForeground(Color.WHITE);
        history.setFont(new Font("Consolas", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(history);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "LỊCH SỬ DỮ LIỆU MQTT"
        ));

        scroll.setPreferredSize(new Dimension(1100, 150));

        add(scroll, BorderLayout.SOUTH);
    }

    private void connectMQTT() {
        mqtt.connect();

        if (mqtt.isConnected()) {
            lblConnection.setText("● CONNECTED");
            lblConnection.setForeground(Color.GREEN);

            mqtt.subscribe("farm/cow1/temperature",
                (topic, message) -> {
                    String data = new String(message.getPayload());

                    SwingUtilities.invokeLater(() ->
                        updateTemperature(data)
                    );
                }
            );

            mqtt.subscribe("farm/cow1/humidity",
                (topic, message) -> {
                    String data = new String(message.getPayload());

                    SwingUtilities.invokeLater(() ->
                        updateHumidity(data)
                    );
                }
            );
        }
    }

    private void updateTemperature(String data) {
        try {
            double value = Double.parseDouble(data);

            lblTemperature.setText(data + " °C");

            temperatures.add(value);

            if (temperatures.size() > 20) {
                temperatures.remove(0);
            }

            chart.repaint();
            updateWarning();
            addHistory("NHIỆT ĐỘ", data + " °C");

        } catch (NumberFormatException e) {
            addHistory("LỖI", "Dữ liệu nhiệt độ không hợp lệ");
        }
    }

    private void updateHumidity(String data) {
        try {
            double value = Double.parseDouble(data);

            lblHumidity.setText(data + " %");

            humidities.add(value);

            if (humidities.size() > 20) {
                humidities.remove(0);
            }

            chart.repaint();
            updateWarning();
            addHistory("ĐỘ ẨM", data + " %");

        } catch (NumberFormatException e) {
            addHistory("LỖI", "Dữ liệu độ ẩm không hợp lệ");
        }
    }

    private void updateWarning() {
        if (!temperatures.isEmpty()) {
            double temp = temperatures.get(temperatures.size() - 1);

            if (temp >= 38) {
                lblStatus.setText("CẢNH BÁO");
                lblStatus.setForeground(Color.RED);
                lblWarning.setText("CẢNH BÁO: NHIỆT ĐỘ CAO!");
                lblWarning.setForeground(Color.RED);
                return;
            }
        }

        if (!humidities.isEmpty()) {
            double humidity = humidities.get(humidities.size() - 1);

            if (humidity > 80 || humidity < 30) {
                lblStatus.setText("CẢNH BÁO");
                lblStatus.setForeground(Color.RED);
                lblWarning.setText("CẢNH BÁO: ĐỘ ẨM BẤT THƯỜNG!");
                lblWarning.setForeground(Color.RED);
                return;
            }
        }

        lblStatus.setText("BÌNH THƯỜNG");
        lblStatus.setForeground(Color.GREEN);
        lblWarning.setText("Các chỉ số cảm biến đang ổn định.");
        lblWarning.setForeground(Color.GREEN);
    }

    private void addHistory(String type, String value) {
        String time = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss"
        ).format(new Date());

        history.append(
            "[" + time + "] " + type + ": " + value + "\n"
        );

        history.setCaretPosition(
            history.getDocument().getLength()
        );
    }

    class ChartPanel extends JPanel {

        public ChartPanel() {
            setBackground(PANEL);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            g2.setColor(Color.GRAY);

            for (int i = 1; i <= 4; i++) {
                int y = i * height / 5;
                g2.drawLine(45, y, width - 20, y);
            }

            drawLine(g2, temperatures, ORANGE);
            drawLine(g2, humidities, BLUE);

            g2.setColor(ORANGE);
            g2.drawString("Nhiệt độ", 50, 15);

            g2.setColor(BLUE);
            g2.drawString("Độ ẩm", 130, 15);
        }

        private void drawLine(
            Graphics2D g2, List<Double> values, Color color
        ) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(3));

            for (int i = 1; i < values.size(); i++) {
                int x1 = 45 + (i - 1) * (getWidth() - 65) / 19;
                int x2 = 45 + i * (getWidth() - 65) / 19;

                int y1 = getHeight() - 20
                    - (int)(values.get(i - 1) * 2.5);

                int y2 = getHeight() - 20
                    - (int)(values.get(i) * 2.5);

                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
