create database shopapp;
use shopapp
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL UNIQUE,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active TINYINT(1) DEFAULT 1 NOT NULL,
    date_of_birth DATE,
    facebook_account_id int default 0,
    google_account_id int default 0
);
create table tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expriration_date DATETIME,--so ngay cho phep
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id int, --FK
    FOREIGN KEY  (user_id) REFERENCES users(id)
);
-- hỗ trợ đăng nhập fb và gg
create table social_accounts(
    id int PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL comment 'Tôi là google',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL comment 'Email tài khoản',
    name VARCHAR(100) NOT NULL comment 'Tên người dùng',
    user_id int,
    FOREIGN KEY  (user_id) REFERENCES users(id)

);
-- Danh mục sản phẩm (Category)
create table cetegories(
    id int PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL DEFAULT '' comment 'Ten danh muc'
);
--Bảng chứ sản phẩm
create table products(
    id int PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(350) comment 'Tên sản phẩm',
    price FLOAT NOT NULL CHECK(price >= 0),
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT DEFAULT '', -- mô tả sản phẩm
    create_at DATETIME,
    update_at DATETIME,
    category_id int,
    FOREIGN KEY (category_id) REFERENCES cetegories(id),
);
-- Bảng ảnh sp
CREATE TABLE product_images (
    id int PRIMARY KEY AUTO_INCREMENT,
    product_id int,
    FOREIGN KEY (product_id) REFERENCES products(id),
    constraint fk_product_images_product_id
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE -- nếu id ảnh bị xóa thì id sp cũng bị xóa
);
ALTER TABLE product_images add column image_url VARCHAR(300)
ALTER TABLE users add column role_id INT ;
create table roles(
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
ALTER TABLE users Add FOREIGN KEY (role_id) REFERENCES roles (id);

-- Đặt hàng
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT, -- tự động tăng lên 1
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    fullname VARCHAR(100) DEFAULT'',
    email VARCHAR(100) DEFAULT'',
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT'',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- lấy ngày hiện tại
    total_money FLOAT CHECK(total_money >=0)

);
ALTER TABLE orders ADD column shipping_method VARCHAR(100);
ALTER TABLE orders ADD column shipping_address VARCHAR(200);
ALTER TABLE orders ADD column shipping_date DATE;
ALTER TABLE orders ADD column tracking_number VARCHAR(100);
ALTER TABLE orders ADD column paying_method VARCHAR(100);
ALTER TABLE orders Add column status VARCHAR(20);
-- xóa 1 đơn hàng => xóa mềm =>active
ALTER TABLE orders ADD column active TINYINT(1);
-- Trạng thái đơn hàng chỉ đc phép nhận 1 số giá trị
ALTER TABLE orders
MODIFY column status ENUM('pending','processing','shipped','delivered','cancelled')
COMMENT 'Trạng thái đơn hàng';

CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products (id),
    price FLOAT CHECK (price >=0),
    number_of_product INT CHECK (number_of_product >0),
    total_money FLOAT CHECK (total_money >=0),
    color VARCHAR(20) DEFAULT''
);




