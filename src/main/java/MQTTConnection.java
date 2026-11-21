
import org.eclipse.paho.client.mqttv3.*;
import java.util.UUID;

public class MQTTConnection {

    private MqttClient client;

    private final String broker = "tcp://broker.hivemq.com:1883";

    public void connect() {
        try {
            String clientId = "JavaDashboard_" + UUID.randomUUID();

            client = new MqttClient(broker, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.connect(options);

            System.out.println("Da ket noi MQTT Broker!");

        } catch (MqttException e) {
            System.out.println("Loi ket noi: " + e.getMessage());
        }
    }

    public void subscribe(String topic, IMqttMessageListener listener) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic, 0, listener);
                System.out.println("Dang theo doi: " + topic);
            }
        } catch (MqttException e) {
            System.out.println("Loi Subscribe: " + e.getMessage());
        }
    }

    public void publish(String topic, String data) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(data.getBytes());
                message.setQos(0);
                client.publish(topic, message);
            }
        } catch (MqttException e) {
            System.out.println("Loi Publish: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                System.out.println("Da ngat ket noi MQTT!");
            }
        } catch (MqttException e) {
            System.out.println("Loi ngat ket noi: " + e.getMessage());
        }
    }
}
