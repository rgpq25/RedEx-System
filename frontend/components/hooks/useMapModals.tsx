import { Aeropuerto, Vuelo } from "@/lib/types";
import { useState } from "react";

export type MapModalAttributes = {
	isAirportModalOpen: boolean;
	isFlightModalOpen: boolean;
	setIsAirportModalOpen: (isOpen: boolean) => void;
	setIsFlightModalOpen: (isOpen: boolean) => void;
	currentAirportModal: Aeropuerto | null;
	currentFlightModal: Vuelo | null;
	setCurrentAirportModal: (airport: Aeropuerto | null) => void;
	setCurrentFlightModal: (flight: Vuelo | null) => void;
    openFlightModal: (flight: Vuelo) => void;
    openAirportModal: (airport: Aeropuerto) => void;
};

const useMapModals = (): MapModalAttributes => {
	const [isAirportModalOpen, setIsAirportModalOpen] = useState<boolean>(false);
	const [isFlightModalOpen, setIsFlightModalOpen] = useState<boolean>(false);
	const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | null>(null);
	const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | null>(null);

    const openFlightModal = (flight: Vuelo) => {
		setIsAirportModalOpen(false);
		setCurrentAirportModal(null);

        setIsFlightModalOpen(true)
        setCurrentFlightModal(flight);
    }

    const openAirportModal = (airport: Aeropuerto) => {
		setIsFlightModalOpen(false);
		setCurrentFlightModal(null);
		
        setIsAirportModalOpen(true)
        setCurrentAirportModal(airport);
    }

	return {
		isAirportModalOpen,
		isFlightModalOpen,
		setIsAirportModalOpen,
		setIsFlightModalOpen,
		currentAirportModal,
		currentFlightModal,
		setCurrentAirportModal,
		setCurrentFlightModal,
        openFlightModal,
        openAirportModal,
	};
};

export default useMapModals;
