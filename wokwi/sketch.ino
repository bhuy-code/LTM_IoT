
#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>

// Cau hinh cam bien DHT22
#define DHT_PIN 15
#define DHT_TYPE DHT22

DHT dht(DHT_PIN, DHT_TYPE);

// WiFi Wokwi
const char* WIFI_SSID = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";

// MQTT Broker tren may tinh
const char* MQTT_SERVER = "host.wokwi.internal";
const int MQTT_PORT = 1883;

// Topic MQTT
const char* TOPIC_TEMP = "farm/cow1/temperature";
const char* TOPIC_HUMIDITY = "farm/cow1/humidity";

WiFiClient espClient;
PubSubClient mqtt(espClient);

unsigned long lastSend = 0;

void connectWiFi() {
  Serial.print("Dang ket noi WiFi");

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD, 6);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nDa ket noi WiFi!");
  Serial.println(WiFi.localIP());
}

void connectMQTT() {
  while (!mqtt.connected()) {
    Serial.print("Dang ket noi MQTT...");

    String clientId = "ESP32_SmartFarm_";
    clientId += String(random(0xffff), HEX);

    if (mqtt.connect(clientId.c_str())) {
      Serial.println("Thanh cong!");
    } else {
      Serial.print("That bai, ma loi: ");
      Serial.println(mqtt.state());
      delay(2000);
    }
  }
}

void setup() {
  Serial.begin(115200);

  dht.begin();

  connectWiFi();

  mqtt.setServer(MQTT_SERVER, MQTT_PORT);

  connectMQTT();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();
  }

  if (!mqtt.connected()) {
    connectMQTT();
  }

  mqtt.loop();

  if (millis() - lastSend >= 2000) {
    lastSend = millis();

    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();

    if (isnan(temperature) || isnan(humidity)) {
      Serial.println("Loi doc cam bien DHT22!");
      return;
    }

    char tempData[10];
    char humidityData[10];

    dtostrf(temperature, 1, 1, tempData);
    dtostrf(humidity, 1, 1, humidityData);

    mqtt.publish(TOPIC_TEMP, tempData);
    mqtt.publish(TOPIC_HUMIDITY, humidityData);

    Serial.print("Nhiet do: ");
    Serial.print(tempData);
    Serial.println(" °C");

    Serial.print("Do am: ");
    Serial.print(humidityData);
    Serial.println(" %");

    Serial.println("----------------------");
  }
}
