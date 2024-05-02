"use client";

import { cn } from "@/lib/utils";
import React, { useEffect, useState } from "react";
import {
	ComposableMap,
	Geographies,
	Geography,
	Marker,
	Annotation,
	ZoomableGroup,
} from "react-simple-maps";

import { Tooltip } from "react-tooltip";
import Plane from "./plane";

//TODO: Download and store on local repository, currently depends on third party URL
const geoUrl = "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json";

const markers = [
	{
		markerOffset: -15,
		name: "Buenos Aires",
		coordinates: [-58.3816, -34.6037],
	},
];

//define props for coordinates and zoom
type Position = {
	coordinates: [number, number];
	zoom: number;
};

function Map({ className }: { className: string }) {
	const [content, setContent] = useState<string>("");

	const [position, setPosition] = useState<Position>({
		coordinates: [0, 0],
		zoom: 1,
	});

	function zoomIn(coordinates: [number, number]) {
		setPosition({ coordinates: coordinates, zoom: 3});
	}

	function handleMoveEnd(position: Position) {
        console.log(position);
		setPosition(position);
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
					"border rounded-xl flex justify-center items-center flex-1  overflow-hidden border-red-500",
					className
				)}
			>
				<ComposableMap data-tip="" className="h-full w-full">
					<ZoomableGroup
						zoom={position.zoom}
						center={position.coordinates}
						onMoveEnd={handleMoveEnd}
                        
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
						{markers.map(({ name, coordinates, markerOffset }) => (
							<Marker
								key={name}
								coordinates={coordinates as [number, number]}
							>
								{/* <circle
									r={8}
									stroke="#fff"
									strokeWidth={1}
									className="fill-mainRed"
								/>  */}
								<Plane
									onClick={() => {
										console.log("clicked");
										zoomIn(coordinates as [number, number]);
									}}
								/>
							</Marker>
						))}
						<Annotation
							subject={[2.3522, 48.8566]}
							dx={102}
							dy={30}
							connectorProps={{
								strokeLinecap: "round",
								className: "stroke-mainRed stroke-[2px]",
							}}
						>
							<text
								x="-8"
								textAnchor="end"
								alignmentBaseline="middle"
								// fill="#F53"
								className="fill-mainRed"
							>
								{"Paris"}
							</text>
						</Annotation>
					</ZoomableGroup>
				</ComposableMap>
			</div>
		</>
	);
}

export default Map;
