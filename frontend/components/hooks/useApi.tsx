import axios, { AxiosResponse } from "axios";
import { useEffect, useState } from "react";

//TODO: Cant catch connection errors

function useApi(
	method: "GET" | "POST" | "PUT" | "DELETE",
	url: string,
	successCallback: (data: any) => void,
	errorCallback: (error: any) => void
) {
	const [isLoading, setIsLoading] = useState(true);

	useEffect(() => {
		async function fetchApi() {
			try {
				setIsLoading(true);
				const response: AxiosResponse = await axios({
					method: method,
					url: url,
				});

				setIsLoading(false);
				successCallback(response.data);
			} catch (error: any) {
				setIsLoading(false);
				errorCallback(error.message);
			}
		}

		try {
			fetchApi();
		} catch (error) {
			console.log("Error en el fetchApi")
		}
	}, []);

	return {
		isLoading,
	};
}

export default useApi;
