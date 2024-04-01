"use client";

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

//TODO: Download and store on local repository, currently depends on third party URL
const geoUrl = "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json";

const markers = [
    {
        markerOffset: -15,
        name: "Buenos Aires",
        coordinates: [-58.3816, -34.6037],
    },
];

function Visualizator({ className }: { className: string }) {
    const [content, setContent] = useState<string>("");

    useEffect(() => {
        console.log(content);
    }, [content]);

    return (
        <>
            <Tooltip
                id="my-tooltip"
                className="border border-white"
                classNameArrow="border-b-[1px] border-r-[1px] border-white"
            >
                {content}
            </Tooltip>
            <div className={className}>
                <ComposableMap data-tip="">
                    <ZoomableGroup>
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
                                <circle
                                    r={8}
                                    stroke="#fff"
                                    strokeWidth={1}
                                    className="fill-mainRed"
                                />
                                <text
                                    textAnchor="middle"
                                    y={markerOffset}
                                    className="fill-mainRed"
                                >
                                    {name}
                                </text>
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

export default Visualizator;
