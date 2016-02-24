package smartsewage;

import java.sql.*;
import java.net.*;
//num is ??
public class TreatmentPlantInput implements SensorDataListener,Runnable{
  private int TpID;
  private int PsID;
  private int num;
  private Time switchedOnAt;
  private Time duration;
  private String status;
  private Connection connection;
  private String query="select * from treatment_plant_input where TpID=?";
  private String query_ps="select * from pumping_station  where PsID=?";
  private String update_query="update treatment_plant_input set PsID=?, switchedOnAt=? ,duration=?, status=? where TpID=?";
  //A thread that will update the db at the required rate
  private Thread updater;
  public TreatmentPlantInputData(int TpID,Connection conn)
  {
    this.TpID=TpID;
    this.connection=conn;
    if(conn!=null){
      try{
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setInt(1,TpID);
        ResultSet result=ps.executeQuery();
        if(result.next())
        {
          pumpID = result.getInt("PsID");
          num_1 = result.getInt("num");
          status_1 = result.getString("status");
          switched_on_at = result.getTime("switchedOnAt");
          duration_1 = result.getTime("duration");

        }
        else{
          System.out.println("Incorrect Id of the treatment plant");
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
      System.out.println("Connection does  not exist");
    }
  }

  public void sensorDataReceived(SensorData data,Socket sock)
  {
//if a new Pumping station id is received, then update
    int new_id = data.getId();

    if(new_id != pumpID)
    {
      updater.start();
    }
    else
    {
      System.out.println("No new pumping station request");
    }
  }

  public void run()
  {
    update();
  }

  public void update()
  {
    try{
      PreparedStatement ps=connection.prepareStatement(update_query);
      ps.setInt(1,new_id);
      ps.setTime(2,//switched_on_at);
      ps.setInt(3,//duration);
      ps.setString(4,status)
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }
}
