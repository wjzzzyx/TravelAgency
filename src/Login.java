import java.sql.*;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;

public class Login extends JFrame implements ActionListener {
	private JPanel window;
	private JLabel userNameL, passwordL;
	private JTextField userNameTF, passwordTF;
	private JButton login, register;
	public Login() {
		super("不靠谱旅行社欢迎您！");
		setWindow();
	}
	
	public void setWindow() {
		Container cp = getContentPane();
		FlowLayout layout = new FlowLayout();
		cp.setLayout(layout);
		
		userNameL = new JLabel("用户名", JLabel.CENTER);
		passwordL = new JLabel("密码", JLabel.CENTER);
		userNameTF = new JTextField("");
		passwordTF = new JTextField("");
		login = new JButton("登录");
		login.addActionListener(this);
		register = new JButton("注册");
		register.addActionListener(this);
		
		window = new JPanel(new GridLayout(3, 2, 0, 0));
		window.setPreferredSize(new Dimension(200, 100));
		window.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		window.add(userNameL);
		window.add(userNameTF);
		window.add(passwordL);
		window.add(passwordTF);
		window.add(login);
		window.add(register);
		window.setVisible(true);
		
		cp.add(window);
		setSize(300, 300);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void check() {
		Connection conn = DBInterface.getConnection();
		ResultSet rs = null;
		String query = null;
		String userName = userNameTF.getText();
		String password = passwordTF.getText();
		try {
			query = "select custName, passWord from CUSTOMERS where custName = '" + userName + "';";
			rs = DBInterface.executeQuery(conn, query);
			if(!rs.next()) {
				JOptionPane.showMessageDialog(null, "用户名不存在", "哈哈", JOptionPane.ERROR_MESSAGE);
			}
			else {
				if(password.matches(rs.getString("password"))) {
					this.dispose();
					new Services(userName);
				}
				else {
					JOptionPane.showMessageDialog(null, "密码错误", "哈哈", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch(SQLException e) {
			System.out.println("登录时数据库操作错误。");
			e.printStackTrace();
		} finally {
			DBInterface.closeConnection(conn);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == login) {
			check();
		}
		if(e.getSource() == register) {
			this.dispose();
			new Registration();
		}
	}
}
