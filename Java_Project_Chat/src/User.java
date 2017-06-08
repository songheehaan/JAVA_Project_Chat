import java.net.*;
import java.util.*;
import java.io.*;

public class User extends Thread {
	private Socket cltSoc;
	ChattingServer server;
	BufferedReader br;
	BufferedWriter bw;
	String name, roomtitle, say;
	
	public User(Socket cltSoc, ChattingServer server){
		this.cltSoc = cltSoc;
		this.server = server;
		try{
			br = new BufferedReader(new InputStreamReader(cltSoc.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(cltSoc.getOutputStream()));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		String state = "";
		try{
			while(state != null){
				state = br.readLine();
				System.out.println("msg from client "+state);
				String[] arr = parseMsg(state.substring(state.indexOf(":")+1));// ¼öÁ¤ +1
				if(state.startsWith("in:")){
					name = arr[0];
					server.broadcastRoomList();
					server.broadcastUserList();
				} else if(state.startsWith("out:")){
					name = arr[0];
					server.removeUser(this);
					server.broadcastRoomList();
					server.broadcastUserList();
					close();
				} else if(state.startsWith("makeroom:")){
					roomtitle = arr[0];
					name = arr[1];
					server.addRoom(roomtitle);
					server.addRoomUser(roomtitle,this);
					server.removeUser(this);
					server.broadcastRoomList();
					server.broadcastUserList();
					server.broadcastRoomUserList(roomtitle);
				} else if(state.startsWith("removeroom:")){
					server.broadcastRoomList();
					server.broadcastUserList();
				} else if(state.startsWith("enterroom:")){
					roomtitle = arr[0];
					name = arr[1];
					server.addRoomUser(roomtitle,this);
					server.removeUser(this);
					server.broadcastRoomList();
					server.broadcastUserList();
					server.broadcastRoomUserList(roomtitle);
					server.broadcastRoom(roomtitle, state);
				} else if(state.startsWith("exitroom:")){
					roomtitle = arr[0];
					name = arr[1];
					server.addUser(this);
					server.removeRoomUser(this);
					server.broadcastRoomList();
					server.broadcastUserList();
					server.broadcastRoomUserList(roomtitle);
					server.broadcastRoom(roomtitle, state);
				} else if(state.startsWith("say:")){
					roomtitle = arr[0];
					name = arr[1];
					say = arr[2];
					server.broadcastRoom(roomtitle, state);
				}
				System.out.println(server.al.toString());
				System.out.println(server.hm.toString());
			}
		} catch(IOException e){
//			e.printStackTrace();
			
			server.removeUser(this);
			server.removeRoomUser(this);
			server.broadcastRoomList();
			server.broadcastUserList();
			server.broadcastRoomUserList(roomtitle);
			server.broadcastRoom(roomtitle, state);
		}
	}
	
	public void sendMsg(String msg){
		try{
			bw.write(msg+ "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public String getIpAddr(){
		String ipAddr = cltSoc.getInetAddress().getHostName().toString();
		return ipAddr;
	}
	
	public String[] parseMsg(String msg){
		StringTokenizer st = new StringTokenizer(msg, ":");
		String[] arr = new String[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens()) {
			arr[i] = st.nextToken();
			i++;
		}
		return arr;
	}
	
	public void close(){
		try {
			br.close();
			bw.close();
			cltSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
}
