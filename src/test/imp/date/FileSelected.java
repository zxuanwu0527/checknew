package test.imp.date;

import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import test.imp.date.DataLoadConstants.InsertTye;
import test.imp.date.DataLoadConstants.ParamName;


public class FileSelected implements ActionListener{
	
	static Logger logger = Logger.getLogger(FileSelected.class);
	JFrame frame = new JFrame("FileCheck");// 框架布局
	JTabbedPane tabPane = new JTabbedPane();// 选项卡布局
	Container con = new Container();//
	JLabel label1 = new JLabel("文件目录");
	JTextField text1 = new JTextField();// TextField 目录的路径
	JLabel label5 = new JLabel("日志存放目录");
	JTextField text5 = new JTextField();// TextField 目录的路径
	JButton button1 = new JButton("选择一个目录");// 选择
	JButton button5 = new JButton("选择一个目录");// 选择
	JButton button2 = new JButton("选择一个文件");// 选择
	JLabel label2= new JLabel("数据库用户");
	JTextField text2 = new JTextField("krcs");
	JLabel label3= new JLabel("数据库密码");
	JPasswordField text3 = new JPasswordField("krcs");
	JLabel label4= new JLabel("数据库实例名");
	JTextField text4 = new JTextField("mast");
	JLabel label6= new JLabel("提交的行数");
	JTextField text6 = new JTextField();
	JLabel label7= new JLabel("bindsize");
	JTextField text7 = new JTextField();
	JLabel label8= new JLabel("readsize");
	JTextField text8 = new JTextField();
	JLabel label9= new JLabel("streamsize");
	JTextField text9 = new JTextField();
	JLabel label10= new JLabel("线程数");
	JTextField text10 = new JTextField();
	JLabel label11= new JLabel("date_cache");
	JTextField text11 = new JTextField();
	
	JFileChooser jfc = new JFileChooser();// 文件选择器
	JFileChooser logJfc = new JFileChooser();// 文件选择器
	JButton button3 = new JButton("确定");//
	JRadioButton radioBtn1 = new JRadioButton("常规导入");
	JRadioButton radioBtn2 = new JRadioButton("直接导入");
	JRadioButton radioBtn3 = new JRadioButton("外部表导入");
	ButtonGroup bg = new ButtonGroup();
	
	public FileSelected() {
		jfc.setCurrentDirectory(new File("E:\\successfile\\13090000\\20121026"));
		logJfc.setCurrentDirectory(new File("E:\\"));
		
		double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		
		double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));
		frame.setSize(380, 365);
		frame.setContentPane(tabPane);
		label1.setBounds(10, 10, 110, 20);
		text1.setBounds(100, 10, 160, 20);
		button1.setBounds(260, 10, 115, 20);
		button2.setBounds(380, 10, 115, 20);
		label5.setBounds(10, 30, 110, 20);
		text5.setBounds(100, 30, 160, 20);
		button5.setBounds(260, 30, 115, 20);
		label2.setBounds(10, 60, 110, 20);
		text2.setBounds(100, 60, 160, 20);
		label3.setBounds(10, 80, 110, 20);
		text3.setBounds(100, 80, 160, 20);
		label4.setBounds(10, 100, 110, 20);
		text4.setBounds(100, 100, 160, 20);
		label6.setBounds(10, 120, 110, 20);
		text6.setBounds(100, 120, 160, 20);
		label7.setBounds(10, 140, 110, 20);
		text7.setBounds(100, 140, 160, 20);
		label8.setBounds(10, 160, 110, 20);
		text8.setBounds(100, 160, 160, 20);
		label9.setBounds(10, 180, 110, 20);
		text9.setBounds(100, 180, 160, 20);
		label11.setBounds(10, 200, 110, 20);
		text11.setBounds(100, 200, 160, 20);
		label10.setBounds(10, 220, 110, 20);
		text10.setBounds(100, 220, 160, 20);
		
		radioBtn1.setBounds(10, 250, 100, 20);
		radioBtn2.setBounds(110, 250, 100, 20);
		radioBtn3.setBounds(210, 250, 100, 20);
		button3.setBounds(30, 275, 60, 20);
		button1.addActionListener(this);
		button2.addActionListener(this);
		button3.addActionListener(this); 
		button5.addActionListener(this); 
		bg.add(radioBtn1);
		bg.add(radioBtn2);
		bg.add(radioBtn3);
		con.add(label1);
		con.add(text1);
		con.add(button1);
		con.add(button2);
		con.add(label2);
		con.add(text2);
		con.add(label3);
		con.add(text3);
		con.add(label4);
		con.add(text4);
		con.add(label5);
		con.add(text5);
		con.add(label6);
		con.add(text6);
		con.add(label7);
		con.add(text7);
		con.add(label8);
		con.add(text8);
		con.add(label9);
		con.add(text9);
		con.add(label10);
		con.add(text10);
		con.add(label11);
		con.add(text11);
		
		con.add(button5);
		con.add(button3);
		con.add(radioBtn1);
		con.add(radioBtn2);
		con.add(radioBtn3);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabPane.add("tab1", con);
	}

	public static void main(String[] args) {
		new FileSelected();
	}

	public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(button1)) {
					//置1 ，表示选文件夹
					jfc.setFileSelectionMode(1);
					int state = jfc.showOpenDialog(null);
					if (state == 1) {
						return;
					} else {
						File f = jfc.getSelectedFile();
						text1.setText(f.getAbsolutePath());
						logger.debug("path:"+text1.getText());
					}
				}else if (e.getSource().equals(button2)) {
					//置1 ，表示选文件夹
					int state = logJfc.showOpenDialog(null);
					if (state == 1) {
						return;
					} else {
						File f = logJfc.getSelectedFile();
						text1.setText(f.getAbsolutePath());
						logger.debug("file:"+text1.getText());
					}
				}else if (e.getSource().equals(button5)) {
					//置1 ，表示选文件夹
					jfc.setFileSelectionMode(1);
					int state = jfc.showOpenDialog(null);
					if (state == 1) {
						return;
					} else {
						File f = jfc.getSelectedFile();
						text5.setText(f.getAbsolutePath());
						logger.debug("path:"+text5.getText());
					}
				}
				if (e.getSource().equals(button3)) {
					long allStart = System.currentTimeMillis();
					if(StringUtils.isNotBlank(text1.getText() ) && StringUtils.isNotBlank(text4.getText()) 
							&& StringUtils.isNotBlank(text5.getText() ) && text3.getPassword() != null){
						try {
							Map<String, Integer> params = new HashMap<String, Integer>();
							InsertTye  it = InsertTye.NORMAL; 
							if(StringUtils.isNotBlank(text6.getText())){
								params.put(ParamName.ROWS.getName(), Integer.parseInt(text6.getText().trim()));
							}else{
								params.put(ParamName.ROWS.getName(), 64);
							}
							if(StringUtils.isNotBlank(text7.getText())){
								params.put(ParamName.BINDSIZE.getName(), Integer.parseInt(text7.getText().trim()));
							}else{
								params.put(ParamName.BINDSIZE.getName(), 256000);
							}
							if(StringUtils.isNotBlank(text8.getText())){
								params.put(ParamName.READSIZE.getName(), Integer.parseInt(text8.getText().trim()));
							}else{
								params.put(ParamName.READSIZE.getName(), 1048576);
							}
							if(StringUtils.isNotBlank(text9.getText())){
								params.put(ParamName.STREAMSIZE.getName(), Integer.parseInt(text9.getText().trim()));
							}else{
								params.put(ParamName.STREAMSIZE.getName(), 256000);
							}
							if(StringUtils.isNotBlank(text10.getText())){
								params.put(ParamName.THREADS.getName(), Integer.parseInt(text10.getText().trim()));
							}else{
								params.put(ParamName.THREADS.getName(), 1);
							}if(StringUtils.isNotBlank(text11.getText())){
								params.put(ParamName.DATE_CACHE.getName(), Integer.parseInt(text11.getText().trim()));
							}else{
								params.put(ParamName.DATE_CACHE.getName(), 1000);
							}
							
							if(radioBtn1.isSelected()){
								it = InsertTye.NORMAL;
								logger.info("import type ,NORMAL");
							}else if(radioBtn2.isSelected()){
								it = InsertTye.DIRECT;
								logger.info("import type ,DIRECT");
							}else if(radioBtn3.isSelected()){
								it = InsertTye.EXTERNAL;
								logger.info("import type ,EXTERNAL");
							}else{
								logger.warn("import type: normal ,for get no insert type info!");
							}

							Map<String, List<File>> insFiles = FileOper.ListFile(new File(text1.getText()));
							DataLoader dl = new DataLoader();
							long start = System.currentTimeMillis();
							dl.loadDate(it, text2.getText(), new String(text3.getPassword()), text4.getText(), insFiles, 
									text5.getText(), params);
							long end = System.currentTimeMillis();
							System.out.println("data load cost:"+(end - start));
							System.out.println("total cost:"+(end - allStart));
						} catch (Exception e1) {
							logger.error("error:", e1);
						}
					}else{
						JOptionPane.showMessageDialog(null, "不能有空字段", "提示", 2);
					}
				}
		}
	
}
