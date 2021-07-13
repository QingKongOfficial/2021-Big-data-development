-- drop schema if exists homework CASCADE;
-- create schema homework;
-- 地市
drop table if exists homework.address;
create table homework.address
(
  address_code VARCHAR(4) PRIMARY KEY,
  address_name VARCHAR(4)
);
-- Add comments to the table 
comment on table homework.address
  is '区域';
-- Add comments to the columns 
comment on column homework.address.address_code
  is '地址号码';
comment on column homework.address.address_name
  is '地址名称';
-- users
drop table if exists homework.users;
create table homework.users
(
  user_id VARCHAR(4) PRIMARY KEY,
  user_name VARCHAR(20),
  user_gender VARCHAR(6), 
  register_date VARCHAR(6),
  user_years VARCHAR(6),
  email VARCHAR(100),
  phone VARCHAR(100)
);
comment on table homework.users
  is '用户';
comment on column homework.users.user_id
  is '用户号码';
comment on column homework.users.user_name
  is '用户名称';
comment on column homework.users.user_gender
  is '性别';
comment on column homework.users.register_date
  is '注册月份';
 comment on column homework.users.user_years
  is '出生年份';
 comment on column homework.users.email
  is '用户邮箱';
 comment on column homework.users.phone
  is '手机号码';
 -- 商户
drop table if exists homework.shop;
create table homework.shop
(
  shop_id VARCHAR(4) PRIMARY KEY,
  shop_name VARCHAR(20)
);
comment on table homework.shop
  is '店铺';
comment on column homework.shop.shop_id
  is '店铺号码';
comment on column homework.shop.shop_name
  is '店铺名称';
  -- 商品
drop table if exists homework.goods;
create table homework.goods
(
  good_id VARCHAR(4) PRIMARY KEY,
  good_name VARCHAR(20),
  good_type_id VARCHAR(4),
  good_address VARCHAR(100),
  price VARCHAR(20),
  shop_id VARCHAR(4),
  num VARCHAR(420)
);
comment on table homework.goods
  is '商品';
comment on column homework.goods.good_id
  is '商品号码';
comment on column homework.goods.good_name
  is '商品名称';
 comment on column homework.goods.good_type_id
  is '商品类别号码';
 comment on column homework.goods.num
  is '商品数量';
 comment on column homework.goods.good_address
  is '发货地址';
 comment on column homework.goods.shop_id
  is '商品所在店铺号码';
 comment on column homework.goods.price
  is '商品价格';
 -- 商品类别
 drop table if exists homework.goodtype;
create table homework.goodtype
(
  goodtype_id VARCHAR(4) PRIMARY KEY,
  goodtype_name VARCHAR(20)
);
comment on table homework.goodtype
  is '商品类别';
comment on column homework.goodtype.goodtype_id
  is '商品类别号码';
comment on column homework.goodtype.goodtype_name
  is '商品类别名称';
  -- 商品购买
 drop table if exists homework.purchase;
create table homework.purchase
(
  user_id VARCHAR(4) ,
  good_id VARCHAR(4),
  receive_address VARCHAR(20),
  num VARCHAR(20),
	price VARCHAR(20),
  purchase_date VARCHAR(6)
);
comment on table homework.purchase
  is '商品购买';
comment on column homework.purchase.user_id
  is '购买用户号码';
comment on column homework.purchase.good_id
  is '购买商品号码';
comment on column homework.purchase.receive_address
  is '收货城市';
 comment on column homework.purchase.num
  is '购买数量';
 comment on column homework.purchase.purchase_date
  is '购买月份';
