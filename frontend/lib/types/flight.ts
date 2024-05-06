export type Flight = {
    originTime: Date;
    originCoordinate: [number, number];
    destinationTime: Date;
    destinationCoordinate: [number, number];
    capacity: number;
}