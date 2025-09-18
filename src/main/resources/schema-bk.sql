CREATE TABLE Image
(
    Id      INT PRIMARY KEY,
    Name    VARCHAR(100) NOT NULL UNIQUE,
    Content VARBINARY    NOT NULL
);

CREATE TABLE Products
(
    Id          INTEGER PRIMARY KEY,
    Name        VARCHAR(50)    NOT NULL UNIQUE,
    Description VARCHAR(100)   NOT NULL,
    Price       DECIMAL(10, 2) NOT NULL,
    Quantity    INTEGER        NOT NULL,
    DateAdded   DATETIME DEFAULT CURRENT_TIMESTAMP,
    ImageId     INTEGER  NOT NULL,
    FOREIGN KEY (ImageId) REFERENCES Image(Id)
);

CREATE TABLE cash
(
    Id           INTEGER PRIMARY KEY,
    Name         VARCHAR(50) NOT NULL,
    Quantity     INTEGER     NOT NULL,
    Denomination INTEGER     NOT NULL
);

CREATE TABLE sales
(
    Id          INTEGER PRIMARY KEY,
    DateAdded   DATETIME DEFAULT CURRENT_TIMESTAMP,
    Price       DECIMAL(10, 2) NOT NULL,
    ProductName VARCHAR(50)    NOT NULL,
    ProductId   INT            NOT NULL
);