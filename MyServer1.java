import java.io.*;  
import java.net.*;  
import java.util.*;

public class MyServer1 {  
	public static void main(String[] args){  
		try{  
			String servername="cs.ssn.edu";
			ServerSocket serverSocket=new ServerSocket(6666);  
			Socket socket=serverSocket.accept();//establishes connection  
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());  
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

			String  str=(String)dataInputStream.readUTF();  
			dataOutputStream.writeUTF("connected to "+servername);  
			str= (""+"username: ");
			dataOutputStream.writeUTF(str);
			dataOutputStream.flush();

			String  username=(String)dataInputStream.readUTF();
			System.out.println("Username: " + username);
			str= (""+"Password: ");
			dataOutputStream.writeUTF(str);
			dataOutputStream.flush();

			String  password=(String)dataInputStream.readUTF();
			System.out.println("password: "+password);

			if((username.compareTo("akshay")==0) && (password.compareTo("pass")==0)){
				str = (""+"login success");
				dataOutputStream.writeUTF(str);
				dataOutputStream.flush(); 
				while(true)
				{        
				
					str=(String)dataInputStream.readUTF();
					dataOutputStream.writeUTF(str);
					if(str.compareTo("ls")==0)
					{
						File dir = new File(System.getProperty("user.dir"));
						String children[] = dir.list();
						for(String child: children)
							dataOutputStream.writeUTF(child);
						dataOutputStream.writeUTF("end");
						dataOutputStream.flush();
					}
					else if(str.compareTo("bye")==0)
					{
						break;   
					}
					else if(str.contains(" "))
					{ 
						String parts[] = str.split(" ");
						if(parts[0].compareTo("cd")==0){ 
							File dir = new File(parts[1]);
							if(dir.isDirectory()==true) {
								System.setProperty("user.dir", dir.getAbsolutePath());
								dataOutputStream.writeUTF("directory changed");
								dataOutputStream.flush(); 
							}
							else {
								System.out.println(parts[1] + "is not a directory.");
								dataOutputStream.writeUTF("directory error");
								dataOutputStream.flush();
							} 
						}	
						if(parts[0].compareTo("get")==0)
						{
							String contents;
							str = "";
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(parts[1])));
							while((contents=bufferedReader.readLine())!=null)
							{
								str = str + contents +"\n";
							}
							dataOutputStream.writeUTF(str);
							dataOutputStream.flush();
							bufferedReader.close();
						}
					}
				}
			}
			else{
				System.out.println("\n Error");
			}
			serverSocket.close();
			dataOutputStream.close();
			dataInputStream.close();
		}
		catch(Exception e){}
	}
}  
