import { forwardRef, useEffect, useRef } from "react";
import { Marker } from "react-leaflet";
import "leaflet-rotatedmarker"

const RotateMarker = forwardRef(({ children, ...props }:any, forwardRef:any) => {
    const markerRef:any = useRef();

    const { rotationAngle, rotationOrigin } = props;
    useEffect(() => {
        const marker = markerRef.current;
        if (marker) {
            marker.setRotationAngle(rotationAngle);
            marker.setRotationOrigin(rotationOrigin);
        }
    }, [rotationAngle, rotationOrigin]);

    return (
        <Marker
            ref={(ref) => {
                markerRef.current = ref;
                if (forwardRef) {
                    forwardRef.current = ref;
                }
            }}
            {...props}
        >
            {children}
        </Marker>
    );
});

export default RotateMarker;