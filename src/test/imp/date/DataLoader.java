package test.imp.date;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import test.imp.date.DataLoadConstants.InsertTye;
import test.imp.date.DataLoadConstants.ParamName;

public class DataLoader {
	static Logger logger = Logger.getLogger(DataLoader.class);
	//任务队列
	ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus = new ConcurrentLinkedQueue<Entry<String,List<File>>>();
	
	public void loadDate(InsertTye it, String user, String pwd, String domain, Map<String, List<File>> insFiles, String logPath, Map<String, Integer> params) throws Exception{
		if(insFiles != null && insFiles.keySet() != null && insFiles.keySet().size() != 0){
			String cmd = "sqlldr "+user+"/"+pwd;
			if(domain != null && domain.trim() != ""){
				cmd +="@"+domain;
			}
			TreeMap<String, List<File>> treemap = new TreeMap<String, List<File>>(insFiles);
			int rows = params.get(ParamName.ROWS.getName());
			int binds = params.get(ParamName.BINDSIZE.getName());
			int readsize = params.get(ParamName.READSIZE.getName()) ;
			//readsize不能小于bindsize
			readsize = readsize >= binds ? readsize : binds;
			int threadNum = params.get(ParamName.THREADS.getName());
			int date_cache = params.get(ParamName.DATE_CACHE.getName());
			logPath = logPath + File.separator;
			
			cmd +=  " skip=1 rows="+rows+"  bindsize="+binds + " readsize="+readsize + " date_cache="+date_cache ;
			for(Entry<String, List<File>> entry : treemap.entrySet()){
				taskQueus.offer(entry);
			}
			treemap.clear();
			insFiles.clear();
			ExecutorService service = Executors.newFixedThreadPool(threadNum);
			logger.info("thread num:"+threadNum);
			AtomicInteger totalDataCount = new AtomicInteger(0);
			switch (it) {
			case NORMAL:
				for(int index = 0; index < threadNum; index++){
					NormalCmdRunner ncr = new NormalCmdRunner(taskQueus, logPath, cmd, totalDataCount);
					service.execute(ncr);
				}
				service.shutdown();
				while(true){
					if(service.isTerminated()){
						logger.info("total data:"+totalDataCount.get());
						break;
					}
				}
				break;
			case DIRECT:
				int streamSize = params.get(ParamName.STREAMSIZE.getName()) == 0? 256000 : params.get(ParamName.STREAMSIZE.getName());
				cmd += " multithreading=true streamsize="+streamSize;
				for(int index = 0; index < threadNum; index++){
					DirectCmdRunner ncr = new DirectCmdRunner(taskQueus, logPath, cmd,totalDataCount);
					service.execute(ncr);
				}
				service.shutdown();
				while(true){
					if(service.isTerminated()){
						logger.info("total data:"+totalDataCount.get());
						break;
					}
				}
				break;
			case EXTERNAL:
				for(int index = 0; index < threadNum; index++){
					ExternalCmdRunner ncr = new ExternalCmdRunner(taskQueus, logPath, cmd,totalDataCount);
					service.execute(ncr);
				}
				service.shutdown();
				while(true){
					if(service.isTerminated()){
						logger.info("total data:"+totalDataCount.get());
						break;
					}
				}
				break;
			default:
				for(int index = 0; index < threadNum; index++){
					NormalCmdRunner ncr = new NormalCmdRunner(taskQueus, logPath, cmd,totalDataCount);
					service.execute(ncr);
				}
				service.shutdown();
				while(true){
					if(service.isTerminated()){
						logger.info("total data:"+totalDataCount.get());
						break;
					}
				}
				break;
			}
		}
		
	}
	
	public static void runCmd(String cmd)throws IOException, Exception{
		Runtime run = Runtime.getRuntime();
		Process proc = run.exec(cmd);
		//记录一次执行的日志
		StringBuffer logSb = new StringBuffer();
		StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error", logSb);            
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output", logSb);
		errorGobbler.start();
		outputGobbler.start();
		proc.waitFor();
		logger.info(logSb);
	}
	
	public static void main(String[] args) throws Exception{
//		DataLoader dl = new DataLoader();
//		try {
//			List<File> dateFile = new ArrayList<File>();
//			dateFile.add(new File("E:\\successfile\\13370000\\20121019\\SC_13370000_20121019_001N_B22_Q.TXT"));
////			dl.loadDate(InsertTye.NORMAL, "aaa", "bbb","ccc", FileOper.ListFile(new File("E:\\successfile")), "E:\\successfile", 64, 33554432 );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}


class StreamGobbler extends Thread {
	static Logger logger = Logger.getLogger(StreamGobbler.class);
	
	 InputStream is;
	 String type;
	 StringBuffer logSb;
	 StreamGobbler(InputStream is, String type, StringBuffer logSb) {
		  this.is = is;
		  this.type = type;
		  this.logSb = logSb;
	 }
	 public void run() {
		  try {
			   InputStreamReader isr = new InputStreamReader(is);
			   BufferedReader br = new BufferedReader(isr);
			   String line = null;
			   while ((line = br.readLine()) != null) {
				    if (type.equals("Error")){
					    logSb.append(FileOper.lineSeparator + System.currentTimeMillis()+", error:"+line);
				    }else{
					    logSb.append(FileOper.lineSeparator + System.currentTimeMillis()+", output:"+line);
				    }
			   }
		  } catch (IOException ioe) {
			  logger.error("error!", ioe);
		  }
	 }
}

class NormalCmdRunner implements Runnable{
	static Logger logger = Logger.getLogger(NormalCmdRunner.class);
	//需要导入的数据对应的文件
	private final ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus;
	private final String logPath;
	private final String cmdPrefix;
	private final AtomicInteger totalDataCount;
	public NormalCmdRunner(ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus, 
			String logPath, String cmdPrefix, AtomicInteger totalDataCount) {
		super();
		this.taskQueus = taskQueus;
		this.logPath = logPath;
		this.cmdPrefix  = cmdPrefix;
		this.totalDataCount = totalDataCount;
	}

	public void run() {
		Entry<String, List<File>> task = null;
//		StringBuffer sb1 = new StringBuffer();
//		StringBuffer sb2 = new StringBuffer();
		while( (task = taskQueus.poll() ) != null){
			List<File> dateFiles = task.getValue();
			if(dateFiles != null && dateFiles.size() > 0){
				String badNlogName = logPath +task.getKey()+System.currentTimeMillis();
				try {
					CtlFilePo cp = FileOper.makeCtlFile( logPath, dateFiles, task.getKey(),false);
						if(cp != null && cp.getFilePath() != null){
//						sb1.append("truncate table  "+task.getKey()+" reuse storage; \n");
//						sb2.append("(select count(*) from "+task.getKey()+")+");
							totalDataCount.addAndGet(cp.getDataSum());
							String runCmd =  cmdPrefix +" log="+ badNlogName +".log control="+cp.getFilePath();
							logger.info("run command:"+runCmd);
							DataLoader.runCmd(runCmd);
						}
				} catch (IOException e) {
					logger.error("NormalCmdRunner error! ", e);
				} catch (Exception e) {
					logger.error("NormalCmdRunner error! ", e);
				}
			}
		}
//		System.out.println(sb1);
//		System.out.println(sb2);
	}
}

class DirectCmdRunner implements Runnable{
	static Logger logger = Logger.getLogger(NormalCmdRunner.class);
	//需要导入的数据对应的文件
	private final ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus;
	private final String logPath;
	private final String cmdPrefix;
	private final AtomicInteger totalDataCount;
	public DirectCmdRunner(ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus, String logPath,
			String cmdPrefix, AtomicInteger totalDataCount) {
		super();
		this.taskQueus = taskQueus;
		this.logPath = logPath;
		this.cmdPrefix  = cmdPrefix;
		this.totalDataCount = totalDataCount;
	}


	public void run() {
		Entry<String, List<File>> task = null;
		while( (task = taskQueus.poll() ) != null){
			List<File> dateFiles = task.getValue();
			if(dateFiles != null && dateFiles.size() > 0){
				String badNlogName = logPath +task.getKey()+System.currentTimeMillis();
				try {
					CtlFilePo cp = FileOper.makeCtlFile( logPath, dateFiles, task.getKey(), true);
						if(cp != null && cp.getFilePath() != null){
							totalDataCount.addAndGet(cp.getDataSum());
							String runCmd =  cmdPrefix + " direct=true  parallel=true "+" bad="+badNlogName+".bad log="+ badNlogName +".log control="+cp.getFilePath();
							logger.info("run command:"+runCmd);
							DataLoader.runCmd(runCmd);
						}
				} catch (IOException e) {
					logger.error("NormalCmdRunner error! ", e);
				} catch (Exception e) {
					logger.error("NormalCmdRunner error! ", e);
				}
			}
		}
	}
}
class ExternalCmdRunner implements Runnable{
	static Logger logger = Logger.getLogger(NormalCmdRunner.class);
	//需要导入的数据对应的文件
	private final ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus;
	private final String logPath;
	private final String cmdPrefix;
	private final AtomicInteger totalDataCount;
	public ExternalCmdRunner(ConcurrentLinkedQueue<Entry<String, List<File>>> taskQueus, String logPath, 
			String cmdPrefix, AtomicInteger totalDataCount) {
		super();
		this.taskQueus = taskQueus;
		this.logPath = logPath;
		this.cmdPrefix  = cmdPrefix;
		this.totalDataCount = totalDataCount;
	}


	public void run() {
		Entry<String, List<File>> task = null;
		while( (task = taskQueus.poll() ) != null){
			List<File> dateFiles = task.getValue();
			if(dateFiles != null && dateFiles.size() > 0){
				String badNlogName = logPath +task.getKey()+System.currentTimeMillis();
				try {
					CtlFilePo cp = FileOper.makeCtlFile( logPath, dateFiles, task.getKey(), false);
					if(cp != null && cp.getFilePath() != null){
						totalDataCount.addAndGet(cp.getDataSum());
						String runCmd =  cmdPrefix +" bad="+badNlogName+".bad log="+ badNlogName +".log control="+cp.getFilePath() +" external_table=execute";
						logger.info("run command:"+runCmd);
						DataLoader.runCmd(runCmd);
					}
				} catch (IOException e) {
					logger.error("NormalCmdRunner error! ", e);
				} catch (Exception e) {
					logger.error("NormalCmdRunner error! ", e);
				}
			}
		}
	}
}