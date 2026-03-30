create database if not exists smartphone_store_db;
use smartphone_store_db;

create table if not exists users (
    user_id int auto_increment primary key,
    full_name varchar(100) not null,
    email varchar(100) not null unique,
    phone varchar(20) not null unique,
    password varchar(255) not null,
    address varchar(255),
    role enum('ADMIN', 'CUSTOMER') not null default 'CUSTOMER',
    status enum('ACTIVE', 'INACTIVE') not null default 'ACTIVE',
    created_at datetime default current_timestamp
) engine=InnoDB;

create table if not exists categories (
    category_id int auto_increment primary key,
    category_name varchar(100) not null unique,
    description varchar(255),
    status enum('ACTIVE', 'DELETED') not null default 'ACTIVE'
) engine=InnoDB;

create table if not exists products (
    product_id int auto_increment primary key,
    product_name varchar(150) not null,
    storage varchar(50),
    color varchar(50),
    price double not null check (price > 0),
    stock int not null check (stock >= 0),
    description varchar(255),
    category_id int not null,
    status enum('ACTIVE', 'DELETED') not null default 'ACTIVE',
    created_at datetime default current_timestamp,
    constraint fk_products_categories
        foreign key (category_id) references categories(category_id)
) engine=InnoDB;

create table if not exists flash_sales (
    flash_sale_id int auto_increment primary key,
    product_id int not null,
    discount_percent double not null check (discount_percent > 0 and discount_percent <= 100),
    start_time datetime not null,
    end_time datetime not null,
    max_quantity int not null check (max_quantity > 0),
    sold_quantity int not null default 0,
    status enum('ACTIVE','INACTIVE') not null default 'ACTIVE',
    foreign key (product_id) references products(product_id)
) engine=InnoDB;

create table if not exists coupons (
    coupon_id int auto_increment primary key,
    code varchar(50) not null unique,
    discount_percent double not null check (discount_percent > 0 and discount_percent <= 100),
    start_time datetime not null,
    end_time datetime not null,
    quantity int not null check (quantity >= 0),
    used_count int not null default 0,
    min_order_amount double not null default 0,
    status enum('ACTIVE','INACTIVE') not null default 'ACTIVE'
) engine=InnoDB;

create table if not exists orders (
    order_id int auto_increment primary key,
    user_id int not null,
    total_amount double not null default 0 check (total_amount >= 0),
    status enum('PENDING', 'SHIPPING', 'DELIVERED', 'CANCELLED') not null default 'PENDING',
    created_at datetime default current_timestamp,
    coupon_code varchar(50) null,
	discount_amount double not null default 0,
    constraint fk_orders_users
        foreign key (user_id) references users(user_id)
) engine=InnoDB;

create table if not exists order_details (
    order_detail_id int auto_increment primary key,
    order_id int not null,
    product_id int not null,
    quantity int not null check (quantity > 0),
    price double not null check (price > 0),
    constraint fk_order_details_orders
        foreign key (order_id) references orders(order_id),
    constraint fk_order_details_products
        foreign key (product_id) references products(product_id)
) engine=InnoDB;


-- insert into users(full_name, email, phone, password, address, role, status)
-- values (
--     'Nguyen Trung',
--     'nguyentrung27092006@gmail.com',
--     '0968038690',
--     '$2a$10$7Qz0nGqvRzXz5i4kT7yV3e8YbqK9Yh9l3oV2p1XzN4mQ6s8d9e0fG',
--     'Ha Noi',
--     'ADMIN',
--     'ACTIVE'
-- );
select * from users;
-- insert into categories (category_name, description, status)
-- values
-- ('Apple', 'Hang dien thoai Apple', 'ACTIVE'),
-- ('Samsung', 'Hang dien thoai Samsung', 'ACTIVE'),
-- ('Xiaomi', 'Hang dien thoai Xiaomi', 'ACTIVE');

-- insert into products (product_name, storage, color, price, stock, description, category_id, status)
-- values
-- ('iPhone 15', '128GB', 'Black', 20000000, 10, 'Dien thoai iPhone 15', 1, 'ACTIVE'),
-- ('Samsung S23', '256GB', 'White', 18000000, 15, 'Dien thoai Samsung S23', 2, 'ACTIVE'),
-- ('Xiaomi 14', '256GB', 'Blue', 15000000, 8, 'Dien thoai Xiaomi 14', 3, 'ACTIVE');

