/** @type {import('next').NextConfig} */
const nextConfig = {
    env: {
      API_URL_BASE: "https://inf226-981-3c.inf.pucp.edu.pe"
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
  