import axios from 'axios';
import { LoginForm, LoginResponse } from '../types/LoginTypes';

const BASE_URL = process.env.REACT_APP_API_URL;

export const login = async (data: LoginForm): Promise<LoginResponse> => {
  try {
    const url = `${BASE_URL}/auth/login`
    const response = await axios.post<LoginResponse>(url, data)
    return response.data
  } catch (error) {
    throw error;
  }
};
