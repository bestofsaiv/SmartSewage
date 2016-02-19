package smartsewage;
import java.sql.*;
import java.net.*;
import java.util.*;

public class SensorDataLogger implements SensorDataListener{
  private String username;
  private boolean connected=false;
  private String password;
  private String connectionString;
  private Connection connection;


  public SensorDataLogger(String conn,String user,String pwd)
  {
    Publisher pub=Publisher.getInstance();
    pub.addSensorDataListener(this);
    connectionString=conn;
    username=user;
    password=pwd;
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      connection = DriverManager.getConnection(connectionString,username,password);
      connected=true;
    }
    catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }
   catch(ClassNotFoundException ce)
   {
     ce.printStackTrace();
   }
  }

  public void sensorDataReceived(SensorData data,Socket sock)
  {
    String sql="insert into sensor_log(PsID,level,time) values(?,?,?)";
    PreparedStatement ps=connection.prepareStatement(sql);
    ps.setInt(1,data.getId());
    ps.setInt(2,data.getLevel());
  }
}
