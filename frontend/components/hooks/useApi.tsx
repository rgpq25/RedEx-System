import axios from "axios";

import { useEffect, useState } from "react";


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
				const response = await axios({
					method: method,
					url: url,
				});
				setIsLoading(false);
				successCallback(response.data);
			} catch (error) {
				console.log(error);
				errorCallback(error);
			}
		}

		fetchApi();
	}, []);

	return {
		isLoading,
	};
}

export default useApi;
