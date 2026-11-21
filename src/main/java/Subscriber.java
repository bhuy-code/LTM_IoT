
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subscriber {

    public static void main(String[] args) {

        // Địa chỉ MQTT Broker
        String broker = "tcp://localhost:1883";

        // Topic cần nhận dữ liệu
        String topic = "farm/cow1/temperature";

        // Mã định danh Subscriber
        String clientId = "JavaSubscriber";

        try {
            // Tạo MQTT Client
            MqttClient client = new MqttClient(broker, clientId);

            // Kết nối Broker
            client.connect();

            System.out.println("Da ket noi MQTT Broker!");

            System.out.println("Dang theo doi: " + topic);
            System.out.println("Cho du lieu...\n");

            // Xử lý khi nhận được dữ liệu
            client.setCallback(new MqttCallback() {

                @Override
                public void messageArrived(
                    String receivedTopic,
                    MqttMessage message
                ) throws Exception {

                    // Lấy dữ liệu nhận được
                    String data = new String(message.getPayload());

                    // Lấy thời gian hiện tại
                    LocalDateTime now = LocalDateTime.now();

                    DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                    String time = now.format(formatter);

                    // Hiển thị dữ liệu
                    System.out.println("Thoi gian: " + time);
                    System.out.println("Topic: " + receivedTopic);
                    System.out.println("Nhiet do: " + data + " °C");
                    System.out.println("--------------------------");

                    // Ghi dữ liệu vào file
                    try (FileWriter writer =
                        new FileWriter("data.txt", true)) {

                        writer.write(
                            time + " | Nhiet do: " + data + " °C\n"
                        );
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Mat ket noi MQTT Broker!");
                }

                @Override
                public void deliveryComplete(
                    org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token
                ) {
                    // Không cần xử lý vì Subscriber không gửi dữ liệu
                }
            });
            client.subscribe(topic);

        } catch (Exception e) {
            System.out.println("Loi Subscriber: " + e.getMessage());
        }
    }
}