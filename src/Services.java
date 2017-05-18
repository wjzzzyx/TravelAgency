import java.sql.*;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.JTable;
import javax.swing.table.*;

public class Services extends JFrame implements ActionListener {
	private Container cp;
	private JPanel menu;
	private JButton bookFlight, reserveHotel, reserveCar, myReservation, exit;
	private JPanel serviceCP;
	private CardLayout card;
	private int serviceID = 3;
	
	public class Flight extends JPanel implements ActionListener {
		private String custName;
		
		private JPanel flightSearchP;
		private JLabel fromCityL, arivCityL;
		private JTextField fromCityTF, arivCityTF;
		private JButton searchB;
		private JPanel flightResultsP;
		private JScrollPane resultsSP;
		private JTable resultsT;
		private DefaultTableModel resultsTM;
		private JButton reserveB;
		
		public Flight(String custName) {
			super(new FlowLayout(FlowLayout.CENTER, 10, 10));
			this.custName = custName; 
			setFlightWindow();
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == searchB) {
				search();
			}
			if(e.getSource() == reserveB) {
				reserve();
			}
		}

		public void setFlightWindow() {
			flightSearchP = new JPanel(new GridLayout(5, 1, 0, 0));
			flightSearchP.setPreferredSize(new Dimension(300, 200));
			fromCityL = new JLabel("始发地");
			fromCityTF = new JTextField("");
			arivCityL = new JLabel("目的地");
			arivCityTF = new JTextField("");
			searchB = new JButton("查询");
			searchB.addActionListener(this);
			flightSearchP.add(fromCityL);
			flightSearchP.add(fromCityTF);
			flightSearchP.add(arivCityL);
			flightSearchP.add(arivCityTF);
			flightSearchP.add(searchB);
			flightSearchP.setVisible(true);
			
			resultsTM = new DefaultTableModel();
			resultsTM.setColumnIdentifiers(new String[] {"航班号", "价格", "座位数", "剩余座位数", "始发地", "目的地"});
			resultsT = new JTable(resultsTM) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			resultsT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			resultsT.setFillsViewportHeight(true);
			resultsSP = new JScrollPane();
			resultsSP.setLayout(new ScrollPaneLayout());
			resultsSP.setPreferredSize(new Dimension(500, 300));
			resultsSP.setViewportView(resultsT);
			reserveB = new JButton("预订");
			reserveB.addActionListener(this);
			reserveB.setEnabled(false);
			flightResultsP = new JPanel();
			flightResultsP.setPreferredSize(new Dimension(550, 350));
			flightResultsP.add(resultsSP);
			flightResultsP.add(reserveB);
			
			add(flightSearchP);
			add(flightResultsP);
			setVisible(true);
			setPreferredSize(new Dimension(800, 800));
		}
		
		public void search() {
			String flightNum = null, price = null, numSeats = null, numAvail = null;
			String fromCity = fromCityTF.getText();
			String arivCity = arivCityTF.getText();
			Connection conn = DBInterface.getConnection();
			ResultSet rs = null;
			
			if(fromCity.matches("") || arivCity.matches("")) {
				JOptionPane.showMessageDialog(null, "出发地和目的地不能为空", "没找到航班", JOptionPane.ERROR_MESSAGE);
			}
			else {
				String query = "select * from FLIGHTS where fromCity = '" + fromCity + "' and arivCity = '" + arivCity + "';";
				try {
					rs = DBInterface.executeQuery(conn, query);
					while(rs.next()) {
						flightNum = rs.getString("flightNum");
						price = rs.getString("price");
						numSeats = rs.getString("numSeats");
						numAvail = rs.getString("numAvail");
						if(Integer.parseInt(numAvail) > 0) {
							resultsTM.addRow(new Object[] {flightNum, price, numSeats, numAvail, fromCity, arivCity});
						}
					}
				} catch(SQLException e) {
					System.out.println("查询航班时数据库操作错误。");
					e.printStackTrace();
				}
			}
			DBInterface.closeConnection(conn);
			//resultsT.setModel(resultsTM);
			//resultsT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			//resultsT.setFillsViewportHeight(true);
			//flightResultsP.add(scrollPane);
			flightResultsP.setVisible(true);
			reserveB.setEnabled(true);
		}
		
		public void reserve() {
			int i = resultsT.getSelectedRow();
			if(i != -1) {
				Object _flightNum = resultsT.getValueAt(i, 0);
				//Object _price = resultsT.getValueAt(i, 1);
				//Object _numSeats = resultsT.getValueAt(i, 2);
				Object _numAvail = resultsT.getValueAt(i, 3);
				//Object _fromCity = resultsT.getValueAt(i, 4);
				//Object _arivCity = resultsT.getValueAt(i, 5);
				String flightNum = String.valueOf(_flightNum);
				int numAvail = Integer.parseInt(String.valueOf(_numAvail));
				
				Connection conn = DBInterface.getConnection();
				String insert, update;
				long timeStamp = new java.util.Date().getTime();
				insert = "insert into RESERVATIONS (custName, resvType, resvKey) values ('" + custName + "', 1, '" + custName + timeStamp + "');";
				update = "update FLIGHTS set numAvail = " + (numAvail - 1) + " where flightNum = '" + flightNum + "';";
				try {
					conn.setAutoCommit(false);
					DBInterface.executeUpdate(conn, insert);
					DBInterface.executeUpdate(conn, update);
					conn.commit();
					conn.setAutoCommit(true);
				} catch(SQLException e) {
					System.out.println("订购机票时数据库操作错误。");
					e.printStackTrace();
					return;
				}
				JOptionPane.showMessageDialog(null, "订票成功", "啦啦啦", JOptionPane.WARNING_MESSAGE);
				DBInterface.closeConnection(conn);
				fromCityTF.setText("");
				arivCityTF.setText("");
				resultsTM.setRowCount(0);
				reserveB.setEnabled(false);
			}
		}
	}
	
	public class Hotel extends JPanel implements ActionListener {
		private String custName;
		
		private JPanel searchHotelP;
		private JLabel hotelLocationL;
		private JTextField hotelLocationTF;
		private JButton searchB;
		private JPanel hotelResultsP;
		private JScrollPane resultsSP;
		private JTable resultsT;
		private DefaultTableModel resultsTM;
		private JButton reserveB;
		
		public Hotel(String custName) {
			super(new FlowLayout(FlowLayout.CENTER, 0, 0));
			this.custName = custName;
			setHotelWindow();
		}
		
		public void setHotelWindow() {
			hotelLocationL = new JLabel("请输入车辆所在城市");
			hotelLocationTF = new JTextField("");
			searchB = new JButton("查询");
			searchB.addActionListener(this);
			searchHotelP = new JPanel(new GridLayout(3, 1, 0, 0));
			searchHotelP.setPreferredSize(new Dimension(200, 200));
			searchHotelP.add(hotelLocationL);
			searchHotelP.add(hotelLocationTF);
			searchHotelP.add(searchB);
			
			resultsTM = new DefaultTableModel();
			resultsTM.setColumnIdentifiers(new String[] {"地点", "价格", "房间数", "剩余房间数"});
			resultsT = new JTable(resultsTM) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			resultsT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			resultsT.setFillsViewportHeight(true);
			resultsSP = new JScrollPane();
			resultsSP.setLayout(new ScrollPaneLayout());
			resultsSP.setPreferredSize(new Dimension(500, 300));
			resultsSP.setViewportView(resultsT);
			reserveB = new JButton("预订");
			reserveB.addActionListener(this);
			reserveB.setEnabled(false);
			hotelResultsP = new JPanel();
			hotelResultsP.setPreferredSize(new Dimension(550, 350));
			hotelResultsP.add(resultsSP);
			hotelResultsP.add(reserveB);
			hotelResultsP.setVisible(false);
			
			add(searchHotelP);
			add(hotelResultsP);
			setVisible(true);
			setPreferredSize(new Dimension(800, 800));
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == searchB) {
				search();
			}
			if(e.getSource() == reserveB) {
				reserve();
			}
		}
		
		public void search() {
			Connection conn = DBInterface.getConnection();
			String query;
			ResultSet rs;
			String location = hotelLocationTF.getText();
			
			query = "select * from HOTELS where location = '" + location + "';";
			try {
				rs = DBInterface.executeQuery(conn, query);
				if(!rs.next()){
					JOptionPane.showMessageDialog(null, "抱歉", "没找到房", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(rs.getInt("numAvail") <= 0) {
					JOptionPane.showMessageDialog(null, "来晚一步", "没房间了", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String price = rs.getString("price");
				String numRooms = rs.getString("numRooms");
				String numAvail = rs.getString("numAvail");
				resultsTM.addRow(new Object[] {location, price, numRooms, numAvail});
			} catch(SQLException e) {
				System.out.println("预订旅馆时数据库操作错误。");
				e.printStackTrace();
				return;
			}
			DBInterface.closeConnection(conn);
			hotelResultsP.setVisible(true);
			reserveB.setEnabled(true);
			return;
		}
		
		public void reserve() {
			int i = resultsT.getSelectedRow();
			if(i != -1) {
				Object _location = resultsT.getValueAt(i, 0);
				String location = String.valueOf(_location);
				
				Connection conn = DBInterface.getConnection();
				String insert, update;
				long timeStamp = new java.util.Date().getTime();
				insert = "insert into RESERVATIONS (custName, resvType, resvKey) values ('" + custName + "', 2, '" + custName + timeStamp + "');";
				update = "update HOTELS set numAvail = numAvail - 1 where location = '" + location + "';";
				try {
					conn.setAutoCommit(false);
					DBInterface.executeQuery(conn, insert);
					DBInterface.executeQuery(conn, update);
					conn.commit();
					conn.setAutoCommit(true);
				} catch(SQLException e) {
					System.out.println("预订旅馆时数据库操作错误。");
					e.printStackTrace();
					return;
				}
				DBInterface.closeConnection(conn);
				JOptionPane.showMessageDialog(null, "订到房间啦", "啦啦啦", JOptionPane.WARNING_MESSAGE);
				hotelLocationTF.setText("");
				resultsTM.setRowCount(0);
				reserveB.setEnabled(false);
			}
		}
	}
	
	public class Car extends JPanel implements ActionListener {
		private String custName;
		
		private JPanel searchCarP;
		private JLabel carLocationL;
		private JTextField carLocationTF;
		private JButton searchB;
		private JPanel carResultsP;
		private JScrollPane resultsSP;
		private JTable resultsT;
		private DefaultTableModel resultsTM;
		private JButton reserveB;
		
		public Car(String custName) {
			super(new FlowLayout(FlowLayout.CENTER, 0, 0));
			this.custName = custName;
			setCarWindow();
		}
		
		public void setCarWindow() {
			carLocationL = new JLabel("请输入车辆所在城市");
			carLocationTF = new JTextField("");
			searchB = new JButton("查询");
			searchB.addActionListener(this);
			searchCarP = new JPanel(new GridLayout(3, 1, 0, 0));
			searchCarP.setPreferredSize(new Dimension(200, 200));
			searchCarP.add(carLocationL);
			searchCarP.add(carLocationTF);
			searchCarP.add(searchB);
			
			resultsTM = new DefaultTableModel();
			resultsTM.setColumnIdentifiers(new String[] {"地点", "价格", "数量", "剩余数量"});
			resultsT = new JTable(resultsTM) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			resultsT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			resultsT.setFillsViewportHeight(true);
			resultsSP = new JScrollPane();
			resultsSP.setLayout(new ScrollPaneLayout());
			resultsSP.setPreferredSize(new Dimension(500, 300));
			resultsSP.setViewportView(resultsT);
			reserveB = new JButton("预订");
			reserveB.addActionListener(this);
			reserveB.setEnabled(false);
			carResultsP = new JPanel();
			carResultsP.setPreferredSize(new Dimension(550, 350));
			carResultsP.add(resultsSP);
			carResultsP.add(reserveB);
			carResultsP.setVisible(false);
			
			add(searchCarP);
			add(carResultsP);
			setVisible(true);
			setPreferredSize(new Dimension(800, 800));
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == searchB) {
				search();
			}
			if(e.getSource() == reserveB) {
				reserve();
			}
		}
		
		public void search() {
			Connection conn = DBInterface.getConnection();
			String query;
			ResultSet rs;
			String location = carLocationTF.getText();
			
			query = "select * from CARS where location = '" + location + "';";
			try {
				rs = DBInterface.executeQuery(conn, query);
				if(!rs.next()){
					JOptionPane.showMessageDialog(null, "抱歉", "没找到车", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(rs.getInt("numAvail") <= 0) {
					JOptionPane.showMessageDialog(null, "来晚一步", "没车了", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String price = rs.getString("price");
				String numCars = rs.getString("numCars");
				String numAvail = rs.getString("numAvail");
				resultsTM.addRow(new Object[] {location, price, numCars, numAvail});
			} catch(SQLException e) {
				System.out.println("预订车辆时数据库操作错误。");
				e.printStackTrace();
				return;
			}
			DBInterface.closeConnection(conn);
			carResultsP.setVisible(true);
			reserveB.setEnabled(true);
			return;
		}
		
		public void reserve() {
			int i = resultsT.getSelectedRow();
			if(i != -1) {
				Object _location = resultsT.getValueAt(i, 0);
				String location = String.valueOf(_location);
				
				Connection conn = DBInterface.getConnection();
				String insert, update;
				long timeStamp = new java.util.Date().getTime();
				insert = "insert into RESERVATIONS (custName, resvType, resvKey) values ('" + custName + "', 3, '" + custName + timeStamp + "');";
				update = "update CARS set numAvail = numAvail - 1 where location = '" + location + "';";
				try {
					conn.setAutoCommit(false);
					DBInterface.executeQuery(conn, insert);
					DBInterface.executeQuery(conn, update);
					conn.commit();
					conn.setAutoCommit(true);
				} catch(SQLException e) {
					System.out.println("预订车辆时数据库操作错误。");
					e.printStackTrace();
					return;
				}
				DBInterface.closeConnection(conn);
				JOptionPane.showMessageDialog(null, "嘀", "学生卡", JOptionPane.WARNING_MESSAGE);
				carLocationTF.setText("");
				resultsTM.setRowCount(0);
				reserveB.setEnabled(false);
			}
		}
	}
	
	public class Reservation extends JPanel implements ActionListener {
		private String custName;
		
		private JPanel myResvP;
		private JScrollPane flightResvSP;
		private JTable flightResvT;
		private DefaultTableModel flightResvTM;
		private JScrollPane hotelResvSP;
		private JTable hotelResvT;
		private DefaultTableModel hotelResvTM;
		private JScrollPane carResvSP;
		private JTable carResvT;
		private DefaultTableModel carResvTM;
		
		public Reservation(String custName) {
			super(new FlowLayout(FlowLayout.CENTER, 0, 0));
			this.custName = custName;
			setResvWindow();
		}
		
		public void setResvWindow() {
			flightResvTM = new DefaultTableModel();
			flightResvTM.setColumnIdentifiers(new String[] {"订单号", "始发地", "目的地", "票价"});
			flightResvT = new JTable(flightResvTM) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			flightResvT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			flightResvT.setFillsViewportHeight(true);
			flightResvSP = new JScrollPane();
			flightResvSP.setLayout(new ScrollPaneLayout());
			flightResvSP.setPreferredSize(new Dimension(200, 300));
			flightResvSP.setViewportView(flightResvT);
			
			hotelResvTM = new DefaultTableModel();
			hotelResvTM.setColumnIdentifiers(new String[] {"订单号", "地点", "票价"});
			hotelResvT = new JTable(hotelResvTM) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			hotelResvT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			hotelResvT.setFillsViewportHeight(true);
			hotelResvSP = new JScrollPane();
			hotelResvSP.setLayout(new ScrollPaneLayout());
			hotelResvSP.setPreferredSize(new Dimension(200, 300));
			hotelResvSP.setViewportView(hotelResvT);
			
			carResvTM = new DefaultTableModel();
			carResvTM.setColumnIdentifiers(new String[] {"订单号", "地点", "票价"});
			carResvT = new JTable(carResvTM) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			carResvT.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			carResvT.setFillsViewportHeight(true);
			carResvSP = new JScrollPane();
			carResvSP.setLayout(new ScrollPaneLayout());
			carResvSP.setPreferredSize(new Dimension(200, 300));
			carResvSP.setViewportView(carResvT);
			
			myResvP = new JPanel();
			myResvP.setPreferredSize(new Dimension(650, 350));
			myResvP.add(flightResvSP);
			myResvP.add(hotelResvSP);
			myResvP.add(carResvSP);
			myResvP.setVisible(true);
			
			Connection conn = DBInterface.getConnection();
			String query;
			ResultSet rs;
			
			try {
				query = "select resvKey, fromCity, arivCity, price" +
						"from RESERVATIONS, FLIGHTS" +
						"where custName = '" + custName + "' and resvType = ;";
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	Flight flightP;
	Hotel hotelP;
	Car carP;
	Reservation reservationP;
	
	public Services(String userName) {
		super("请选择服务");
		flightP = new Flight(userName);
		hotelP = new Hotel(userName);
		carP = new Car(userName);
		reservationP = new Reservation(userName);
		setWindow();
	}
	
	public void setWindow() {
		cp = getContentPane();
		cp.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		menu = new JPanel(new GridLayout(1, 0, 20, 10));
		bookFlight = new JButton("预订航班");
		bookFlight.setPreferredSize(new Dimension(180, 80));
		bookFlight.addActionListener(this);
		reserveHotel = new JButton("预订旅馆");
		reserveHotel.setPreferredSize(new Dimension(180, 80));
		reserveHotel.addActionListener(this);
		reserveCar = new JButton("预订汽车");
		reserveCar.setPreferredSize(new Dimension(180, 80));
		reserveCar.addActionListener(this);
		myReservation = new JButton("我的订单");
		myReservation.setPreferredSize(new Dimension(180, 80));
		myReservation.addActionListener(this);
		exit = new JButton("退出");
		exit.setPreferredSize(new Dimension(180, 80));
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
		serviceCP.add("0", flightP);
		serviceCP.add("1", hotelP);
		serviceCP.add("2", carP);
		serviceCP.add("3", reservationP);
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
				serviceID = 0;
			}
		}
		if(e.getSource() == reserveHotel) {
			if(serviceID != 1) {
				card.show(serviceCP, "1");
				serviceID = 1;
			}
		}
		if(e.getSource() == reserveCar) {
			if(serviceID != 2) {
				card.show(serviceCP, "2");
				serviceID = 2;
			}
		}
		if(e.getSource() == myReservation) {
			if(serviceID != 3) {
				card.show(serviceCP, "3");
				serviceID = 3;
			}
		}
		if(e.getSource() == exit) {
			this.dispose();
			new Login();
		}
	}
}
