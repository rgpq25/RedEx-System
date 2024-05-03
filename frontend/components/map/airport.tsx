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
export default Airport;
