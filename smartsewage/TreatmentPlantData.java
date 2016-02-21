package smartsewage;

import java.sql.*;

public class TreatmentPlantData implements SensorDataListener,RelayCommandListener{
  private int TpID;
  private int level;
  private String status;
  private Connection connection;
  private String query="select * from treatment_plant where TpID=?";
  public TreatmentPlantData(int TpID,Connection conn)
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
          level=result.getInt("level");
          status=result.getString("status");
        }
        else{
          System.out.println("Incorrect Id of the treatment plant");
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }
      Publisher.getInstance().addSensorDataListener(this);
    }
    else{
      System.out.pritnln("Connection does  not exist");
    }
  }

  public void sensorDataReceived(SensorData data,Socket sock)
  {
    if(data.getId()==TpID)
    {
      
    }
  }
}
