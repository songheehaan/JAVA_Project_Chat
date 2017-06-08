import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.*;
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

class ChattingClient extends JFrame implements ActionListener, Runnable, MouseListener, ListSelectionListener, KeyListener {
	private JPanel jp1 = new JPanel();
	private JPanel jp1_jp = new JPanel();
	private JPanel jp1_jp_jpn = new JPanel();
	private JPanel jp1_jp_jpr = new JPanel();
	private JPanel jp1_jpt = new JPanel();
	private JPanel jp2 = new JPanel();
	private JPanel jp2_jprl = new JPanel();
	private JPanel jp2_jpul = new JPanel();
	private JPanel jp3 = new JPanel();
	private JTextArea jta = new JTextArea();
	private JTextField jtfn = new JTextField();
	private JTextField jtfr = new JTextField();
	private JTextField jtf = new JTextField();
	private JLabel jlb1 = new JLabel(" Name");
	private JLabel jlb2 = new JLabel(" Room");
	private JLabel jlb3 = new JLabel("[Room List]");
	private JLabel jlb4 = new JLabel("[User List]");
	private JButton jbt_s = new JButton("Send");
	private JButton jbt_n = new JButton("Connect");
	private JButton jbt_r = new JButton("Creat Room");
	private JList<String> jlistr = new JList<>();
	private JList<String> jlistu = new JList<>();

	private InetAddress ia;
	private Socket chatSoc;
	private BufferedWriter bw;
	private BufferedReader br;
/*	private ObjectOutputStream oos;
	private ObjectInputStream ois;*/

	private String name;
	private String roomname;
	private boolean waitRoom = true;

	public void init() {
		Container con = this.getContentPane();
		con.setLayout(new BorderLayout());
		jp1.setLayout(new BorderLayout());
		jp1_jp.setLayout(new BorderLayout());
		jp1_jp_jpn.setLayout(new BorderLayout());
		jp1_jp_jpr.setLayout(new BorderLayout());
		jp1_jpt.setLayout(new BorderLayout());
		jp2.setLayout(new GridLayout(2, 1));
		jp2_jprl.setLayout(new BorderLayout());
		jp2_jpul.setLayout(new BorderLayout());
		jp2.setPreferredSize(new Dimension(200, 500));
		jp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		jp2.setBorder(new EmptyBorder(5, 0, 5, 5));
		jp1.setBackground(SystemColor.activeCaption);
		jp2.setBackground(SystemColor.activeCaption);
		jta.setBackground(new Color(255, 255, 224)); // (new
														// Color(233,233,243));
		jlistr.setBackground(new Color(255, 239, 213));
		jlistu.setBackground(new Color(255, 239, 213));
		jlb1.setPreferredSize(new Dimension(65, 30));
		jlb2.setPreferredSize(new Dimension(65, 30));
		jbt_n.setPreferredSize(new Dimension(90, 30));
		jbt_r.setPreferredSize(new Dimension(90, 30));
		jlb1.setFont(new Font("", Font.BOLD, 15));
		jlb2.setFont(new Font("", Font.BOLD, 15));
		jlb3.setFont(new Font("", Font.BOLD, 16));
		jlb4.setFont(new Font("", Font.BOLD, 16));
		jta.setEditable(false);

		con.add("Center", jp1);
		con.add("East", jp2);
		jp1.add("North", jp1_jp);
		jp1.add("South", jp1_jpt);
		jp1.add("Center", jta);
		jp1_jp.add("North", jp1_jp_jpn);
		jp1_jp.add("South", jp1_jp_jpr);
		jp1_jp_jpn.add("West", jlb1);
		jp1_jp_jpn.add("Center", jtfn);
		jp1_jp_jpn.add("East", jbt_n);
		jp1_jp_jpr.add("West", jlb2);
		jp1_jp_jpr.add("Center", jtfr);
		jp1_jp_jpr.add("East", jbt_r);
		jp1_jpt.add("Center", jtf);
		jp1_jpt.add("East", jbt_s);
		jp2.add("North", jp2_jprl);
		jp2.add("South", jp2_jpul);
		jp2_jprl.add("North", jlb3);
		jp2_jprl.add("Center", new JScrollPane(jlistr));
		jp2_jpul.add("North", jlb4);
		jp2_jpul.add("Center", new JScrollPane(jlistu));

	}

	public void setDefaultCloseOperation() {

	}

	public void start() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jbt_n.addActionListener(this);
		jbt_r.addActionListener(this);
		jbt_s.addActionListener(this);
		jlistr.addMouseListener(this);
		jtf.addKeyListener(this);
	}

	public ChattingClient(String title) {
		super(title);
		this.init();
		this.start();
		super.setSize(700, 500);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (screen.getWidth() / 2) - super.getWidth() / 2;
		int y = (int) (screen.getHeight() / 2) - super.getHeight() / 2;
		super.setLocation(x, y);
		super.setResizable(false);
		super.setVisible(true);

		try {
			ia = InetAddress.getByName("127.0.0.1");
			chatSoc = new Socket(ia, 20003);
/*			ois = new ObjectInputStream(chatSoc.getInputStream());
			oos = new ObjectOutputStream(chatSoc.getOutputStream());*/
			br = new BufferedReader(new InputStreamReader(chatSoc.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(chatSoc.getOutputStream()));
			// roomList(jtfn.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread th = new Thread(this);
		th.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbt_r) {
			if (waitRoom == true && !jtfr.getText().equals("")) {
				roomname = jtfr.getText(); // room title 내가 만든 방이름 저장
				// if(jtfr.getText().equals(""))
				// jtfr.setText("");
				sendMsg("makeroom:" + jtfr.getText() + ":" + name);
				setProperties("roomMake");
				jbt_r.setText("방 나가기");
			} else if (waitRoom == false) {
				sendMsg("exitroom:" + ":" + roomname + ":" + name);
				setProperties("roomOut");
				jbt_r.setText("create a room");
				jtfr.setText("");
			}
		} else if (e.getSource() == jtf) {
			if (waitRoom == false) {
				sendMsg("say:" + ":" + name + ":" + jtf.getText() + ":");
				jtf.setText("");
			} else if (waitRoom == true) {
				if (jtf.getText().equals(""))
					jtf.setText("");
				sendMsg("makeroom:" + jtf.getText() + ":" + name);
				setProperties("roomMake");
			}
		} else if (e.getSource() == jbt_n) {
			name = jtfn.getText();
			sendMsg("in:" + name);
			setProperties("Connect");
		} else if (e.getSource() == jbt_s) {
			if(!jtf.getText().equals("")){
				sendMsg("say:" + roomname + ":" + name + ":" + jtf.getText());
				jtf.setText("");
			}
		}
	}

	@Override
	public void run() {
		String state = "";
		try {
			while (state != null) {
				state = br.readLine();
				System.out.println("서버에서 받은 메세지 : " + state);
				String[] arr = parseMsg(state.substring(state.indexOf(":") + 1));
				if (state.startsWith("roomlist:")) {
					jlistr.removeAll();
					String[] room = new String[arr.length];
					for (int i = 0, j = 1; i < arr.length; i += 2, j += 2) {
						room[i] = arr[i] + ":" + arr[j];
					}
					jlistr.setListData(room);
				} else if (state.startsWith("userlist:")) {
					jlistu.removeAll();
					jlistu.setListData(arr);
				} else if (state.startsWith("roomuserlist:")) {
					jlistu.removeAll();
					jlistu.setListData(arr);
				} else if (state.startsWith("enterroom:")) {
					jta.append("[" + arr[1] + "] 님이 입장했습니다." + "\n");
				} else if (state.startsWith("exitroom:")) {
					jta.append("[" + arr[1] + "] 님이 퇴장했습니다." + "\n");
				} else if (state.startsWith("say:")) {
					jta.append("[" + arr[1] + "] : " + arr[2] + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void sendMsg(String msg) {
		try {
			bw.write(msg + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void roomList(String name) {
		sendMsg("in:" + name);
	}

	public String[] parseMsg(String msg) {
		StringTokenizer st = new StringTokenizer(msg, ":");
		String[] arr = new String[st.countTokens()];
		// System.out.println("토큰 갯수 : "+st.countTokens());
		int i = 0;
		while (st.hasMoreTokens()) {
			arr[i] = st.nextToken();
			i++;
		}
		return arr;
	}

	private void setProperties(String setc) {
		if (setc.equals("roomMake") || setc.equals("roomIn")) {
			// jtf.setText("");
			jtfr.setEditable(false);
			jlistr.setEnabled(false);
			waitRoom = false;
			// Room();
		} else if (setc.equals("roomOut")) {
			jta.setText("");
			jtfr.setEditable(true);
			jlistr.setEnabled(true);
			waitRoom = true;
		} else if (setc.equals("Connect")) {
			jbt_n.setEnabled(false);
			jbt_r.setEnabled(true);
		} else if (setc.equals("Disconnect")) {
			jbt_n.setEnabled(true);
			jbt_r.setEnabled(false);
			jlistr.setEnabled(true);
			jta.setText("");
			jtfn.setEnabled(true);
		}
	}

	public void close() {
		try {
			br.close();
			bw.close();
			chatSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			if (JOptionPane.showConfirmDialog(null, "방에 입장하시겠습니까?", "입장", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == 0) {
				if ((String) jlistr.getSelectedValue() != null) {
					String selectroom = (String) jlistr.getSelectedValue();
					selectroom = selectroom.substring(0, selectroom.indexOf(":"));
					roomname = selectroom;
					sendMsg("enterroom:" + selectroom + ":" + name);
					setProperties("roomIn");
					jbt_r.setText("방 나가기");
					jtfr.setText(selectroom);
				}
				jlistr.setEnabled(false);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {	
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!jtf.getText().equals("")){
				sendMsg("say:" + roomname + ":" + name + ":" + jtf.getText());
				jtf.setText("");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {	}

	@Override
	public void keyTyped(KeyEvent e) {	}
	
	public static void main(String[] args) {
		new ChattingClient("chat application");
	}
}
