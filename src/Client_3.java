import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;

//대화내용저장, 귓속말기능 추가, 접속한 유저 리스트로 확인가능
public class Client_3{
    private static final long serialVersionUID = 1222179582713735628L;
    //채팅로그 저장용 파일 변수
    private JFileChooser chooser = new JFileChooser();
    private int ret;
    private String pathName;

    private ButtonGroup group = new ButtonGroup();
    private JRadioButton rb1 = new JRadioButton("전체말",true);
    private JRadioButton rb2 = new JRadioButton("귓속말");
    private JScrollPane chatScrollPane;
    private JList<String> userList;
    private String whisperto= null;//귓속말 대상
    private String chatMode= "all"; //all or whisper
    public JTextPane chatTextPane;
    private JTextArea txtrMessage;
    private DefaultListModel<String> userListModel = new DefaultListModel<String>();
    private boolean isOpenList = false;

    Socket sc= null;
    PrintWriter pw=null;

    private Frame f1=new Frame("Login");
    private Frame f2=new Frame("대화창");
    private Label lhost = new Label("Host  주소 : ",Label.CENTER);
    private Label lport = new Label("Port  번호 : ",Label.CENTER);
    private Label luser = new Label("User Name : ",Label.CENTER);
    private static TextField tf1 = new TextField(20);
    private static TextField tf2 = new TextField(10);
    private static TextField tf3 = new TextField(10);
    private  JButton btn_in = new JButton("접속");
    private JButton btn_out = new JButton("종료");

    //채팅창 부분
    private static String host,port,user,str ;
    public int count =0;

    private  Button  btnend = new  Button("종료") ;
    private ArrayList<String> list = new ArrayList<String>();

    //클라이언트 부분

    public Client_3() {//기본 생성자

    }//생성자 end

    class Handler implements ActionListener,FocusListener{
        //IO처리를 위한 메뉴 생성
        private MenuBar mb=new MenuBar();
        private Menu mfile=new Menu("파일");
        private MenuItem mfile_save =new MenuItem("저장");
        private MenuItem mfile_exit =new MenuItem("종료");

        public void loginFrame(){
            f1.setTitle("Login");
            f1.setLayout(new BorderLayout());
            f1.setBackground(Color.orange);

            JPanel p5 = new JPanel();
            JPanel p6 = new JPanel();
            JPanel p7 = new JPanel();
            JPanel p8 = new JPanel();
            JPanel p9 = new JPanel();
            JPanel p12 = new JPanel();
            JPanel p11 = new JPanel();
            p5.setBackground(Color.orange);
            p5.setLayout(new BorderLayout());
            p8.setBackground(Color.orange);
            p8.setPreferredSize(new Dimension(50,50));
            p9.setBackground(Color.orange);
            p9.setPreferredSize(new Dimension(50,50));
            p6.setBackground(Color.orange);
            p6.setPreferredSize(new Dimension(300,120));
            p7.setBackground(Color.orange);
            p7.setPreferredSize(new Dimension(300,100));
            p11.setBackground(Color.orange);
            p11.setLayout(new BorderLayout());
            p12.setLayout(new GridLayout(4,1));
            p12.setBackground(Color.orange);
            //네트워크톡 글자
            JLabel lblTitle = new JLabel("Nettalk");
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 34));
            lblTitle.setSize(276, 46);
            p11.add(lblTitle,BorderLayout.NORTH);
            //채팅아이콘
            JPanel p14 = new JPanel();
            JPanel p16 = new JPanel();

            p14.setLayout(new BorderLayout());
            p16.setPreferredSize(new Dimension(45,120));
            p16.setBackground(Color.orange);
            MyPanel p13 = new MyPanel();
            p13.setBackground(Color.orange);
            p14.add(p16,BorderLayout.WEST);
            p14.add(p13,BorderLayout.CENTER);
            p11.add(p14,BorderLayout.CENTER);



            //host,user,port 번호 입력란
            JPanel phost = new JPanel();
            JPanel puser = new JPanel();
            JPanel pport = new JPanel();

            phost.setLayout(new GridLayout(1,2,3,3));
            phost.add(lhost);
            phost.add(tf1);

            puser.setLayout(new GridLayout(1,2,3,3));
            puser.add(lport);
            puser.add(tf2);

            pport.setLayout(new GridLayout(1,2,3,3));
            pport.add(luser);
            pport.add(tf3);

            JPanel p10 = new JPanel();
            p10.setLayout(new FlowLayout());

            p10.add(btn_in);
            p10.add(btn_out);

            phost.setBackground(Color.orange);
            pport.setBackground(Color.orange);
            puser.setBackground(Color.orange);
            p10.setBackground(Color.orange);
            p12.add(phost);
            p12.add(pport);
            p12.add(puser);
            p12.add(p10);
            p5.add(p11,BorderLayout.CENTER);
            p5.add(p12,BorderLayout.SOUTH);

            f1.add(p5, BorderLayout.CENTER);
            f1.add(p6,BorderLayout.SOUTH);
            f1.add(p7,BorderLayout.NORTH);
            f1.add(p8,BorderLayout.WEST);
            f1.add(p9,BorderLayout.EAST);
            f1.pack();

            f1.addWindowListener(new WindowAdapter( ) {
                public void windowClosing(WindowEvent we){ System.exit(1) ; }
            }) ;

            f1.setBounds(100, 100, 305, 580);
            f1.setResizable(false);
            f1.setVisible(true);

            btn_in.addActionListener(this);
            btn_out.addActionListener(this);

            mfile_save.addActionListener(this);
            mfile_exit.addActionListener(this);
        }//로그인창 부분 add

        public void chatFrame(){
            //채팅창 왼쪽 화면
            f2.setLayout(null);
            //채팅보드
            JPanel chatBoardPane = new JPanel();
            chatBoardPane.setBackground(Color.pink);
            chatBoardPane.setBounds(10, 0, 300, 430);
            f2.add(chatBoardPane);
            chatBoardPane.setLayout(null);
            //채팅보드내에 채팅내용올라오는 부분
            chatScrollPane = new JScrollPane();
            chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            chatScrollPane.setBounds(0, 90, 300, 340);
            chatBoardPane.add(chatScrollPane);
            //스크롤페인 안에 텍스트 페인 넣어서 사용
            chatTextPane = new JTextPane();
            chatTextPane.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            chatTextPane.setBackground(Color.PINK);
            chatScrollPane.setViewportView(chatTextPane);
            chatTextPane.setText("");
            //유저리스트 설정
            userList = new JList<String>(userListModel);
            userList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isDoubleClicked(e)) {
                        //유저리스트 클릭하면 해당 대상으로 귓속말 대상설정되는 함수넣어주기
                        whisperto = userList.getSelectedValue().toString();
                    }
                }
            });
            userList.setBackground(Color.WHITE);
            userList.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            chatScrollPane.setColumnHeaderView(userList);
            userList.setVisible(false);
            userList.setVisibleRowCount(0);
            userList.setAutoscrolls(true);

            JLabel lblUserList = new JLabel("▼");
            lblUserList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    userListControl();
                }
            });
            lblUserList.setFont(new Font("맑은 고딕", Font.BOLD, 36));
            lblUserList.setHorizontalAlignment(SwingConstants.CENTER);
            lblUserList.setBounds(12,45, 40, 40);
            chatBoardPane.add(lblUserList);
            chatTextPane.setEditable(false);



            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(15, 450, 189, 70);
            f2.add(scrollPane);

            txtrMessage = new JTextArea();
            txtrMessage.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (isEnter(e)) {
                        sendprocess(txtrMessage.getText().replaceAll("\n", ""));
                        txtrMessage.setText("");
                        txtrMessage.requestFocus(); //컴포넌트 포커스 강제지정
                    }
                }
            });
            txtrMessage.setLineWrap(true);
            txtrMessage.setWrapStyleWord(true);
            scrollPane.setViewportView(txtrMessage);

            JButton btnNewButton = new JButton("전송");
            btnNewButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            btnNewButton.setBackground(Color.ORANGE);
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendprocess(txtrMessage.getText().replaceAll("\n", ""));
                    txtrMessage.setText("");
                    txtrMessage.requestFocus(); //컴포넌트 포커스 강제지정
                }
            });
            btnNewButton.setBounds(211, 450,65, 35);
            f2.add(btnNewButton);

            MyItemListener m1 = new MyItemListener();
            group.add(rb1);
            group.add(rb2);
            rb1.setBounds(211, 490,80,30);
            rb1.setSelected(true);
            rb2.setBounds(211, 530,80,30);
            rb1.setBackground(Color.LIGHT_GRAY);
            rb2.setBackground(Color.LIGHT_GRAY);
            rb1.addItemListener(m1);
            rb2.addItemListener(m1);
            f2.add(rb1);
            f2.add(rb2);

            mfile.add(mfile_save);
            mfile.addSeparator(); mfile.add(mfile_exit);
            //메뉴에 아이템삽입
            mb.add(mfile);
            //메뉴바에 메뉴 삽입
            f2.setMenuBar(mb);
            //프레임에 메뉴바 부착


            txtrMessage.addFocusListener(this);
            btnNewButton.addActionListener(this);
            btnend.addActionListener(this);
            //이벤트리스너 문자입력란, 전송버튼,종료버튼에 부착

            f2.addWindowListener(new WindowAdapter( ) {
                public void windowClosing(WindowEvent we){ System.exit(1); }
            }) ;

            f2.setResizable(false);//고정사이즈
            f2.setBackground(Color.lightGray);
            f2.setBounds(100, 100, 305, 580);
            f2.setVisible(true);
        }//채팅창 부분 add



        public void filesave(){
            try{
                chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                ret = chooser.showSaveDialog(null);
                if (ret==JFileChooser.APPROVE_OPTION) {
                    pathName = chooser.getSelectedFile().getPath();//완전경로명
                }
                FileWriter fw=new FileWriter(pathName);
                BufferedWriter out = new BufferedWriter(fw);
                String data="";
                while(true){
                    data=chatTextPane.getText(); //지금까지 채팅창(chatTextPane)에 올라온내용 파일에 저장
                    if(data==null) break;

                    out.write(data);
                    out.close();
                }
            }catch(Exception ex){}
        }


        @Override
        public void actionPerformed(ActionEvent ae) {
            // TODO Auto-generated method stub
            if(ae.getSource()==btn_in){
                f1.setVisible(false);
                f1.dispose();//해당 프레임만 종료
                host=tf1.getText();
                port=tf2.getText();
                user=tf3.getText();
                chatFrame(); //f2 visible

                connect(host,port,user);
            }
            if(ae.getSource()==mfile_save){
                filesave();
                JOptionPane.showMessageDialog(null, "저장되었습니다.\n 저장경로" + pathName);
            }
            if(ae.getSource()==mfile_exit){
                System.exit(0);
            }//종료
            if(ae.getSource()==btnend||ae.getSource()==btn_out){
                System.exit(0);
            }
        }//버튼 액션 지정

        public void sendprocess(String string){//채팅 전송하는 메서드
            try{
                String str2=string;
                if(chatMode.equals("all")){//전체말이 체크되어있는 경우
                    pw.println("/a"+str2);
                    System.out.println("보냄 : "+ str2+"\n");
                }
                else if(chatMode.equals("whisper")&& (whisperto != null)){//귓속말이 체크된 경우
                    try{
                        String name=whisperto;
                        pw.println(("/s"+name+"-"+str2));//name은 받을 사람의 이름
                        System.out.println("보냄  : /s"+name+"-"+str2);
                        chatTextPane.setText(chatTextPane.getDocument().getText(0,chatTextPane.getDocument().getLength()) +" "+name+"님께 보내는 귓말 ▶▶ "+str2+"\n");
                    }catch(Exception ex){
                        chatTextPane.setText(chatTextPane.getDocument().getText(0,chatTextPane.getDocument().getLength()) +" "+ex.getMessage());
                    }
                }
                else if(chatMode.equals("whisper")&& (whisperto == null)){
                    JOptionPane.showMessageDialog(null, "귓속말 대상을 설정해주세요","Message",JOptionPane.INFORMATION_MESSAGE);
                }
            }catch(Exception ex){}
            txtrMessage.setText("");
            txtrMessage.setCaretPosition(0);
        }

        @Override
        public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            txtrMessage.setText("");
        }

        @Override
        public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
        }

    }

    public void connect(String host, String port, String user){
        try{
            sc=new Socket(host,Integer.parseInt(port));
            System.out.println("채팅방에 접속하였습니다.");

            ClientThread th = new ClientThread(sc,this);
            th.start();

            pw=new PrintWriter(new OutputStreamWriter(sc.getOutputStream()),true);
            pw.println(user);
        }catch(Exception ex){System.out.println(ex.getMessage());}
    }//connect end

    //-------------------------------리스트에 이름추가-------------------
    public void addName(String user){
        System.out.println(user+"들와요");
        list.add(user);
        userListModel.addElement(user);
        userList.setModel(userListModel);
    }
    public void removeName(String user){
        System.out.println(user+"나가요");
        list.remove(user);
        userListModel.remove(userListModel.indexOf(user));
        userList.setModel(userListModel);
    }
    //--------------------------------------------------------------

    class MyPanel extends JPanel{
        ImageIcon icon; //= new ImageIcon ("images/noimage.png");
        private Image img; //= icon.getImage();//이미지 객체
        public MyPanel() {
            this.icon= new ImageIcon(new ImageIcon("images/profile0.png").getImage().getScaledInstance(160, 160, java.awt.Image.SCALE_SMOOTH));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            img = icon.getImage();
            g.drawImage(img,0,0, 100,100,this);
        }

    }

    private boolean isDoubleClicked(MouseEvent e) {
        return e.getClickCount() == 2;
    }

    private void userListControl() {
        if (isOpenList) {
            userListClose();
        } else {
            userListOpen();
        }
    }
    private void userListOpen() {
        setUserList();
        userList.setVisible(true);
        userList.setVisibleRowCount(8);
        isOpenList = true;
    }

    private void setUserList() {
        userListModel.clear();
        for (count=1;count<list.size();count++) {//0번째 인덱스는 무조건 본인이기 때문에 1부터 반영
            userListModel.addElement((String) list.get(count));
        }
    }

    private void userListClose() {
        userList.setVisible(false);
        userList.setVisibleRowCount(0);
        isOpenList = false;
    }
    private boolean isEnter(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }

    private class MyItemListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent e) {
            if(rb1.isSelected()&&!rb2.isSelected()) {
                chatMode= "all";

            }
            else if(!rb1.isSelected()&&rb2.isSelected()) {
                chatMode= "whisper";

            }
        }
    }

    public static void main(String[] args) {
        Client_3 c3 = new Client_3();//생성자 호출
        Client_3.Handler hd=c3.new Handler();//내부클래스 생성자 호출
        hd.loginFrame();//로그인화면 띄우기
    }//main end
}//class end

class ClientThread extends Thread{
    Socket sc=null;
    BufferedReader br=null;
    String str;
    String user;
    Client_3 c3;

    public ClientThread(Socket sc, Client_3 c3) {
        this.sc=sc;
        this.c3=c3;
    }//생성자 end
    @Override
    public void run() {
        try{
            br= new BufferedReader(new InputStreamReader(sc.getInputStream()));//서버에서 넘어온값 받기
            while(true){
                str=br.readLine();
                System.out.println(str);
                if(str.indexOf("/f")==0){
                    user=str.substring(2);
                    System.out.println("123 :"+user);
                    c3.count++;//접속인원수 증가
                    c3.addName(user);
                }
                else if(str.indexOf("/e")==0){
                    user=str.substring(2);
                    c3.count--;//접속인원수 감소
                    c3.removeName(user);

                }
                else{//str이 그냥 대화내용 문자열일때
                    c3.chatTextPane.setText(c3.chatTextPane.getDocument().getText(0,c3.chatTextPane.getDocument().getLength())+"  " +str+"\n");
                }
            }//while end
        }catch(Exception ex){System.out.println("에러");}
    }//run end

}//class end
