/** @type {import('next').NextConfig} */
const nextConfig = {
    env: {
        // NEXT_PUBLIC_API: "https://inf226-981-3c.inf.pucp.edu.pe",
        // NEXT_PUBLIC_SOCKET: "wss://inf226-981-3c.inf.pucp.edu.pe:8080",
        NEXT_PUBLIC_API: "http://localhost:8080",
        NEXT_PUBLIC_SOCKET: "ws://localhost:8080",
    },
    images: { unoptimized: true },
    output: "export",
    trailingSlash: true,
    swcMinify: false,
    typescript: {
        ignoreBuildErrors: true,
    },
    eslint: {
        ignoreDuringBuilds: true,
    },
};

export default nextConfig;
