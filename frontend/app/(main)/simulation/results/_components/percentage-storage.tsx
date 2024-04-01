import { cn } from "@/lib/utils";

const mockData = [
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 10,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 30,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 80,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 30,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 4,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 50,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 30,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 30,
    },
    {
        city: "Lima",
        country: "Peru",
        airportName: "Jorge Chavez",
        percentage: 90,
    },
];

function PercentageStorage({ className }: { className: string }) {
    return (
        <div className={cn(className, "flex flex-col gap-3")}>
            <h2
                className="font-medium text-xl py-4 px-5  font-poppins text-center
                           bg-resultsPieceBackground border border-resultsPieceBorder/10 rounded-lg
            "
            >
                Promedio de capacidad usada por almacen
            </h2>
            <div
                className="flex flex-col flex-1
                            bg-resultsPieceBackground border border-resultsPieceBorder/10  rounded-lg
                            px-4 py-3 gap-1
            "
            >
                {mockData.map(({ city, country, airportName, percentage }) => {
                    return (
                        <StorageRow
                            key={city}
                            title={`${city}, ${country}`}
                            airportName={airportName}
                            percentage={percentage}
                        />
                    );
                })}
            </div>
        </div>
    );
}
export default PercentageStorage;

function StorageRow({
    title,
    airportName,
    percentage,
}: {
    title: string;
    airportName: string;
    percentage: number;
}) {
    return (
        <div className="flex items-center gap-3 border">
            <div className="flex-1 flex flex-col">
                <p className="text-xl font-semibold">{title}</p>
                <p className="text-muted-foreground text-sm">{airportName}</p>
            </div>
            <div
                className={cn(
                    "flex-1 flex justify-end font-medium text-xl",
                    percentage >= 30
                        ? percentage >= 50
                            ? percentage >= 80
                                ? "text-red-500"
                                : "text-yellow-500"
                            : "text-yellow-500"
                        : "text-green-500"
                )}
            >
                {percentage}%
            </div>
        </div>
    );
}
