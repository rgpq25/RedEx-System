DROP TABLE IF EXISTS Notificaciones;
DROP TABLE IF EXISTS Trazabilidad_Paquete;
DROP TABLE IF EXISTS Estado_Paquete;
DROP TABLE IF EXISTS Paquete;
DROP TABLE IF EXISTS Envio;
DROP TABLE IF EXISTS Plan_Ruta;
DROP TABLE IF EXISTS Ruta;
DROP TABLE IF EXISTS Vuelo;
DROP TABLE IF EXISTS Plan_Vuelo;
DROP TABLE IF EXISTS Aeropuerto;
DROP TABLE IF EXISTS PersonalAutorizado;
DROP TABLE IF EXISTS Clientes;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Ubicacion;
DROP TABLE IF EXISTS Simulaciones;



CREATE TABLE Usuarios (
    ID_Usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(255),
    Correo VARCHAR(255),
    Tipo ENUM('Continental', 'Intercontinental')
);

CREATE TABLE Clientes (
    ID_Cliente INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Usuario INT,
    Informacion_Contacto TEXT,
    Preferencias_Notificacion TEXT,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

CREATE TABLE PersonalAutorizado (
    ID_PersonalAutorizado INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Usuario INT,
    Cargo VARCHAR(255),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

CREATE TABLE Ubicacion (
    ID_Ubicacion VARCHAR(5) PRIMARY KEY,
    Continente VARCHAR(255),
    Pais VARCHAR(255),
	Coordenadas POINT,
    Ciudad VARCHAR(255),
    Ciudad_abreviada VARCHAR(255),
    Zona_Horaria VARCHAR(255)
);

CREATE TABLE Aeropuerto (
    ID_Aeropuerto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion VARCHAR(5),
    Capacidad_Utilizada INT,
    Capacidad_Maxima INT,
    FOREIGN KEY (ID_Ubicacion) REFERENCES Ubicacion(ID_Ubicacion)
);


CREATE TABLE Plan_Vuelo (
    ID_Plan_Vuelo INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion_Origen VARCHAR(5),
    ID_Ubicacion_Destino VARCHAR(5),
    Hora_Ciudad_Origen VARCHAR(255),
    Hora_Ciudad_Destino VARCHAR(255),
    Capacidad_Maxima INT,
    FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion)
);

CREATE TABLE Vuelo (
    ID_Vuelo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Plan_Vuelo INT,
    Fecha_Origen DATETIME,
    Fecha_Destino DATETIME,
    Tiempo_Estimado FLOAT,
    Capacidad_Utilizada INT,
    FOREIGN KEY (ID_Plan_Vuelo) REFERENCES Plan_Vuelo(ID_Plan_Vuelo)
);

CREATE TABLE Ruta (
    ID_Ruta INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Vuelo INT,
    Orden_Secuencia INT,
    Activo TINYINT,
    Distancia FLOAT,
    Ruta_Empleada TINYINT,
    ID_Plan_Ruta INT,
    ID_Ubicacion_Origen VARCHAR(5),
    ID_Ubicacion_Destino VARCHAR(5),
    Fecha_Inicio DATETIME,
    Fecha_Fin DATETIME,
    FOREIGN KEY (ID_Vuelo) REFERENCES Vuelo(ID_Vuelo),
    FOREIGN KEY (ID_Plan_Ruta) REFERENCES Plan_Vuelo(ID_Plan_Vuelo),
    FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion)
);

CREATE TABLE Envio (
    ID_Envio INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion_Origen VARCHAR(5),
    ID_Ubicacion_Destino VARCHAR(5),
    Codigo_paquete VARCHAR(15),
    Fecha_Recepcion DATETIME,
    Tiempo_Entrega_Estimada TIME NULL,
    Fecha_Limite_Entrega DATETIME NULL,
    Estado VARCHAR(255),
    Cantidad_Paquetes INT,
    Codigo_Seguridad VARCHAR(255) NULL,
    ID_Emisor INT NULL,
    ID_Receptor INT NULL,
    FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Emisor) REFERENCES Clientes(ID_Cliente),
    FOREIGN KEY (ID_Receptor) REFERENCES Clientes(ID_Cliente)
);

CREATE TABLE Paquete (
    ID_Paquete INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Coordenada_Actual VARCHAR(255),
    ID_Almacen INT,
    En_Almacen TINYINT,
    Entregado TINYINT,
    ID_Envio INT,
    FOREIGN KEY (ID_Almacen) REFERENCES Aeropuerto(ID_Aeropuerto),
    FOREIGN KEY (ID_Envio) REFERENCES Envio(ID_Envio)
);

CREATE TABLE Estado_Paquete (
    ID_Estado INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(255),
    Variante VARCHAR(255)
);

CREATE TABLE Trazabilidad_Paquete (
    ID_Trazabilidad INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Activo TINYINT,
    ID_Paquete INT,
    ID_Estado INT,
    Fecha_Hora_Cambio DATETIME,
    Detalles TEXT,
    FOREIGN KEY (ID_Paquete) REFERENCES Paquete(ID_Paquete),
    FOREIGN KEY (ID_Estado) REFERENCES Estado_Paquete(ID_Estado)
);

CREATE TABLE Notificaciones (
    ID_Notificacion INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INT,
    Tipo_Notificacion VARCHAR(255),
    Mensaje TEXT,
    Fecha_Hora DATETIME,
    FOREIGN KEY (ID_Cliente) REFERENCES Clientes(ID_Cliente)
);

CREATE TABLE Simulaciones (
    ID_Simulacion INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Fecha_Inicio DATETIME,
    Fecha_Fin DATETIME,
    Estado VARCHAR(255),
    Resultados TEXT
);

