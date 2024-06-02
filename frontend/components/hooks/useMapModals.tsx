import { Aeropuerto, Envio, Vuelo } from "@/lib/types";
import { useState } from "react";

export type MapModalAttributes = {
	isAirportModalOpen: boolean;
	isFlightModalOpen: boolean;
	isEnvioModalOpen: boolean;
	setIsAirportModalOpen: (isOpen: boolean) => void;
	setIsFlightModalOpen: (isOpen: boolean) => void;
	setIsEnvioModalOpen: (isOpen: boolean) => void;
	currentAirportModal: Aeropuerto | null;
	currentFlightModal: Vuelo | null;
	currentEnvioModal: Envio | null;
	setCurrentAirportModal: (airport: Aeropuerto | null) => void;
	setCurrentFlightModal: (flight: Vuelo | null) => void;
	setCurrentEnvioModal: (envio: Envio | null) => void;
    openFlightModal: (flight: Vuelo) => void;
    openAirportModal: (airport: Aeropuerto) => void;
	openEnvioModal: (envio: Envio) => void;
};

const useMapModals = (): MapModalAttributes => {
	const [isAirportModalOpen, setIsAirportModalOpen] = useState<boolean>(false);
	const [isFlightModalOpen, setIsFlightModalOpen] = useState<boolean>(false);
	const [isEnvioModalOpen, setIsEnvioModalOpen] = useState<boolean>(false);
	const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | null>(null);
	const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | null>(null);
	const [currentEnvioModal, setCurrentEnvioModal] = useState<Envio | null>(null);

    const openFlightModal = (flight: Vuelo) => {
		setCurrentAirportModal(null);
		setCurrentEnvioModal(null);

        setIsFlightModalOpen(true)
        setCurrentFlightModal(flight);
    }

    const openAirportModal = (airport: Aeropuerto) => {
		setCurrentFlightModal(null);
		setCurrentEnvioModal(null);
		
        setIsAirportModalOpen(true)
        setCurrentAirportModal(airport);
    }

	const openEnvioModal = (envio: Envio) => {
		setCurrentFlightModal(null);
		setCurrentAirportModal(null);

		setIsEnvioModalOpen(true);
		setCurrentEnvioModal(envio);
	}

	return {
		isAirportModalOpen,
		isFlightModalOpen,
		isEnvioModalOpen,
		setIsAirportModalOpen,
		setIsFlightModalOpen,
		setIsEnvioModalOpen,
		currentAirportModal,
		currentFlightModal,
		currentEnvioModal,
		setCurrentAirportModal,
		setCurrentFlightModal,
		setCurrentEnvioModal,
        openFlightModal,
        openAirportModal,
		openEnvioModal
	};
};

export default useMapModals;
