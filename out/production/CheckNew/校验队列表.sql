-- Create table
create table ZQGS_CHECK_STATUS
(
  zqgsdm         VARCHAR2(8) not null,
  checkstatus    NUMBER(1),
  checkprocessor VARCHAR2(20),
  updatetime     DATE
)
tablespace KRCS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 16K
    next 8K
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate primary, unique and foreign key constraints 
alter table ZQGS_CHECK_STATUS
  add constraint PK_CHECK_STATUS primary key (ZQGSDM)
  using index 
  tablespace KRCS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
