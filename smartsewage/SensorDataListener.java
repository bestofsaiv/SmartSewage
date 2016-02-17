package smartsewage;

import java.net.*;

public interface SensorDataListener{
  void sensorDataReceived(SensorData data,Socket sock);
}
