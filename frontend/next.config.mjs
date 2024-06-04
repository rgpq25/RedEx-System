/** @type {import('next').NextConfig} */
const nextConfig = {
    env: {
        NEXT_PUBLIC_API: "https://inf226-981-3c.inf.pucp.edu.pe",
    },
    images: { unoptimized: true },
    output: "export",
    trailingSlash: true,
    typescript: {
        ignoreBuildErrors: true,
    },
    eslint: {
        ignoreDuringBuilds: true,
    },
};

export default nextConfig;
  