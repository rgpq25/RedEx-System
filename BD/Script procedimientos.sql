DROP PROCEDURE IF EXISTS InsertarVueloYRuta;
DROP PROCEDURE IF EXISTS InsertarUbicacionYAlmacen;
DROP PROCEDURE IF EXISTS InsertarPaqueteYEnvio;
DROP TRIGGER IF EXISTS NuevoPaquete;

DELIMITER #

CREATE PROCEDURE InsertarUbicacionYAlmacen(
    IN _ID_Ubicacion VARCHAR(5),
    IN _Continente VARCHAR(50),
    IN _Pais VARCHAR(50),
    IN _Ciudad VARCHAR(50),
    IN _Ciudad_abreviada VARCHAR(10),
    IN _Zona_Horaria VARCHAR(5),
    IN _Capacidad_Maxima INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
        @sqlstate = RETURNED_SQLSTATE, @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
        SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
        SELECT @full_error AS error_message;
        ROLLBACK;
    END;

    START TRANSACTION;

    -- Inserta en la tabla Ubicacion
    IF NOT EXISTS (SELECT * FROM Ubicacion WHERE ID_Ubicacion = _ID_Ubicacion) THEN
        INSERT INTO Ubicacion (ID_Ubicacion, Continente, Pais, Ciudad, Coordenadas, Ciudad_abreviada, Zona_Horaria)
        VALUES (_ID_Ubicacion, _Continente, _Pais, _Ciudad, NULL, _Ciudad_abreviada, _Zona_Horaria);
    END IF;

    -- Inserta en la tabla Almacenes
    INSERT INTO Aeropuerto (ID_Ubicacion, Capacidad_Utilizada, Capacidad_Maxima)
    VALUES (_ID_Ubicacion, 0, _Capacidad_Maxima);

    COMMIT;
END#


CREATE PROCEDURE InsertarVueloYRuta(
    IN _origen VARCHAR(5),
    IN _destino VARCHAR(5),
    IN _hora_origen TIME,
    IN _hora_destino TIME,
    IN _capacidad_paquetes INT
)
BEGIN
    DECLARE _id_ruta INT;
    DECLARE _tipo ENUM('Continental', 'Intercontinental');
    DECLARE _continente_origen VARCHAR(50);
    DECLARE _continente_destino VARCHAR(50);

    -- Obtener el continente de la ubicación de origen
    SELECT Continente INTO _continente_origen
    FROM Ubicacion
    WHERE ID_Ubicacion = _origen;

    -- Obtener el continente de la ubicación de destino
    SELECT Continente INTO _continente_destino
    FROM Ubicacion
    WHERE ID_Ubicacion = _destino;

    -- Determinar si el vuelo es Continental o Intercontinental
    IF _continente_origen = _continente_destino THEN
        SET _tipo = 'Continental';
    ELSE
        SET _tipo = 'Intercontinental';
    END IF;
    
    -- Inserta en la tabla Vuelos con el ID de la ruta
    INSERT INTO Plan_Vuelo (ID_Ubicacion_Origen, ID_Ubicacion_Destino, Hora_Ciudad_Origen, Hora_Ciudad_Destino, Capacidad_Maxima)
    VALUES (_origen, _destino, _hora_origen, _hora_destino, _capacidad_paquetes);
    
END#

CREATE PROCEDURE InsertarPaqueteYEnvio(
    IN _ID_Paquete VARCHAR(255),
    IN _ID_Ubicacion_Origen VARCHAR(5),
    IN _ID_Ubicacion_Destino VARCHAR(5),
    IN _Cantidad_unidades INT,
    IN _Fecha_Recepcion DATETIME
)
BEGIN
	DECLARE _ID_Ruta INT;
    DECLARE _Coordenadas POINT;
    DECLARE _ID_Aeropuerto INT;
    DECLARE _ID_Envio INT;
    DECLARE i INT DEFAULT 0;
    
    SELECT Coordenadas INTO _Coordenadas FROM Ubicacion WHERE ID_Ubicacion = _ID_Ubicacion_Origen;
    SELECT ID_Aeropuerto INTO _ID_Aeropuerto FROM Aeropuerto WHERE ID_Ubicacion = _ID_Ubicacion_Origen;
    
    
    INSERT INTO Envio (Codigo_paquete,ID_Ubicacion_Origen, ID_Ubicacion_Destino, Fecha_Recepcion, Estado, Cantidad_Paquetes)
    VALUES (_ID_Paquete, _ID_Ubicacion_Origen, _ID_Ubicacion_Destino, NOW(), 'En almacen origen', _Cantidad_unidades);
    
	SET _ID_Envio = @@last_insert_id;
    
    WHILE i < _Cantidad_unidades DO
        INSERT INTO Paquete (Coordenada_Actual,ID_Almacen,En_Almacen,Entregado,ID_Envio) VALUES (_Coordenadas,_ID_Aeropuerto , 1, 0, _ID_Envio);
        SET i = i + 1;
    END WHILE;
END#

DELIMITER ;

