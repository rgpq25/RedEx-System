DROP PROCEDURE IF EXISTS InsertarVueloYRuta;
DROP PROCEDURE IF EXISTS InsertarUbicacionYAlmacen;
DROP PROCEDURE IF EXISTS InsertarPaqueteYEnvio;
DROP TRIGGER IF EXISTS NuevoPaquete;

DELIMITER #

CREATE PROCEDURE InsertarVueloYRuta(
    IN _origen VARCHAR(5),
    IN _destino VARCHAR(5),
    IN _fecha_origen DATETIME,
    IN _fecha_destino DATETIME,
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

    -- Verifica si la ruta existe, si no, la inserta
    IF NOT EXISTS (SELECT * FROM Rutas WHERE ID_Ubicacion_Origen = _origen AND ID_Ubicacion_Destino = _destino) THEN
        INSERT INTO Rutas (ID_Ubicacion_Origen, ID_Ubicacion_Destino, Tipo, Distancia)
        VALUES (_origen, _destino, _tipo, 0);  -- Considera actualizar la distancia con un valor real en lugar de 0
    END IF;

    -- Obtener el ID de la ruta
    SELECT ID_Ruta INTO _id_ruta FROM Rutas
    WHERE ID_Ubicacion_Origen = _origen AND ID_Ubicacion_Destino = _destino;

    -- Inserta en la tabla Vuelos con el ID de la ruta
    INSERT INTO Vuelos (ID_Ruta, Fecha_Origen, Fecha_Destino, Capacidad_Paquetes)
    VALUES (_id_ruta, _fecha_origen, _fecha_destino, _capacidad_paquetes);
    
END#

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
    -- Inserta en la tabla Ubicacion
    IF NOT EXISTS (SELECT * FROM Ubicacion WHERE ID_Ubicacion = _ID_Ubicacion) THEN
        INSERT INTO Ubicacion (ID_Ubicacion, Continente, Pais, Ciudad, Ciudad_abreviada, Zona_Horaria)
		VALUES (_ID_Ubicacion, _Continente, _Pais, _Ciudad, _Ciudad_abreviada, _Zona_Horaria);
    END IF;
    

    -- Inserta en la tabla Almacenes, suponiendo que la ubicación y el almacén son lo mismo
    INSERT INTO Aeropuertos (ID_Ubicacion, Coordenadas, Capacidad_Utilizada, Capacidad_Maxima)
    VALUES (_ID_Ubicacion, NULL, 0, _Capacidad_Maxima);
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

    -- Intenta encontrar una ruta existente que coincida con las ubicaciones de origen y destino
    SELECT ID_Ruta INTO _ID_Ruta
    FROM Rutas
    WHERE ID_Ubicacion_Origen = _ID_Ubicacion_Origen AND ID_Ubicacion_Destino = _ID_Ubicacion_Destino
    LIMIT 1;
    
    -- Inserta en la tabla Paquetes
    INSERT INTO Paquetes (Identificador_Envio, ID_Ubicacion_Origen, ID_Ubicacion_Destino, Codigo_Seguridad, Cantidad_unidades)
    VALUES (_ID_Paquete, _ID_Ubicacion_Origen, _ID_Ubicacion_Destino, "-", _Cantidad_unidades);

    -- Inserta en la tabla Envios
    -- Asumimos que el ID_Ruta se obtiene o se calcula de alguna manera. Aquí se inserta como NULL.
    INSERT INTO Envios (ID_Paquete, ID_Ubicacion_Origen, ID_Ubicacion_Destino, Fecha_Recepcion, Fecha_Limite_Entrega, Estado)
    VALUES (_ID_Paquete, _ID_Ubicacion_Origen, _ID_Ubicacion_Destino, Fecha_Recepcion, NOW(), 'En tránsito');
END#

DELIMITER ;

