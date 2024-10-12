import axios, { AxiosRequestConfig } from "axios";
import queryString from "query-string";

const BASE_URL = import.meta.env.REST_API
  ? import.meta.env.REST_API
  : "http://localhost:8082/";
const TOKEN =
  "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJGcmFtZXdvcmstU2hvd2Nhc2UtQXBwIiwic3ViIjoiZGF0YSIsInRva2VuIjoib29leXBES2N6Ry9lUkVVWkpXWHpGZTlRMkEyeVB3dVBzWlg2Q1pqeERtdz0iLCJleHAiOjE3Mjg3OTIwMDB9._3wd9EGj-RKupDKK--qtrzxqsuQ7d5bYPw1-IZWX5LOv1aA24FokDmiRSypOY4JHab1_FRoGmN2weS4KPfDBSw";
// const TOKEN = localStorage.getItem("token")

const axiosRequest = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
    Authorization: `Bearer ${TOKEN}`,
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers":
      "Origin, X-Requested-With, Content-Type, Accept",
    // You can add other headers like authorization token here
  },
});

axios.interceptors.request.use(request => {
  console.log('Starting Request', JSON.stringify(request, null, 2))
  return request
})

export const getRequest = async <T>(url: string, params?: T) => {

  const tmp = queryString.stringify({word:"sed", testTypeRest: {id:1}})
  console.log(tmp)

  const response = await axiosRequest.get(url, {
    params: params
  });

  return response.data;
};

export const postRequest = async <T>(
  url: string,
  params?: string,
  body?: T
) => {
  const response = await axiosRequest.post(url, {
    params: params,
    data: body,
  });

  return response.data;
};