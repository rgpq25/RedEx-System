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
	useMapContext,
} from "react-simple-maps";

import { Tooltip } from "react-tooltip";
import Plane from "./plane";
import useAnimation from "../hooks/useAnimation";
import Airport from "./airport";

//TODO: Download and store on local repository, currently depends on third party URL
const geoUrl = "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json";

const planes = [
	{
		rotation: 0,
		coordinates: [-58.3816, -34.6037],
		originCoordinate: [-58.3816, -54.6037],
		destinationCoordinate: [-60, -100], //TODO: Check logic, by placing actual markers on coordinate
		capacity: 98,
	},
	{
		rotation: 0,
		coordinates: [2.3522, 48.8566],
		originCoordinate: [-58.3816, -34.6037],
		destinationCoordinate: [20, 48.8566],
		capacity: 30,
	},
	{
		rotation: -45,
		coordinates: [10, 10],
		originCoordinate: [59, 0],
		destinationCoordinate: [0, 0],
		capacity: 2,
	},
	{
		rotation: 0,
		coordinates: [0, 0],
		originCoordinate: [0, 0],
		destinationCoordinate: [10, 10],
		capacity: 0,
	},
];

const airports = [
	{
		coordinates: [-65, -34.6037],
	},
	{
		coordinates: [26, 0],
	},
];

//define props for coordinates and zoom
type Position = {
	coordinates: [number, number];
	zoom: number;
};

const CustomLine = forwardRef(
	(
		{
			coordinates = [
				[0, 0],
				[0, 0],
			],
			...restProps
		},
		ref
	) => {
		const { projection } = useMapContext();
		const [x1, y1] = projection(coordinates[0]);
		const [x2, y2] = projection(coordinates[1]);
		return <line x1={x1} y1={y1} x2={x2} y2={y2} {...restProps} />;
	}
);

function Map({ className }: { className: string }) {
	const [content, setContent] = useState<string>("");
	const zoom = useAnimation(1.3);
	const coordinatesX = useAnimation(0);
	const coordinatesY = useAnimation(0);

	function zoomIn(coordinates: [number, number]) {
		zoom.setValue(3, 1000);
		coordinatesX.setValue(coordinates[0], 1000);
		coordinatesY.setValue(coordinates[1], 1000);
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

	function getCurrentPositionByTime(
		originTime: Date,
		destinationTime: Date,
		currentTime: Date,
		originCoordinate: [number, number],
		destinationCoordinate: [number, number]
	) {
		//given the origin and
	}

	return (
		<>
			<Tooltip
				id="my-tooltip"
				className="border border-white"
				classNameArrow="border-b-[1px] border-r-[1px] border-white"
			>
				{content}
			</Tooltip>
			<div
				className={cn(
					"border rounded-xl flex justify-center items-center flex-1  overflow-hidden ",
					className
				)}
			>
				<ComposableMap data-tip="" className="h-full w-full">
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
										className="hover:fill-mainRed transition-all duration-75 ease-in-out"
									/>
								))
							}
						</Geographies>
						{planes.map(
							({ rotation, coordinates, capacity }, idx) => (
								<Marker
									key={idx}
									coordinates={
										coordinates as [number, number]
									}
								>
									<Plane
										rotationAngle={rotation}
										capacity={capacity}
										onClick={() => {
											zoomIn(
												coordinates as [number, number]
											);
										}}
									/>
								</Marker>
							)
						)}
						{airports.map(({ coordinates }, idx) => (
							<Marker
								key={idx}
								coordinates={coordinates as [number, number]}
							>
								<Airport
									onClick={() => {
										zoomIn(coordinates as [number, number]);
									}}
								/>
							</Marker>
						))}
						{planes.map(
							(
								{ originCoordinate, destinationCoordinate },
								idx
							) => (
								<CustomLine
									key={idx}
									coordinates={[
										originCoordinate as [number, number],
										destinationCoordinate as [
											number,
											number
										],
									]}
									strokeWidth={3}
									stroke="#FF0000"
								/>
							)
						)}
					</ZoomableGroup>
				</ComposableMap>
			</div>
		</>
	);
}

export default Map;
