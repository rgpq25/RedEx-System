const localeIdentifier = "es-ES";

export function formatDateShort(date: Date | string): string {
    const fecha: Date = typeof date === "string" ? new Date(date) : date;

    if (isNaN(fecha.getTime())) {
        return "";
    }

    const opciones: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "numeric",
        day: "numeric",
    };

    return fecha.toLocaleString(localeIdentifier, opciones);
}

export function formatDateTimeShort(date: Date | string): string {
    const fecha: Date = typeof date === "string" ? new Date(date) : date;

    if (isNaN(fecha.getTime())) {
        return "";
    }

    const opciones: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "numeric",
        day: "numeric",
        hour: "numeric",
        minute: "numeric",
    };

    return fecha.toLocaleString(localeIdentifier, opciones);
}

export function formatDateLong(date: Date | string): string {
    const fecha: Date = typeof date === "string" ? new Date(date) : date;

    if (isNaN(fecha.getTime())) {
        return "";
    }

    const opciones: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "long",
        day: "numeric",
    };

    return fecha.toLocaleString(localeIdentifier, opciones);
}

export function formatDateTimeLong(date: Date | string): string {
    const fecha: Date = typeof date === "string" ? new Date(date) : date;

    if (isNaN(fecha.getTime())) {
        return "";
    }

    const opciones: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "numeric",
        minute: "numeric",
    };

    return fecha.toLocaleString(localeIdentifier, opciones);
}
