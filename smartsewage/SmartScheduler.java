package smartsewage;

public class SmartScheduler extends Scheduler{
  private TreatmentPlant tp;
  private ArrayList<PumpingStation> ps;
  /**
  * The username of the database to be connected to
  */
  private String username;
  /**
  * Tracking wherther the connection to databse has been successfully established
  */
  private boolean connected=false;
  /**
  * The password of the databse to be connected to
  */
  private String password;
  /**
  * The connection string to be used for connecting to the database
  */
  private String connectionString;
  /**
  * The databse connection object to add the data to the database
  */
  private Connection connection;

  public SmartScheduler(int TpID,String conn,String user,String pwd)
  {
    connection=conn;
    username=user;
    password=pwd;
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      connection = DriverManager.getConnection(connectionString,username,password);
      connected=true;
      System.out.println("Connected");
      tp=new TreatmentPlant(TpID,connection);
      ps=PumpingStation.getPumpingStations(TpID,connection);
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
}
