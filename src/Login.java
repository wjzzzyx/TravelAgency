import java.sql.*;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class Login extends JFrame implements ActionListener {
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
	}
	
	public void check() {
		Connection conn = DBInterface.getConnection();
		ResultSet rs = null;
		String query = null;
		String userName = userNameTF.getText();
		String password = passwordTF.getText();
		try {
			query = "select custName, passWord from customers where custName = '" + userName + "';";
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
			System.out.println("数据库操作错误。");
			e.printStackTrace();
		}
		conn.close();
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
