package ftest;//20210026 배지현

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;






class BookdbView extends JFrame{
	JPanel[] pnl=new JPanel[4];
	JLabel[] label=new JLabel[4];
	JTextField[] tf=new JTextField[5];
	JButton[] button=new JButton[4];
	JTextArea ta=new JTextArea();
	
	JTable table;
	DefaultTableModel model;

    BookdbView() {
    	String[] lbl_tf= {"ID","Ttile","Publisher","Price"};
		String[] lbl_button= {"추가","수정","삭제","제목 검색"};
		Container c=getContentPane();
		pnl[0]=new JPanel();
		pnl[1]=new JPanel();
		pnl[2]=new JPanel();
		pnl[3]=new JPanel();
		for(int i=0;i<4;i++) {
			label[i]=new JLabel(lbl_tf[i]);
			label[i].setHorizontalAlignment(WIDTH/2);
			button[i]=new JButton(lbl_button[i]);
		}
		tf[0]=new JTextField(10);
		tf[1]=new JTextField(15);
		tf[2]=new JTextField(10);
		tf[3]=new JTextField(5);
		tf[4]=new JTextField(15);
		tf[3].setHorizontalAlignment(SwingConstants.RIGHT);
		String[] columnNames={"ID","책제목","출판사","가격"};
		model = new DefaultTableModel(columnNames,0) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) { 
				return false;
			}
		};
		table=new JTable(model);
		setComponents();
		
		for(int i=0;i<4;i++) {
			pnl[0].add(label[i]);
			pnl[0].add(tf[i]);
		}
		for(int i=0;i<3;i++) {
			pnl[1].add(button[i]);
		}
		button[0].setEnabled(false);
		button[1].setEnabled(false);
		button[2].setEnabled(false);
		pnl[2].add(tf[4]);
		pnl[2].add(button[3]);
		pnl[3].setLayout(new FlowLayout(FlowLayout.LEFT,60,0));
		pnl[3].add(pnl[1]);
		pnl[3].add(pnl[2]);
		//pnl[0].setLayout(new FlowLayout());		
		c.add(pnl[0],BorderLayout.NORTH);
		c.add(new JScrollPane(table),BorderLayout.CENTER);
		c.add(pnl[3],BorderLayout.SOUTH);
		setTitle("서적 관리");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(650,400);
		setVisible(true);
	}
    void setComponents() {
		tf[0].setBackground(Color.YELLOW);
		DefaultTableCellRenderer renderer=new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (column == 3) {
					setHorizontalAlignment(SwingConstants.RIGHT);
				} else {
					setHorizontalAlignment(SwingConstants.CENTER);
				}
				return this;
			}
		};
		table.setEnabled(true);
		//		table.setAutoCreateRowSorter(true);
		int[] columnWidths= {30,160,100,70};
		for(int i=0;i<columnWidths.length;i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);			
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
	}
}

public class BookEx {
	BookdbView v = new BookdbView();
	ActionHandler handler=new ActionHandler();
	Connection con=null;
	Statement stmt=null;
	ResultSet rs=null;

	public BookEx() {
		v.button[0].addActionListener(handler);
		v.button[1].addActionListener(handler);
		v.button[2].addActionListener(handler);
		v.button[3].addActionListener(handler);
		v.table.addMouseListener(new MouseHandler());
		v.tf[0].addKeyListener(new KeyHandler());
		
	}
	class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int row=v.table.getSelectedRow();
			for(int i=0;i<4;i++) {
				v.tf[i].setText(v.model.getValueAt(row, i).toString());
			}
		}
	}
	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				makeConnection();
				String sql="SELECT * FROM book where id='"+v.tf[0].getText()+"'";
				try {
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						v.button[0].setEnabled(false);
						v.button[1].setEnabled(true);
						v.button[2].setEnabled(true);
					}
					else {
						v.button[0].setEnabled(true);
						v.button[1].setEnabled(false);
						v.button[2].setEnabled(false);
					}
				} catch(SQLException e1) {
					e1.printStackTrace();
				}
				disConnection();
			}
		}
	}
	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			makeConnection();
			if(e.getSource()==v.button[0]) {	
				insertbutton();
				getData();
			}
			else if(e.getSource()==v.button[1]) {
				try {
					updateData(v.tf[0].getText());
					getData();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(e.getSource()==v.button[2]) {
				try {
					deleteData(v.tf[0].getText());
					getData();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(e.getSource()==v.button[3]) {
				String k="";
				if(v.tf[4].getText().equals(k)) {
					getData();
				}
				else {
					selectData();
				}
			}
			disConnection();		
		}
	}
	void insertbutton() {
		makeConnection();
		PreparedStatement ps;
		String sql="INSERT INTO book (id,title,publisher,price) values(?,?,?,?)";
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, v.tf[0].getText());
	        ps.setString(2, v.tf[1].getText());
	        ps.setString(3, v.tf[2].getText());
	        ps.setString(4, v.tf[3].getText());
	        ps.executeUpdate();
	        System.out.println("INSERT INTO");
	        for(int i=0;i<4;i++) {
	       	    v.tf[i].setText("");
	        } 
		} catch (SQLException e1) {
			e1.printStackTrace();
		}	
	}
	void getData() {
		makeConnection();
		String sql="SELECT * FROM book";
		String[] schemaValue = new String[4];  // table에 넣을 값을 보관하는 배열
		v.ta.setText("");
		((DefaultTableModel) v.model).setNumRows(0);
		try {
			System.out.println(sql+"\n");
			rs=stmt.executeQuery(sql);
			while(rs.next()) {
				schemaValue[0]=rs.getString("id");
				schemaValue[1]=rs.getString("title")+"\t";
				schemaValue[2]=rs.getString("publisher")+"\t";
				schemaValue[3]=rs.getString("price")+"\n";
				((DefaultTableModel) v.model).addRow(schemaValue);
			}		
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		disConnection();  
	}
	void selectData() {
		makeConnection();
		String sql="SELECT * FROM book where title Like'%"+v.tf[4].getText()+"%'";
		String[] schemaValue = new String[4];  // table에 넣을 값을 보관하는 배열
		v.ta.setText("");
		((DefaultTableModel) v.model).setNumRows(0);
		try {
			System.out.println(sql+"\n");
			rs=stmt.executeQuery(sql);
			while(rs.next()) {
				schemaValue[0]=rs.getString("id");
				schemaValue[1]=rs.getString("title")+"\t";
				schemaValue[2]=rs.getString("publisher")+"\t";
				schemaValue[3]=rs.getString("price")+"\n";
				((DefaultTableModel) v.model).addRow(schemaValue);
			}		
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		disConnection();  
	}
	void updateData(String key) throws SQLException{
		makeConnection();
		String sql="";
		sql="UPDATE book set title='"+v.tf[1].getText()+"',publisher='"+v.tf[2].getText()+
				"',price="+v.tf[3].getText()+" WHERE id='"+v.tf[0].getText()+"'";
		System.out.println(sql);
		try {
			stmt.executeUpdate(sql);
		}catch (SQLException e1) {
			e1.printStackTrace();
		}

		disConnection();	
	}
	void deleteData(String key) throws SQLException {
		makeConnection();	
		String sql="";
		try {
			sql="DELETE FROM book WHERE id='"+v.tf[0].getText()+"'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			
		}catch (SQLException e1) {
			e1.printStackTrace();
		}
		disConnection();	
	}
	public Connection makeConnection(){
		String url="jdbc:mysql://localhost:3306/bookdb?serverTimezone=Asia/Seoul";
		String id="root";
		String password="1234";
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("드라이브 적재 성공");
			con=DriverManager.getConnection(url, id, password);
			stmt=con.createStatement();
			System.out.println("데이터베이스 연결 성공");
		}catch(ClassNotFoundException e){
			System.out.println("드라이버를 찾을 수 없습니다");
		}catch(SQLException e){
			System.out.println("연결에 실패하였습니다");
		}
		return con;
	}

	public void disConnection() {
		try{
			rs.close();
			stmt.close();
			con.close();
		}catch(SQLException e){System.out.println(e.getMessage());}
	}

	public static void main(String[] args) {
		new BookEx();

	}

}
