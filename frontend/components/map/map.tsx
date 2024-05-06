"use client";

import { cn } from "@/lib/utils";
import React, { forwardRef, useEffect, useState } from "react";
import {
	ComposableMap,
	Geographies,
	Geography,
	Marker,
	Annotation,
	ZoomableGroup,
	Line,
	useMapContext,
} from "react-simple-maps";
import { Tooltip } from "react-tooltip";
import useAnimation from "../hooks/useAnimation";
import Airport from "./airport";
import Chip from "../ui/chip";
import { Flight } from "@/lib/types/flight";
import PlaneMarker from "./plane-marker";

//TODO: Download and store on local repository, currently depends on third party URL
const geoUrl = "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json";

const flights: Flight[] = [
	// {
	// 	rotation: 0,
	// 	originCoordinate: [-65, -34.6037],
	// 	destinationCoordinate: [26, 0],
	// 	originTime: new Date("2024-05-04T00:00:03"),
	// 	destinationTime: new Date("2024-05-05T22:17:00"),
	// 	capacity: 98,
	// },
	// {
	// 	rotation: 0,
	// 	originCoordinate: [-100, 40],
	// 	destinationCoordinate: [26, 50],
	// 	originTime: new Date("2024-05-04T00:00:03"),
	// 	destinationTime: new Date("2024-05-05T22:17:00"),
	// 	capacity: 98,
	// },
	{
		originCoordinate: [0, 0],
		destinationCoordinate: [50, 50],
		originTime: new Date(),
		destinationTime: new Date("2024-05-05T22:17:00"),
		capacity: 0,
	},
];

const airports = [
	{
		coordinates: [0, 0],
	},
	// {
	// 	coordinates: [26, 0],
	// },
	{
		coordinates: [50, 50],
	},
	// {
	// 	coordinates: [-100, 40],
	// },
	// {
	// 	coordinates: [5, 5],
	// },
	// {
	// 	coordinates: [10, 10],
	// },
	// {
	// 	coordinates: [15, 15],
	// },
	// {
	// 	coordinates: [20, 20],
	// },

	// {
	// 	coordinates: [25, 25],
	// },
	// {
	// 	coordinates: [30, 30],
	// },
	// {
	// 	coordinates: [35, 35],
	// },
	// {
	// 	coordinates: [40, 40],
	// },
	// {
	// 	coordinates: [45, 45],
	// },
];

//define props for coordinates and zoom
type Position = {
	coordinates: [number, number];
	zoom: number;
};

// const Line = ({
// 	coordinates = [
// 		[0, 0],
// 		[0, 0],
// 	],
// 	...restProps
// }) => {
// 	const { projection } = useMapContext();
// 	const [x1, y1] = projection(coordinates[0]);
// 	const [x2, y2] = projection(coordinates[1]);
// 	return (
// 		<>
// 			<line x1={x1} y1={y1} x2={x2} y2={y2} {...restProps} />
// 		</>
// 	);
// };

function Map({ className }: { className?: string }) {
	const [content, setContent] = useState<string>("");
	const [currentTime, setCurrentTime] = useState<Date>(new Date());

	const zoom = useAnimation(1.3);
	const coordinatesX = useAnimation(0);
	const coordinatesY = useAnimation(0);

	function zoomIn(coordinates: [number, number]) {
		zoom.setValue(3, 1000);
		coordinatesX.setValue(coordinates[0], 1000);
		coordinatesY.setValue(coordinates[1], 1000);

		console.log("hello")
	}

	function handleMoveEnd(position: Position) {
		zoom.setValueNoAnimation(position.zoom);
		coordinatesX.setValueNoAnimation(position.coordinates[0]);
		coordinatesY.setValueNoAnimation(position.coordinates[1]);
	}

	function handleMoveStart() {
		zoom.cancelAnimation();
		coordinatesX.cancelAnimation();
		coordinatesY.cancelAnimation();
	}

	// function getFlightPosition(
	// 	departureTime: Date,
	// 	departurePosition: [number, number],
	// 	arrivalTime: Date,
	// 	arrivalPosition: [number, number],
	// 	currentTime: Date
	// ) {
	// 	// If the current time is before the departure time, return the departure position
	// 	if (currentTime < departureTime) {
	// 		return departurePosition;
	// 	}

	// 	// If the current time is after the arrival time, return the arrival position
	// 	if (currentTime > arrivalTime) {
	// 		return arrivalPosition;
	// 	}

	// 	const { projection } = useMapContext();
	// 	const [x1, y1] = projection(departurePosition);
	// 	const [x2, y2] = projection(arrivalPosition);

	// 	// Calculate the position based on the time
	// 	const totalDuration = arrivalTime.getTime() - departureTime.getTime();
	// 	const elapsed = currentTime.getTime() - departureTime.getTime();

	// 	const deltaX = x2 - x1;
	// 	const deltaY = y2 - y1;

	// 	const currentX =
	// 		x1 + deltaX * (elapsed / totalDuration);
	// 	const currentY =
	// 		y1 + deltaY * (elapsed / totalDuration);

	// 	console.log(currentX, currentY)

	// 	return [currentX, currentY];
	// }

	useEffect(() => {
		const startTime = new Date().getTime();
		const interval = setInterval(() => {
			const currentTime = new Date().getTime();
			const elapsedTime = (currentTime - startTime) * 500;
			setCurrentTime(new Date(startTime + elapsedTime));
		}, 1000);

		return () => {
			clearInterval(interval);
		};
	}, []);

	return (
		<>
			<Tooltip
				id="my-tooltip"
				className="border border-white z-[100]"
				classNameArrow="border-b-[1px] border-r-[1px] border-white"
			>
				{content}
			</Tooltip>
			<div
				className={cn(
					"border rounded-xl flex justify-center items-center flex-1  overflow-hidden relative",
					className
				)}
			>
				<Chip
					color="blue"
					className="absolute top-8 right-8 text-2xl h-[40px] w-[130px] px-4  rounded-xl"
				>
					{currentTime.toLocaleTimeString()}
				</Chip>
				<ComposableMap
					className="h-full w-full"
					projection={"geoEqualEarth"}
				>
					<ZoomableGroup
						zoom={zoom.value}
						center={[coordinatesX.value, coordinatesY.value]}
						onMoveEnd={handleMoveEnd}
						onMoveStart={handleMoveStart}
					>
						<Geographies geography={geoUrl}>
							{({ geographies }) =>
								geographies.map((geo) => (
									<Geography
										data-tooltip-id="my-tooltip"
										key={geo.rsmKey}
										geography={geo}
										onMouseEnter={() => {
											const { name } = geo.properties;
											setContent(name);
										}}
										onMouseLeave={() => {
											setContent("");
										}}
										className="hover:fill-mainRed transition-all duration-75 ease-in-out stroke-white stroke-[0.2px]"
									/>
								))
							}
						</Geographies>
						{flights.map((flight, idx) => {
							return (
								<PlaneMarker
									currentTime={currentTime}
									flight={flight}
									key={idx}
									onClick={zoomIn}
								/>
							);
						})}
						{airports.map(({ coordinates }, idx) => (
							<Marker
								key={idx}
								coordinates={coordinates as [number, number]}
							>
								{/* <circle
									r={1}
									className="fill-yellow-500"
									// onClick={() => {
									// 	zoomIn(coordinates as [number, number]);
									// }}
								/> */}
								 <Airport
									onClick={() => {
										zoomIn(coordinates as [number, number]);
									}}
								/>
							</Marker>
						))}
						{/* {flights.map(
							(
								{ originCoordinate, destinationCoordinate },
								idx
							) => (
								<Line
									key={idx}
									from={originCoordinate as [number, number]}
									to={
										destinationCoordinate as [
											number,
											number
										]
									}
									className="border-dashed stroke-[#FF0000] stroke-[1px]"
									// style={{
									// 	strokeDasharray: "5, 5",
									// }}
								/>
							)
						)} */}
					</ZoomableGroup>
				</ComposableMap>
			</div>
		</>
	);
}

export default Map;
