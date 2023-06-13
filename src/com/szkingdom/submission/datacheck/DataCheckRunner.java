package com.szkingdom.submission.datacheck;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.szkingdom.submission.datacheck.FileUtils;
import com.szkingdom.submission.datacheck.FileFilter;
import com.szkingdom.XMLUtil;
import com.szkingdom.submission.datacheck.po.CheckFile;
import com.szkingdom.submission.datacheck.utils.PropertyUtil;
import java.util.regex.Matcher;

@SuppressWarnings("rawtypes")
public class DataCheckRunner implements Runnable {

	private static final Logger logger = Logger.getLogger(DataCheckRunner.class);
	private static final String FILE_NUM_WRONG_MSG = "请每次传送的文件个数务必正确,在没有收到反馈之前请耐心等待,报送过来的数据文件个数不对或者压缩文件里面的文件格式不是TXT或者文件的格式不正确!";
	private static final String ZQGSDM_WRONG = "券商报送过来机构编码错误！";
	private static final String SJRQ_WRONG = "*.list文件记录的接口文件名的数据日期标志不一致！";
	private static final String Q_DAY_WRONG = "周五应该报全量文件，发现了增量文件！";
	private static final String DATA_NUM_WRONG = "*.list文件中记录的文件数量或者报送过来的接口文件数量不为应报数！";
	private static final String FILE_NAME_WRONG = "*.list文件中记录的文件名称和报送过来的文件名称不符";
	private static final String LIST_FILE_NOT_FOUND = "list文件不存在！";
	private static final String DEL_BAK_FAIL = "删除备用库失败";
	static final Document orderConfig = XMLUtil.loadFile("xml/FileOrder.xml");// 用于配置文件校验调用方式的参数的
	private static final String UNZIP_FAIL = "解压缩文件失败";
	private static final String DCYPATH = "dcydir";

	private static final AtomicInteger procCheckCount = new AtomicInteger(0);
	/* 将配置的接口列数放入HashMap里面 */
	static final HashMap<String, Integer> colNumMap = new HashMap<String, Integer>();
	static {
		List params = DataCheckRunner.orderConfig.selectNodes("//order/type/dict/params/param");
		for (int f = 0; f < params.size(); f++) {
			if (params.get(f) != null) {
				Element elementparam = (Element) (params.get(f));
				String param_value = elementparam.getText();
				colNumMap.put(param_value.split(",")[0], Integer.parseInt(param_value.split(",")[1].trim()));
			}
		}
	}
	// 正在执行的任务队列
	private final ConcurrentLinkedQueue<CheckFile> runingTaskQueue;
	private final AtomicBoolean isRunning;
	private String batFile = "1.bat";

	public DataCheckRunner(ConcurrentLinkedQueue<CheckFile> preTaskQueue,
			ConcurrentLinkedQueue<CheckFile> runingTaskQueue, AtomicBoolean isRunning, String batFile) {
		this.runingTaskQueue = runingTaskQueue;
		this.isRunning = isRunning;
		if (StringUtils.isNotBlank(batFile)) {
			this.batFile = batFile;
		}
	}

	public void run() {
		try {
			DataCheckProperty dcp = (DataCheckProperty) ObjectFactory.instance().getBean("dataCheckProperty");
			CheckService cs = (CheckService) ObjectFactory.instance().getBean("checkservice");

			ThreadParam tp = (ThreadParam) ObjectFactory.instance().getBean("threaParam");
			int zqgsNumLimit = tp.getLogicCheckZqgsNum() - 1;
			AtomicInteger totalWt = new AtomicInteger(0);
			while (isRunning.get()) {
				CheckFile checkFile = null;// preTaskQueue.poll();
				String zqgsdm = null;
				// 同步获取证券公司的操作，使多个进程尽量均衡
				synchronized (this) {
					if (!runingTaskQueue.isEmpty()) {
						Iterator<CheckFile> ite = runingTaskQueue.iterator();
						// 总权重
						while (ite.hasNext()) {
							int wt = 1;
							try {
								CheckFile runningTask = ite.next();
								String weight = PropertyUtil.get(runningTask.getZqgsdm(), "conf/zqgswt.properties");
								if (StringUtils.isNotBlank(weight)) {
									wt = Integer.parseInt(weight);
								}
							} catch (Exception e) {
							}
							totalWt.addAndGet(wt);
						}
						// 根据权重总和暂停一段时间
						try {
							Thread.sleep(totalWt.get() * Integer.parseInt(PropertyUtil.get("fragmet", "conf/zqgswt.properties")));
						} catch (Exception e) {
							Thread.sleep(100);
						}
					}
					zqgsdm = cs.getValiableZqgs(dcp.getRootDir(), dcp.getSubmissionFileCount());
					if (StringUtils.isNotBlank(zqgsdm)) {
						File zqgsDir = new File(dcp.getRootDir() + File.separator + zqgsdm);
						if (!zqgsDir.exists()) {
							Thread.sleep(100);
							continue;
						}
						checkFile = new CheckFile(zqgsdm, zqgsDir);
					} else {
						Thread.sleep(100);
						continue;
					}
					if (checkFile != null && !runingTaskQueue.contains(checkFile)) {
						runingTaskQueue.offer(checkFile);
					} else {
						Thread.sleep(100);
						continue;
					}
				}

				boolean transmissComplete = true;
				long startTime = System.currentTimeMillis();
				String dateSjrq = null;
				try {
					/**
					 * 每检验一轮重新给checkTimestamp赋值,避免券商上报了几次,
					 * 导致后续的接口处理有错误，把上次检验的日志数据又处理了一次
					 */
					String checkTimestamp = String.valueOf(new Date().getTime());
					
					//检验是否存在垃圾文件，如果存在，保存至backup目录 并且反馈券商
					String backupDir =dcp.getBackupEncryptFileDir() + File.separator + zqgsdm;
					FileUtils.CreateFile(backupDir);

					String rubbishCheckMsg=FileUtils.checkRubbishFileAndMove(checkFile.getZqgsDir(),backupDir);

					if(!rubbishCheckMsg.isEmpty()){

						logger.warn(zqgsdm +" "+ rubbishCheckMsg);
						//FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
						//		rubbishCheckMsg, false);
						//return;
					}

					FileUtils.CreateFile(dcp.getWorkSpaceFileDir());

					dateSjrq = FileUtils.getInterfaceDate(checkFile.getZqgsDir());
					if (!StringUtils.isNotBlank(dateSjrq)) {
						logger.warn("券商" + zqgsdm + "没有报送完毕任何合法文件");
						continue;
					}
										
					logger.info("开始校验证券公司："+checkFile.getZqgsdm()+", 数据日期："+dateSjrq);
											
					// 转移券商报送过来的原始所有文件至工作区
					FileFilter filter = new FileFilter(".7z");
					String dateDir =dcp.getWorkSpaceFileDir() + File.separator + zqgsdm + File.separator + dateSjrq;					
					FileUtils.CreateFile(dateDir);					
					File dateDirFile = new File(dateDir);
					File[] rmlist = dateDirFile.listFiles(filter);
					for (int i = 0; i < rmlist.length; i++) {
						rmlist[i].delete();
					}
					
					File[] filelist = checkFile.getZqgsDir().listFiles(filter);
					logger.info("move zqgs " + checkFile.getZqgsdm() + " 7z file to working space");
					
					for (int i = 0; i < filelist.length; i++) {
						Matcher matcher = FileUtils.namePattern.matcher
								(filelist[i].getName().toUpperCase());
						if (matcher.find()) {
							if (filelist[i].getName().split("_")[2].equals(dateSjrq)) {
								FileUtils.Move7zFile(filelist[i], dateDir);
								break;
							}
						}
					}
										
					//处理工作区中的压缩文件
					boolean gate = false;						//数据导入内存标志
					File workSpace = new File(dateDir);			
					File[] filelist2 = workSpace.listFiles(filter);
					File[] filelist3 = null;
					//List<String> fileLists = null;
						
					for (int i = 0; i < filelist2.length; i++) {
						String fn = filelist2[i].getName().substring(0, filelist2[i].getName().length() - 3);
						File extractdir = new File(dateDir + File.separator + fn);
						if (extractdir.exists()) {
							continue;
						}
						//解压缩文件至同名目录
						logger.info("unzip 7z file。");
						String cmdstring = "7z x " + filelist2[i].getAbsolutePath()
								+ " -aoa -pnopwd -o" + extractdir.getAbsolutePath();				
						RuntimeResult rr = CmdRunner.runCmd(cmdstring);
						if (!rr.isSuccess()) {
							filelist2[i].delete();
							logger.warn("run comand 7z wrong!" + rr.getErrInfo());
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									UNZIP_FAIL, false);
							continue;
						}
					
						//本批次文件名及数目校验（文件完整性校验）
						//检验*.list文件是否存在
						logger.info("find list file");
						String listfilename = FileUtils.Find_ListFile(extractdir);
						File listFile = null;
						if (listfilename == null) {

							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									LIST_FILE_NOT_FOUND, false);
							logger.warn(zqgsdm + LIST_FILE_NOT_FOUND);
							continue;
						} else {
							/*
							 * 检验*.list里面记录各个文件名对应的数据日期是否一致
							 */
							logger.info("check sjrq equal for each file");
							listFile = new File(extractdir.getAbsoluteFile() + File.separator
									+ listfilename);
							//fileLists= FileUtils.getFileListFromList(listFile);
							boolean sjrqEqual = FileUtils.check_List_Sjrq(listFile);
							if (!sjrqEqual) {
								FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
										SJRQ_WRONG, false);
								logger.warn(zqgsdm + SJRQ_WRONG);
								continue;
							}
						}
						
						//计算文件个数和*.list文件中是否相符
						int listfilecount = FileUtils.count(new File(extractdir.getAbsoluteFile()
								+ File.separator + listfilename));
						int filecount = extractdir.getAbsoluteFile().listFiles().length - 1;
						logger.info("check the equality of num in listFile and submission files");
						// 判断.list文件个数是否与上报的文件数相等
						if (listfilecount > filecount) {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									DATA_NUM_WRONG, false);
							logger.warn(zqgsdm + DATA_NUM_WRONG);
							continue;
						}
						
						/**
						 * 检查如果数据日期为周五，是否都是报的全量
						 */
						if (dcp.isCheckQOnFri() && FileUtils.isFriDay(dateSjrq)) {
							boolean isQdayList = FileUtils.checkIsQDayList(listFile);
							if (!isQdayList) {
								FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
										Q_DAY_WRONG, false);
								logger.error(zqgsdm + ":" + dateSjrq
										+ " is  a Q day, but find Z files!");
								continue;
							}
						}
												
						//校验list列举的文件格式是否正确
						logger.info("*.list文件中列举的文件格式是否正确");
						boolean txtMatch = FileUtils.checkListFile(new File(extractdir.getAbsoluteFile()
								+ File.separator + listfilename), extractdir);
						if (!txtMatch) {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									FILE_NAME_WRONG, false);
							logger.info(zqgsdm + FILE_NAME_WRONG);
							continue;
						}
						
						//解密数据文件
						String[] params = fn.split("_");
						int flag = params[3].toUpperCase().indexOf("S");
						File dcydir = null;
						if (flag > -1) {
							logger.info("decrypt txt files");
							String dcydirpath = dateDir + File.separator + fn + File.separator + DCYPATH;
							FileUtils.CreateFile(dcydirpath);
							dcydir = new File(dcydirpath);
							boolean dercyptSucc = FileUtils.PMFile_sm4(extractdir, zqgsdm, dcydirpath);				// 解密文件
							if (logger.isDebugEnabled()) {
								logger.debug("解密是否成功标志：" + dercyptSucc);
							}
							if (!dercyptSucc) {
								FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
										"解密机构编码为" + zqgsdm + "的券商报送过来的文件失败", false);
								logger.info(zqgsdm + "解密机构编码为" + zqgsdm + "的券商报送过来的文件失败");
								continue;
							}
						} else {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									"解密机构编码为" + zqgsdm + "的券商报送过来的文件失败", false);
							logger.info(zqgsdm + "解密机构编码为" + zqgsdm + "的券商报送过来的文件失败");
							continue;
						}
					
						//将数据文件移至校验工作区
						FileFilter filter2 = new FileFilter(".txt");
						filelist3 = dcydir.listFiles(filter2);
						/*for (int j = 0; j < filelist3.length; j++) {
							fn = filelist3[j].getName();
							File tmpFile = new File(dateDir + File.separator + fn.substring(2));
							Path sPath = Paths.get(filelist3[j].getAbsolutePath());
							Path dPath = Paths.get(tmpFile.getAbsolutePath());
							if (!tmpFile.exists()){
								Files.move(sPath, dPath);
							} else if (filelist3[j].lastModified() >= tmpFile.lastModified()) {
								Files.move(sPath, dPath, REPLACE_EXISTING);
							}
						}*/
						gate = true;
					}
					
					//转移压缩文件至备份目录
					backupDir =dcp.getBackupEncryptFileDir() + File.separator + zqgsdm;
					FileUtils.CreateFile(backupDir);
					for (int i = 0; i < filelist2.length; i++) {
						Path source = Paths.get(filelist2[i].getAbsolutePath());
						Path destination = Paths.get(backupDir + File.separator + filelist2[i].getName());
						Files.move(source, destination);
					}
					
					//数据进行下一步校验
					if (!gate) {
						continue;
					}
					
					logger.info("***********开始检测文件数量***********");
					//FileFilter filter3 = new FileFilter(".txt");
					//File[] filelist4 = workSpace.listFiles(filter3);
					if (filelist3.length != dcp.getDataFileCount()) {
						//反馈错误
						FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
							"报送过来的数据文件个数错误，数量为：" + filelist3.length, false);
						logger.error(zqgsdm + ": " + dateSjrq
								+ ": the number of data files is wrong!");
						continue;
					}
					boolean allTxtFormat = true;
					for (int i = 0; i < filelist3.length; i++) {
						if (filelist3[i].getName().split("_").length != 7) {
							allTxtFormat = false;
							break;
						}
					}
					if (!allTxtFormat) {
						FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
							FILE_NUM_WRONG_MSG, false);
						logger.info(zqgsdm + FILE_NUM_WRONG_MSG);
						continue;
					}
					
					logger.info("checkfile");
					boolean checkFileSucc = FileUtils.CheckFile(filelist3, checkTimestamp);
					// long checkFileEnd = System.currentTimeMillis();
					// System.out.println("checkfile cost:"+(checkFileEnd
					// - checkFileStart)+":"+checkFileSucc);
					if (logger.isDebugEnabled()) {
						logger.debug("当前券商编码为：" + zqgsdm);
					}
					
					logger.info("delete  history basic data from DB");
					if (!FileUtils.delBak(zqgsdm)) {
						logger.info("删除备用库失败");
						// 删除当前未处理掉的解压解密后文件
						//FileUtils.deleteAllFile(checkFile.getZqgsDir());
						FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp, DEL_BAK_FAIL,
								false);
						continue;
					}
					// long backupStart = System.currentTimeMillis();
					if (checkFileSucc) {
						// 数据入库，等待校验
						FileUtils.saveListFilesToKrcs(dcp, filelist3);
						boolean initFlag = false;
						if (zqgsdm != null && zqgsdm != "") {
							// 没有同步，可能不是很精确
							while (procCheckCount.get() > zqgsNumLimit) {
								Thread.sleep(0);
							}
							// System.out.println(zqgsdm+","+interfaceSjrq+","+checkTimestamp);			
							
							procCheckCount.incrementAndGet();
							initFlag = FileUtils.init(dcp, zqgsdm, dateSjrq, checkTimestamp);
							procCheckCount.decrementAndGet();
						}
						if (initFlag) {
							logger.info("backup success file:" + zqgsdm);					
							FileUtils.classifySubmissionListFiles(dcp, filelist3, zqgsdm,
									dateSjrq);
							logger.info("文件目录 " + dcp.getRootDir() + zqgsdm + "/ 下的数据日期为："+dateSjrq+"的文件检验完毕, 成功");
						} else {
							//FileUtils.deleteAllFile(checkFile.getZqgsDir());
							logger.info("文件目录 " + dcp.getRootDir() + zqgsdm + "/ 下的数据日期为："+dateSjrq+"的文件检验完毕, 失败");
						}
					} else {
						// logger.info("back failed file:"+zqgsdm);
						// FileUtils.classifySubmissionFiles(dcp,
						// checkFile.getZqgsDir(), zqgsdm,
						// interfaceSjrq, false);
						// 删除当前未处理掉的解压解密后文件
						//FileUtils.deleteAllFile(checkFile.getZqgsDir());
						logger.info("文件目录 " + dcp.getRootDir() + zqgsdm + "/ 下的数据日期为："+dateSjrq+"的文件检验完毕， 失败");
						if (zqgsdm != null && zqgsdm != "") {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp, false);
						}
					}
					// long backupEnd = System.currentTimeMillis();
					// System.out.println("backup total time:"+(backupEnd
					// - backupStart));
				} finally {
					runingTaskQueue.remove(checkFile);

					cs.setFinished(zqgsdm);
					if (transmissComplete) {
						long endTime = System.currentTimeMillis();
						logger.info("check for zqgs" + checkFile.getZqgsdm() + " finished! cost:"
								+ (endTime - startTime));
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static void main(String[] args) {
		System.out.println("start==========" + System.currentTimeMillis());

		System.out.println("end==========" + System.currentTimeMillis());
	}

	public static void dealFile(CheckFile checkFile) {

	}
}
