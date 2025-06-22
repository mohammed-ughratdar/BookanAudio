import axios from 'axios';
import { LoginForm, LoginResponse } from '../types/LoginTypes';
import { RegisterForm } from '../types/RegisterTypes';

const BASE_URL = process.env.REACT_APP_API_URL;
const CALLBACK_URL = process.env.REACT_CALLBACK_URL;
const AUTH_URL = process.env.REACT_AUTH_URL;
const CLIENT_ID = process.env.REACT_CLIENT_ID;
// TODO: add creds to .env

export const login = async (data: LoginForm): Promise<LoginResponse> => {
  try {
    const url = `${BASE_URL}/auth/login`
    const response = await axios.post<LoginResponse>(url, data)
    return response.data
  } catch (error) {
    throw error;
  }
};

export const register = async (data: RegisterForm): Promise<boolean> => {
    try {
      const url = `${BASE_URL}/auth/register`;
      const response = await axios.post<string>(url, data);
  
      return response.status === 200 || response.status === 201;
    } catch (error) {
      console.error("Registration failed:", error);
      return false;
    }
};  

export const oauthLogin = (): void => {
    const url = `${AUTH_URL}?client_id=${CLIENT_ID}&redirect_uri=${CALLBACK_URL}&response_type=code&scope=openid%20profile%20email`;
  
    window.location.href = url; 
  };
  