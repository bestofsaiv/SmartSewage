package smartsewage;

import java.sql.*;
import java.net.*;

public class PumpingStationData implements SensorDataListener,Runnable{
  private int PsID;
  private String location;
  private int priority;
  private long capacity; //capacity in ml
  private int level;
  private Timestamp lastSwitchedOff;
  private Time durationLastOn;
  private Time minTimeToEmpty;
  private String status;
  private Connection connection;
  //private PumpData pump;
  private Socket sock;
  //A thread that will update the db at the required rate
  private Thread updater;

  private String query="select * from pumping_station where PsID=?";

  private String query_update_sensor="update pumping_station set minTimeToEmpty=?, status= ? where PsID=?";
  private String query_update_relay="update pumping_station set durationLastOn=? , lastSwitchedOff=? , status=?";

  public PumpingStationData(int PsID,Connection conn)
  {
    this.PsID=PsID;
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
          lastSwitchedOff=result.getTimestamp("lastSwitchedOff");
          durationLastOn=result.getTime("durationLastOn");
          minTimeToEmpty=result.getTime("minTimeToEmpty");
          status=result.getString("status");
          int pumpId=result.getInt("PumpID");
          //pump=new PumpData(pumpId);
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
    this.sock=sock;
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
      /*if((minTimeToEmpty>=minRunTime)&&System.currentTimeMillis()-lastSwitchedOff.getTime()>=durationLastOn.getTime())
      {
        status="AVAILABLE";
      }*/
      try{
        PreparedStatement ps=connection.prepareStatement(query_update_sensor);
        ps.setTime(1,minTimeToEmpty);
        ps.setString(2,status);
        ps.setInt(3,PsID);
        ps.execute();
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }

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

  public long getCapacity()
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

  public Timestamp getLastSwitchedOff()
  {
    return lastSwitchedOff;
  }

  public String getStatus()
  {
    return status;
  }

  /*public PumpData getPump()
  {
    return pump;
  }*/

  /**
  * Static method used to get and generate all the pumping stations belonging to a given treatment plant
  */
  public static ArrayList<PumpingStation> getPumpingStations(int TpID,Connection conn)
  {
    ArrayList<PumpingStation> list=new ArrayList<PumpingStation>();
    if(conn!=null)
    {
      try{
        String query="select PsID from pumping_station where TpID=?";
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setInt(1,TpID);
        ResultSet rs=ps.executeQuery();
        while(rs.next())
        {
          int PsID=rs.getInt("PsID");
          PumpingStation new_pumping_station=new PumpingStation(PsID,conn);
          list.add(new_pumping_station);
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }

    }
    else{
      System.out.println("No connection to database");
    }
    return list;
  }

}
