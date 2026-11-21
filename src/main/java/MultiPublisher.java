
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class MultiPublisher {

    public static void main(String[] args) {

        String broker = "tcp://localhost:1883";
        String clientId = "MultiPublisher";

        try {
            // Kết nối MQTT Broker
            MqttClient client = new MqttClient(broker, clientId);
            client.connect();

            System.out.println("Da ket noi Broker!");
            System.out.println("Bat dau gui nhieu loai du lieu...\n");

            Random random = new Random();

            while (true) {

                // Tạo nhiệt độ ngẫu nhiên
                double temperature = 35 + random.nextDouble() * 4;

                // Tạo độ ẩm ngẫu nhiên
                int humidity = 50 + random.nextInt(31);

                // Tạo trạng thái cảm biến
                String status = "Hoat dong binh thuong";

                // Gửi nhiệt độ
                guiDuLieu(client,
                    "farm/cow1/temperature",
                    String.format("%.1f", temperature));

                // Gửi độ ẩm
                guiDuLieu(client,
                    "farm/cow1/humidity",
                    humidity + "%");

                // Gửi trạng thái
                guiDuLieu(client,
                    "farm/cow1/status",
                    status);

                System.out.println("----------------------");

                // Chờ 2 giây
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    // Hàm gửi dữ liệu lên MQTT Broker
    public static void guiDuLieu(
        MqttClient client,
        String topic,
        String data
    ) throws Exception {

        MqttMessage message =
            new MqttMessage(data.getBytes());

        // Sử dụng QoS 0
        message.setQos(0);

        // Publish dữ liệu
        client.publish(topic, message);

        System.out.println(topic + ": " + data);
    }
}