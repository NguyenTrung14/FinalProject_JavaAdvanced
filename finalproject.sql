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
    flash_sale_id int null,
    flash_sale_quantity int not null default 0,
    normal_quantity int not null default 0,
    constraint fk_order_details_flash_sale
		foreign key (flash_sale_id) references flash_sales(flash_sale_id),
    constraint fk_order_details_orders
        foreign key (order_id) references orders(order_id),
    constraint fk_order_details_products
        foreign key (product_id) references products(product_id)
) engine=InnoDB;

create table if not exists carts (
    cart_id int auto_increment primary key,
    user_id int not null unique,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (user_id) references users(user_id)
) engine=InnoDB;

create table if not exists cart_items (
    cart_item_id int auto_increment primary key,
    cart_id int not null,
    product_id int not null,
    quantity int not null,
    reserved_flash_quantity int not null default 0,
    reserved_normal_quantity int not null default 0,
    flash_unit_price double not null default 0,
    normal_unit_price double not null,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    unique key uk_cart_product (cart_id, product_id),
    foreign key (cart_id) references carts(cart_id) on delete cascade,
    foreign key (product_id) references products(product_id)
) engine=InnoDB;

update users set password = "$2a$12$vmL7dRDE2xK0kphG.uDdIO0IMPqx5dDRUhPTSh9usB9Ne6HCnk.82" where user_id = 1;
insert into users(full_name, email, phone, password, address, role, status)
values (
    'Nguyen Trung',
    'nguyentrung27092006@gmail.com',
    '0968038690',
    '$2a$12$4YS8Fl3VZAJwmw8UQqTWBODg1Dn0W5z.PECMavJ/XKByO/nzSUDAO',
    'Ha Noi',
    'ADMIN',
    'ACTIVE'
);
select * from users;
insert into categories (category_name, description, status)
values
('Apple', 'Hang dien thoai Apple', 'ACTIVE'),
('Samsung', 'Hang dien thoai Samsung', 'ACTIVE'),
('Xiaomi', 'Hang dien thoai Xiaomi', 'ACTIVE');

insert into products (product_name, storage, color, price, stock, description, category_id, status)
values
('iPhone 15', '128GB', 'Black', 20000000, 10, 'Dien thoai iPhone 15', 1, 'ACTIVE'),
('Samsung S23', '256GB', 'White', 18000000, 15, 'Dien thoai Samsung S23', 2, 'ACTIVE'),
('Xiaomi 14', '256GB', 'Blue', 15000000, 8, 'Dien thoai Xiaomi 14', 3, 'ACTIVE');


delimiter $$

drop procedure if exists cancel_order $$
create procedure cancel_order(in p_order_id int)
begin
    declare v_done int default 0;
    declare v_product_id int;
    declare v_quantity int;
    declare v_current_status varchar(20);

    declare cur cursor for
        select product_id, quantity
        from order_details
        where order_id = p_order_id;

    declare continue handler for not found set v_done = 1;

    start transaction;

    select status into v_current_status
    from orders
    where order_id = p_order_id
    for update;

    if v_current_status is null then
        rollback;
        signal sqlstate '45000'
        set message_text = 'order khong ton tai';
    end if;

    if v_current_status = 'CANCELLED' then
        rollback;
        signal sqlstate '45000'
        set message_text = 'don hang da bi huy truoc do';
    end if;

    if v_current_status = 'DELIVERED' then
        rollback;
        signal sqlstate '45000'
        set message_text = 'khong the huy don da giao';
    end if;

    open cur;

    read_loop: loop
        fetch cur into v_product_id, v_quantity;
        if v_done = 1 then
            leave read_loop;
        end if;

        update products
        set stock = stock + v_quantity
        where product_id = v_product_id;
    end loop;

    close cur;

    update orders
    set status = 'CANCELLED'
    where order_id = p_order_id;

    commit;
end $$

delimiter ;

