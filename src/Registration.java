import java.sql.*;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;

public class Registration extends JFrame implements ActionListener {
	private Container cp;
	
	private JPanel window;
	private JLabel userNameL, passwordL, password2L;
	private JButton register, cancel;
	private JTextField userNameTF, passwordTF, password2TF;
	
	public Registration() {
		super("注册");
		setWindow();
	}
	
	void setWindow() {
		cp = getContentPane();
		FlowLayout layout = new FlowLayout();
		cp.setLayout(layout);
		
		userNameL = new JLabel("用户名", JLabel.CENTER);
		passwordL = new JLabel("密码", JLabel.CENTER);
		password2L = new JLabel("再次输入密码", JLabel.CENTER);
		userNameTF = new JTextField("");
		passwordTF = new JTextField("");
		password2TF = new JTextField("");
		register = new JButton("注册");
		register.addActionListener(this);
		cancel = new JButton("取消");
		cancel.addActionListener(this);
		
		window = new JPanel(new GridLayout(4, 2, 0, 0));
		window.setPreferredSize(new Dimension(300, 300));
		window.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		window.add(userNameL);
		window.add(userNameTF);
		window.add(passwordL);
		window.add(passwordTF);
		window.add(password2L);
		window.add(password2TF);
		window.add(register);
		window.add(cancel);
		window.setVisible(true);
		
		cp.add(window);
		setSize(300, 300);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void register() {
		Connection conn = DBInterface.getConnection();
		ResultSet rs = null;
		
		String userName = userNameTF.getText();
		String password = passwordTF.getText();
		String password2 = passwordTF.getText();
		
		if(!password.matches(password2)) {
			JOptionPane.showMessageDialog(null, "两次输入密码不同", "请重新输入", JOptionPane.ERROR_MESSAGE);
		}
		else {
			try {
				String query = "select custName from CUSTOMERS where custName = '" + userName + "';";
				rs = DBInterface.executeQuery(conn, query);
				if(rs.next()) {
					JOptionPane.showMessageDialog(null, "用户已存在", "请重新输入用户名", JOptionPane.ERROR_MESSAGE);
				}
				else {
					String insert = "insert into CUSTOMERS (custName, password) value ('" + userName + "','" + password + "');"; 
					int res = DBInterface.executeUpdate(conn, insert);
					if(res > 0) {
						JOptionPane.showMessageDialog(null, "注册成功", "请至登录界面登录", JOptionPane.WARNING_MESSAGE);
						this.dispose();
						new Login();
					}
					else {
						JOptionPane.showMessageDialog(null, "不知怎么注册失败了。。。", "要不再试试？", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch(SQLException e) {
				System.out.println("注册时数据库操作错误。");
				e.printStackTrace();
			} finally {
				DBInterface.closeConnection(conn);
			}
			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == register) {
			register();
		}
		if(e.getSource() == cancel) {
			this.dispose();
			new Login();
		}
	}
}
