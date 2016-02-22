package smartsewage;

import java.sql.*;
import java.net.*;

public class PumpingStationData implements SensorDataListener,Runnable{
  private int PsID;
  private String location;
  private int priority;
  private int capacity;
  private int level;
  private TimeStamp lastSwitchedOff;
  private Time durationLastOn;
  private Time minTimeToEmpty;
  private String status;
  private Connection connection;
  private PumpData pump;
  //A thread that will update the db at the required rate
  private Thread updater;

  private String query="select * from pumping_station where PsID=?";

  private String query_update_sensor="update pumping_station set minTimeToEmpty=?, status= ? where PsID=?";
  private String query_update_relay="update pumping_station set durationLastOn=? , lastSwitchedOff=? , status=?";

  public PumpingStationData(int PsID,Connection conn)
  {
    this.PsID=PsId;
    this.connection=conn;
    if(conn!=null)
    {
      try{
        PreparedStatement ps=connection.prepareStatement(query);
        ps.setInt(1,PsID);
        ResultSet result=ps.executeQuery();
        if(result.next())
        {
          location=result.getString("location");
          priority=result.getInt("priority");
          capacity=result.getInt("capacity");
          level=result.getInt("level");
          lastSwitchedOff=result.getTimeStamp("lastSwitchedOff");
          durationLastOn=result.getTime("durationLastOn");
          minTimeToEmpty=result.getTime("minTimeToEmpty");
          status=result.getString("status");
        }
        else{
          System.out.println("Incorrect ID");
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }
      updater=new Thread(this);
      Publisher.getInstance().addSensorDataListener(this);
    }
    else{
        System.out.println("Connection does not exist");
    }
  }

  public void sensorDataReceived(SensorData data,Socket sock)
  {
    if(data.getId()==PsID)
    {
      int old=level;
      level=data.getLevel();
      if(old!=level)
        updater.start();
    }
  }

  public void update()
  {
    //Calculate min time to empty
    //minTimeToEmpty=(level*capacity/5)/pumpingrate;
    if(status.equals("OFF")){
      if((minTimeToEmpty>=minRunTime)&&System.currentTimeMillis()-lastSwitchedOff.getTime()>=durationLastOn.getTime())
      {
        status="AVAILABLE";
      }
      PreparedStatement ps=connection.prepareStatement(query_update_sensor);
      ps.setTime(1,minTimeToEmpty);
      ps.setString(2,status);
      ps.setInt(3,PsID);
      ps.execute();
    }

  }

  public void run()
  {
    update();
  }

  //Getter methods
  public int getPsID()
  {
    return PsID;
  }

  public int getLevel()
  {
    return level;
  }

  public int getPriority()
  {
    return priority;
  }

  public int getCapacity()
  {
    return capacity;
  }

  public String getLocation()
  {
    return location;
  }

  public Time getDurationLastOn()
  {
    return durationLastOn;
  }

  public Time getMinTimeToEmpty()
  {
    return minTimeToEmpty;
  }

  public TimeStamp getLastSwitchedOff()
  {
    return lastSwitchedOff;
  }

  public String getStatus()
  {
    return status;
  }

  public PumpData getPump()
  {
    return pump;
  }

}
