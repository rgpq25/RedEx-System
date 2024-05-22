import axios, { AxiosResponse } from "axios";

export async function api(
    method: "GET" | "POST" | "PUT" | "DELETE",
    url: string,
    onSuccess: (data: any) => void,
    onError: (error: string) => void,
    data?: any
) {
    try {
        const response: AxiosResponse = await axios({
            method: method,
            url: url,
            data: data
        });
        onSuccess(response.data);
    } catch (error: any) {
        onError(error.message);
    }
}
