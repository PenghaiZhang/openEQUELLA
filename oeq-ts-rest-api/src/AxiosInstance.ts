import Axios, { AxiosResponse, AxiosError } from 'axios';
import axiosCookieJarSupport from 'axios-cookiejar-support';
import * as tough from 'tough-cookie';
import { repackageError } from './Errors';
import { stringify } from 'query-string';

// So that cookies work when used in non-browser (i.e. Node/Jest) type environments. And seeing
// the oEQ security is based on JSESSIONID cookies currently this is key.
const axios = axiosCookieJarSupport(Axios.create());
axios.defaults.jar = new tough.CookieJar();
axios.defaults.withCredentials = true;

const catchHandler = (error: AxiosError | Error): never => {
  throw repackageError(error);
};

/**
 * Executes a HTTP GET for a given path.
 *
 * @param path The URL path for the target GET
 * @param validator A function to perform runtime type checking against the result - typically with typescript-is
 * @param queryParams The query parameters to send with the GET request
 * @param transformer A function which returns a copy of the raw data from the GET with any required values transformed - this should NOT mutate the input data (transforms should start on a copy/clone of the input)
 */
export function GET<T>(path: string): Promise<unknown>;
export function GET<T>(
  path: string,
  validator: (data: unknown) => data is T,
  queryParams?: object,
  transformer?: (data: unknown) => T
): Promise<T>;
export function GET<T>(
  path: string,
  validator?: (data: unknown) => data is T,
  queryParams?: object,
  transformer?: (data: unknown) => T
): Promise<T> | Promise<unknown> {
  return axios
    .get(path, {
      params: queryParams,
      paramsSerializer: (params) => stringify(params),
    })
    .then((response: AxiosResponse<unknown>) => {
      const data = transformer ? transformer(response.data) : response.data;
      if (validator && !validator(data)) {
        // If a validator is provided, but it fails to validate the provided data...
        throw new Error('Data format mismatch with data received from server.');
      }

      return data;
    })
    .catch(catchHandler);
}

export function PUT<T, R>(path: string, data?: T): Promise<R> {
  return axios
    .put(path, data)
    .then((response: AxiosResponse<R>) => response.data)
    .catch(catchHandler);
}

export default axios;
