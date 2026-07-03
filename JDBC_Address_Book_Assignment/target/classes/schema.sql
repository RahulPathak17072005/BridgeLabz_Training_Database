CREATE DATABASE IF NOT EXISTS addressbook;
USE addressbook;
CREATE TABLE IF NOT EXISTS contacts (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(255)
    );