package smartsewage;

import java.net.*;
import java.io.*;

public class DummyScheduler extends Scheduler implements SensorDataListener {
  public DummyScheduler()
  {

  }


  public void sensorDataReceived(SensorData data,Socket sock){
    System.out.println("Got sensor data");
    if(data.getInput(2)==1)
    {
      System.out.println("Turning on pump for "+data.getId());
      RelayCommand cmd=new RelayCommand((byte)data.getId());
      byte[] outputs={1,0,0,0};
      cmd.setOutputs(outputs);
      try{
        PrintWriter out=new PrintWriter(sock.getOutputStream(),true);
        out.println(cmd.toString());
      }
      catch(IOException ex)
      {
        System.out.println(ex.getMessage());
      }
    }
    else
    {
      RelayCommand cmd=new RelayCommand((byte)data.getId());
      byte[] outputs={0,0,0,0};
      cmd.setOutputs(outputs);
      try{
        PrintWriter out=new PrintWriter(sock.getOutputStream(),true);
        out.println(cmd.toString());
      }
      catch(IOException ex)
      {
        System.out.println(ex.getMessage());
      }
    }
  }

  @Override
  public void startScheduler(){
    Publisher pub=Publisher.getInstance();
    pub.addSensorDataListener((SensorDataListener)this);
    while(true)
    {

    }
  }

  static public void main(String[] args)
  {
    DummyScheduler sch=new DummyScheduler();
    sch.startScheduler();

  }
}
