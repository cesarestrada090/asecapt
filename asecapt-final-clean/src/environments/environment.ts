/**
 * Angular Environment Configuration
 * @deprecated Use ../app/constants.ts instead for application configuration
 */
import { APP_CONFIG, ENV } from '../app/constants';

export const environment = {
  production: ENV.production,
  // Legacy compatibility - redirect to constants
  apiUrl: APP_CONFIG.api.baseUrl,
  baseUrl: APP_CONFIG.frontend.baseUrl
}; 