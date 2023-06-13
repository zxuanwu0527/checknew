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
	private static final String FILE_NUM_WRONG_MSG = "��ÿ�δ��͵��ļ����������ȷ,��û���յ�����֮ǰ�����ĵȴ�,���͹����������ļ��������Ի���ѹ���ļ�������ļ���ʽ����TXT�����ļ��ĸ�ʽ����ȷ!";
	private static final String ZQGSDM_WRONG = "ȯ�̱��͹��������������";
	private static final String SJRQ_WRONG = "*.list�ļ���¼�Ľӿ��ļ������������ڱ�־��һ�£�";
	private static final String Q_DAY_WRONG = "����Ӧ�ñ�ȫ���ļ��������������ļ���";
	private static final String DATA_NUM_WRONG = "*.list�ļ��м�¼���ļ��������߱��͹����Ľӿ��ļ�������ΪӦ������";
	private static final String FILE_NAME_WRONG = "*.list�ļ��м�¼���ļ����ƺͱ��͹������ļ����Ʋ���";
	private static final String LIST_FILE_NOT_FOUND = "list�ļ������ڣ�";
	private static final String DEL_BAK_FAIL = "ɾ�����ÿ�ʧ��";
	static final Document orderConfig = XMLUtil.loadFile("xml/FileOrder.xml");// ���������ļ�У����÷�ʽ�Ĳ�����
	private static final String UNZIP_FAIL = "��ѹ���ļ�ʧ��";
	private static final String DCYPATH = "dcydir";

	private static final AtomicInteger procCheckCount = new AtomicInteger(0);
	/* �����õĽӿ���������HashMap���� */
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
	// ����ִ�е��������
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
				// ͬ����ȡ֤ȯ��˾�Ĳ�����ʹ������̾�������
				synchronized (this) {
					if (!runingTaskQueue.isEmpty()) {
						Iterator<CheckFile> ite = runingTaskQueue.iterator();
						// ��Ȩ��
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
						// ����Ȩ���ܺ���ͣһ��ʱ��
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
					 * ÿ����һ�����¸�checkTimestamp��ֵ,����ȯ���ϱ��˼���,
					 * ���º����Ľӿڴ����д��󣬰��ϴμ������־�����ִ�����һ��
					 */
					String checkTimestamp = String.valueOf(new Date().getTime());
					
					//�����Ƿ���������ļ���������ڣ�������backupĿ¼ ���ҷ���ȯ��
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
						logger.warn("ȯ��" + zqgsdm + "û�б�������κκϷ��ļ�");
						continue;
					}
										
					logger.info("��ʼУ��֤ȯ��˾��"+checkFile.getZqgsdm()+", �������ڣ�"+dateSjrq);
											
					// ת��ȯ�̱��͹�����ԭʼ�����ļ���������
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
										
					//���������е�ѹ���ļ�
					boolean gate = false;						//���ݵ����ڴ��־
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
						//��ѹ���ļ���ͬ��Ŀ¼
						logger.info("unzip 7z file��");
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
					
						//�������ļ�������ĿУ�飨�ļ�������У�飩
						//����*.list�ļ��Ƿ����
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
							 * ����*.list�����¼�����ļ�����Ӧ�����������Ƿ�һ��
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
						
						//�����ļ�������*.list�ļ����Ƿ����
						int listfilecount = FileUtils.count(new File(extractdir.getAbsoluteFile()
								+ File.separator + listfilename));
						int filecount = extractdir.getAbsoluteFile().listFiles().length - 1;
						logger.info("check the equality of num in listFile and submission files");
						// �ж�.list�ļ������Ƿ����ϱ����ļ������
						if (listfilecount > filecount) {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									DATA_NUM_WRONG, false);
							logger.warn(zqgsdm + DATA_NUM_WRONG);
							continue;
						}
						
						/**
						 * ��������������Ϊ���壬�Ƿ��Ǳ���ȫ��
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
												
						//У��list�оٵ��ļ���ʽ�Ƿ���ȷ
						logger.info("*.list�ļ����оٵ��ļ���ʽ�Ƿ���ȷ");
						boolean txtMatch = FileUtils.checkListFile(new File(extractdir.getAbsoluteFile()
								+ File.separator + listfilename), extractdir);
						if (!txtMatch) {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									FILE_NAME_WRONG, false);
							logger.info(zqgsdm + FILE_NAME_WRONG);
							continue;
						}
						
						//���������ļ�
						String[] params = fn.split("_");
						int flag = params[3].toUpperCase().indexOf("S");
						File dcydir = null;
						if (flag > -1) {
							logger.info("decrypt txt files");
							String dcydirpath = dateDir + File.separator + fn + File.separator + DCYPATH;
							FileUtils.CreateFile(dcydirpath);
							dcydir = new File(dcydirpath);
							boolean dercyptSucc = FileUtils.PMFile_sm4(extractdir, zqgsdm, dcydirpath);				// �����ļ�
							if (logger.isDebugEnabled()) {
								logger.debug("�����Ƿ�ɹ���־��" + dercyptSucc);
							}
							if (!dercyptSucc) {
								FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
										"���ܻ�������Ϊ" + zqgsdm + "��ȯ�̱��͹������ļ�ʧ��", false);
								logger.info(zqgsdm + "���ܻ�������Ϊ" + zqgsdm + "��ȯ�̱��͹������ļ�ʧ��");
								continue;
							}
						} else {
							FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
									"���ܻ�������Ϊ" + zqgsdm + "��ȯ�̱��͹������ļ�ʧ��", false);
							logger.info(zqgsdm + "���ܻ�������Ϊ" + zqgsdm + "��ȯ�̱��͹������ļ�ʧ��");
							continue;
						}
					
						//�������ļ�����У�鹤����
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
					
					//ת��ѹ���ļ�������Ŀ¼
					backupDir =dcp.getBackupEncryptFileDir() + File.separator + zqgsdm;
					FileUtils.CreateFile(backupDir);
					for (int i = 0; i < filelist2.length; i++) {
						Path source = Paths.get(filelist2[i].getAbsolutePath());
						Path destination = Paths.get(backupDir + File.separator + filelist2[i].getName());
						Files.move(source, destination);
					}
					
					//���ݽ�����һ��У��
					if (!gate) {
						continue;
					}
					
					logger.info("***********��ʼ����ļ�����***********");
					//FileFilter filter3 = new FileFilter(".txt");
					//File[] filelist4 = workSpace.listFiles(filter3);
					if (filelist3.length != dcp.getDataFileCount()) {
						//��������
						FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp,
							"���͹����������ļ�������������Ϊ��" + filelist3.length, false);
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
						logger.debug("��ǰȯ�̱���Ϊ��" + zqgsdm);
					}
					
					logger.info("delete  history basic data from DB");
					if (!FileUtils.delBak(zqgsdm)) {
						logger.info("ɾ�����ÿ�ʧ��");
						// ɾ����ǰδ������Ľ�ѹ���ܺ��ļ�
						//FileUtils.deleteAllFile(checkFile.getZqgsDir());
						FileUtils.createResultFile(dcp, zqgsdm, dateSjrq, checkTimestamp, DEL_BAK_FAIL,
								false);
						continue;
					}
					// long backupStart = System.currentTimeMillis();
					if (checkFileSucc) {
						// ������⣬�ȴ�У��
						FileUtils.saveListFilesToKrcs(dcp, filelist3);
						boolean initFlag = false;
						if (zqgsdm != null && zqgsdm != "") {
							// û��ͬ�������ܲ��Ǻܾ�ȷ
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
							logger.info("�ļ�Ŀ¼ " + dcp.getRootDir() + zqgsdm + "/ �µ���������Ϊ��"+dateSjrq+"���ļ��������, �ɹ�");
						} else {
							//FileUtils.deleteAllFile(checkFile.getZqgsDir());
							logger.info("�ļ�Ŀ¼ " + dcp.getRootDir() + zqgsdm + "/ �µ���������Ϊ��"+dateSjrq+"���ļ��������, ʧ��");
						}
					} else {
						// logger.info("back failed file:"+zqgsdm);
						// FileUtils.classifySubmissionFiles(dcp,
						// checkFile.getZqgsDir(), zqgsdm,
						// interfaceSjrq, false);
						// ɾ����ǰδ������Ľ�ѹ���ܺ��ļ�
						//FileUtils.deleteAllFile(checkFile.getZqgsDir());
						logger.info("�ļ�Ŀ¼ " + dcp.getRootDir() + zqgsdm + "/ �µ���������Ϊ��"+dateSjrq+"���ļ�������ϣ� ʧ��");
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
