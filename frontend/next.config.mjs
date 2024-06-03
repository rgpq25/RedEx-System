/** @type {import('next').NextConfig} */
const nextConfig = {
    env: {
      NEXT_PUBLIC_API: "http://localhost:8080"
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
  