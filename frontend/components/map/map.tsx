import { FunctionComponent, useEffect, useState } from "react";
import { MapModalAttributes } from "../hooks/useMapModals";
import { MapZoomAttributes } from "../hooks/useMapZoom";
import { Aeropuerto, EstadoAlmacen, Simulacion, Vuelo } from "@/lib/types";
import { MapProps } from "./client-map";

// this is a "barrel file" that prevents the ClientMap from ever getting loaded in the server.
export const Map: FunctionComponent<MapProps> = (props) => {
	const [Client, setClient] = useState<FunctionComponent<MapProps>>();

	useEffect(() => {
		(async () => {
			if (typeof global.window !== "undefined") {
				const newClient = (await import("./client-map")).default as FunctionComponent<MapProps>;
				setClient(() => newClient);
			}
		})();
	}, []);

	if (typeof global.window === "undefined" || !Client) {
		return null;
	}

	return <Client {...props} />;
};
