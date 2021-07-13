-- 商品地址统计
drop table if exists homework.addresstype;
create table homework.addresstype
(
  int_date  VARCHAR(6),
  address_name VARCHAR(4),
  splb      VARCHAR(34),
  xse      VARCHAR(30)
);
comment on table homework.addresstype
  is '地址销售统计';
comment on column homework.addresstype.int_date
  is '月';
comment on column homework.addresstype.address_name
  is '地址名称';
comment on column homework.addresstype.splb
  is '商品类别';
comment on column homework.addresstype.xse
  is '销售数量';
-- 性别商品统计
drop table if exists homework.gendertype;
create table homework.gendertype
(
  int_date  VARCHAR(6),
  gender VARCHAR(4),
  splb      VARCHAR(34),
  xse      VARCHAR(30)
);
comment on table homework.gendertype
  is '性别商品统计';
comment on column homework.gendertype.int_date
  is '月';
comment on column homework.gendertype.gender
  is '性别';
comment on column homework.gendertype.splb
  is '商品种类';
comment on column homework.gendertype.xse
  is '销售数量';
	-- 收件地址统计
drop table if exists homework.address;
create table homework.address
(
  int_date  VARCHAR(6),
  address_name VARCHAR(4),
  xse      VARCHAR(30)
);
comment on table homework.address
  is '地址统计';
comment on column homework.address.int_date
  is '月';
comment on column homework.address.address_name
  is '地址名称';
comment on column homework.address.xse
  is '销售数量';
	-- 月度统计
drop table if exists homework.date;
create table homework.date
(
  int_date  VARCHAR(6),
	zcrs VARCHAR(20),
  xse      VARCHAR(30)
);
comment on table homework.date
  is '月度统计';
comment on column homework.date.zcrs
  is '注册数量';
comment on column homework.date.xse
  is '销售数量';
	