
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class Publisher {

    public static void main(String[] args) {

        // Địa chỉ MQTT Broker
        String broker = "tcp://localhost:1883";

        // Topic gửi dữ liệu nhiệt độ
        String topic = "farm/cow1/temperature";

        // Tạo mã định danh cho Publisher
        String clientId = "JavaPublisher";

        try {
            // Tạo kết nối đến MQTT Broker
            MqttClient client = new MqttClient(broker, clientId);

            // Kết nối Broker
            client.connect();

            System.out.println("Da ket noi MQTT Broker!");
            System.out.println("Bat dau gui du lieu nhiet do...\n");

            Random random = new Random();

            // Gửi dữ liệu liên tục
            while (true) {

                // Tạo nhiệt độ ngẫu nhiên từ 35 đến 39 độ C
                double temperature = 35 + random.nextDouble() * 4;

                // Chuyển nhiệt độ thành chuỗi
                String data = String.format("%.1f", temperature);

                // Tạo thông điệp MQTT
                MqttMessage message = new MqttMessage(
                    data.getBytes()
                );

                // QoS 0: Gửi dữ liệu không yêu cầu xác nhận
                message.setQos(0);

                // Gửi dữ liệu lên Topic
                client.publish(topic, message);

                System.out.println("Da gui: " + data + " °C");

                // Chờ 2 giây trước khi gửi tiếp
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            System.out.println("Loi Publisher: " + e.getMessage());
        }
    }
}