import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

public class Services extends JFrame implements ActionListener {
	private Container cp;
	private JPanel menu;
	private JButton bookFlight, reserveHotel, reserveCar, myReservation, exit;
	private JPanel serviceCP;
	private CardLayout card;
	private int serviceID;
	
	public class Flight extends JPanel implements ActionListener {
		
	}
	
	public class Hotel extends JPanel implements ActionListener {
		
	}
	
	public class Car extends JPanel implements ActionListener {
		
	}
	
	public class Reservation extends JPanel implements ActionListener {
		
	}
	
	public Services(String userName) {
		super("请选择服务");
		setWindow();
	}
	
	public void setWindow() {
		cp = getContentPane();
		cp.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		menu = new JPanel(new GridLayout(1, 0, 20, 10));
		bookFlight = new JButton("预订航班");
		bookFlight.setPreferredSize(new Dimension(200, 100));
		bookFlight.addActionListener(this);
		reserveHotel = new JButton("预订旅馆");
		reserveHotel.setPreferredSize(new Dimension(200, 100));
		reserveHotel.addActionListener(this);
		reserveCar = new JButton("预订汽车");
		reserveCar.setPreferredSize(new Dimension(200, 100));
		reserveCar.addActionListener(this);
		myReservation = new JButton("我的订单");
		myReservation.setPreferredSize(new Dimension(200, 100));
		myReservation.addActionListener(this);
		exit = new JButton("退出");
		exit.setPreferredSize(new Dimension(200, 100));
		exit.addActionListener(this);
		menu.add(bookFlight);
		menu.add(reserveHotel);
		menu.add(reserveCar);
		menu.add(myReservation);
		menu.add(exit);
		menu.setVisible(true);
		
		serviceCP = new JPanel();
		card = new CardLayout();
		serviceCP.setLayout(card);
		serviceCP.add("0", flight);
		serviceCP.add("1", hotel);
		serviceCP.add("2", car);
		serviceCP.add("3", reservation);
		serviceCP.setPreferredSize(new Dimension(600, 600));
		serviceCP.setVisible(true);
		
		cp.add(menu);
		cp.add(serviceCP);
		setSize(1000, 1000);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == bookFlight) {
			if(serviceID != 0) {
				card.show(serviceCP, "0");
			}
		}
		if(e.getSource() == reserveHotel) {
			if(serviceID != 1) {
				card.show(serviceCP, "1");
			}
		}
		if(e.getSource() == reserveCar) {
			if(serviceID != 2) {
				card.show(serviceCP, "2");
			}
		}
		if(e.getSource() == myReservation) {
			if(serviceID != 3) {
				card.show(serviceCP, "3");
			}
		}
		if(e.getSource() == exit) {
			this.dispose();
			new Login();
		}
	}
}
