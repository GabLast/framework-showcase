import axios, { AxiosRequestConfig } from 'axios';

const BASE_URL = import.meta.env.REST_API ? import.meta.env.REST_API : "http://localhost:8082/"
const TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJGcmFtZXdvcmstU2hvd2Nhc2UtQXBwIiwic3ViIjoiZGF0YSIsInRva2VuIjoib29leXBES2N6Ry9lUkVVWkpXWHpGZTlRMkEyeVB3dVBzWlg2Q1pqeERtdz0iLCJleHAiOjE3Mjg3OTIwMDB9._3wd9EGj-RKupDKK--qtrzxqsuQ7d5bYPw1-IZWX5LOv1aA24FokDmiRSypOY4JHab1_FRoGmN2weS4KPfDBSw"
// const TOKEN = localStorage.getItem("token")

const axiosRequest = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${TOKEN}`,
    // 'Access-Control-Allow-Origin': '*'
    // You can add other headers like authorization token here
  },
});


export const getRequest = async <T> (url: string, params?: string, body?:T) => {

  const response = await axiosRequest.get(
    url,
    {
      params: params, 
      data:body
    },
  )

  return response.data
}

export const postRequest = async <T> (url: string, params?: string, body?:T) => {
  const response = await axiosRequest.post(
    url,
    {
      params: params, 
      data:body
    },
  )

  return response.data
}

export const postRequestConfig = async <T> (url: string, body?:T, config?: AxiosRequestConfig) => {
  const response = await axiosRequest.post(
    url,
    body,
    config
  )

  return response.data
}

