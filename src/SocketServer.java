import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;




public class SocketServer {
	private JFrame fame;
	private JPanel panel;
	private JLabel LBmsg;
	private JTextField TFmsg;
	private JLabel LBIpAddress;
	private JTextArea TAcontent;
	private JScrollPane SPcontent;
	private JButton BTdisconnectUser;
	private JButton BTdisconnectUserAll;
	private JComboBox<String> CBuser;
	
	public static final int LISTEN_PORT = 2525;
	public static void main (String[] args){
		SocketServer socketServer=new SocketServer();
		socketServer.initLayout();
		socketServer.initListener();
		socketServer.setIPAdrress();
		socketServer.ServerListener();
	}

	private void initLayout() {	
		fame=new JFrame("Socket Server");
		fame.setBounds(0,0,600,500);
		fame.setVisible(true);
		fame.setResizable(false);
		
		LBmsg=new JLabel("由此傳送訊息：");
		LBmsg.setLocation(10,10);
		LBmsg.setSize(100,30);
		
		TFmsg=new JTextField();
		TFmsg.setLocation(110,10);
		TFmsg.setSize(450,30);
		
		LBIpAddress=new JLabel("IP Address:");
		LBIpAddress.setLocation(10,50);
		LBIpAddress.setSize(400,30);
		
		TAcontent=new JTextArea("");
		TAcontent.setLineWrap(true);
		
		SPcontent=new JScrollPane(TAcontent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		SPcontent.setLocation(10,90); 
		SPcontent.setSize(400,350);
		
		BTdisconnectUserAll=new JButton("斷開所有用戶");
		BTdisconnectUserAll.setLocation(420,90);
		BTdisconnectUserAll.setSize(150,50);
		
		BTdisconnectUser=new JButton("斷開所選用戶");
		BTdisconnectUser.setLocation(420,150);
		BTdisconnectUser.setSize(150,50);
		
		CBuser=new JComboBox<String>();
		CBuser.setLocation(420,220);
		CBuser.setSize(150, 20);
		
		panel=new JPanel(null);
		panel.add(LBmsg);
		panel.add(TFmsg);
		panel.add(LBIpAddress);
		panel.add(SPcontent);
		panel.add(BTdisconnectUserAll);
		panel.add(BTdisconnectUser);
		panel.add(CBuser);
		
	    DefaultCaret caret = (DefaultCaret)TAcontent.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	   
		fame.add(panel);
		fame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		fame.revalidate() ;	
	}

	private void initListener(){
		
	}
	
	private void setIPAdrress(){
		InetAddress adr = null;
		try {
			adr = InetAddress.getLocalHost(); //IPv6
			adr =Inet4Address.getLocalHost();//IPv4
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LBIpAddress.setText("IP Address:"+adr.getHostAddress());
	}
	
	private void ServerListener(){
		ServerSocket serverSocket = null;
        ExecutorService threadExecutor = Executors.newCachedThreadPool();
        
        try{
            serverSocket = new ServerSocket( LISTEN_PORT );
          	TAcontent.append("Server listening requests..."+"\r\n");
          	
            while ( true ){
                Socket socket = serverSocket.accept();
                threadExecutor.execute( new ServerThread( socket,TAcontent ) ); 
            }
        }
        catch ( IOException e ){
            e.printStackTrace();
            TAcontent.append("Server failed to start,whether the "+LISTEN_PORT+" port has been occupied?");
        }
        finally{
            if ( threadExecutor != null )
                threadExecutor.shutdown();
            if ( serverSocket != null )
                try{
                    serverSocket.close();
                }
                catch ( IOException e ){
                    e.printStackTrace();
                }
        }
	}
}

class ServerThread implements Runnable{
	private Socket clientSocket=null;
	private JTextArea TAcontent;
	private DataOutputStream socketOutput = null;
	private DataInputStream socketInput=null;
	private String inputMsg="",outputMsg="";
	private int heartBeatTime=30;
	
	public ServerThread(Socket clientSocket,JTextArea TAcontent){
		try {
			this.clientSocket=clientSocket;
			this.clientSocket.setSoTimeout(60000);
			this.TAcontent=TAcontent;
			socketInput=new DataInputStream(this.clientSocket.getInputStream());
			socketOutput = new DataOutputStream( this.clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	 
	@Override
	public void run(){
		try{
			while((inputMsg=socketInput.readUTF())!=null){
				if(inputMsg.startsWith(Protocol.C_USER_LOGIN.toString()) && inputMsg.endsWith(Protocol.C_USER_LOGIN.toString())){
					inputMsg=getRealMsg(inputMsg);
					c_UserLogin(inputMsg);
				}
				TAcontent.append(inputMsg+"\r\n");	
			}
		}catch(Exception e){
			
		}
	}
	
	private void c_UserLogin(String msg){
		writeContent(msg);
		s_UserLogin();
	}
	
	private void s_UserLogin(){
		try {
			outputMsg=Protocol.S_USER_SUCCESS.toString()+"Login Success"+Protocol.S_USER_SUCCESS.toString();
			socketOutput.writeUTF(outputMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getRealMsg(String msg){
		return msg.substring(Integer.parseInt(Protocol.LENGTH.toString()),msg.length()-Integer.parseInt(Protocol.LENGTH.toString()));
	}
	
	private void writeContent(String msg){
		TAcontent.append(msg+"\r\n");	
	}
	
	
}