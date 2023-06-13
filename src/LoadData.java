import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.szkingdom.LogUtils;


public class LoadData {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//String fileName="E:"+File.separator+"data2.csv";
		//File f=new File(fileName);
		//long start = System.currentTimeMillis();
		//program(f);
//		sqlldrDirec();
//		sqlldrnormal();
//		sqlldrExternal();
		//long end = System.currentTimeMillis();
		
		//System.out.println("total:"+(end - start));
		
		try{
			String driverclass = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@10.12.5.151:1521:mast";
			String userid = "krcs";
			String password = "krcs";
			Class.forName(driverclass);
			Connection conn = DriverManager.getConnection(url, userid, password);
			CallableStatement proc=null;
			proc=conn.prepareCall("{call proc_credit_rating_rq(?,?)}");

	        proc.registerOutParameter(1, Types.NUMERIC);
	        proc.registerOutParameter(2, Types.VARCHAR);

	        proc.execute();
	        int code=proc.getInt(1);
	        String rest=proc.getString(2);
	        System.out.println(code+":"+rest);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	public static void runCmd(String cmd)throws IOException, Exception{
		LoadData ld = new LoadData();
		Runtime run = Runtime.getRuntime();
		Process proc = run.exec(cmd);
		  StreamGobbler errorGobbler = ld.new StreamGobbler(proc.getErrorStream(), "Error");            
          StreamGobbler outputGobbler = ld.new StreamGobbler(proc.getInputStream(), "Output");
          errorGobbler.start();
          outputGobbler.start();
		proc.waitFor();
	}
	
	public static void sqlldrnormal() throws IOException, Exception {
		String cmd = "sqlldr scott/123456 rows=1000 control=E:\\oracletest\\test.ctl log=E:\\oracletest\\test.log readsize=10485760";
		runCmd(cmd);
	}
	
	public static void sqlldrDirec() throws IOException, Exception {
		String cmd = "sqlldr scott/123456 rows=1000 control=E:\\oracletest\\test.ctl log=E:\\oracletest\\test.log direct=true";
		runCmd(cmd);
	}
	
	public static void sqlldrExternal() throws IOException, Exception {
		String cmd = "sqlldr krcs/krcs@mast control=E:\\oracletest\\sqlldrexternal.ctl external_table=execute";
		runCmd(cmd);
	}
	
	class StreamGobbler extends Thread {
		 InputStream is;
		 String type;
		 StreamGobbler(InputStream is, String type) {
			  this.is = is;
			  this.type = type;
		 }
		 public void run() {
			  try {
				   InputStreamReader isr = new InputStreamReader(is);
				   BufferedReader br = new BufferedReader(isr);
				   String line = null;
				   while ((line = br.readLine()) != null) {
					    if (type.equals("Error"))
					    System.out.println("err:"+line);
//					    else
//					    System.out.println("out:"+line);
				   }
			  } catch (IOException ioe) {
				  ioe.printStackTrace();
			  }
		 }
	}
	
	public static void program(File f) throws Exception{
		String driverclass = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@10.12.134.138:1521:orcl";
		String userid = "scott";
		String password = "123456";
		Class.forName(driverclass);
		Connection conn = DriverManager.getConnection(url, userid, password);
		insert_data(conn, f, "utf8", 3);
	}
	
	public   static  void insert_data(Connection conn,File file,String encoding,int cols)
	{
		int datalen = 2;
		String tablename = "";
		String[] params  = null;
        FileInputStream fis1 = null;
		
		BufferedReader  br1  = null;
		
		PreparedStatement pst = null;
		
		try
		{
			tablename = "dept";
			
			//zqgsdm   = filename.split("_")[1];
			
			String sql ="insert into "+tablename+"  values(";
			
			String s = "";
			
			int i = 0 ; //用于记录读取的条数,每次保存datalen条
			
		
			for(int j=0;j<cols;j++)
	    	{
	    		sql += "?";
				
				if (j < cols-1)
				{
					sql += ",";
				}
			}
		
		    sql += ")";		
		    System.out.println(sql);
			
		    
		    
			
			//插入数据,每次datalen条
		    fis1 = new FileInputStream(file);
			
			br1  = new BufferedReader(new InputStreamReader(fis1,encoding)); 
			
		    pst = conn.prepareStatement(sql);
		    
		    while((s = br1.readLine())!=null)
			{
					
				params = s.split(",");
				if(params.length>=cols)
				{
					for(int m=0;m<cols;m++)
			    	{
					    pst.setString(m+1, params[m]);	
			    	}
					
					pst.addBatch();
				}
				else
				{
					int k=0;
					while(k<cols)
					{
						
						if(k<params.length)
						{							
							pst.setString(k+1, params[k]);	
						}
						else
						{
							pst.setString(k+1, null);
						}
						k++;
					}
					
					pst.addBatch();
				}
				i++;
				
				
				if(i%datalen==0)
				{
					pst.executeBatch();
				}
			
			}
			if((i%datalen!=0))	
			{
				pst.executeBatch();
			}
			br1.close();
			fis1.close();
			pst.close();
			pst = null;
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.debugStackTrace(e.getStackTrace()); 
			System.out.println("插入备用数据库出错!");
			if(pst!=null)
			{
				try {
					pst.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					System.out.println("关闭数据库查询成功!");
				}
				pst = null;
			}
			if(br1!=null)
			{
				try {
					br1.close();
					fis1.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("关闭当前读取的文件成功!");
				}
			}
		}
	}

}
