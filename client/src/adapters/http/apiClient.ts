import { FetchHttpClient } from './FetchHttpClient';

let sharedClient: FetchHttpClient | null = null;

/**
 * Single factory for the app-wide HTTP client. Every caller shares one
 * instance so the unauthorized interceptor registered by SessionProvider
 * observes every request in the app.
 */
export function getApiClient(): FetchHttpClient {
  if (!sharedClient) {
    const baseUrl = import.meta.env.VITE_API_URL?.trim() ?? '';
    if (!baseUrl) {
      console.error(
        '[Intensity] VITE_API_URL is empty. Rebuild with production/demo env or API calls will hit the WebView origin.',
      );
    }
    sharedClient = new FetchHttpClient(baseUrl);
  }
  return sharedClient;
}
