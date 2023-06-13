package test.imp.date;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DblinkTest {

	static Logger logger = Logger.getLogger(DblinkTest.class);
	/**
	 * 证券公司代码
	 */
	static String[] zqgsdm = {"10080000", "13510000", "13230000", "10140000", "10500000", 
								    "10680000", "10730000", "11010000", "11200000", "11300000",
								    "11380000", "13110000", "13200000", "13230000"};
	volatile  static Connection conn = null;
	
	public static void main(String[] args) {
		try{
			Connection conn = getConnection();
			
			ExecutorService es = Executors.newFixedThreadPool(8);
			Map<String, List<String>> dateMap =  ListFile(new File("E:\\successfile")); 
			ConcurrentLinkedQueue<Entry<String, List<String>>> cq1 = new ConcurrentLinkedQueue<Entry<String,List<String>>>();
			ConcurrentLinkedQueue<Entry<String, List<String>>> cq2= new ConcurrentLinkedQueue<Entry<String,List<String>>>();
			
			cq1.addAll(dateMap.entrySet());
			cq2.addAll(dateMap.entrySet());
			
			long start  = System.currentTimeMillis();
			es.execute(new TshRunner(conn, cq1));
			es.execute(new TshRunner(conn, cq1));
			es.execute(new TshRunner(conn, cq1));
			es.execute(new TshRunner(conn, cq1));
			es.execute(new TsqRunner(conn, cq2));
			es.execute(new TsqRunner(conn, cq2));
			es.execute(new TsqRunner(conn, cq2));
			es.execute(new TsqRunner(conn, cq2));
			
			es.shutdown();
			while(true){
				if(es.isTerminated()){
					long end = System.currentTimeMillis();
					System.out.println("cost:"+(end - start));
					break;
				}
			}
//	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Connection   getConnection()   {       
		if(conn == null){
			synchronized(DblinkTest.class){
				if(conn == null){
					ComboPooledDataSource   dataSource  = null;    
					try   {       
						dataSource =new   ComboPooledDataSource();       
						dataSource.setUser( "krcs");       
						dataSource.setPassword( "krcs");       
						dataSource.setJdbcUrl( "jdbc:oracle:thin:@10.12.5.151:1521:krcs"); 
						dataSource.setDriverClass( "oracle.jdbc.driver.OracleDriver"); 
						dataSource.setInitialPoolSize(10); 
						dataSource.setMinPoolSize(10); 
						dataSource.setMaxPoolSize(50); 
						dataSource.setMaxStatements(100); 
						dataSource.setMaxIdleTime(60);    
						conn = dataSource.getConnection(); 
					}   catch   (SQLException   e)   {
				                   e.printStackTrace(); 
					} catch (PropertyVetoException e) {
				      	     e.printStackTrace();
					}
					return conn;   
				}
			}
		}else{
			return conn;
		}
		return conn;
		    
	}     
	//获取日期列表
	public static  Map<String, List<String>> ListFile(File successFile) throws Exception{
		//key: zqgsmd; value:datelist
		Map<String, List<String>> zqgsDateMap = new HashMap<String, List<String>>();
		if(successFile!=null){
	            if(successFile.isDirectory()){
	            	File[] fileArray=successFile.listFiles();
	            	if(fileArray!=null){
	            		//文件夹名错误缓存
	            		StringBuffer errorDateSb = new StringBuffer();
		                    for (int i = 0; i < fileArray.length; i++) {
		                        //设置日期文件
		                    	if(fileArray[i].isDirectory()){
		                    		List<String> dateList = new ArrayList<String>();
		                    		zqgsDateMap.put(fileArray[i].getName(), dateList);
		                    		
		                    		File[] dateFiles = fileArray[i].listFiles();
		                    		//文件名错误缓存
		                    		for (int j = 0; j < dateFiles.length; j++){
		                    			if(dateFiles[j].isDirectory()){
		                    				if(FileOper.matchYearMm(dateFiles[j].getName())){
		                    					dateList.add(dateFiles[j].getName());
//		                    					dates.add(dateFiles[j].getName());
		                    				}else{
		                    					errorDateSb.append(dateFiles[j].getAbsolutePath());
		                    					errorDateSb.append("; ");
		                    				}
		                    			}
		                    		}
		                    	}else{
		                            throw new Exception("文件夹读取失败");
		                        }
		                    }
		                    logger.error("文件夹名错误列表："+errorDateSb);
	            	}else{
	            		throw new Exception("文件夹读取失败");
	            	}
	            }
		}
		return zqgsDateMap;
	}
}
class TshRunner implements Runnable{

	private  Connection conn;
	private ConcurrentLinkedQueue<Entry<String, List<String>>> cq;
	
	public TshRunner(Connection conn, ConcurrentLinkedQueue<Entry<String, List<String>>> cq ) {
		super();
		this.conn = conn;
		this.cq = cq;
	}

	public void run() {
		CallableStatement procTsh=null;
		try {
			procTsh=conn.prepareCall("{call RZRQ_SNATHDATA_GD_TSH(?,?,?,?,?)}");
			Entry<String, List<String>> task = null;
			while((task = cq.poll()) != null){
				List<String> dates = task.getValue();
				if(dates != null && dates.size() > 0){
					for(String date : dates){
						procTsh.setString(1, "1");
						procTsh.setString(2, "check_1_1");
						procTsh.setString(3, task.getKey());
						procTsh.setString(4, date);
						procTsh.registerOutParameter(5, Types.VARCHAR);procTsh.execute();
						String code=procTsh.getString(5);
						System.out.println(code);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

class TsqRunner implements Runnable{

	private  Connection conn;
	private ConcurrentLinkedQueue<Entry<String, List<String>>> cq;
	
	public TsqRunner(Connection conn, ConcurrentLinkedQueue<Entry<String, List<String>>> cq) {
		super();
		this.conn = conn;
		this.cq = cq;
	}

	public void run() {
		try {
			CallableStatement procTsq=null;
			procTsq=conn.prepareCall("{call RZRQ_SNATHDATA_GD_TSQ(?,?,?,?,?)}");
			Entry<String, List<String>> task = null;
			while((task = cq.poll()) != null){
				List<String> dates = task.getValue();
				if(dates != null && dates.size() > 0){
					for(String date : dates){
						procTsq.setString(1, "1");
						procTsq.setString(2, "check_1_1");
						procTsq.setString(3, task.getKey());
						procTsq.setString(4, date);
						procTsq.registerOutParameter(5, Types.VARCHAR);
						procTsq.execute();
						String codeTs=procTsq.getString(5);
						System.out.println(codeTs);
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
