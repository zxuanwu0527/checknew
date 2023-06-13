prompt PL/SQL Developer import file
prompt Created on 2012Äê10ÔÂ8ÈÕ by Administrator
set feedback off
set define off
prompt Creating SC_B01_13360000_Q...

create table SC_B01_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  ZQGSMC VARCHAR2(255),
  ZQGSJZB VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B01_13360000_Q to KRCS;

prompt Creating SC_B02_13360000_Q...

create table SC_B02_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  ZHLX   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  KHRQ   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B02_13360000_Q to KRCS;

prompt Creating SC_B03_13360000_Q...

create table SC_B03_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  ZHLX   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  ZQSL   VARCHAR2(255),
  WDZSL  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B03_13360000_Q to KRCS;

prompt Creating SC_B04_13360000_Q...

create table SC_B04_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZHLX   VARCHAR2(255),
  BZ     VARCHAR2(255),
  ZJYE   VARCHAR2(255),
  WDZJE  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B04_13360000_Q to KRCS;

prompt Creating SC_B05_13360000_Q...

create table SC_B05_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  RZRQBD VARCHAR2(255),
  DBWBD  VARCHAR2(255),
  ZSL    VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B05_13360000_Q to KRCS;

prompt Creating SC_B06_13360000_Q...

create table SC_B06_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  YHDM   VARCHAR2(255),
  YHMC   VARCHAR2(255),
  CGLX   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B06_13360000_Q to KRCS;

prompt Creating SC_B07_13360000_Q...

create table SC_B07_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  YYBDM  VARCHAR2(255),
  YYBMC  VARCHAR2(255),
  YYBDZ  VARCHAR2(255),
  ZJJDM  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B07_13360000_Q to KRCS;

prompt Creating SC_B08_13360000_Q...

create table SC_B08_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  FLLX   VARCHAR2(255),
  XFL    VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B08_13360000_Q to KRCS;

prompt Creating SC_B09_13360000_Q...

create table SC_B09_13360000_Q
(
  ZQGSDM    VARCHAR2(255),
  XYJBDM    VARCHAR2(255),
  XYJBMC    VARCHAR2(255),
  RZCSBZJBL VARCHAR2(255),
  RZSXEDBZ  VARCHAR2(255),
  RQCSBZJBL VARCHAR2(255),
  RQSXEDBZ  VARCHAR2(255),
  BZZ       VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B09_13360000_Q to KRCS;

prompt Creating SC_B10_13360000_Q...

create table SC_B10_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  QYDM   VARCHAR2(255),
  QYLX   VARCHAR2(255),
  ZYXX   VARCHAR2(255),
  QYBL   VARCHAR2(255),
  DJRQ   VARCHAR2(255),
  CQRQ   VARCHAR2(255),
  QYSSRQ VARCHAR2(255),
  QYLSH  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B10_13360000_Q to KRCS;

prompt Creating SC_B10_13360000_Z...

create table SC_B10_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  QYDM   VARCHAR2(255),
  QYLX   VARCHAR2(255),
  ZYXX   VARCHAR2(255),
  QYBL   VARCHAR2(255),
  DJRQ   VARCHAR2(255),
  CQRQ   VARCHAR2(255),
  QYSSRQ VARCHAR2(255),
  QYLSH  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B10_13360000_Z to KRCS;

prompt Creating SC_B11_13360000_Q...

create table SC_B11_13360000_Q
(
  ZQGSDM    VARCHAR2(255),
  BZ        VARCHAR2(255),
  KHZJYE    VARCHAR2(255),
  KHZYZJ    VARCHAR2(255),
  KHRQMCZJ  VARCHAR2(255),
  KHWDZZJ   VARCHAR2(255),
  KHZQSZ    VARCHAR2(255),
  KHWDZZQSZ VARCHAR2(255),
  KHZZC     VARCHAR2(255),
  KHRZFZ    VARCHAR2(255),
  KHRZJE    VARCHAR2(255),
  KHRZSXFY  VARCHAR2(255),
  KHRZXF    VARCHAR2(255),
  KHRZQTFY  VARCHAR2(255),
  KHRQFZ    VARCHAR2(255),
  KHRQJE    VARCHAR2(255),
  KHRQSXFY  VARCHAR2(255),
  KHRQFY    VARCHAR2(255),
  KHRQQTFY  VARCHAR2(255),
  KHBZJYE   VARCHAR2(255),
  RZZYBZJ   VARCHAR2(255),
  RQZYBZJ   VARCHAR2(255),
  KHZFZ     VARCHAR2(255),
  ZTWCDBBL  VARCHAR2(255),
  RZGGL     VARCHAR2(255),
  RQGGL     VARCHAR2(255),
  ZTGGL     VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B11_13360000_Q to KRCS;

prompt Creating SC_B12_13360000_Q...

create table SC_B12_13360000_Q
(
  ZQGSDM  VARCHAR2(255),
  BZ      VARCHAR2(255),
  QSRQ    VARCHAR2(255),
  ZZRQ    VARCHAR2(255),
  DBPMRJE VARCHAR2(255),
  DBPMCJE VARCHAR2(255),
  RZMRJE  VARCHAR2(255),
  MQHKJE  VARCHAR2(255),
  ZJHKJE  VARCHAR2(255),
  RZPCJE  VARCHAR2(255),
  RQMCJE  VARCHAR2(255),
  MQHQJE  VARCHAR2(255),
  HQHZJE  VARCHAR2(255),
  YQHZJE  VARCHAR2(255),
  RQPCJE  VARCHAR2(255),
  PTJYJE  VARCHAR2(255),
  XYJYJE  VARCHAR2(255),
  JYJEHJ  VARCHAR2(255),
  SJRQ    VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B12_13360000_Q to KRCS;

prompt Creating SC_B13_13360000_Q...

create table SC_B13_13360000_Q
(
  ZQGSDM   VARCHAR2(255),
  QSRQ     VARCHAR2(255),
  ZZRQ     VARCHAR2(255),
  BZ       VARCHAR2(255),
  RZLX     VARCHAR2(255),
  RQFY     VARCHAR2(255),
  YQFX     VARCHAR2(255),
  QTXFSR   VARCHAR2(255),
  KHWJXF   VARCHAR2(255),
  KHYJWFXF VARCHAR2(255),
  XFSRHJ   VARCHAR2(255),
  JYGF     VARCHAR2(255),
  PTJYYJ   VARCHAR2(255),
  XYJYYJ   VARCHAR2(255),
  YJHJ     VARCHAR2(255),
  PTYJL    VARCHAR2(255),
  XYYJL    VARCHAR2(255),
  ZTYJL    VARCHAR2(255),
  HZSXF    VARCHAR2(255),
  QTJYSR   VARCHAR2(255),
  JYSRHJ   VARCHAR2(255),
  ZTSRHJ   VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_B13_13360000_Q to KRCS;

prompt Creating SC_C01_13360000...

create table SC_C01_13360000
(
  ZQGSDM    VARCHAR2(255),
  YYBDM     VARCHAR2(255),
  KHDM      VARCHAR2(255),
  KHKHRQ    VARCHAR2(255),
  KHZXRQ    VARCHAR2(255),
  KHMC      VARCHAR2(255),
  KHLX      VARCHAR2(255),
  KHZT      VARCHAR2(255),
  ZJLX1     VARCHAR2(255),
  ZJHM1     VARCHAR2(255),
  ZJYXQJZR1 VARCHAR2(255),
  ZJLX2     VARCHAR2(255),
  ZJHM2     VARCHAR2(255),
  ZJYXQJZR2 VARCHAR2(255),
  KHGJ      VARCHAR2(255),
  KHXL      VARCHAR2(255),
  KHZY      VARCHAR2(255),
  FRDB      VARCHAR2(255),
  FRDBZJHM  VARCHAR2(255),
  JBR       VARCHAR2(255),
  JBRZJHM   VARCHAR2(255),
  ZCDZ      VARCHAR2(255),
  LXDZ      VARCHAR2(255),
  YZBM      VARCHAR2(255),
  LXDH      VARCHAR2(255),
  SJHM      VARCHAR2(255),
  DZYX      VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C01_13360000 to KRCS;

prompt Creating SC_C01_13360000_Q...

create table SC_C01_13360000_Q
(
  ZQGSDM    VARCHAR2(255),
  YYBDM     VARCHAR2(255),
  KHDM      VARCHAR2(255),
  KHKHRQ    VARCHAR2(255),
  KHZXRQ    VARCHAR2(255),
  KHMC      VARCHAR2(255),
  KHLX      VARCHAR2(255),
  KHZT      VARCHAR2(255),
  ZJLX1     VARCHAR2(255),
  ZJHM1     VARCHAR2(255),
  ZJYXQJZR1 VARCHAR2(255),
  ZJLX2     VARCHAR2(255),
  ZJHM2     VARCHAR2(255),
  ZJYXQJZR2 VARCHAR2(255),
  KHGJ      VARCHAR2(255),
  KHXL      VARCHAR2(255),
  KHZY      VARCHAR2(255),
  FRDB      VARCHAR2(255),
  FRDBZJHM  VARCHAR2(255),
  JBR       VARCHAR2(255),
  JBRZJHM   VARCHAR2(255),
  ZCDZ      VARCHAR2(255),
  LXDZ      VARCHAR2(255),
  YZBM      VARCHAR2(255),
  LXDH      VARCHAR2(255),
  SJHM      VARCHAR2(255),
  DZYX      VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C01_13360000_Q to KRCS;

prompt Creating SC_C01_13360000_Z...

create table SC_C01_13360000_Z
(
  ZQGSDM    VARCHAR2(255),
  YYBDM     VARCHAR2(255),
  KHDM      VARCHAR2(255),
  KHKHRQ    VARCHAR2(255),
  KHZXRQ    VARCHAR2(255),
  KHMC      VARCHAR2(255),
  KHLX      VARCHAR2(255),
  KHZT      VARCHAR2(255),
  ZJLX1     VARCHAR2(255),
  ZJHM1     VARCHAR2(255),
  ZJYXQJZR1 VARCHAR2(255),
  ZJLX2     VARCHAR2(255),
  ZJHM2     VARCHAR2(255),
  ZJYXQJZR2 VARCHAR2(255),
  KHGJ      VARCHAR2(255),
  KHXL      VARCHAR2(255),
  KHZY      VARCHAR2(255),
  FRDB      VARCHAR2(255),
  FRDBZJHM  VARCHAR2(255),
  JBR       VARCHAR2(255),
  JBRZJHM   VARCHAR2(255),
  ZCDZ      VARCHAR2(255),
  LXDZ      VARCHAR2(255),
  YZBM      VARCHAR2(255),
  LXDH      VARCHAR2(255),
  SJHM      VARCHAR2(255),
  DZYX      VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C01_13360000_Z to KRCS;

prompt Creating SC_C02_13360000_Q...

create table SC_C02_13360000_Q
(
  ZQGSDM    VARCHAR2(255),
  KHDM      VARCHAR2(255),
  HTQXMS    VARCHAR2(255),
  HTZT      VARCHAR2(255),
  HTHM      VARCHAR2(255),
  HTQSRQ    VARCHAR2(255),
  HTZZRQ    VARCHAR2(255),
  XYJBDM    VARCHAR2(255),
  RZCSBZJBL VARCHAR2(255),
  RQCSBZJBL VARCHAR2(255),
  SXED      VARCHAR2(255),
  RZSX      VARCHAR2(255),
  RQSX      VARCHAR2(255),
  SFHMDKH   VARCHAR2(255),
  SFFXKH    VARCHAR2(255),
  SFXZJYKH  VARCHAR2(255),
  SFXSGKH   VARCHAR2(255),
  SFDJGKH   VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C02_13360000_Q to KRCS;

prompt Creating SC_C02_13360000_Z...

create table SC_C02_13360000_Z
(
  ZQGSDM    VARCHAR2(255),
  KHDM      VARCHAR2(255),
  HTQXMS    VARCHAR2(255),
  HTZT      VARCHAR2(255),
  HTHM      VARCHAR2(255),
  HTQSRQ    VARCHAR2(255),
  HTZZRQ    VARCHAR2(255),
  XYJBDM    VARCHAR2(255),
  RZCSBZJBL VARCHAR2(255),
  RQCSBZJBL VARCHAR2(255),
  SXED      VARCHAR2(255),
  RZSX      VARCHAR2(255),
  RQSX      VARCHAR2(255),
  SFHMDKH   VARCHAR2(255),
  SFFXKH    VARCHAR2(255),
  SFXZJYKH  VARCHAR2(255),
  SFXSGKH   VARCHAR2(255),
  SFDJGKH   VARCHAR2(255),
  SJRQ      VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C02_13360000_Z to KRCS;

prompt Creating SC_C03_13360000...

create table SC_C03_13360000
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHLX   VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZDZT   VARCHAR2(255),
  XWDM   VARCHAR2(255),
  YHDM   VARCHAR2(255),
  ZHZT   VARCHAR2(255),
  KHRQ   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C03_13360000 to KRCS;

prompt Creating SC_C03_13360000_Q...

create table SC_C03_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHLX   VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZDZT   VARCHAR2(255),
  XWDM   VARCHAR2(255),
  YHDM   VARCHAR2(255),
  ZHZT   VARCHAR2(255),
  KHRQ   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C03_13360000_Q to KRCS;

prompt Creating SC_C03_13360000_Z...

create table SC_C03_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHLX   VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZDZT   VARCHAR2(255),
  XWDM   VARCHAR2(255),
  YHDM   VARCHAR2(255),
  ZHZT   VARCHAR2(255),
  KHRQ   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C03_13360000_Z to KRCS;

prompt Creating SC_C04_13360000...

create table SC_C04_13360000
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  ZJYE   VARCHAR2(255),
  DJZJ   VARCHAR2(255),
  WDZZJ  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C04_13360000 to KRCS;

prompt Creating SC_C04_13360000_Q...

create table SC_C04_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  ZJYE   VARCHAR2(255),
  DJZJ   VARCHAR2(255),
  WDZZJ  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C04_13360000_Q to KRCS;

prompt Creating SC_C05_13360000_Q...

create table SC_C05_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  FSRQ   VARCHAR2(255),
  FSSJ   VARCHAR2(255),
  BDLB   VARCHAR2(255),
  ZJLSH  VARCHAR2(255),
  ZY     VARCHAR2(255),
  FSJE   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C05_13360000_Q to KRCS;

prompt Creating SC_C05_13360000_Z...

create table SC_C05_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  FSRQ   VARCHAR2(255),
  FSSJ   VARCHAR2(255),
  BDLB   VARCHAR2(255),
  ZJLSH  VARCHAR2(255),
  ZY     VARCHAR2(255),
  FSJE   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C05_13360000_Z to KRCS;

prompt Creating SC_C06_13360000...

create table SC_C06_13360000
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  ZQSL   VARCHAR2(255),
  WDZSL  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C06_13360000 to KRCS;

prompt Creating SC_C06_13360000_Q...

create table SC_C06_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  ZQSL   VARCHAR2(255),
  WDZSL  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C06_13360000_Q to KRCS;

prompt Creating SC_C07_13360000_Q...

create table SC_C07_13360000_Q
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  BDLB     VARCHAR2(255),
  FJYZQLSH VARCHAR2(255),
  ZY       VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  BDSL     VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C07_13360000_Q to KRCS;

prompt Creating SC_C07_13360000_Z...

create table SC_C07_13360000_Z
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  BDLB     VARCHAR2(255),
  FJYZQLSH VARCHAR2(255),
  ZY       VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  BDSL     VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C07_13360000_Z to KRCS;

prompt Creating SC_C08_13360000...

create table SC_C08_13360000
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  RZYE   VARCHAR2(255),
  GF     VARCHAR2(255),
  JYYJ   VARCHAR2(255),
  RZLX   VARCHAR2(255),
  QTFY   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C08_13360000 to KRCS;

prompt Creating SC_C08_13360000_Q...

create table SC_C08_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  RZYE   VARCHAR2(255),
  GF     VARCHAR2(255),
  JYYJ   VARCHAR2(255),
  RZLX   VARCHAR2(255),
  QTFY   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C08_13360000_Q to KRCS;

prompt Creating SC_C09_13360000...

create table SC_C09_13360000
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  RZKCDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  CJJG     VARCHAR2(255),
  KCJE     VARCHAR2(255),
  CHJE     VARCHAR2(255),
  RZJE     VARCHAR2(255),
  RZLX     VARCHAR2(255),
  YQFX     VARCHAR2(255),
  WJXF     VARCHAR2(255),
  YJWFXF   VARCHAR2(255),
  YJYFXF   VARCHAR2(255),
  LJBZ     VARCHAR2(255),
  CDDQR    VARCHAR2(255),
  BZZ      VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C09_13360000 to KRCS;

prompt Creating SC_C09_13360000_Q...

create table SC_C09_13360000_Q
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  RZKCDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  CJJG     VARCHAR2(255),
  KCJE     VARCHAR2(255),
  CHJE     VARCHAR2(255),
  RZJE     VARCHAR2(255),
  RZLX     VARCHAR2(255),
  YQFX     VARCHAR2(255),
  WJXF     VARCHAR2(255),
  YJWFXF   VARCHAR2(255),
  YJYFXF   VARCHAR2(255),
  LJBZ     VARCHAR2(255),
  CDDQR    VARCHAR2(255),
  BZZ      VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C09_13360000_Q to KRCS;

prompt Creating SC_C10_13360000_Q...

create table SC_C10_13360000_Q
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  RZKCDJHM VARCHAR2(255),
  CHLSH    VARCHAR2(255),
  CHLX     VARCHAR2(255),
  QZPCBZ   VARCHAR2(255),
  ZY       VARCHAR2(255),
  CHJE     VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C10_13360000_Q to KRCS;

prompt Creating SC_C10_13360000_Z...

create table SC_C10_13360000_Z
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  RZKCDJHM VARCHAR2(255),
  CHLSH    VARCHAR2(255),
  CHLX     VARCHAR2(255),
  QZPCBZ   VARCHAR2(255),
  ZY       VARCHAR2(255),
  CHJE     VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C10_13360000_Z to KRCS;

prompt Creating SC_C11_13360000...

create table SC_C11_13360000
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  RQSL   VARCHAR2(255),
  RQJE   VARCHAR2(255),
  RQFY   VARCHAR2(255),
  QTFY   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C11_13360000 to KRCS;

prompt Creating SC_C11_13360000_Q...

create table SC_C11_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  RQSL   VARCHAR2(255),
  RQJE   VARCHAR2(255),
  RQFY   VARCHAR2(255),
  QTFY   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C11_13360000_Q to KRCS;

prompt Creating SC_C12_13360000...

create table SC_C12_13360000
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  RQKCDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  CJJG     VARCHAR2(255),
  KCSL     VARCHAR2(255),
  CHSL     VARCHAR2(255),
  RQSL     VARCHAR2(255),
  RQFY     VARCHAR2(255),
  YQFX     VARCHAR2(255),
  WJXF     VARCHAR2(255),
  YJWFXF   VARCHAR2(255),
  YJYFXF   VARCHAR2(255),
  LJBZ     VARCHAR2(255),
  CDDQR    VARCHAR2(255),
  BZZ      VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C12_13360000 to KRCS;

prompt Creating SC_C12_13360000_Q...

create table SC_C12_13360000_Q
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  FSSJ     VARCHAR2(255),
  RQKCDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  CJJG     VARCHAR2(255),
  KCSL     VARCHAR2(255),
  CHSL     VARCHAR2(255),
  RQSL     VARCHAR2(255),
  RQFY     VARCHAR2(255),
  YQFX     VARCHAR2(255),
  WJXF     VARCHAR2(255),
  YJWFXF   VARCHAR2(255),
  YJYFXF   VARCHAR2(255),
  LJBZ     VARCHAR2(255),
  CDDQR    VARCHAR2(255),
  BZZ      VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C12_13360000_Q to KRCS;

prompt Creating SC_C13_13360000_Q...

create table SC_C13_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  FSRQ   VARCHAR2(255),
  FSSJ   VARCHAR2(255),
  KCDJHM VARCHAR2(255),
  CHLSH  VARCHAR2(255),
  CHLX   VARCHAR2(255),
  QZPCBZ VARCHAR2(255),
  ZY     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  CHSL   VARCHAR2(255),
  CHJE   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C13_13360000_Q to KRCS;

prompt Creating SC_C13_13360000_Z...

create table SC_C13_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  FSRQ   VARCHAR2(255),
  FSSJ   VARCHAR2(255),
  KCDJHM VARCHAR2(255),
  CHLSH  VARCHAR2(255),
  CHLX   VARCHAR2(255),
  QZPCBZ VARCHAR2(255),
  ZY     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  CHSL   VARCHAR2(255),
  CHJE   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C13_13360000_Z to KRCS;

prompt Creating SC_C14_13360000_Q...

create table SC_C14_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  YQSL   VARCHAR2(255),
  YQHG   VARCHAR2(255),
  YQHL   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C14_13360000_Q to KRCS;

prompt Creating SC_C14_13360000_Z...

create table SC_C14_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  YQSL   VARCHAR2(255),
  YQHG   VARCHAR2(255),
  YQHL   VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C14_13360000_Z to KRCS;

prompt Creating SC_C15_13360000_Q...

create table SC_C15_13360000_Q
(
  ZQGSDM  VARCHAR2(255),
  KHDM    VARCHAR2(255),
  BZ      VARCHAR2(255),
  ZJYE    VARCHAR2(255),
  WDZZJ   VARCHAR2(255),
  ZQSZ    VARCHAR2(255),
  RZFZ    VARCHAR2(255),
  RQFZ    VARCHAR2(255),
  WCDBBL  VARCHAR2(255),
  BZJKYYE VARCHAR2(255),
  SJRQ    VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C15_13360000_Q to KRCS;

prompt Creating SC_C16_13360000_Q...

create table SC_C16_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  JYXW   VARCHAR2(255),
  FSRQ   VARCHAR2(255),
  FSSJ   VARCHAR2(255),
  WTLX   VARCHAR2(255),
  MMLB   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  ZQJC   VARCHAR2(255),
  CJJG   VARCHAR2(255),
  CJSL   VARCHAR2(255),
  CJJE   VARCHAR2(255),
  JYYJ   VARCHAR2(255),
  YHS    VARCHAR2(255),
  GHF    VARCHAR2(255),
  QTFY   VARCHAR2(255),
  WTLSH  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C16_13360000_Q to KRCS;

prompt Creating SC_C16_13360000_Z...

create table SC_C16_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  ZHDM   VARCHAR2(255),
  BZ     VARCHAR2(255),
  SCDM   VARCHAR2(255),
  JYXW   VARCHAR2(255),
  FSRQ   VARCHAR2(255),
  FSSJ   VARCHAR2(255),
  WTLX   VARCHAR2(255),
  MMLB   VARCHAR2(255),
  ZQDM   VARCHAR2(255),
  ZQJC   VARCHAR2(255),
  CJJG   VARCHAR2(255),
  CJSL   VARCHAR2(255),
  CJJE   VARCHAR2(255),
  JYYJ   VARCHAR2(255),
  YHS    VARCHAR2(255),
  GHF    VARCHAR2(255),
  QTFY   VARCHAR2(255),
  WTLSH  VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C16_13360000_Z to KRCS;

prompt Creating SC_C17_13360000...

create table SC_C17_13360000
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  KCDJHM   VARCHAR2(255),
  QYRQDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  RQSL     VARCHAR2(255),
  BCLX     VARCHAR2(255),
  BCJE     VARCHAR2(255),
  BCSL     VARCHAR2(255),
  CHJE     VARCHAR2(255),
  CHSL     VARCHAR2(255),
  QYLSH    VARCHAR2(255),
  QYRZDJHM VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C17_13360000 to KRCS;

prompt Creating SC_C17_13360000_Q...

create table SC_C17_13360000_Q
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  KCDJHM   VARCHAR2(255),
  QYRQDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  RQSL     VARCHAR2(255),
  BCLX     VARCHAR2(255),
  BCJE     VARCHAR2(255),
  BCSL     VARCHAR2(255),
  CHJE     VARCHAR2(255),
  CHSL     VARCHAR2(255),
  QYLSH    VARCHAR2(255),
  QYRZDJHM VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C17_13360000_Q to KRCS;

prompt Creating SC_C17_13360000_Z...

create table SC_C17_13360000_Z
(
  ZQGSDM   VARCHAR2(255),
  KHDM     VARCHAR2(255),
  ZHDM     VARCHAR2(255),
  BZ       VARCHAR2(255),
  FSRQ     VARCHAR2(255),
  KCDJHM   VARCHAR2(255),
  QYRQDJHM VARCHAR2(255),
  SCDM     VARCHAR2(255),
  ZQDM     VARCHAR2(255),
  RQSL     VARCHAR2(255),
  BCLX     VARCHAR2(255),
  BCJE     VARCHAR2(255),
  BCSL     VARCHAR2(255),
  CHJE     VARCHAR2(255),
  CHSL     VARCHAR2(255),
  QYLSH    VARCHAR2(255),
  QYRZDJHM VARCHAR2(255),
  SJRQ     VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C17_13360000_Z to KRCS;

prompt Creating SC_C18_13360000_Q...

create table SC_C18_13360000_Q
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  SCDM   VARCHAR2(255),
  GZLB   VARCHAR2(255),
  BZ     VARCHAR2(255),
  WYJE   VARCHAR2(255),
  GZBZ   VARCHAR2(255),
  BZZ    VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C18_13360000_Q to KRCS;

prompt Creating SC_C18_13360000_Z...

create table SC_C18_13360000_Z
(
  ZQGSDM VARCHAR2(255),
  KHDM   VARCHAR2(255),
  SCDM   VARCHAR2(255),
  GZLB   VARCHAR2(255),
  BZ     VARCHAR2(255),
  WYJE   VARCHAR2(255),
  GZBZ   VARCHAR2(255),
  BZZ    VARCHAR2(255),
  SJRQ   VARCHAR2(255)
)
tablespace RZRQ
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on SC_C18_13360000_Z to KRCS;
set feedback on
set define on
prompt Done.
