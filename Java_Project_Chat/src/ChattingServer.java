import java.net.*;
import java.io.*;
import java.util.*;


public class ChattingServer {
	private ServerSocket ss;
	Vector<User> al = new Vector<User>();
	HashMap<String, Vector> hm = new HashMap<String, Vector>();
//	Vector<User> al_r = new Vector<User>();
	public ChattingServer(){
		try {
			ss = new ServerSocket(20003);
			while (true) {
				Socket cltSoc = ss.accept();
				User user = new User(cltSoc, this);
				addUser(user);
				user.start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addUser(User u){
		al.add(u);
	}
	
	public void removeUser(User u) {
		al.remove(u);
	}
	
	public void addRoom(String roomtitle) {
		Vector<User> al_r = new Vector<User>();
		hm.put(roomtitle, al_r);
	}
	
	public void removeRoom(String roomtitle) {
		hm.remove(roomtitle);
	}
	
	public void addRoomUser(String roomtitle,User u) {
		Vector<User> al_r = hm.get(roomtitle);
//		al_r = new Vector<User>();
		al_r.add(u);
		
		
	}
	
	public void removeRoomUser(User u) {
		Vector<User> al_r = hm.get(u.roomtitle);
//		al_r = hm.get(u.roomtitle);
		al_r.remove(u);
		if (al_r.size() == 0) {
			removeRoom(u.roomtitle);
			broadcast("removeroom:");
		}
	}
	
	public void broadcast(String msg) {
		for (User u : al) {
			u.sendMsg(msg);
		}
	}

	public void broadcastRoom(String roomtitle, String msg) {
		if (hm.get(roomtitle) != null) {
			Vector<User> al_r = hm.get(roomtitle);
			al_r = hm.get(roomtitle);
			for (User u : al_r) {
				u.sendMsg(msg);
			}
		}
	}
	
	public void broadcastRoomList() {
		String roomlist = "roomlist:";
		Set<String> set = hm.keySet();
		for (String roomtitle : set) {
			String name = roomtitle;
			roomlist += roomtitle + ":";
//			for (int i = 0; i < hm.size(); i++) {
				Vector<User> al_r = hm.get(name);
				roomlist += al_r.size() + ":";
//			}
		}
		broadcast(roomlist);
	}
	
	public void broadcastUserList() {
		String userlist = "userlist:";
		for (User u : al) {
			userlist += u.name + ":";
		}
		broadcast(userlist);
	}
	
	public void broadcastRoomUserList(String roomtitle) {
		String roomuserlist = "roomuserlist:";
		if (hm.get(roomtitle) != null) {
			Vector<User> al_r = hm.get(roomtitle);
//			al_r = hm.get(roomtitle);
			if (al_r.size() > 0) {
				for (User u : al_r) {
					roomuserlist += u.name + ":";
				}
				broadcastRoom(roomtitle, roomuserlist);
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		new ChattingServer();
	}
}