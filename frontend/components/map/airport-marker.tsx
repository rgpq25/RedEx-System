"use client";
import { Aeropuerto } from "@/lib/types";
import { cn } from "@/lib/utils";
import { useEffect, useState } from "react";
import { Marker } from "react-simple-maps";

function AirportMarker({
	currentZoom,
	aeropuerto,
	coordinates,
	onClick,
}: {
	currentZoom: number;
	aeropuerto: Aeropuerto;
	coordinates: [number, number];
	onClick: (coordinates: [number, number]) => void;
}) {
	//TODO: Aqui se deberia mostrar la capacidad actual, pero no se tiene la informacion

	const [isHovering, setIsHovering] = useState(false);

	return (
		<>
			<Marker
				coordinates={coordinates}
				onMouseEnter={() => {
					setIsHovering(true);
				}}
				onMouseLeave={() => {
					setIsHovering(false);
				}}
			>
				<Airport3 currentZoom={currentZoom} onClick={() => onClick(coordinates)} />
				{isHovering && (
					<>
						<rect
							x={-10}
							y={-18.4}
							width="20"
							height="9"
							fill="black"
							stroke="white"
							strokeWidth="1"
							rx={2}
							ry={2}
						/>
						<text
							textAnchor="middle"
							y={-12}
							x={0.5}
							className="text-[5px] font-poppins fill-white bg-black"
						>
							{aeropuerto.capacidad_maxima}%
						</text>
					</>
				)}
				<text
					textAnchor="middle"
					y={currentZoom > 4 ? 1.2 : 2.3}
					fontSize={currentZoom > 4 ? 3 - 0.3 * currentZoom : 3}
					className={cn(
						"font-poppins font-semibold transition-all duration-200",
						currentZoom > 3 ? "fill-black" : "fill-transparent"
					)}
				>
					{aeropuerto.ubicacion.id}
				</text>
			</Marker>
		</>
	);
}
export default AirportMarker;

function Airport({ onClick }: { onClick: () => void }) {
	const transformation = "scale(0.02) translate(-250, -250)";

	return (
		<>
			<g>
				<polygon
					fill="#D8D8DA"
					points="18.285,176.821 146.286,166.263 274.285,324.623 274.285,472.424 18.285,324.623 "
					transform={transformation}
				/>
				<polygon
					fill="#003169"
					points="229.107,446.264 63.465,350.63 63.465,202.901 229.107,298.534 "
					transform={transformation}
				/>
				<polygon
					fill="#013F8A"
					points="54.858,197.936 237.715,303.508 237.715,288.428 54.858,182.856 "
					transform={transformation}
				/>
				<polygon
					fill="#C6C5CB"
					points="274.285,472.424 493.715,345.737 493.715,197.936 274.285,324.623 "
					transform={transformation}
				/>
				<polygon
					fill="#003169"
					points="146.286,166.263 365.715,39.576 512,220.545 292.572,347.232 "
					transform={transformation}
				/>
				<polygon
					fill="#013F8A"
					points="146.286,166.263 0,178.394 219.429,51.707 365.715,39.576 "
					transform={transformation}
				/>
				<polygon
					fill="#ACABB1"
					points="73.143,356.294 73.143,208.492 219.429,292.95 219.429,440.752 "
					transform={transformation}
				/>
				<path
					fill="#898890"
					d="M73.143,312.825l146.286,84.458v2.414L73.143,315.239C73.143,315.239,73.143,312.825,73.143,312.825z
                    M219.429,418.381L73.143,333.923v2.414l146.286,84.458V418.381z M73.143,294.141L219.429,378.6v-2.414L73.143,291.728
                    C73.143,291.728,73.143,294.141,73.143,294.141z M73.143,252.409l146.286,84.458v-2.414L73.143,249.995
                    C73.143,249.995,73.143,252.409,73.143,252.409z M73.143,229.362v2.414l146.286,84.458v-2.414L73.143,229.362z M73.143,273.043
                    l146.286,84.458v-2.414L73.143,270.629C73.143,270.629,73.143,273.043,73.143,273.043z"
					transform={transformation}
				/>
				<g>
					<polygon
						fill="#2487FF"
						points="310.887,345.754 347.428,324.623 347.428,366.852 310.887,388.049 	"
						transform={transformation}
					/>
					<polygon
						fill="#2487FF"
						points="365.743,314.082 402.286,292.95 402.286,335.179 365.743,356.378 	"
						transform={transformation}
					/>
					<polygon
						fill="#2487FF"
						points="420.601,282.411 457.144,261.279 457.144,303.508 420.601,324.706 	"
						transform={transformation}
					/>
				</g>

				<circle r={12} className="fill-transparent" onClick={onClick} />
			</g>
		</>
	);
}

function Airport2({ onClick }: { onClick: () => void }) {
	const transformation = "scale(0.015) translate(-250, -290)";

	return (
		<>
			<polygon
				fill="#F07B52"
				points="17.351,170.311 256.534,8.008 495.716,170.311 495.716,503.458 17.351,503.458 "
				transform={transformation}
			/>
			<polygon
				fill="#E5563C"
				points="273.618,19.601 256.534,8.008 17.351,170.311 17.351,503.458 51.52,503.458 51.52,170.311"
				transform={transformation}
			/>
			<rect
				id="SVGCleanerId_0"
				x="102.774"
				y="213.022"
				fill="#74757B"
				width="307.52"
				height="290.436"
				transform={transformation}
			/>
			<g>
				<rect
					id="SVGCleanerId_0_1_"
					x="102.774"
					y="213.022"
					fill="#74757B"
					width="307.52"
					height="290.436"
					transform={transformation}
				/>
			</g>
			<rect x="102.774" y="213.022" fill="#606268" width="34.169" height="290.436" transform={transformation} />
			<rect x="85.689" y="178.853" fill="#D7D8D9" width="341.689" height="34.169" transform={transformation} />
			<rect x="102.774" y="298.444" fill="#FDDD85" width="102.507" height="102.507" transform={transformation} />
			<rect x="102.774" y="298.444" fill="#FDD042" width="25.627" height="102.507" transform={transformation} />
			<rect x="136.943" y="298.444" fill="#E1A527" width="34.169" height="51.253" transform={transformation} />
			<rect x="102.774" y="400.951" fill="#FDDD85" width="102.507" height="102.507" transform={transformation} />
			<rect x="102.774" y="400.951" fill="#FDD042" width="25.627" height="102.507" transform={transformation} />
			<rect x="136.943" y="400.951" fill="#E1A527" width="34.169" height="51.253" transform={transformation} />
			<rect x="205.281" y="400.951" fill="#FDDD85" width="102.507" height="102.507" transform={transformation} />
			<rect x="205.281" y="400.951" fill="#FDD042" width="25.627" height="102.507" transform={transformation} />
			<rect x="239.449" y="400.951" fill="#E1A527" width="34.169" height="51.253" transform={transformation} />
			<path
				d="M499.946,163.684L260.897,1.382c-2.713-1.843-6.211-1.842-8.927,0L12.554,163.684c-2.196,1.491-3.745,3.972-3.745,6.627
				v333.147c0,4.423,4.119,8.542,8.542,8.542h478.365c4.424,0,7.474-4.119,7.474-8.542V170.311
				C503.191,167.656,502.143,165.175,499.946,163.684z M93.164,205.548v-18.152h325.673v18.152H93.164z M144.417,306.987h18.152v35.237
				h-18.152V306.987z M136.943,358.24h34.169c4.423,0,7.474-4.119,7.474-8.542v-42.711h18.152v86.49h-25.627h-34.169h-26.694v-86.49
				H128.4v42.711C128.4,354.12,132.52,358.24,136.943,358.24z M144.417,409.493h18.152v35.237h-18.152V409.493z M110.248,409.493H128.4
				v42.711c0,4.423,4.119,8.542,8.542,8.542h34.169c4.423,0,7.474-4.119,7.474-8.542v-42.711h18.152v86.49h-86.49V409.493z
				M212.755,495.983v-86.49h18.152v42.711c0,4.423,4.119,8.542,8.542,8.542h34.169c4.424,0,7.474-4.119,7.474-8.542v-42.711h18.152
				v86.49H212.755z M246.924,409.493h18.152v35.237h-18.152V409.493z M487.174,495.983h-69.406V324.071
				c0-4.423-3.585-8.008-8.008-8.008c-4.424,0-8.008,3.586-8.008,8.008v171.912h-86.49v-95.032c0-4.423-3.051-7.474-7.474-7.474
				h-95.032v-95.032c0-4.423-3.052-7.474-7.474-7.474h-95.032v-69.406h291.504v68.338c0,4.423,3.585,8.008,8.008,8.008
				c4.424,0,8.008-3.586,8.008-8.008v-68.338h9.61c4.424,0,7.474-4.119,7.474-8.542v-34.169c0-4.423-3.051-7.474-7.474-7.474H85.689
				c-4.423,0-8.542,3.052-8.542,7.474v34.169c0,4.423,4.119,8.542,8.542,8.542h8.542v274.419H24.826V174.555L256,17.687
				l231.174,156.868V495.983z"
				transform={transformation}
			/>
			<path
				d="M214.357,76.88c0,4.423,3.586,8.008,8.008,8.008h68.338c4.424,0,8.008-3.586,8.008-8.008s-3.585-8.008-8.008-8.008h-68.338
				C217.942,68.872,214.357,72.457,214.357,76.88z"
				transform={transformation}
			/>
			<path
				d="M290.703,103.041h-68.338c-4.423,0-8.008,3.586-8.008,8.008c0,4.423,3.586,8.008,8.008,8.008h68.338
				c4.424,0,8.008-3.586,8.008-8.008C298.711,106.626,295.127,103.041,290.703,103.041z"
				transform={transformation}
			/>
			<path
				d="M290.703,137.21h-68.338c-4.423,0-8.008,3.586-8.008,8.008s3.586,8.008,8.008,8.008h68.338c4.424,0,8.008-3.586,8.008-8.008
				S295.127,137.21,290.703,137.21z"
				transform={transformation}
			/>
			<path
				d="M136.943,383.867h34.169c4.423,0,8.008-3.586,8.008-8.008c0-4.423-3.586-8.008-8.008-8.008h-34.169
				c-4.423,0-8.008,3.586-8.008,8.008C128.934,380.281,132.52,383.867,136.943,383.867z"
				transform={transformation}
			/>
			<path
				d="M171.112,470.357h-34.169c-4.423,0-8.008,3.586-8.008,8.008c0,4.423,3.586,8.008,8.008,8.008h34.169
				c4.423,0,8.008-3.586,8.008-8.008C179.12,473.942,175.534,470.357,171.112,470.357z"
				transform={transformation}
			/>
			<path
				d="M273.618,470.357h-34.169c-4.423,0-8.008,3.586-8.008,8.008c0,4.423,3.586,8.008,8.008,8.008h34.169
				c4.424,0,8.008-3.586,8.008-8.008C281.627,473.942,278.042,470.357,273.618,470.357z"
				transform={transformation}
			/>
			{/* <circle r={1} className="fill-red-500" /> */}
		</>
	);
}

function Airport3({ currentZoom, onClick }: { currentZoom: number; onClick: () => void }) {
	return (
		<>
			<g
				fill="none"
				stroke="#B31412"
				strokeWidth="0.4"
				strokeLinecap="round"
				strokeLinejoin="round"
				transform={"scale(" + (0.3 - (0.01)*currentZoom) + ") translate(-12, -24)"}
			>
				<path d="M12 21.7C17.3 17 20 13 20 10a8 8 0 1 0-16 0c0 3 2.7 6.9 8 11.7z" className="fill-[#EA4335]" />
				<circle cx="12" cy="10" r="3" className="fill-[#B31412] stroke-[#B31412]" />
			</g>
			<circle r={7} className="fill-transparent" onClick={onClick} />
			{/* <text textAnchor="middle" y={15} style={{ fontFamily: "system-ui", fill: "#5D5A6D" }}>
				{'a'}
			</text> */}
			{/* <circle
				r={0.2}
				className="fill-red-500"
			/> */}
		</>
	);
}
